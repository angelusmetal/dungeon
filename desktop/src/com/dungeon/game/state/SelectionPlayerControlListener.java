package com.dungeon.game.state;

import com.badlogic.gdx.Gdx;
import com.dungeon.engine.controller.pov.PovDirection;
import com.dungeon.game.controller.AbstractControlBundleListener;
import com.dungeon.game.controller.ControlBundle;
import com.dungeon.game.controller.ControlBundleListener;

public class SelectionPlayerControlListener extends AbstractControlBundleListener implements ControlBundleListener {

	private final ControlBundle control;
	private final CharacterSelection characterSelection;
	private boolean added;

	public SelectionPlayerControlListener(ControlBundle control, CharacterSelection characterSelection) {
		this.control = control;
		this.characterSelection = characterSelection;
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
	public void triggerA() {
		if (!added) {
			added = characterSelection.addControl(control);
		} else {
			Gdx.app.log("Level", "Player confirmed!");
			characterSelection.confirmSelection(control);
		}
	}

}
