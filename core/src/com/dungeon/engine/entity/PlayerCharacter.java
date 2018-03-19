package com.dungeon.engine.entity;

import com.badlogic.gdx.graphics.Color;
import com.dungeon.engine.physics.Body;
import com.dungeon.engine.render.Light;
import com.dungeon.game.object.Tombstone;
import com.dungeon.game.state.GameState;

import java.util.function.Function;

public abstract class PlayerCharacter extends Character {

	static private Light TORCH_LIGHT = new Light(160, new Color(0.25f, 0.2f, 0.1f, 0.2f), Light.NORMAL_TEXTURE, Light::torchlight, Light::noRotate);

	protected PlayerCharacter(Body body) {
		super(body);
		light = TORCH_LIGHT;
	}

	public static final Function<Entity, Boolean> IS_PLAYER = entity -> entity instanceof PlayerCharacter;
	public static final Function<Entity, Boolean> IS_NON_PLAYER = entity -> entity instanceof Character && !(entity instanceof PlayerCharacter);

	@Override
	protected void onExpire(GameState state) {
		Tombstone tombstone = new Tombstone(state, getPos());
		state.addEntity(tombstone);
	}

	public void heal(int amount) {
		health += amount;
		if (health > maxHealth) {
			health = maxHealth;
		}
	}
}
