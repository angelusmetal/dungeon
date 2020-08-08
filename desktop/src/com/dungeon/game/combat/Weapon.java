package com.dungeon.game.combat;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.dungeon.engine.entity.Entity;

import java.util.function.Supplier;

public abstract class Weapon {
	private String name;

	public Weapon(String name) {
		this.name = name;
	}

	/**
	 * Create the required entities to spawn an attack in the world
	 */
	public abstract void attack(Vector2 origin, Vector2 aim);

	public String getName() {
		return name;
	}

	public abstract Animation<Sprite> getAnimation();

	public float attackCooldown() {
		return 0.25f;
	}

	public float energyDrain() {
		return 20;
	}

	abstract public int getPrice();
}
