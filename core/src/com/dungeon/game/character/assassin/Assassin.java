package com.dungeon.game.character.assassin;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.dungeon.engine.animation.GameAnimation;
import com.dungeon.engine.entity.PlayerCharacter;
import com.dungeon.engine.entity.Projectile;
import com.dungeon.engine.physics.Body;
import com.dungeon.game.state.GameState;

public class Assassin extends PlayerCharacter {

	private final AssassinFactory factory;

	Assassin(AssassinFactory factory, Vector2 pos) {
		super(new Body(pos, new Vector2(13, 20)));
		this.factory = factory;
		setCurrentAnimation(new GameAnimation(getIdleAnimation(), factory.state.getStateTime()));
		speed = 60f;
		health = 100f;
	}

	@Override
	protected Projectile createProjectile(GameState state, Vector2 origin) {
		return new AssassinBullet(factory, origin, state.getStateTime());
	}

	@Override protected Animation<TextureRegion> getAttackAnimation() {
		return factory.attackAnimation;
	}

	@Override protected Animation<TextureRegion> getIdleAnimation() {
		return factory.idleAnimation;
	}

	@Override protected Animation<TextureRegion> getWalkAnimation() {
		return factory.walkAnimation;
	}

	@Override
	protected void onExpire(GameState state) {
		state.addEntity(factory.tombstoneSpawner.apply(getPos()));
	}

}
