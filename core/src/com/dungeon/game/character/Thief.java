package com.dungeon.game.character;

import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector2;
import com.dungeon.engine.animation.AnimationProvider;
import com.dungeon.engine.entity.CooldownTrigger;
import com.dungeon.engine.entity.Entity;
import com.dungeon.engine.entity.PlayerCharacter;
import com.dungeon.engine.entity.Projectile;
import com.dungeon.engine.physics.Body;
import com.dungeon.engine.render.Light;
import com.dungeon.game.GameState;

public class Thief extends PlayerCharacter {

	public static Projectile.Builder BULLET_PROTOTYPE = new Projectile.Builder().speed(400).timeToLive(10);
	static private Light PROJECTILE_LIGHT = new Light(60, new Quaternion(0.3f, 0.9f, 0.2f, 0.5f), Light.NORMAL_TEXTURE, () -> 1f, Light::noRotate);

	public Thief(GameState state, Vector2 pos) {
		super(new Body(pos, new Vector2(13, 20)));
		AnimationProvider<AnimationType> provider = new AnimationProvider<>(AnimationType.class, state);
		provider.register(AnimationType.IDLE, state.getTilesetManager().getCharactersTileset().THIEF_IDLE_ANIMATION);
		provider.register(AnimationType.WALK, state.getTilesetManager().getCharactersTileset().THIEF_WALK_ANIMATION);
		provider.register(AnimationType.JUMP, state.getTilesetManager().getCharactersTileset().THIEF_JUMP_ANIMATION);
		provider.register(AnimationType.HIT, state.getTilesetManager().getCharactersTileset().THIEF_HIT_ANIMATION, this::onSelfMovementUpdate);
		provider.register(AnimationType.SLASH, state.getTilesetManager().getCharactersTileset().THIEF_SLASH_ANIMATION, this::onSelfMovementUpdate);
		provider.register(AnimationType.PUNCH, state.getTilesetManager().getCharactersTileset().THIEF_PUNCH_ANIMATION, this::onSelfMovementUpdate);
		provider.register(AnimationType.RUN, state.getTilesetManager().getCharactersTileset().THIEF_RUN_ANIMATION);
		provider.register(AnimationType.CLIMB, state.getTilesetManager().getCharactersTileset().THIEF_CLIMB_ANIMATION);
		setAnimationProvider(provider);
		setCurrentAnimation(provider.get(AnimationType.IDLE));
		health = 60;
		maxSpeed = 96;
		dmg = 30;
		fireCooldown = new CooldownTrigger(0.2f);
	}

	public static class Bullet extends Projectile {

		private int dmg;

		public Bullet(GameState state, Vector2 origin, float startTime, int dmg) {
			super(new Body(origin, new Vector2(6, 6)), startTime, BULLET_PROTOTYPE);
			this.dmg = dmg;
			AnimationProvider<AnimationType> provider = new AnimationProvider<>(AnimationType.class, state);
			provider.register(AnimationType.FLY_NORTH, state.getTilesetManager().getProjectileTileset().PROJECTILE_THIEF_FLY_ANIMATION);
			provider.register(AnimationType.FLY_SOUTH, state.getTilesetManager().getProjectileTileset().PROJECTILE_THIEF_FLY_ANIMATION);
			provider.register(AnimationType.FLY_SIDE, state.getTilesetManager().getProjectileTileset().PROJECTILE_THIEF_FLY_ANIMATION);
			provider.register(AnimationType.EXPLOSION, state.getTilesetManager().getProjectileTileset().PROJECTILE_THIEF_EXPLODE_ANIMATION);
			animationProvider = provider;
			setCurrentAnimation(provider.get(AnimationType.FLY_NORTH));
			light = PROJECTILE_LIGHT;
		}
		@Override
		protected boolean onEntityCollision(GameState state, Entity<?> entity) {
			if (exploding) {
				return false;
			}
			// Don't hurt other players!
			if (!(entity instanceof PlayerCharacter)) {
				explode(state);
				entity.hit(state, dmg);
				return true;
			} else {
				return false;
			}
		}
	}

	@Override
	protected Projectile createProjectile(GameState state, Vector2 origin) {
		return new Bullet(state, origin, state.getStateTime(), dmg);
	}

}
