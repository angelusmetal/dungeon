package com.dungeon.game.character;

import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector2;
import com.dungeon.engine.animation.AnimationProvider;
import com.dungeon.engine.entity.PlayerCharacter;
import com.dungeon.engine.entity.Projectile;
import com.dungeon.engine.physics.Body;
import com.dungeon.engine.render.Light;
import com.dungeon.game.state.GameState;

public class Assasin extends PlayerCharacter {

	public static Projectile.Builder BULLET_PROTOTYPE = new Projectile.Builder().speed(80).timeToLive(10).damage(100);
	static private Light PROJECTILE_LIGHT = new Light(60, new Quaternion(0.8f, 0.3f, 0.2f, 0.5f), Light.NORMAL_TEXTURE, () -> 1f, Light::noRotate);

	public static class Factory {

		private GameState state;
		private AnimationProvider<AnimationType> provider = new AnimationProvider<>(AnimationType.class);

		public Factory(GameState state) {
			this.state = state;
			provider.register(AnimationType.IDLE, state.getTilesetManager().getCharactersTileset().ASSASIN_IDLE_ANIMATION);
			provider.register(AnimationType.WALK, state.getTilesetManager().getCharactersTileset().ASSASIN_WALK_ANIMATION);
			provider.register(AnimationType.ATTACK, state.getTilesetManager().getCharactersTileset().ASSASIN_ATTACK_ANIMATION);
		}

		public Assasin build(Vector2 origin) {
			Assasin entity = new Assasin(origin);
			entity.setAnimationProvider(provider);
			entity.setCurrentAnimation(provider.get(AnimationType.IDLE, state.getStateTime()));
			entity.speed = 60f;
			entity.health = 100;
			return entity;
		}
	}

	private Assasin(Vector2 pos) {
		super(new Body(pos, new Vector2(13, 20)));
	}

	public static class Bullet extends Projectile {

		public Bullet(GameState state, Vector2 origin, float startTime) {
			super(new Body(origin, new Vector2(6, 6)), startTime, BULLET_PROTOTYPE);
			AnimationProvider<AnimationType> provider = new AnimationProvider<>(AnimationType.class);
			provider.register(AnimationType.FLY_NORTH, state.getTilesetManager().getProjectileTileset().PROJECTILE_ASSASIN_FLY_ANIMATION);
			provider.register(AnimationType.FLY_SOUTH, state.getTilesetManager().getProjectileTileset().PROJECTILE_ASSASIN_FLY_ANIMATION);
			provider.register(AnimationType.FLY_SIDE, state.getTilesetManager().getProjectileTileset().PROJECTILE_ASSASIN_FLY_ANIMATION);
			provider.register(AnimationType.EXPLOSION, state.getTilesetManager().getProjectileTileset().PROJECTILE_ASSASIN_EXPLODE_ANIMATION);
			animationProvider = provider;
			setCurrentAnimation(provider.get(AnimationType.FLY_NORTH, state.getStateTime()));
			light = PROJECTILE_LIGHT;
		}

	}

	@Override
	protected Projectile createProjectile(GameState state, Vector2 origin) {
		return new Bullet(state, origin, state.getStateTime());
	}

}
