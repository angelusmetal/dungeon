package com.dungeon.engine.entity;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.dungeon.engine.animation.AnimationProvider;
import com.dungeon.engine.render.Drawable;
import com.dungeon.game.GameState;

/**
 * Base class for all projectiles
 */
public abstract class Projectile extends Entity<Projectile.AnimationType> implements Drawable {

	/**
	 * Describes animation types for projectiles
	 */
	public enum AnimationType {
		FLY_NORTH, FLY_SOUTH, FLY_SIDE, EXPLOSION;
	}

	protected AnimationProvider<AnimationType> animationProvider;
	protected final Vector2 linearVelocity;
	protected float timeToLive;
	protected float startTime;
	protected boolean exploding = false;

	public Projectile(Vector2 pos, Vector2 linearVelocity, float timeToLive, float startTime) {
		super(pos);
		this.linearVelocity = linearVelocity;
		this.timeToLive = timeToLive;
		this.startTime = startTime;
	}

	@Override
	public void attachToPhysics(GameState state){
		BodyDef def = new BodyDef();
		def.type = BodyDef.BodyType.DynamicBody;
		def.position.set(startingPosition);
		def.bullet = true;
		body = state.getWorld().createBody(def);

		CircleShape circle = new CircleShape();
		circle.setRadius(3f);

		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.shape = circle;
		fixtureDef.isSensor = true;
		body.createFixture(fixtureDef);

		circle.dispose();
		body.setUserData(this);
		System.out.println("Bullet with linearVelocity " + linearVelocity);
		body.setLinearVelocity(linearVelocity);

		// TODO This should be moved somewhere else
		// Updates current animation based on the direction vector
		AnimationType animationType;
		if (Math.abs(linearVelocity.x) > Math.abs(linearVelocity.y)) {
			// Sideways animation; negative values invert X
			animationType = AnimationType.FLY_SIDE;
			setInvertX(linearVelocity.x < 0);
		} else {
			// North / south animation
			animationType = linearVelocity.y < 0 ? AnimationType.FLY_SOUTH : AnimationType.FLY_NORTH;
		}
		setCurrentAnimation(animationProvider.get(animationType));
	}

	@Override
	public boolean isExpired(float time) {
		return (startTime + timeToLive) < time;
	}

	@Override
	public boolean isSolid() {
		return false;
	}

	/**
	 * Triggers the projectile explosion
	 */
	protected void explode(GameState state) {
		// Set exploding status and animation (and TTL to expire right after explosion end)
		exploding = true;
		setLinearVelocity(Vector2.Zero);
		setCurrentAnimation(animationProvider.get(AnimationType.EXPLOSION));
		startTime = state.getStateTime();
		timeToLive = getCurrentAnimation().getDuration();
	}

}
