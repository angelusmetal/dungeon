package com.dungeon.game.object;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.dungeon.game.GameState;
import com.dungeon.engine.animation.GameAnimation;
import com.dungeon.engine.entity.Entity;

public class Tombstone extends Entity<Tombstone.AnimationType> {

	public Tombstone(GameState state, Vector2 pos) {
		super(pos);
		setCurrentAnimation(new GameAnimation<>(AnimationType.SPAWN, state.getTilesetManager().getTombstoneTileset().TOMBSTONE_SPAWN_ANIMATION, state.getStateTime()));
	}

	public enum AnimationType {
		SPAWN;
	}

	@Override
	public void attachToPhysics(GameState state){
		BodyDef def = new BodyDef();
		def.type = BodyDef.BodyType.DynamicBody;
		def.position.set(startingPosition);
		body = state.getWorld().createBody(def);

		CircleShape circle = new CircleShape();
		circle.setRadius(10f);

		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.shape = circle;
		fixtureDef.isSensor = true;
		body.createFixture(fixtureDef);

		circle.dispose();
		body.setUserData(this);
	}

	@Override
	public boolean isExpired(float time) {
		return expired;
	}

	@Override
	public boolean isSolid() {
		return true;
	}

}
