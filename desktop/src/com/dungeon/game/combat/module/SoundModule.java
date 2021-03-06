package com.dungeon.game.combat.module;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.math.Vector2;
import com.dungeon.engine.Engine;

/**
 * Plays a sound when the weapon is used
 */
public class SoundModule implements WeaponModule {
	private final Sound sound;

	/**
	 * Create a weapon module that plays a sound when the weapon is used
	 * @param sound Sound to play
	 */
	public SoundModule(Sound sound) {
		this.sound = sound;
	}
	@Override
	public void apply(Vector2 origin, Vector2 aim) {
		Engine.audio.playSound(sound, origin, 1f, 0.05f);
	}
}
