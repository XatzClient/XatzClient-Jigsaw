package me.xatzdevelopments.xatz.client.modules;

import java.util.ArrayList;

import org.lwjgl.input.Keyboard;

import me.xatzdevelopments.xatz.client.main.Xatz;
import me.xatzdevelopments.xatz.client.module.state.Category;
import me.xatzdevelopments.xatz.client.tools.Utils;
import me.xatzdevelopments.xatz.module.Module;
import net.minecraft.init.Blocks;
import net.minecraft.network.AbstractPacket;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.server.S06PacketUpdateHealth;
import net.minecraft.util.BlockPos;
import net.minecraft.util.Vec3;

public class TpBedBreaker extends Module {
	
	private boolean dead = false;
	private double posY = 0;

	public TpBedBreaker() {
		super("TpBedBreaker", Keyboard.KEY_NONE, Category.EXPLOITS, "This is really laggy because it scans a lot of blocks!");
	}
	
	@Override
	public void onEnable() {

		//Xatz.sendChatMessage(".damage 20");
		ArrayList<BlockPos> targets = new ArrayList<BlockPos>();
		int range = 100;
		for(int y = range; y >= -range; y--)
		{
			for(int x = range; x >= -range; x--)
			{
				for(int z = range; z >= -range; z--)
				{
					int posX = (int)(mc.thePlayer.posX + x);
					int posY = (int)(mc.thePlayer.posY + y);
					int posZ = (int)(mc.thePlayer.posZ + z);
					BlockPos pos = new BlockPos(posX, posY, posZ);
					if(Utils.getBlock(pos).equals(Blocks.bed)) {
						targets.add(pos);
					}
				}
			}
		}
		for(BlockPos pos : targets) {
			Utils.infiniteReach(100, 9.5, 9, new ArrayList<Vec3>(), new ArrayList<Vec3>(), pos);
		}
		Flight.cubecraftOverrideTpaurathingidkwhattonameit = true; // xD nice override name, Robofan. 
		this.setToggled(false, true);
		
		
		super.onEnable();
	}
	
}
