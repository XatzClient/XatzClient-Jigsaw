package me.xatzdevelopments.xatz.client.chat;

import me.xatzdevelopments.xatz.client.main.Xatz;
import net.minecraft.client.Minecraft;
import net.minecraft.network.play.client.C01PacketChatMessage;

public class ChatMananger {

	private Minecraft mc = Minecraft.getMinecraft();

	public ChatMananger() {

	}

	public void onMessageSent(C01PacketChatMessage packet) throws Exception {
		if (packet.getMessage().startsWith(".") && !Xatz.ghostMode) {
			String[] commands = packet.getMessage().trim().split("\\s++");
			boolean success = Xatz.getCommandManager().onCommand(commands[0], commands);
			if (!success) {
				Xatz.chatMessage("§cCould not find command!");
			}
			// if(command.equalsIgnoreCase(".save")) {
			// AutoBuild.saveDone(commands[1]);
			// return;
			// }
			// if(command.equalsIgnoreCase(".load")) {
			// AutoBuild.loadConf(commands[1]);
			// return;
			// }
		} else {
			mc.thePlayer.sendQueue.getNetworkManager().sendPacketFinal(packet);
		}
	}

}
