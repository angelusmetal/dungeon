package com.dungeon.game.character;

import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector2;
import com.dungeon.engine.animation.AnimationProvider;
import com.dungeon.engine.entity.Entity;
import com.dungeon.engine.entity.PlayerCharacter;
import com.dungeon.engine.entity.Projectile;
import com.dungeon.engine.physics.Body;
import com.dungeon.engine.render.Light;
import com.dungeon.game.GameState;

public class Assasin extends PlayerCharacter {

	public static Projectile.Builder BULLET_PROTOTYPE = new Projectile.Builder().speed(80).timeToLive(10);
	static private Light PROJECTILE_LIGHT = new Light(15, new Quaternion(0.8f, 0.3f, 0.2f, 0.5f), () -> 1f);

	public Assasin(GameState state, Vector2 pos) {
		super(new Body(pos, new Vector2(13, 20)));
		AnimationProvider<AnimationType> provider = new AnimationProvider<>(AnimationType.class, state);
		provider.register(AnimationType.IDLE, state.getTilesetManager().getCharactersTileset().ASSASIN_IDLE_ANIMATION);
		provider.register(AnimationType.WALK, state.getTilesetManager().getCharactersTileset().ASSASIN_WALK_ANIMATION);
		provider.register(AnimationType.JUMP, state.getTilesetManager().getCharactersTileset().ASSASIN_JUMP_ANIMATION);
		provider.register(AnimationType.HIT, state.getTilesetManager().getCharactersTileset().ASSASIN_HIT_ANIMATION, this::onSelfMovementUpdate);
		provider.register(AnimationType.SLASH, state.getTilesetManager().getCharactersTileset().ASSASIN_SLASH_ANIMATION, this::onSelfMovementUpdate);
		provider.register(AnimationType.PUNCH, state.getTilesetManager().getCharactersTileset().ASSASIN_PUNCH_ANIMATION, this::onSelfMovementUpdate);
		provider.register(AnimationType.RUN, state.getTilesetManager().getCharactersTileset().ASSASIN_RUN_ANIMATION);
		provider.register(AnimationType.CLIMB, state.getTilesetManager().getCharactersTileset().ASSASIN_CLIMB_ANIMATION);
		setAnimationProvider(provider);
		setCurrentAnimation(provider.get(AnimationType.IDLE));
		health = 100;
		maxSpeed = 60;
		dmg = 65;
	}

	public static class Bullet extends Projectile {

		private int dmg;

		public Bullet(GameState state, Vector2 origin, float startTime, int dmg) {
			super(new Body(origin, new Vector2(6, 6)), startTime, BULLET_PROTOTYPE);
			this.dmg = dmg;
			AnimationProvider<AnimationType> provider = new AnimationProvider<>(AnimationType.class, state);
			provider.register(AnimationType.FLY_NORTH, state.getTilesetManager().getProjectileTileset().PROJECTILE_ASSASIN_FLY_ANIMATION);
			provider.register(AnimationType.FLY_SOUTH, state.getTilesetManager().getProjectileTileset().PROJECTILE_ASSASIN_FLY_ANIMATION);
			provider.register(AnimationType.FLY_SIDE, state.getTilesetManager().getProjectileTileset().PROJECTILE_ASSASIN_FLY_ANIMATION);
			provider.register(AnimationType.EXPLOSION, state.getTilesetManager().getProjectileTileset().PROJECTILE_ASSASIN_EXPLODE_ANIMATION);
			animationProvider = provider;
			setCurrentAnimation(provider.get(AnimationType.FLY_NORTH));
			light = PROJECTILE_LIGHT;
		}
		@Override
		protected boolean onEntityCollision(GameState state, Entity<?> entity) {
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
