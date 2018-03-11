package com.dungeon.game.state;

import com.badlogic.gdx.controllers.PovDirection;
import com.badlogic.gdx.math.Vector2;
import com.dungeon.engine.controller.player.PlayerControl;

public class SelectionPlayerControlListener implements PlayerControl.PlayerControlListener {

	private final PlayerControl control;
	private final CharacterSelection characterSelection;
	private boolean added;

	public SelectionPlayerControlListener(PlayerControl control, CharacterSelection characterSelection) {
		this.control = control;
		this.characterSelection = characterSelection;
	}
	
	@Override
	public void updateDirection(PovDirection pov, Vector2 vector) {
		
	}

	@Override
	public void start() {

	}

	@Override
	public void trigger1() {
		if (!added) {
			added = characterSelection.addControl(control);
		} else {
			System.out.println("Player confirmed!");
			characterSelection.confirmSelection(control);
		}
	}

	@Override
	public void trigger2() {
		characterSelection.selectNextCharacter(control);
	}

	@Override
	public void trigger3() {
		characterSelection.selectPrevCharacter(control);
	}

	@Override
	public void trigger4() {

	}
}
