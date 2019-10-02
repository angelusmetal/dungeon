package com.dungeon.game.controller;

import com.badlogic.gdx.controllers.PovDirection;
import com.badlogic.gdx.math.Vector2;

public abstract class AbstractControlBundleListener implements ControlBundleListener {
	@Override public void updateDirection(Vector2 vector) {}
	@Override public void updateAim(Vector2 vector) {}
	@Override public void povTrigger(PovDirection pov) {}
	@Override public void toggleA(boolean on) {}
	@Override public void toggleB(boolean on) {}
	@Override public void toggleX(boolean on) {}
	@Override public void toggleY(boolean on) {}
	@Override public void toggleL1(boolean on) {}
	@Override public void toggleL2(boolean on) {}
	@Override public void toggleR1(boolean on) {}
	@Override public void toggleR2(boolean on) {}
	@Override public void triggerA() {}
	@Override public void triggerB() {}
	@Override public void triggerX() {}
	@Override public void triggerY() {}
	@Override public void triggerL1() {}
	@Override public void triggerL2() {}
	@Override public void triggerR1() {}
	@Override public void triggerR2() {}
}
