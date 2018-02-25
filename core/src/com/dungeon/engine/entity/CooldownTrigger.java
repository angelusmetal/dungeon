package com.dungeon.engine.entity;

/**
 * Keeps track of a cooldown. An action can be attempted and it will only succeed if the cooldown has completed (in
 * which case the cooldown process will initiate again).
 */
public class CooldownTrigger {
	private final float cooldown;
	private float lastTrigger;

	public CooldownTrigger(float cooldown) {
		this.cooldown = cooldown;
	}

	public boolean attempt(float time, Runnable action) {
		if (time > lastTrigger + cooldown) {
			action.run();
			lastTrigger = time;
			return true;
		} else {
			return false;
		}
	}
}
