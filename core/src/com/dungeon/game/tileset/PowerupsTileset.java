package com.dungeon.game.tileset;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.dungeon.engine.render.Tileset;

public class PowerupsTileset extends Tileset {

	private final TextureRegion HEALTH = getTile(0, 0);
	private final TextureRegion DAMAGE = getTile(1, 0);
	public final Animation<TextureRegion> HEALTH_ANIMATION = new Animation<>(0.25f, HEALTH);
	public final Animation<TextureRegion> DAMAGE_ANIMATION = new Animation<>(0.25f, DAMAGE);

	PowerupsTileset() {
		super(new Texture("powerups.png"), 20);
	}

}
