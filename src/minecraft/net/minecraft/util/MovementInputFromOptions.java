package net.minecraft.util;

import me.xatzdevelopments.xatz.client.Unused.inEvents.inEventType;
import me.xatzdevelopments.xatz.client.Unused.inEvents.Listeners.inEventMove;
import me.xatzdevelopments.xatz.client.main.Xatz;
import net.minecraft.client.settings.GameSettings;

public class MovementInputFromOptions extends MovementInput {
	private final GameSettings gameSettings;

	public MovementInputFromOptions(GameSettings gameSettingsIn) {
		this.gameSettings = gameSettingsIn;
	}

	public void updatePlayerMoveState() {
		this.moveStrafe = 0.0F;
		this.moveForward = 0.0F;
//        final inEventMove Event = new inEventMove();
//        Event.setType(inEventType.PRE);
//        Xatz.oninEvent(Event);
//        if (Event.isCancelled) {
//            return;
//        }

		if (this.gameSettings.keyBindForward.isKeyDown()) {
			++this.moveForward;
		}

		if (this.gameSettings.keyBindBack.isKeyDown()) {
			--this.moveForward;
		}

		if (this.gameSettings.keyBindLeft.isKeyDown()) {
			++this.moveStrafe;
		}

		if (this.gameSettings.keyBindRight.isKeyDown()) {
			--this.moveStrafe;
		}

		this.jump = this.gameSettings.keyBindJump.isKeyDown();
		this.sneak = this.gameSettings.keyBindSneak.isKeyDown();

		if (this.sneak) {
			// TODO Xatz fastnneak
			if (!Xatz.getModuleByName("FastSneak").isToggled()) {
				this.moveStrafe = (float) ((double) this.moveStrafe * 0.3D);
				this.moveForward = (float) ((double) this.moveForward * 0.3D);
			}

		}
	}
}
