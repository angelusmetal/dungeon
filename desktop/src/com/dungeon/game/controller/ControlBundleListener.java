package com.dungeon.game.controller;

import com.badlogic.gdx.math.Vector2;
import com.dungeon.engine.controller.pov.PovDirection;

public interface ControlBundleListener {
	/** Update analog direction */
	void updateDirection(Vector2 vector);
	/** Update analog aim */
	void updateAim(Vector2 vector);
	/** POV trigger */
	void povTrigger(PovDirection pov);
	/** Toggle A */
	void toggleA(boolean on);
	/** Toggle B */
	void toggleB(boolean on);
	/** Toggle X */
	void toggleX(boolean on);
	/** Toggle Y */
	void toggleY(boolean on);
	/** Toggle L1 */
	void toggleL1(boolean on);
	/** Toggle L2 */
	void toggleL2(boolean on);
	/** Toggle R1 */
	void toggleR1(boolean on);
	/** Toggle R2 */
	void toggleR2(boolean on);
	/** Trigger A */
	void triggerA();
	/** Trigger B */
	void triggerB();
	/** Trigger X */
	void triggerX();
	/** Trigger Y */
	void triggerY();
	/** Trigger L1 */
	void triggerL1();
	/** Trigger L2 */
	void triggerL2();
	/** Trigger R1 */
	void triggerR1();
	/** Trigger R2 */
	void triggerR2();
}
