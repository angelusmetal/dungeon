package com.dungeon.engine.controller.player;

import com.badlogic.gdx.controllers.PovDirection;
import com.badlogic.gdx.math.Vector2;
import com.dungeon.engine.controller.ControlScope;
import com.dungeon.engine.controller.character.CharacterControl;
import com.dungeon.game.GameState;

public abstract class PlayerControl {

	private final GameState state;
	private final CharacterControl characterControl;

	public PlayerControl(GameState state, CharacterControl characterControl) {
		this.state = state;
		this.characterControl = characterControl;
	}

	void updateDirection(PovDirection pov, Vector2 vector) {
		if (state.getControlScope() == ControlScope.INGAME) {
			characterControl.updateDirection(pov, vector);
		}
	}

	void start() {
		if (state.getControlScope() == ControlScope.INGAME) {
			characterControl.start();
		}
	}

	void trigger1() {
		if (state.getControlScope() == ControlScope.INGAME) {
			characterControl.fire();
		}
	}

	void trigger2() {
		if (state.getControlScope() == ControlScope.INGAME) {
			characterControl.fire();
		}
	}

	void trigger3() {
		if (state.getControlScope() == ControlScope.INGAME) {
			characterControl.fire();
		}
	}

	void trigger4() {
		if (state.getControlScope() == ControlScope.INGAME) {
			characterControl.fire();
		}
	}

}
