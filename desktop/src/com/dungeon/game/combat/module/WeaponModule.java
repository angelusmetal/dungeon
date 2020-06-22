package com.dungeon.game.combat.module;

import com.badlogic.gdx.math.Vector2;

/**
 * Weapons are made of one or more modules; each of them may spawn particles, projectiles, play sounds, etc.
 */
public interface WeaponModule {
	/**
	 * Swing (attack) weapon on the specified origin towards the specified aim direction
	 * @param origin Usually the origin of the emitter entity
	 * @param aim    Direction on which the attack is directed
	 */
	void apply(Vector2 origin, Vector2 aim);
}
