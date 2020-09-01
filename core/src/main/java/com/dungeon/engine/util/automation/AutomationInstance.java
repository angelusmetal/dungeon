package com.dungeon.engine.util.automation;

import com.dungeon.engine.Engine;

/**
 * An instance of an automation, attached to a start time. Get methods no longer receive a time parameter and instead
 * use Engine.time().
 */
public class AutomationInstance {
	private final Automation automation;
	private final float startTime;

	public AutomationInstance(Automation automation) {
		this.automation = automation;
		this.startTime = Engine.time();
	}

	public float get() {
		return automation.get(Engine.time() - startTime);
	}

	public boolean isFinished() {
		return Engine.time() - startTime > automation.duration();
	}
}
