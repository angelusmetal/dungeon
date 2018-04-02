package com.dungeon.game.character.ghost;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.dungeon.engine.entity.Entity;
import com.dungeon.engine.render.Light;
import com.dungeon.engine.resource.ResourceManager;
import com.dungeon.game.level.entity.EntityFactory;
import com.dungeon.game.state.GameState;

public class GhostFactory implements EntityFactory.EntityTypeFactory {

	final GameState state;
	final Animation<TextureRegion> idleAnimation;
	final Light characterLight;

	public GhostFactory(GameState state) {
		this.state = state;
		idleAnimation = ResourceManager.instance().getAnimation(GhostSheet.HOVER, GhostSheet::hover);
		characterLight = new Light(200, new Color(0.2f, 0.4f, 1, 0.5f), Light.RAYS_TEXTURE, () -> 1f, Light::rotateSlow);
	}

	@Override
	public Entity build(Vector2 origin) {
		return new Ghost(this, origin);
	}
}
