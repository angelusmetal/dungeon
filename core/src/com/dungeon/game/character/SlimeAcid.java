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
import com.dungeon.engine.resource.ResourceManager;
import com.dungeon.game.level.entity.EntityFactory;
import com.dungeon.game.state.GameState;
import com.dungeon.game.tileset.ProjectileTileset;
import com.dungeon.game.tileset.SlimeAcidTileset;

public class SlimeAcid extends Character {

	private static final float MIN_TARGET_DISTANCE = 200 * 200;
	static private Light SLIME_ACID_LIGHT = new Light(100, new Color(0, 1, 0, 0.5f), Light.RAYS_TEXTURE, () -> 1f, Light::rotateMedium);
	static private Light POOL_LIGHT = new Light(100, new Color(0, 0.5f, 0, 0.2f), Light.NORMAL_TEXTURE, () -> 1f, Light::noRotate);

	public static class Factory implements EntityFactory.EntityTypeFactory {

		private GameState state;
		private AnimationProvider<AnimationType> provider = new AnimationProvider<>(AnimationType.class);
		private AnimationProvider<PoolAnimationType> poolAnimationProvider = new AnimationProvider<>(PoolAnimationType.class);
		private AnimationProvider<SplatAnimationType> splatAnimationProvider = new AnimationProvider<>(SplatAnimationType.class);

		public Factory(GameState state) {
			this.state = state;
			provider.register(AnimationType.IDLE, ResourceManager.instance().getAnimation(SlimeAcidTileset.IDLE, SlimeAcidTileset::idle));
			provider.register(AnimationType.WALK, ResourceManager.instance().getAnimation(SlimeAcidTileset.IDLE, SlimeAcidTileset::idle));
			provider.register(AnimationType.ATTACK, ResourceManager.instance().getAnimation(SlimeAcidTileset.ATTACK, SlimeAcidTileset::attack));
			provider.register(AnimationType.DIE, ResourceManager.instance().getAnimation(SlimeAcidTileset.DIE, SlimeAcidTileset::die));

			poolAnimationProvider.register(PoolAnimationType.FLOOD, ResourceManager.instance().getAnimation(SlimeAcidTileset.POOL_FLOOD, SlimeAcidTileset::poolFlood));
			poolAnimationProvider.register(PoolAnimationType.DRY, ResourceManager.instance().getAnimation(SlimeAcidTileset.POOL_DRY, SlimeAcidTileset::poolDry));

			splatAnimationProvider.register(SplatAnimationType.FLY, ResourceManager.instance().getAnimation(SlimeAcidTileset.BLOB, SlimeAcidTileset::blob));
			splatAnimationProvider.register(SplatAnimationType.BLOW, ResourceManager.instance().getAnimation(SlimeAcidTileset.SPLAT, SlimeAcidTileset::splat));
		}

		@Override
		public Entity<?> build(Vector2 origin) {
			SlimeAcid entity = new SlimeAcid(origin);
			entity.poolAnimationProvider = poolAnimationProvider;
			entity.splatAnimationProvider = splatAnimationProvider;
			entity.setAnimationProvider(provider);
			entity.setCurrentAnimation(provider.get(AnimationType.IDLE, state.getStateTime()));
			entity.speed = 20f;
			entity.light = SLIME_ACID_LIGHT;
			entity.maxHealth = 100 * state.getPlayerCount();
			entity.health = entity.maxHealth;
			return entity;
		}
	}

	public class DieSplatter extends Entity<AnimationType> {

		private final float expirationTime;

		public DieSplatter(GameState state, Vector2 origin) {
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

	private enum PoolAnimationType {
		FLOOD, DRY
	}

	public class Pool extends Entity<PoolAnimationType> {

		private final float expirationTime;
		private AnimationProvider<PoolAnimationType> animationProvider;

		public Pool(GameState state, Vector2 origin, AnimationProvider<PoolAnimationType> animationProvider) {
			super(new Body(origin, new Vector2(22, 12)));
			getPos().add(0, -8);
			this.animationProvider = animationProvider;
			setCurrentAnimation(animationProvider.get(PoolAnimationType.FLOOD, state.getStateTime()));
			expirationTime = state.getStateTime() + 5f;
			light = POOL_LIGHT;
			drawContext = new ColorContext(new Color(1, 1, 1, 0.2f));
		}

		@Override
		public void think(GameState state) {
			if (state.getStateTime() > expirationTime - 0.5f && getCurrentAnimation().getId() != PoolAnimationType.DRY) {
				setCurrentAnimation(animationProvider.get(PoolAnimationType.DRY, state.getStateTime()));
			}
		}

		@Override
		public boolean isExpired(float time) {
			return time > expirationTime;
		}

		@Override
		public boolean isSolid() {
			return false;
		}

		@Override
		public float getZIndex() {
			return -1;
		}

		@Override
		protected boolean onEntityCollision(GameState state, Entity<?> entity) {
			if (entity instanceof PlayerCharacter) {
				entity.hit(state, 5 * state.getFrameTime());
				return true;
			} else {
				return false;
			}
		}
	}

	private enum SplatAnimationType {
		FLY, BLOW
	}

	public class Splat extends Entity<SplatAnimationType> {

		private float expirationTime;
		private AnimationProvider<SplatAnimationType> animationProvider;
		private float z;
		private float zSpeed;
		private boolean collided;
		private Vector2 drawOffset = new Vector2(0, 0);

		public Splat(GameState state, Vector2 origin, AnimationProvider<SplatAnimationType> animationProvider) {
			super(new Body(origin, new Vector2(8, 8)));
			getPos().add(0, -8);
			z = 1;
			zSpeed = (float) Math.random() * 100;
			this.animationProvider = animationProvider;
			setCurrentAnimation(animationProvider.get(SplatAnimationType.FLY, state.getStateTime()));
			expirationTime = Float.MAX_VALUE;
			light = POOL_LIGHT;
			drawContext = new ColorContext(new Color(1, 1, 1, 0.5f));
			setSelfXMovement((float) Math.random() * 100f - 50f);
			setSelfYMovement((float) Math.random() * 20f - 10f);
			speed = 50;
		}

		@Override
		public Vector2 getDrawOffset() {
			drawOffset.y = -z;
			return drawOffset;
		}

		@Override
		public void think(GameState state) {
			if (!collided) {
				z += zSpeed * state.getFrameTime();
				zSpeed -= 70 * state.getFrameTime();
				if (z <= 0) {
					collided = true;
					expirationTime = state.getStateTime() + getCurrentAnimation().getDuration();
					setSelfMovement(Vector2.Zero);
					setCurrentAnimation(animationProvider.get(SplatAnimationType.BLOW, state.getStateTime()));
				}
			}
		}

		@Override
		public boolean isExpired(float time) {
			return collided && time > expirationTime;
		}

		@Override
		public boolean isSolid() {
			return false;
		}

		@Override
		public float getZIndex() {
			return -1;
		}

		@Override
		protected boolean onEntityCollision(GameState state, Entity<?> entity) {
			if (entity instanceof PlayerCharacter) {
				entity.hit(state, 5 * state.getFrameTime());
				return true;
			} else {
				return false;
			}
		}
	}

	private float nextThink;
	private float nextAttack;
	private final Color color;
	private AnimationProvider<PoolAnimationType> poolAnimationProvider;
	private AnimationProvider<SplatAnimationType> splatAnimationProvider;
	private enum Status {
		IDLE, ATTACKING
	}
	private Status status;

	private SlimeAcid(Vector2 pos) {
		super(new Body(pos, new Vector2(22, 12)));
		nextThink = 0f;
		color = new Color(1, 1, 1, 0.5f);
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
				this.status = Status.ATTACKING;
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
				this.status = Status.IDLE;
			}
		} else {
			speed *= 1 - 0.5 * state.getFrameTime();
			if (status == Status.ATTACKING && state.getStateTime() > nextAttack) {
				nextAttack = state.getStateTime() + 0.6f;
				state.addEntity(new Pool(state, getPos(), poolAnimationProvider));
			}
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
		state.addEntity(new DieSplatter(state, getPos()));
		state.addEntity(new Pool(state, getPos(), poolAnimationProvider));
		int splats = (int) (5 + Math.random() * 5);
		for (int i = 0; i < splats; ++i) {
			state.addEntity(new Splat(state, getPos(), splatAnimationProvider));
		}
	}

	@Override
	protected boolean onEntityCollision(GameState state, Entity<?> entity) {
		if (entity instanceof PlayerCharacter) {
			entity.hit(state, 10 * state.getFrameTime());
			return true;
		} else {
			return false;
		}
	}

}
