package com.dungeon.game.character.ghost;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.dungeon.engine.animation.GameAnimation;
import com.dungeon.engine.entity.Entity;
import com.dungeon.engine.entity.EntityPrototype;
import com.dungeon.engine.entity.Traits;
import com.dungeon.engine.render.ColorContext;
import com.dungeon.engine.render.Light;
import com.dungeon.engine.resource.ResourceManager;
import com.dungeon.game.level.entity.EntityFactory;
import com.dungeon.game.state.GameState;

public class GhostFactory implements EntityFactory.EntityTypeFactory {

	final Animation<TextureRegion> idleAnimation;
	final EntityPrototype character;
	final EntityPrototype death;

	public GhostFactory() {
		idleAnimation = ResourceManager.instance().getAnimation(GhostSheet.HOVER, GhostSheet::hover);

		Vector2 boundingBox = new Vector2(16, 26);
		Vector2 drawOffset = new Vector2(16, 16);

		Light characterLight = new Light(200, new Color(0.2f, 0.4f, 1, 0.5f), Light.RAYS_TEXTURE, () -> 1f, Light::rotateSlow);
		character = new EntityPrototype()
				.boundingBox(boundingBox)
				.drawOffset(drawOffset)
				.color(new Color(1, 1, 1, 0.5f))
				.light(characterLight)
				.speed(20f);
		death = new EntityPrototype()
				.animation(idleAnimation)
				.boundingBox(boundingBox)
				.drawOffset(drawOffset)
				.color(new Color(1, 0, 0, 0.5f))
				.light(characterLight)
				.with(Traits.fadeOut(0.5f))
				.with(Traits.fadeOutLight())
				.timeToLive(1f)
				.zSpeed(50f);
	}

	@Override
	public Entity build(Vector2 origin) {
		return new Ghost(origin, this);
	}

	public Entity createDeath(Vector2 origin, boolean invertX) {
		Entity entity = new Entity(origin, death);
		entity.setInvertX(invertX);
		return entity;
	}
}
