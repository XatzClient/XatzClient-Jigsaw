package me.xatzdevelopments.xatz.client.modules;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Random;

import org.lwjgl.input.Keyboard;

import me.xatzdevelopments.xatz.client.gui.tab.TabGuiContainer;
import me.xatzdevelopments.xatz.client.module.state.Category;
import me.xatzdevelopments.xatz.module.Module;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagDouble;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.play.client.C10PacketCreativeInventoryAction;

public class HologramSpam extends Module {

	ArrayList<String> staff;
	public static boolean enabled = false;
	  public static Random rnd = new Random();
	  
		  public static double x;
		  public static double y;
		  public static double z;
		  public static double motionX;
		  public static double motionY;
		  public static String[] meme = {"Yo momma so fat we are in her right now","Yo momma so fat people jog around her for exercise","Yo momma so fat she went to the movies and sat next to everyone","Yo momma so fat she has been declared a natural habitat for Condors","Yo momma so fat you haveta roll over twice to get off her...","Yo momma so fat she was floating in the ocean and spain claimed her for then new world","Yo momma so fat she lay on the beach and people run around yelling Free Willy","Yo momma so fat when you get on top of her your ears pop!","Yo momma so fat when she has sex, she has to give directions!","Yo momma so fat she goes to a resturant, looks at the menu and says \"okay!\"","Yo momma so fat when she wears a yellow raincoat, people said \"Taxi!\"","Yo momma so fat she had to go to Sea World to get baptized","Yo momma so fat she got to iron her pants on the driveway","Yo momma so fat she put on her lipstick with a paint-roller","Yo momma so fat she got to pull down her pants to get into her pockets","Yo momma so fat when she tripped over on 4th Ave, she landed on 12th","Yo momma so fat when she bungee jumps, she brings down the bridge too","Yo momma so fat the highway patrol made her wear \"Caution! Wide Turn\"","Yo momma so fat when she sits around the house, she SITS AROUND THE HOUSE!","Yo momma so fat when she steps on a scale, it read \"one at a time, please\"","Yo momma so fat when she sits on my face I can't hear the stereo.","Yo momma so fat she fell in love and broke it.","Yo momma so fat when she gets on the scale it says to be continued.","Yo momma so fat when she gets on the scale it says we don't do livestock.","Yo momma so fat whenever she goes to the beach the tide comes in!","Yo momma so fat when she plays hopscotch, she goes New York, L.A.,  Chicago...","Yo momma so fat she's got Amtrak written on her leg.","Yo momma so fat even Bill Gates couldn't pay for her liposuction!","Yo momma so fat her legs is like spoiled milk - white & chunky!"};
		  public static double motionZ;
		  
	  
	public HologramSpam() {
		super("HologramSpam", Keyboard.KEY_NONE, Category.FUN,
				"Use this in a creative server for a disco ;D. (place the armor stand on the ground/");
	}

	

	@Override
	public void onDisable() {
     mc.rightClickDelayTimer = 4;
		super.onDisable();
	}

	@Override
	public void onEnable() {
  x = mc.thePlayer.posX;
  y = mc.thePlayer.posY;
  z = mc.thePlayer.posZ;
		super.onEnable();
	}
	 public static ItemStack cheatArmorstand(double x, double y, double z, double motionX, double motionY, double motionZ, double color)
	  {
		    ItemStack itm = new ItemStack(Items.armor_stand);
		    NBTTagCompound base = new NBTTagCompound();
		    NBTTagCompound entityTag = new NBTTagCompound();
		    
		    entityTag.setBoolean("NoBasePlate", true);
		    entityTag.setBoolean("Invisible", false);
		    
		    NBTTagCompound randomColor = new NBTTagCompound();
		    NBTTagCompound display = new NBTTagCompound();
		    		  NBTTagCompound effect = new NBTTagCompound();
		    
		    display.setInteger("color", TabGuiContainer.col);
		    randomColor.setTag("display", display);
		    
		    NBTTagList pos = new NBTTagList();
		    NBTTagList motion = new NBTTagList();
		    NBTTagList armorItems = new NBTTagList();
		    
		    pos.appendTag(new NBTTagDouble(x));
		    pos.appendTag(new NBTTagDouble(y));
		    pos.appendTag(new NBTTagDouble(z));
		    
		    motion.appendTag(new NBTTagDouble(motionX));
		    motion.appendTag(new NBTTagDouble(motionY));
		    motion.appendTag(new NBTTagDouble(motionZ));
		    
		    entityTag.setBoolean("Invisible", true);
	
		    entityTag.setBoolean("CustomNameVisible", true);
		    String random = (meme[new Random().nextInt(meme.length)]);
		    int randomNum = 1 + (int)(Math.random() * 9); 

		    entityTag.setString("CustomName", "§" + randomNum + "§l" + random);
		   
		    
		 
		    
		    
		   
		    entityTag.setTag("Pos", pos);
		    entityTag.setTag("Motion", motion);
		    
		    base.setTag("EntityTag", entityTag);
		    
		    
		   itm.setTagCompound(base);
		    
		    return itm;
		  }

	 
	
	@Override
	public void onUpdate(){

		         mc.rightClickDelayTimer = 0;
		         if (rnd.nextInt(2) == 0) {
		           motionX = rnd.nextInt(700) / 100.0D;
		         } else {
		           motionX = rnd.nextInt(700) / 100 * -1.0D;
		         }
		         motionY = rnd.nextInt(300) / 100.0D;
		         if (rnd.nextInt(2) == 0) {
		           motionZ = rnd.nextInt(700) / 100.0D;
		         } else {
		           motionZ = rnd.nextInt(700) / 100 * -1.0D;
		         }
		         mc.thePlayer.sendQueue.addToSendQueue(new C10PacketCreativeInventoryAction(36, cheatArmorstand(x, y, z, motionX, motionY, motionZ, 0.0D)));
		       }
		     
		    
			  
	
}