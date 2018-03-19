package com.dungeon.game.character;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.dungeon.engine.animation.AnimationProvider;
import com.dungeon.engine.entity.Character;
import com.dungeon.engine.entity.Entity;
import com.dungeon.engine.entity.PlayerCharacter;
import com.dungeon.engine.physics.Body;
import com.dungeon.engine.render.ColorContext;
import com.dungeon.engine.render.Light;
import com.dungeon.game.level.entity.EntityFactory;
import com.dungeon.game.state.GameState;

public class SlimeAcid extends Character {

	private static final float MIN_TARGET_DISTANCE = 200 * 200;
	static private Light SLIME_ACID_LIGHT = new Light(100, new Color(0, 1, 0, 0.5f), Light.RAYS_TEXTURE, () -> 1f, Light::rotateMedium);

	public static class Factory implements EntityFactory.EntityTypeFactory {

		private GameState state;
		private AnimationProvider<AnimationType> provider = new AnimationProvider<>(AnimationType.class);

		public Factory(GameState state) {
			this.state = state;
			provider.register(AnimationType.IDLE, state.getTilesetManager().getSlimeAcidTileset().IDLE_ANIMATION);
			provider.register(AnimationType.WALK, state.getTilesetManager().getSlimeAcidTileset().IDLE_ANIMATION);
			provider.register(AnimationType.ATTACK, state.getTilesetManager().getSlimeAcidTileset().ATTACK_ANIMATION);
			provider.register(AnimationType.DIE, state.getTilesetManager().getSlimeAcidTileset().DIE_ANIMATION);
		}

		@Override
		public Entity<?> build(Vector2 origin) {
			SlimeAcid entity = new SlimeAcid(origin);
			entity.setAnimationProvider(provider);
			entity.setCurrentAnimation(provider.get(AnimationType.IDLE, state.getStateTime()));
			entity.speed = 20f;
			entity.light = SLIME_ACID_LIGHT;
			entity.maxHealth = 100 * state.getPlayerCount();
			entity.health = entity.maxHealth;
			return entity;
		}
	}

	public class Splatter extends Entity<AnimationType> {

		private final float expirationTime;

		public Splatter(GameState state, Vector2 origin) {
			super(new Body(origin, new Vector2(22, 12)));
			setCurrentAnimation(animationProvider.get(AnimationType.DIE, state.getStateTime()));
			expirationTime = state.getStateTime() + getCurrentAnimation().getDuration();
			light = SLIME_ACID_LIGHT;
			this.drawContext = SlimeAcid.this.drawContext;
		}

		@Override
		public boolean isExpired(float time) {
			return time > expirationTime;
		}

		@Override
		public boolean isSolid() {
			return false;
		}

	}

	private final Vector2 nextTarget = new Vector2();
	private float nextThink;
	private final Color color;

	private SlimeAcid(Vector2 pos) {
		super(new Body(pos, new Vector2(22, 12)));
		nextTarget.set(pos);
		nextThink = 0f;
		color = new Color(0, 1, 0, 0.5f);
		drawContext = new ColorContext(color);
	}

	@Override
	public void think(GameState state) {
//		super.think(state);
		if (getSelfMovement().x != 0) {
			setInvertX(getSelfMovement().x < 0);
		}
		if (state.getStateTime() > nextThink) {
			Vector2 target = reTarget(state);
			if (target.len2() > 0) {
				// Attack lasts 2 seconds
				nextThink = state.getStateTime() + 3f;
				// Aim towards target
				speed = 75f;
				setSelfMovement(target);
				setCurrentAnimation(animationProvider.get(AnimationType.ATTACK, state.getStateTime()));
			} else {
				nextThink = state.getStateTime() + (float) Math.random() * 3f;
				speed = 5f;
				// Aim random direction
				if (Math.random() > 0.7f) {
					Vector2 newDirection = new Vector2((float)Math.random() * 20f - 10f, (float)Math.random() * 20f - 10f);
					setSelfMovement(newDirection);
					setCurrentAnimation(animationProvider.get(AnimationType.WALK, state.getStateTime()));
				} else {
					setSelfMovement(Vector2.Zero);
					setCurrentAnimation(animationProvider.get(AnimationType.IDLE, state.getStateTime()));
				}
			}
		} else {
			speed *= 1 - 0.5 * state.getFrameTime();
		}
	}

	private Vector2 reTarget(GameState state) {
		Vector2 closestPlayer = new Vector2();
		for (PlayerCharacter playerCharacter : state.getPlayerCharacters()) {
			Vector2 v = playerCharacter.getPos().cpy().sub(getPos());
			float len = v.len2();
			if (len < MIN_TARGET_DISTANCE && (closestPlayer.len2() == 0 || len < closestPlayer.len2())) {
				closestPlayer = v;
			}
		}
		return closestPlayer;
	}

	@Override
	protected void onExpire(GameState state) {
		state.addEntity(new Splatter(state, getPos()));
	}

	@Override
	protected boolean onEntityCollision(GameState state, Entity<?> entity) {
		if (entity instanceof PlayerCharacter) {
			entity.hit(state, 1);
			return true;
		} else {
			return false;
		}
	}

}
