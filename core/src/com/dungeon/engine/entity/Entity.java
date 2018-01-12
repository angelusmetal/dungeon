package com.dungeon.engine.entity;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.dungeon.engine.animation.GameAnimation;
import com.dungeon.engine.render.Drawable;
import com.dungeon.engine.viewport.ViewPort;
import com.dungeon.game.GameState;

abstract public class Entity<A extends Enum<A>> implements Drawable {

	protected Body body;
	private GameAnimation<A> currentAnimation;
	protected final Vector2 startingPosition;
	private final Vector2 hitBox = new Vector2();
	protected float maxSpeed = 3;
	private boolean invertX = false;

	protected boolean expired;
	protected int health = 100;
	protected int maxHealth = 100;

	public Entity(Vector2 startingPosition) {
		this.startingPosition = startingPosition;
	}

	public void attachToPhysics(GameState state){
		BodyDef def = new BodyDef();
		def.type = BodyDef.BodyType.DynamicBody;
		def.position.set(startingPosition);
		body = state.getWorld().createBody(def);

		CircleShape circle = new CircleShape();
		circle.setRadius(10f);

		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.shape = circle;
		body.createFixture(fixtureDef);

		circle.dispose();

		body.setUserData(this);
	}

	@Override
	public TextureRegion getFrame(float stateTime) {
		return currentAnimation.getKeyFrame(stateTime);
	}

	protected GameAnimation<A> getCurrentAnimation() {
		return currentAnimation;
	}

	protected void setCurrentAnimation(GameAnimation<A> currentAnimation) {
		this.currentAnimation = currentAnimation;
	}

	@Override
	public boolean invertX() {
		return invertX;
	}

	protected void setInvertX(boolean invertX) {
		this.invertX = invertX;
	}

	@Override
	public Vector2 getPos() {
		return body.getPosition();
	}

	@Override
	public Vector2 getDrawOffset() {
		return currentAnimation.getDrawOffset();
	}

	public void setLinearVelocity(Vector2 vector) {
		Vector2 v = new Vector2(vector);
		body.setLinearVelocity(v.scl(maxSpeed * 50));
		onSelfMovementUpdate();
	}

	public Vector2 getLinearVelocity() {
		return body.getLinearVelocity();
	}

	public void hit(GameState state, int dmg) {
		health -= dmg;
		System.out.println("Entity hit by " + dmg + "! " + health + " health remaining...");
		if (health <= 0) {
			setExpired(state, true);
		}
	}

	protected void setHitBox(float x, float y) {
		this.hitBox.x = x;
		this.hitBox.y = y;
	}

	protected Vector2 getHitBox() {
		return hitBox;
	}

	@Override
	public void draw(GameState state, SpriteBatch batch, ViewPort viewPort) {
		TextureRegion characterFrame = getFrame(state.getStateTime());
		float invertX = invertX() ? -1 : 1;
		batch.draw(characterFrame, (body.getPosition().x - viewPort.xOffset - getDrawOffset().x * invertX) * viewPort.scale, (body.getPosition().y - viewPort.yOffset - getDrawOffset().y) * viewPort.scale, characterFrame.getRegionWidth() * viewPort.scale * invertX, characterFrame.getRegionHeight() * viewPort.scale);
	}

	public void setExpired(GameState state, boolean expired) {
		this.expired = expired;
		onExpire(state);
	}

	abstract public boolean isExpired(float time);
	abstract public boolean isSolid();
	protected void onExpire(GameState state) {}
	protected void onSelfMovementUpdate() {}

	public void think(GameState state) {}
	public void beginContact(GameState state, Entity<?> other) {}
	public Body getBody() {
		return body;
	}

}
