package com.dungeon.game.state;

import com.badlogic.gdx.math.Vector2;
import com.dungeon.game.controller.AbstractControlBundleListener;
import com.dungeon.game.controller.ControlBundle;
import com.dungeon.game.controller.ControlBundleListener;
import com.dungeon.game.entity.PlayerEntity;

public class CharacterPlayerControlListener extends AbstractControlBundleListener implements ControlBundleListener {

	private final ControlBundle control;

	public CharacterPlayerControlListener(ControlBundle control) {
		this.control = control;
	}

	@Override
	public void updateDirection(Vector2 vector) {
		PlayerEntity character = control.getEntity();
		if (character != null) {
			if (vector.len() > 0.5) {
				character.face(vector);
			}
			character.setSelfImpulse(vector);
		}
	}

	@Override
	public void updateAim(Vector2 vector) {
		PlayerEntity character = control.getEntity();
		if (character != null) {
			character.aim(vector);
			character.setFiring(vector.len() > 0.5);
		}
	}

	@Override
	public void triggerA() {
		PlayerEntity character = control.getEntity();
		if (character != null) {
			character.interact();
		}
	}

}
