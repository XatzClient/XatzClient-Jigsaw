package me.xatzdevelopments.xatz.client.modules.target;

import org.lwjgl.input.Keyboard;

import me.xatzdevelopments.xatz.client.module.state.Category;
import me.xatzdevelopments.xatz.module.Module;

public class HurtResistant extends Module {

	public HurtResistant() {
		super("HurtResistant", Keyboard.KEY_NONE, Category.HIDDEN);
	} // Target

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
	public boolean isCheckbox() {
		return true;
	}

}
