package com.dungeon.game.character.witch;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.dungeon.engine.animation.GameAnimation;
import com.dungeon.engine.entity.PlayerCharacter;
import com.dungeon.engine.entity.Projectile;
import com.dungeon.engine.physics.Body;
import com.dungeon.game.state.GameState;

public class Witch extends PlayerCharacter {

	private static final Vector2 BOUNDING_BOX = new Vector2(14, 22);
	private static final Vector2 DRAW_OFFSET = new Vector2(16, 13);

	private final WitchFactory factory;

	Witch(WitchFactory factory, Vector2 pos) {
		super(new Body(pos, BOUNDING_BOX), DRAW_OFFSET);
		this.factory = factory;
		setCurrentAnimation(new GameAnimation(getIdleAnimation(), factory.state.getStateTime()));
		speed = 60f;
		health = 90;
	}

	@Override
	protected Projectile createProjectile(GameState state, Vector2 origin) {
		return new WitchBullet(factory, origin, state.getStateTime());
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
