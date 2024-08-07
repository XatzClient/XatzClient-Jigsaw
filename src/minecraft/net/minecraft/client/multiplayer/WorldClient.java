package net.minecraft.client.multiplayer;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.Callable;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import me.xatzdevelopments.xatz.client.main.Xatz;
import me.xatzdevelopments.xatz.client.modules.GodMode;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.MovingSoundMinecart;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.client.particle.EntityFirework;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.profiler.Profiler;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.src.BlockPosM;
import net.minecraft.src.Config;
import net.minecraft.src.DynamicLights;
import net.minecraft.src.PlayerControllerOF;
import net.minecraft.src.Reflector;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ReportedException;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.storage.SaveDataMemoryStorage;
import net.minecraft.world.storage.SaveHandlerMP;
import net.minecraft.world.storage.WorldInfo;

public class WorldClient extends World {
	/** The packets that need to be sent to the server. */
	private NetHandlerPlayClient sendQueue;

	/** The ChunkProviderClient instance */
	private ChunkProviderClient clientChunkProvider;

	/** Contains all entities for this client, both spawned and non-spawned. */
	private final Set entityList = Sets.newHashSet();

	/**
	 * Contains all entities for this client that were not spawned due to a
	 * non-present chunk. The game will attempt to spawn up to 10 pending
	 * entities with each subsequent tick until the spawn queue is empty.
	 */
	private final Set entitySpawnQueue = Sets.newHashSet();
	private final Minecraft mc = Minecraft.getMinecraft();
	private final Set previousActiveChunkSet = Sets.newHashSet();
	private static final String __OBFID = "CL_00000882";
	private BlockPosM randomTickPosM = new BlockPosM(0, 0, 0, 3);
	private boolean playerUpdate = false;

	public WorldClient(NetHandlerPlayClient p_i45063_1_, WorldSettings p_i45063_2_, int p_i45063_3_,
			EnumDifficulty p_i45063_4_, Profiler p_i45063_5_) {
		super(new SaveHandlerMP(), new WorldInfo(p_i45063_2_, "MpServer"),
				WorldProvider.getProviderForDimension(p_i45063_3_), p_i45063_5_, true);
		this.sendQueue = p_i45063_1_;
		this.getWorldInfo().setDifficulty(p_i45063_4_);
		this.provider.registerWorld(this);
		this.setSpawnPoint(new BlockPos(8, 64, 8));
		this.chunkProvider = this.createChunkProvider();
		this.mapStorage = new SaveDataMemoryStorage();
		this.calculateInitialSkylight();
		this.calculateInitialWeather();
		Reflector.postForgeBusEvent(Reflector.WorldEvent_Load_Constructor, new Object[] { this });

		if (this.mc.playerController != null && this.mc.playerController.getClass() == PlayerControllerMP.class) {
			this.mc.playerController = new PlayerControllerOF(this.mc, p_i45063_1_);
		}
	}

	/**
	 * Runs a single tick for the world
	 */
	public void tick() {
		super.tick();
		this.setTotalWorldTime(this.getTotalWorldTime() + 1L);

		if (this.getGameRules().getBoolean("doDaylightCycle")) {
			this.setWorldTime(this.getWorldTime() + 1L);
		}

		this.theProfiler.startSection("reEntryProcessing");

		for (int i = 0; i < 10 && !this.entitySpawnQueue.isEmpty(); ++i) {
			Entity entity = (Entity) this.entitySpawnQueue.iterator().next();
			this.entitySpawnQueue.remove(entity);

			if (!this.loadedEntityList.contains(entity)) {
				this.spawnEntityInWorld(entity);
			}
		}

		this.theProfiler.endStartSection("chunkCache");
		this.clientChunkProvider.unloadQueuedChunks();
		this.theProfiler.endStartSection("blocks");
		this.updateBlocks();
		this.theProfiler.endSection();
	}

	/**
	 * Invalidates an AABB region of blocks from the receive queue, in the event
	 * that the block has been modified client-side in the intervening 80
	 * receive ticks.
	 */
	public void invalidateBlockReceiveRegion(int p_73031_1_, int p_73031_2_, int p_73031_3_, int p_73031_4_,
			int p_73031_5_, int p_73031_6_) {
	}

	/**
	 * Creates the chunk provider for this world. Called in the constructor.
	 * Retrieves provider from worldProvider?
	 */
	protected IChunkProvider createChunkProvider() {
		this.clientChunkProvider = new ChunkProviderClient(this);
		return this.clientChunkProvider;
	}

	protected void updateBlocks() {
		super.updateBlocks();
		this.previousActiveChunkSet.retainAll(this.activeChunkSet);

		if (this.previousActiveChunkSet.size() == this.activeChunkSet.size()) {
			this.previousActiveChunkSet.clear();
		}

		int i = 0;

		for (ChunkCoordIntPair chunkcoordintpair : this.activeChunkSet) {
			if (!this.previousActiveChunkSet.contains(chunkcoordintpair)) {
				int j = chunkcoordintpair.chunkXPos * 16;
				int k = chunkcoordintpair.chunkZPos * 16;
				this.theProfiler.startSection("getChunk");
				Chunk chunk = this.getChunkFromChunkCoords(chunkcoordintpair.chunkXPos, chunkcoordintpair.chunkZPos);
				this.playMoodSoundAndCheckLight(j, k, chunk);
				this.theProfiler.endSection();
				this.previousActiveChunkSet.add(chunkcoordintpair);
				++i;

				if (i >= 10) {
					return;
				}
			}
		}
	}

	public void doPreChunk(int p_73025_1_, int p_73025_2_, boolean p_73025_3_) {
		if (p_73025_3_) {
			this.clientChunkProvider.loadChunk(p_73025_1_, p_73025_2_);
		} else {
			this.clientChunkProvider.unloadChunk(p_73025_1_, p_73025_2_);
		}

		if (!p_73025_3_) {
			this.markBlockRangeForRenderUpdate(p_73025_1_ * 16, 0, p_73025_2_ * 16, p_73025_1_ * 16 + 15, 256,
					p_73025_2_ * 16 + 15);
		}
	}

	/**
	 * Called when an entity is spawned in the world. This includes players.
	 */
	public boolean spawnEntityInWorld(Entity entityIn) {
		boolean flag = super.spawnEntityInWorld(entityIn);
		this.entityList.add(entityIn);

		if (!flag) {
			this.entitySpawnQueue.add(entityIn);
		} else if (entityIn instanceof EntityMinecart) {
			this.mc.getSoundHandler().playSound(new MovingSoundMinecart((EntityMinecart) entityIn));
		}

		return flag;
	}

	/**
	 * Schedule the entity for removal during the next tick. Marks the entity
	 * dead in anticipation.
	 */
	public void removeEntity(Entity entityIn) {
		
		super.removeEntity(entityIn);
		this.entityList.remove(entityIn);
	}

	protected void onEntityAdded(Entity entityIn) {
		super.onEntityAdded(entityIn);

		if (this.entitySpawnQueue.contains(entityIn)) {
			this.entitySpawnQueue.remove(entityIn);
		}
	}

	protected void onEntityRemoved(Entity entityIn) {
		super.onEntityRemoved(entityIn);
		boolean flag = false;

		if (this.entityList.contains(entityIn)) {
			if (entityIn.isEntityAlive()) {
				this.entitySpawnQueue.add(entityIn);
				flag = true;
			} else {
				this.entityList.remove(entityIn);
			}
		}
	}

	/**
	 * Add an ID to Entity mapping to entityHashSet
	 */
	public void addEntityToWorld(int p_73027_1_, Entity p_73027_2_) {
		Entity entity = this.getEntityByID(p_73027_1_);

		if (entity != null) {
			this.removeEntity(entity);
		}

		this.entityList.add(p_73027_2_);
		p_73027_2_.setEntityId(p_73027_1_);

		if (!this.spawnEntityInWorld(p_73027_2_)) {
			this.entitySpawnQueue.add(p_73027_2_);
		}

		this.entitiesById.addKey(p_73027_1_, p_73027_2_);
	}

	/**
	 * Returns the Entity with the given ID, or null if it doesn't exist in this
	 * World.
	 */
	public Entity getEntityByID(int id) {
		return (Entity) (id == this.mc.thePlayer.getEntityId() ? this.mc.thePlayer : super.getEntityByID(id));
	}

	public Entity removeEntityFromWorld(int p_73028_1_) {
		Entity entity = (Entity) this.entitiesById.removeObject(p_73028_1_);

		if (entity != null) {
			this.entityList.remove(entity);
			this.removeEntity(entity);
		}

		return entity;
	}

	public boolean invalidateRegionAndSetBlock(BlockPos p_180503_1_, IBlockState p_180503_2_) {
		int i = p_180503_1_.getX();
		int j = p_180503_1_.getY();
		int k = p_180503_1_.getZ();
		this.invalidateBlockReceiveRegion(i, j, k, i, j, k);
		return super.setBlockState(p_180503_1_, p_180503_2_, 3);
	}

	/**
	 * If on MP, sends a quitting packet.
	 */
	public void sendQuittingDisconnectingPacket() {
		this.sendQueue.getNetworkManager().closeChannel(new ChatComponentText("Quitting"));
	}

	/**
	 * Updates all weather states.
	 */
	protected void updateWeather() {
	}

	protected int getRenderDistanceChunks() {
		return this.mc.gameSettings.renderDistanceChunks;
	}

	public void doVoidFogParticles(int p_73029_1_, int p_73029_2_, int p_73029_3_) {
		byte b0 = 16;
		Random random = new Random();
		ItemStack itemstack = this.mc.thePlayer.getHeldItem();
		boolean flag = this.mc.playerController.getCurrentGameType() == WorldSettings.GameType.CREATIVE
				&& itemstack != null && Block.getBlockFromItem(itemstack.getItem()) == Blocks.barrier;
		BlockPosM blockposm = this.randomTickPosM;

		for (int i = 0; i < 1000; ++i) {
			int j = p_73029_1_ + this.rand.nextInt(b0) - this.rand.nextInt(b0);
			int k = p_73029_2_ + this.rand.nextInt(b0) - this.rand.nextInt(b0);
			int l = p_73029_3_ + this.rand.nextInt(b0) - this.rand.nextInt(b0);
			blockposm.setXyz(j, k, l);
			IBlockState iblockstate = this.getBlockState(blockposm);
			iblockstate.getBlock().randomDisplayTick(this, blockposm, iblockstate, random);

			if (flag && iblockstate.getBlock() == Blocks.barrier) {
				this.spawnParticle(EnumParticleTypes.BARRIER, (double) ((float) j + 0.5F), (double) ((float) k + 0.5F),
						(double) ((float) l + 0.5F), 0.0D, 0.0D, 0.0D, new int[0]);
			}
		}
	}

	/**
	 * also releases skins.
	 */
	public void removeAllEntities() {
		this.loadedEntityList.removeAll(this.unloadedEntityList);

		for (int i = 0; i < this.unloadedEntityList.size(); ++i) {
			Entity entity = (Entity) this.unloadedEntityList.get(i);
			int j = entity.chunkCoordX;
			int k = entity.chunkCoordZ;

			if (entity.addedToChunk && this.isChunkLoaded(j, k, true)) {
				this.getChunkFromChunkCoords(j, k).removeEntity(entity);
			}
		}

		for (int l = 0; l < this.unloadedEntityList.size(); ++l) {
			this.onEntityRemoved((Entity) this.unloadedEntityList.get(l));
		}

		this.unloadedEntityList.clear();

		for (int i1 = 0; i1 < this.loadedEntityList.size(); ++i1) {
			Entity entity1 = (Entity) this.loadedEntityList.get(i1);

			if (entity1.ridingEntity != null) {
				if (!entity1.ridingEntity.isDead && entity1.ridingEntity.riddenByEntity == entity1) {
					continue;
				}

				entity1.ridingEntity.riddenByEntity = null;
				entity1.ridingEntity = null;
			}

			if (entity1.isDead) {
				int j1 = entity1.chunkCoordX;
				int k1 = entity1.chunkCoordZ;

				if (entity1.addedToChunk && this.isChunkLoaded(j1, k1, true)) {
					this.getChunkFromChunkCoords(j1, k1).removeEntity(entity1);
				}

				this.loadedEntityList.remove(i1--);
				this.onEntityRemoved(entity1);
			}
		}
	}

	/**
	 * Adds some basic stats of the world to the given crash report.
	 */
	public CrashReportCategory addWorldInfoToCrashReport(CrashReport report) {
		CrashReportCategory crashreportcategory = super.addWorldInfoToCrashReport(report);
		crashreportcategory.addCrashSectionCallable("Forced entities", new Callable() {
			private static final String __OBFID = "CL_00000883";

			public String call() {
				return WorldClient.this.entityList.size() + " total; " + WorldClient.this.entityList.toString();
			}
		});
		crashreportcategory.addCrashSectionCallable("Retry entities", new Callable() {
			private static final String __OBFID = "CL_00000884";

			public String call() {
				return WorldClient.this.entitySpawnQueue.size() + " total; "
						+ WorldClient.this.entitySpawnQueue.toString();
			}
		});
		crashreportcategory.addCrashSectionCallable("Server brand", new Callable() {
			private static final String __OBFID = "CL_00000885";

			public String call() throws Exception {
				return WorldClient.this.mc.thePlayer.getClientBrand();
			}
		});
		crashreportcategory.addCrashSectionCallable("Server type", new Callable() {
			private static final String __OBFID = "CL_00000886";

			public String call() throws Exception {
				return WorldClient.this.mc.getIntegratedServer() == null ? "Non-integrated multiplayer server"
						: "Integrated singleplayer server";
			}
		});
		return crashreportcategory;
	}

	/**
	 * Plays a sound at the specified position.
	 */
	public void playSoundAtPos(BlockPos p_175731_1_, String p_175731_2_, float p_175731_3_, float p_175731_4_,
			boolean p_175731_5_) {
		this.playSound((double) p_175731_1_.getX() + 0.5D, (double) p_175731_1_.getY() + 0.5D,
				(double) p_175731_1_.getZ() + 0.5D, p_175731_2_, p_175731_3_, p_175731_4_, p_175731_5_);
	}

	/**
	 * par8 is loudness, all pars passed to
	 * minecraftInstance.sndManager.playSound
	 */
	public void playSound(double x, double y, double z, String soundName, float volume, float pitch,
			boolean distanceDelay) {
		double d0 = this.mc.getRenderViewEntity().getDistanceSq(x, y, z);
		PositionedSoundRecord positionedsoundrecord = new PositionedSoundRecord(new ResourceLocation(soundName), volume,
				pitch, (float) x, (float) y, (float) z);

		if (distanceDelay && d0 > 100.0D) {
			double d1 = Math.sqrt(d0) / 40.0D;
			this.mc.getSoundHandler().playDelayedSound(positionedsoundrecord, (int) (d1 * 20.0D));
		} else {
			this.mc.getSoundHandler().playSound(positionedsoundrecord);
		}
	}

	public void makeFireworks(double x, double y, double z, double motionX, double motionY, double motionZ,
			NBTTagCompound compund) {
		this.mc.effectRenderer.addEffect(new EntityFirework.StarterFX(this, x, y, z, motionX, motionY, motionZ,
				this.mc.effectRenderer, compund));
	}

	public void setWorldScoreboard(Scoreboard p_96443_1_) {
		this.worldScoreboard = p_96443_1_;
	}

	/**
	 * Sets the world time.
	 */
	public void setWorldTime(long time) {
		if (time < 0L) {
			time = -time;
			this.getGameRules().setOrCreateGameRule("doDaylightCycle", "false");
		} else {
			this.getGameRules().setOrCreateGameRule("doDaylightCycle", "true");
		}

		super.setWorldTime(time);
	}

	
	
	public int getCombinedLight(BlockPos pos, int lightValue) {
		int i = super.getCombinedLight(pos, lightValue);

		if (Config.isDynamicLights()) {
			i = DynamicLights.getCombinedLight(pos, i);
		}

		return i;
	}

	/**
	 * Sets the block state at a given location. Flag 1 will cause a block
	 * update. Flag 2 will send the change to clients (you almost always want
	 * this). Flag 4 prevents the block from being re-rendered, if this is a
	 * client world. Flags can be added together.
	 */
	public boolean setBlockState(BlockPos pos, IBlockState newState, int flags) {
		this.playerUpdate = this.isPlayerActing();
		boolean flag = super.setBlockState(pos, newState, flags);
		this.playerUpdate = false;
		return flag;
	}

	private boolean isPlayerActing() {
		if (this.mc.playerController instanceof PlayerControllerOF) {
			PlayerControllerOF playercontrollerof = (PlayerControllerOF) this.mc.playerController;
			return playercontrollerof.isActing();
		} else {
			return false;
		}
	}

	public boolean isPlayerUpdate() {
		return this.playerUpdate;
	}

	public List getCollidingBoundingBoxes(Entity entityIn, AxisAlignedBB bb, boolean blocksOnly) {
		if (blocksOnly) {
			ArrayList var3 = Lists.newArrayList();
			int var4 = MathHelper.floor_double(bb.minX);
			int var5 = MathHelper.floor_double(bb.maxX + 1.0D);
			int var6 = MathHelper.floor_double(bb.minY);
			int var7 = MathHelper.floor_double(bb.maxY + 1.0D);
			int var8 = MathHelper.floor_double(bb.minZ);
			int var9 = MathHelper.floor_double(bb.maxZ + 1.0D);
			for (int var10 = var4; var10 < var5; var10++) {
				for (int var11 = var8; var11 < var9; var11++) {
					if (isBlockLoaded(new BlockPos(var10, 64, var11))) {
						for (int var12 = var6 - 1; var12 < var7; var12++) {
							BlockPos var13 = new BlockPos(var10, var12, var11);
							boolean var14 = entityIn.isOutsideBorder();
							boolean var15 = isInsideBorder(getWorldBorder(), entityIn);
							if ((var14) && (var15)) {
								entityIn.setOutsideBorder(false);
							} else if ((!var14) && (!var15)) {
								entityIn.setOutsideBorder(true);
							}
							IBlockState var16;
							if ((!getWorldBorder().contains(var13)) && (var15)) {
								var16 = Blocks.stone.getDefaultState();
							} else {
								var16 = getBlockState(var13);
							}
							var16.getBlock().addCollisionBoxesToList(this, var13, var16, bb, var3, entityIn);
						}
					}
				}
			}
			return var3;
		}
		return null;
	}

	public static ArrayList<EntityPlayer> getLoadedPlayers(){
		ArrayList<EntityPlayer> list = new ArrayList<EntityPlayer>();
		for(Object o : Minecraft.getMinecraft().theWorld.loadedEntityList){
			if(o instanceof EntityPlayer){
				EntityPlayer p = (EntityPlayer)o;
				list.add(p);
			}
		}
		return list;
	}

	public Block getBlock(int var12, int var13, int var14) {
		// TODO Auto-generated method stub
		return null;
	}

	}