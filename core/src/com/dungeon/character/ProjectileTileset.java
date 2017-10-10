package com.dungeon.character;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.dungeon.tileset.Tileset;

public class ProjectileTileset extends Tileset {

	public final TextureRegion PROJECTILE = getTile(0, 0);
	public final Animation<TextureRegion> PROJECTILE_ANIMATION = new Animation<>(0.1f, PROJECTILE);

	public ProjectileTileset() {
		super(new Texture("projectile.png"), 8);
	}

}
