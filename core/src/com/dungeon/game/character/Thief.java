package com.dungeon.game.character;

import com.badlogic.gdx.math.Vector2;
import com.dungeon.game.GameState;
import com.dungeon.engine.animation.AnimationProvider;
import com.dungeon.engine.entity.Entity;
import com.dungeon.engine.entity.PlayerCharacter;
import com.dungeon.engine.entity.Projectile;

public class Thief extends PlayerCharacter {

	public Thief(GameState state, Vector2 pos) {
		super(pos);
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

	public static class Bullet extends Projectile {

		private int dmg;

		public Bullet(GameState state, Vector2 pos, Vector2 linearVelocity, float timeToLive, float startTime, int dmg) {
			super(pos, linearVelocity, timeToLive, startTime);
			this.dmg = dmg;
			AnimationProvider<AnimationType> provider = new AnimationProvider<>(AnimationType.class, state);
			provider.register(AnimationType.FLY_NORTH, state.getTilesetManager().getProjectileTileset().PROJECTILE_THIEF_FLY_ANIMATION);
			provider.register(AnimationType.FLY_SOUTH, state.getTilesetManager().getProjectileTileset().PROJECTILE_THIEF_FLY_ANIMATION);
			provider.register(AnimationType.FLY_SIDE, state.getTilesetManager().getProjectileTileset().PROJECTILE_THIEF_FLY_ANIMATION);
			provider.register(AnimationType.EXPLOSION, state.getTilesetManager().getProjectileTileset().PROJECTILE_THIEF_EXPLODE_ANIMATION);
			animationProvider = provider;
			setCurrentAnimation(provider.get(AnimationType.FLY_NORTH));
		}
		@Override
		public void beginContact(GameState state, Entity<?> entity) {
			// Don't hurt other players!
			if (!(entity instanceof PlayerCharacter)) {
				if (!exploding) {
					entity.hit(state, dmg);
					explode(state);
				}
			}
		}
	}

	@Override
	protected Projectile createProjectile(GameState state, Vector2 pos, Vector2 linearVelocity) {
		return new Bullet(state, pos, linearVelocity, 10, state.getStateTime(), dmg);
	}

}
