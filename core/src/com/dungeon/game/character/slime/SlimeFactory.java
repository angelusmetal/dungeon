package com.dungeon.game.character.slime;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.dungeon.engine.entity.Entity;
import com.dungeon.engine.entity.EntityPrototype;
import com.dungeon.engine.render.ColorContext;
import com.dungeon.engine.render.DrawContext;
import com.dungeon.engine.render.Light;
import com.dungeon.engine.resource.ResourceManager;
import com.dungeon.game.level.entity.EntityFactory;
import com.dungeon.game.state.GameState;

public class SlimeFactory implements EntityFactory.EntityTypeFactory {

	final Animation<TextureRegion> idleAnimation;
	final Animation<TextureRegion> attackAnimation;

	final Light characterLight;

	final EntityPrototype character;

	public SlimeFactory() {
		// Character animations
		idleAnimation = ResourceManager.instance().getAnimation(SlimeSheet.IDLE, SlimeSheet::idle);
		attackAnimation = ResourceManager.instance().getAnimation(SlimeSheet.ATTACK, SlimeSheet::attack);

		characterLight = new Light(50, new Color(0, 0.5f, 1, 0.5f), Light.NORMAL_TEXTURE, () -> 1f, Light::noRotate);

		Vector2 BOUNDING_BOX = new Vector2(22, 12);
		Vector2 DRAW_OFFSET = new Vector2(16, 11);

		character = new EntityPrototype()
				.boundingBox(BOUNDING_BOX)
				.drawOffset(DRAW_OFFSET)
				.color(new Color(1, 1, 1, 0.5f))
				.light(characterLight)
				.speed(100f)
				.zSpeed(0)
				.friction(1);
	}

	@Override
	public Entity build(Vector2 origin) {
		return new Slime(origin, this);
	}
}
