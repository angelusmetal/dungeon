package com.dungeon.game.combat.module;

import com.badlogic.gdx.math.Vector2;

public interface WeaponModule {
	void apply(Vector2 origin, Vector2 aim);
}
