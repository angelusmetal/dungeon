package com.dungeon.game.character.fireslime;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.dungeon.engine.render.Tileset;
import com.dungeon.engine.resource.ResourceManager;

public class FireSlimeSheet extends Tileset {

	private final TextureRegion IDLE_1 = getTile(0, 0);
	private final TextureRegion IDLE_2 = getTile(1, 0);
	private final TextureRegion IDLE_3 = getTile(2, 0);
	private final TextureRegion IDLE_4 = getTile(3, 0);
	private final TextureRegion IDLE_5 = getTile(0, 1);
	private final TextureRegion IDLE_6 = getTile(1, 1);
	private final TextureRegion IDLE_7 = getTile(2, 1);

	private final TextureRegion PROJECTILE_1 = getTile(3, 1, 0.5f, 0.5f);
	private final TextureRegion PROJECTILE_2 = getTile(3.5f, 1, 0.5f, 0.5f);
	private final TextureRegion PROJECTILE_3 = getTile(3, 1.5f, 0.5f, 0.5f);
	private final TextureRegion PROJECTILE_4 = getTile(3.5f, 1.5f, 0.5f, 0.5f);

	private final Animation<TextureRegion> IDLE_ANIMATION = loop(0.1f, IDLE_1, IDLE_2, IDLE_3, IDLE_4, IDLE_5, IDLE_6, IDLE_7);
	private final Animation<TextureRegion> PROJECTILE_ANIMATION = loop(0.1f, PROJECTILE_1, PROJECTILE_2);
	private final Animation<TextureRegion> EXPLOSION_ANIMATION = new Animation<>(0.25f, PROJECTILE_3, PROJECTILE_4);

	public static final String IDLE = "slime_fire_idle";
	public static final String PROJECTILE = "slime_fire_projectile";
	public static final String EXPLOSION = "slime_fire_explosion";

	public FireSlimeSheet() {
		super(ResourceManager.instance().getTexture("fire_slime.png"), 32);
	}

	public static Animation<TextureRegion> idle() {
		return new FireSlimeSheet().IDLE_ANIMATION;
	}
	public static Animation<TextureRegion> projectile() {
		return new FireSlimeSheet().PROJECTILE_ANIMATION;
	}
	public static Animation<TextureRegion> explosion() {
		return new FireSlimeSheet().EXPLOSION_ANIMATION;
	}

}
