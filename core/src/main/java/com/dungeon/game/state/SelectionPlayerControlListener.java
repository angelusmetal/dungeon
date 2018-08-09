package com.dungeon.game.state;

import com.badlogic.gdx.controllers.PovDirection;
import com.badlogic.gdx.math.Vector2;
import com.dungeon.engine.controller.player.PlayerControlBundle;

public class SelectionPlayerControlListener implements PlayerControlBundle.Listener {

	private final PlayerControlBundle control;
	private final CharacterSelection characterSelection;
	private boolean added;

	public SelectionPlayerControlListener(PlayerControlBundle control, CharacterSelection characterSelection) {
		this.control = control;
		this.characterSelection = characterSelection;
	}
	
	@Override
	public void updateDirection(Vector2 vector) {

	}

	@Override
	public void povTrigger(PovDirection pov) {
		if (pov == PovDirection.east) {
			characterSelection.selectNextCharacter(control);
		} else if (pov == PovDirection.west) {
			characterSelection.selectPrevCharacter(control);
		}
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
		if (!added) {
			added = characterSelection.addControl(control);
		} else {
			System.out.println("Player confirmed!");
			characterSelection.confirmSelection(control);
		}
	}

	@Override
	public void trigger2() {

	}

	@Override
	public void trigger3() {

	}

	@Override
	public void trigger4() {

	}
}
