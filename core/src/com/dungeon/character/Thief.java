package com.dungeon.character;

import com.dungeon.GameState;
import com.dungeon.animation.AnimationProvider;
import com.dungeon.projectile.BaseProjectile;

public class Thief extends PlayerCharacter {

	public Thief(GameState state) {
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
		setHitBox(13, 20);
		health = 80;
		maxSpeed = 2.5f;
		dmg = 5;
	}

	public static class Projectile extends BaseProjectile {

		public Projectile(GameState state, float timeToLive, float startTime, int dmg) {
			super(timeToLive, startTime, dmg);
			AnimationProvider<AnimationType> provider = new AnimationProvider<>(AnimationType.class, state);
			provider.register(AnimationType.FLY, state.getTilesetManager().getProjectileTileset().PROJECTILE_THIEF_FLY_ANIMATION);
			provider.register(AnimationType.EXPLOSION, state.getTilesetManager().getProjectileTileset().PROJECTILE_THIEF_EXPLODE_ANIMATION);
			animationProvider = provider;
			setCurrentAnimation(provider.get(AnimationType.FLY));
		}
		@Override
		protected void onEntityCollision(GameState state, Entity<?> entity) {
			// Don't hurt other players!
			if (!(entity instanceof PlayerCharacter)) {
				explode(state);
				entity.hit(state, dmg);
			}
		}
	}

	@Override
	protected BaseProjectile createProjectile(GameState state) {
		return new Projectile(state, 10, state.getStateTime(), dmg);
	}

}
