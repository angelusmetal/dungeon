package com.dungeon.engine.entity;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.dungeon.engine.render.Light;

import java.util.function.Function;
import java.util.function.Predicate;

public abstract class PlayerCharacter extends Character {

	static private Light TORCH_LIGHT = new Light(160, new Color(0.25f, 0.2f, 0.1f, 0.2f), Light.NORMAL_TEXTURE, Light::torchlight, Light::noRotate);

	protected PlayerCharacter(Vector2 origin, EntityPrototype prototype) {
		super(origin, prototype);
		light = TORCH_LIGHT;
	}

	public static final Predicate<Entity> IS_PLAYER = entity -> entity instanceof PlayerCharacter;
	public static final Predicate<Entity> IS_NON_PLAYER = entity -> entity instanceof Character && !(entity instanceof PlayerCharacter);

	public void heal(int amount) {
		health += amount;
		if (health > maxHealth) {
			health = maxHealth;
		}
	}

}
