package com.dungeon.game.character.slime;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.dungeon.engine.entity.Entity;
import com.dungeon.engine.render.ColorContext;
import com.dungeon.engine.render.DrawContext;
import com.dungeon.engine.render.Light;
import com.dungeon.engine.resource.ResourceManager;
import com.dungeon.game.level.entity.EntityFactory;
import com.dungeon.game.state.GameState;

public class SlimeFactory implements EntityFactory.EntityTypeFactory {

	final GameState state;

	final Animation<TextureRegion> idleAnimation;
	final Animation<TextureRegion> attackAnimation;

	final Light characterLight;

	final DrawContext drawContext;

	public SlimeFactory(GameState state) {
		this.state = state;
		// Character animations
		idleAnimation = ResourceManager.instance().getAnimation(SlimeSheet.IDLE, SlimeSheet::idle);
		attackAnimation = ResourceManager.instance().getAnimation(SlimeSheet.ATTACK, SlimeSheet::attack);

		characterLight = new Light(50, new Color(0, 0.5f, 1, 0.5f), Light.NORMAL_TEXTURE, () -> 1f, Light::noRotate);

		// Draw context
		drawContext = new ColorContext(new Color(1, 1, 1, 0.5f));
	}

	@Override
	public Entity build(Vector2 origin) {
		return new Slime(this, origin);
	}
}
