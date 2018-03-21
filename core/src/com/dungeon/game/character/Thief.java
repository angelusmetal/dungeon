package com.dungeon.game.character;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.dungeon.engine.animation.AnimationProvider;
import com.dungeon.engine.entity.CooldownTrigger;
import com.dungeon.engine.entity.PlayerCharacter;
import com.dungeon.engine.entity.Projectile;
import com.dungeon.engine.physics.Body;
import com.dungeon.engine.render.Light;
import com.dungeon.engine.resource.ResourceManager;
import com.dungeon.game.state.GameState;
import com.dungeon.game.tileset.CharactersTileset32;
import com.dungeon.game.tileset.ProjectileTileset;

public class Thief extends PlayerCharacter {

	public static Projectile.Builder BULLET_PROTOTYPE = new Projectile.Builder().speed(400).timeToLive(10).bounciness(10).damage(30);
	static private Light PROJECTILE_LIGHT = new Light(60, new Color(0.3f, 0.9f, 0.2f, 0.5f), Light.NORMAL_TEXTURE, () -> 1f, Light::noRotate);

	public static class Factory {

		private GameState state;
		private AnimationProvider<AnimationType> provider = new AnimationProvider<>(AnimationType.class);

		public Factory(GameState state) {
			this.state = state;
			provider.register(AnimationType.IDLE, ResourceManager.instance().getAnimation(CharactersTileset32.THIEF_IDLE, CharactersTileset32::thiefIdle));
			provider.register(AnimationType.WALK, ResourceManager.instance().getAnimation(CharactersTileset32.THIEF_WALK, CharactersTileset32::thiefWalk));
			provider.register(AnimationType.ATTACK, ResourceManager.instance().getAnimation(CharactersTileset32.THIEF_ATTACK, CharactersTileset32::thiefAttack));
		}

		public Thief build(Vector2 origin) {
			Thief entity = new Thief(origin);
			entity.setAnimationProvider(provider);
			entity.setCurrentAnimation(provider.get(AnimationType.IDLE, state.getStateTime()));
			entity.speed = 96f;
			entity.health = 60;
			entity.fireCooldown = new CooldownTrigger(0.2f);
			return entity;
		}
	}

	private Thief(Vector2 pos) {
		super(new Body(pos, new Vector2(14, 28)));
	}

	public static class Bullet extends Projectile {

		public Bullet(GameState state, Vector2 origin, float startTime) {
			super(new Body(origin, new Vector2(6, 6)), startTime, BULLET_PROTOTYPE);
			AnimationProvider<AnimationType> provider = new AnimationProvider<>(AnimationType.class);
			provider.register(AnimationType.FLY_NORTH, ResourceManager.instance().getAnimation(ProjectileTileset.THIEF_FLY, ProjectileTileset::thiefFly));
			provider.register(AnimationType.FLY_SOUTH, ResourceManager.instance().getAnimation(ProjectileTileset.THIEF_FLY, ProjectileTileset::thiefFly));
			provider.register(AnimationType.FLY_SIDE, ResourceManager.instance().getAnimation(ProjectileTileset.THIEF_FLY, ProjectileTileset::thiefFly));
			provider.register(AnimationType.EXPLOSION, ResourceManager.instance().getAnimation(ProjectileTileset.THIEF_EXPLODE, ProjectileTileset::thiefExplode));
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
