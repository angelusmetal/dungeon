package com.dungeon.game.character.assassin;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.dungeon.engine.entity.Projectile;
import com.dungeon.engine.physics.Body;

class AssassinBullet extends Projectile {

	private final AssassinFactory factory;

	public AssassinBullet(AssassinFactory factory, Vector2 origin, float startTime) {
		super(new Body(origin, new Vector2(6, 6)), startTime, factory.bulletPrototype);
		this.factory = factory;
		light = factory.bulletLight;
	}

	@Override
	protected Animation<TextureRegion> getAnimation(Vector2 direction) {
		return factory.bulletFlyAnimation;
	}

	@Override
	protected Animation<TextureRegion> getExplodeAnimation() {
		return factory.bulletExplodeAnimation;
	}
}
