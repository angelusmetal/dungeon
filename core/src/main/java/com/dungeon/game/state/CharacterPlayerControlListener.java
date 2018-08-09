package com.dungeon.game.state;

import com.badlogic.gdx.controllers.PovDirection;
import com.badlogic.gdx.math.Vector2;
import com.dungeon.engine.controller.player.PlayerControlBundle;
import com.dungeon.engine.entity.PlayerEntity;

public class CharacterPlayerControlListener implements PlayerControlBundle.Listener {

	private final PlayerControlBundle control;

	public CharacterPlayerControlListener(PlayerControlBundle control) {
		this.control = control;
	}

	@Override
	public void updateDirection(Vector2 vector) {
		PlayerEntity character = control.getEntity();
		if (character != null) {
			character.setSelfImpulse(vector);
		}
	}

	@Override
	public void povTrigger(PovDirection pov) {

	}

	@Override
	public void toggle1(boolean on) {

	}

	@Override
	public void toggle2(boolean on) {

	}

	@Override
	public void toggle3(boolean on) {

	}

	@Override
	public void toggle4(boolean on) {

	}

	@Override
	public void trigger1() {
		PlayerEntity character = control.getEntity();
		if (character != null) {
			character.fire();
		}
	}

	@Override
	public void trigger2() {
		trigger1();
	}

	@Override
	public void trigger3() {
		trigger1();
	}

	@Override
	public void trigger4() {
		trigger1();
	}

}
