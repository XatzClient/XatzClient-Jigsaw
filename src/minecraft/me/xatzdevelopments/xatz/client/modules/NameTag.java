package me.xatzdevelopments.xatz.client.modules;

import org.lwjgl.input.Keyboard;

import me.xatzdevelopments.xatz.client.main.Xatz;
import me.xatzdevelopments.xatz.client.module.state.Category;
import me.xatzdevelopments.xatz.module.Module;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C0DPacketCloseWindow;
import net.minecraft.network.play.client.C10PacketCreativeInventoryAction;

public class NameTag extends Module {

	public NameTag() {
		super("RenameItem", Keyboard.KEY_NONE, Category.HIDDEN,
				"Renames items. Note: You need to be in creative mode!");
	} // Explots

	@Override
	public void onEnable() {
		if (!mc.thePlayer.capabilities.isCreativeMode) {
			Xatz.chatMessage("§cYou need to be in creative mode.");
			setToggled(false, true);
			return;
		}
		
		super.onEnable();
	}
	
	@Override
	public void onUpdate() {
		GuiScreen before = mc.currentScreen;
		mc.displayGuiScreen(new GuiInventory(mc.thePlayer));
		
		ItemStack stack = new ItemStack(Items.furnace_minecart);
		stack.stackSize = 64;
		String stackName = "";
		for (int ii = 0; ii < 1000; ii++) {
			stackName += "♧";
		}
		for (int ii = 100; ii < 100; ii++) {
			stackName += "§lHeil§c卐§r";
		}
		
		for (int ii = 3; ii < 3; ii++) {
			stackName += "§c❻";
		}
		stack.setStackDisplayName(stackName);
		sendPacket(new C10PacketCreativeInventoryAction(1, stack));
		
		sendPacket(new C0DPacketCloseWindow());
		mc.displayGuiScreen(before);
		super.onUpdate();
	}


}
