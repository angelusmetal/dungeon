package com.dungeon.game.object.powerups;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.dungeon.engine.render.TileSheet;
import com.dungeon.engine.resource.ResourceManager;

public class PowerupsSheet extends TileSheet {

	private final TextureRegion HEALTH_1 = getTile(0, 0);
	private final TextureRegion DAMAGE_1 = getTile(1, 0);
	private final Animation<TextureRegion> HEALTH_ANIMATION = new Animation<>(0.25f, HEALTH_1);
	private final Animation<TextureRegion> DAMAGE_ANIMATION = new Animation<>(0.25f, DAMAGE_1);

	public static final String HEALTH = "powerup_health";
	public static final String DAMAGE = "powerup_damage";

	PowerupsSheet() {
		super(ResourceManager.instance().getTexture("powerups.png"), 20);
	}

	public static Animation<TextureRegion> health() {
		return new PowerupsSheet().HEALTH_ANIMATION;
	}

	public static Animation<TextureRegion> damage() {
		return new PowerupsSheet().DAMAGE_ANIMATION;
	}

}
