package me.xatzdevelopments.xatz.client.modules;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import me.xatzdevelopments.xatz.client.events.UpdateEvent;
import me.xatzdevelopments.xatz.client.gui.tab.TabGuiContainer;
import me.xatzdevelopments.xatz.client.main.Xatz;
import me.xatzdevelopments.xatz.client.module.state.Category;
import me.xatzdevelopments.xatz.client.tools.RotationUtil;
import me.xatzdevelopments.xatz.client.tools.Utils;
import me.xatzdevelopments.xatz.module.Module;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.block.BlockBed;
import net.minecraft.block.BlockButton;
import net.minecraft.block.BlockCake;
import net.minecraft.block.BlockChest;
import net.minecraft.block.BlockDaylightDetector;
import net.minecraft.block.BlockDoublePlant;
import net.minecraft.block.BlockEndPortalFrame;
import net.minecraft.block.BlockFlower;
import net.minecraft.block.BlockLilyPad;
import net.minecraft.block.BlockMushroom;
import net.minecraft.block.BlockPressurePlate;
import net.minecraft.block.BlockRedstoneComparator;
import net.minecraft.block.BlockRedstoneRepeater;
import net.minecraft.block.BlockRedstoneTorch;
import net.minecraft.block.BlockReed;
import net.minecraft.block.BlockSapling;
import net.minecraft.block.BlockSign;
import net.minecraft.block.BlockSnow;
import net.minecraft.block.BlockTallGrass;
import net.minecraft.block.BlockTorch;
import net.minecraft.block.BlockTripWire;
import net.minecraft.block.BlockTripWireHook;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.init.Blocks;
import net.minecraft.network.AbstractPacket;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.server.S02PacketChat;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.Vec3;

public class ClickTeleport2 extends Module {

	
	private final int PACKETS = 120;
	private static Vec3 position;
	private static Vec3  startingPosition;


	public ClickTeleport2() {
		super("ClickTeleport2", Keyboard.KEY_NONE, Category.MOVEMENT, "Teleports you forward when you right-click.");
	}
	
	
	@Override
	   public void onLeftClick() {
		
	         
	     
	       
	}

	   


	@Override
	public void onUpdate(UpdateEvent event) {
		
			
			
			if (mc.gameSettings.keyBindUseItem.pressed) {
				if(Utils.getPlayerBlockDistance(mc.objectMouseOver.getBlockPos()) > 4) {
						
				
			 position = mc.objectMouseOver.getBlockPos().toVector();
			  startingPosition = this.mc.thePlayer.getPositionVector();
	               
	                	
	                    this.teleport(mc.objectMouseOver.getBlockPos());
	                    
				}
			}
	            
			
		
		
		super.onUpdate(event);
			
}

	
	@Override
	public void onRender() {
		if(mc.thePlayer.rayTrace(30.0D, 0.0F) != null && mc.thePlayer.rayTrace(30.0D, 0.0F).getBlockPos() != null) {
	         BlockPos pos = mc.thePlayer.rayTrace(30.0D, 0.0F).getBlockPos();
	         if(mc.thePlayer.getDistanceSq((double)pos.getX() + 0D, (double)(pos.getY() + 0), (double)pos.getZ() + 0.0D) >= 0.0D) {
	            if(pos != null) {
	               Block block = mc.theWorld.getBlockState(pos).getBlock();
	               GL11.glDisable(2896);
	               GL11.glDisable(3553);
	               GL11.glEnable(3042);
	               GL11.glBlendFunc(770, 771);
	               GL11.glDisable(2929);
	               GL11.glEnable(2848);
	               GL11.glDepthMask(false);
	               GL11.glLineWidth(0.75F);
	               double x = (double)pos.getX() - RenderManager.renderPosX;
	               double y = (double)pos.getY() - RenderManager.renderPosY;
	               double z = (double)pos.getZ() - RenderManager.renderPosZ;
	               AxisAlignedBB mask = new AxisAlignedBB(x, y + 1, z, x + 1.0D, y + 1.0D, z + 1.0D);
	               if(!(block instanceof BlockAir)) {
	                  GL11.glColor4f(TabGuiContainer.out,TabGuiContainer.out1,TabGuiContainer.out2,TabGuiContainer.out3);
	                  
	                  RenderGlobal.drawBoundingBox(mask);
	               }

	               GL11.glDepthMask(true);
	               GL11.glDisable(2848);
	               GL11.glEnable(2929);
	               GL11.glDisable(3042);
	               GL11.glEnable(2896);
	               GL11.glEnable(3553);
	               GL11.glDisable(2848);
	            }

	         }
	      }
		super.onRender();
	   }

	
     
 
 public void teleport(final BlockPos pos) {
	 Xatz.chatMessage("tp");
     final Vec3 position = pos.toVector().addVector(0.0, 0.5, 0.0);
     Vec3 currentPosition;
     final Vec3 startingPosition = currentPosition = mc.thePlayer.getPositionVector();
     final float[] rotations = RotationUtil.getRotations(startingPosition, position);
     final double x = -Math.sin(Math.toRadians(rotations[0]));
     final double z = Math.cos(Math.toRadians(rotations[0]));
     double distance = 0.0;
     for (int i = 0; i < 150; ++i) {
         final double distanceToEnd = currentPosition.flat().distanceTo(position.flat());
         if (distanceToEnd == 0.0) {
             break;
         }
         if (!ClickTeleport.mc.theWorld.getCollidingBoundingBoxes(mc.thePlayer, mc.thePlayer.getEntityBoundingBox().offset(x * distance, 0.0, z * distance)).isEmpty()) {
             break;
         }
         distance += Math.min(10, distanceToEnd);
         currentPosition = startingPosition.addVector(x * distance, 0.0, z * distance);
         ClickTeleport.mc.getNetHandler().getNetworkManager().sendPacket(new C03PacketPlayer.C04PacketPlayerPosition(currentPosition.xCoord, currentPosition.yCoord, currentPosition.zCoord, true));
     }
 }

 }