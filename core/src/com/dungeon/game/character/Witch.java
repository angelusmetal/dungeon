package com.dungeon.game.character;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.dungeon.engine.animation.AnimationProvider;
import com.dungeon.engine.entity.PlayerCharacter;
import com.dungeon.engine.entity.Projectile;
import com.dungeon.engine.physics.Body;
import com.dungeon.engine.render.Light;
import com.dungeon.engine.resource.ResourceManager;
import com.dungeon.game.state.GameState;
import com.dungeon.game.tileset.CatProjectileTileset;
import com.dungeon.game.tileset.CharactersTileset32;
import com.dungeon.game.tileset.ProjectileTileset;

public class Witch extends PlayerCharacter {

	public static Projectile.Builder CAT_PROTOTYPE = new Projectile.Builder().speed(200).timeToLive(10).autoseek(0.1f).targetRadius(60).targetPredicate(PlayerCharacter.IS_NON_PLAYER).damage(50);
	static private Light PROJECTILE_LIGHT = new Light(60, new Color(0.8f, 0.2f, 0.8f, 0.5f), Light.FLARE_TEXTURE, () -> 1f, Light::noRotate);

	public static class Factory {

		private GameState state;
		private AnimationProvider<AnimationType> provider = new AnimationProvider<>(AnimationType.class);

		public Factory(GameState state) {
			this.state = state;
			provider.register(AnimationType.IDLE, ResourceManager.instance().getAnimation(CharactersTileset32.WITCH_IDLE, CharactersTileset32::witchIdle));
			provider.register(AnimationType.WALK, ResourceManager.instance().getAnimation(CharactersTileset32.WITCH_WALK, CharactersTileset32::witchWalk));
			provider.register(AnimationType.ATTACK, ResourceManager.instance().getAnimation(CharactersTileset32.WITCH_ATTACK, CharactersTileset32::witchAttack));
		}

		public Witch build(Vector2 origin) {
			Witch entity = new Witch(origin);
			entity.setAnimationProvider(provider);
			entity.setCurrentAnimation(provider.get(AnimationType.IDLE, state.getStateTime()));
			entity.speed = 60f;
			entity.health = 90;
			return entity;
		}
	}

	private Witch(Vector2 pos) {
		super(new Body(pos, new Vector2(14, 28)));
	}

	public class Cat extends Projectile {

		public Cat(GameState state, Vector2 origin, float startTime) {
			super(new Body(origin, new Vector2(6, 6)), startTime, CAT_PROTOTYPE);
			// TODO We shouldn't do this every time a projectile is built
			AnimationProvider<AnimationType> provider = new AnimationProvider<>(AnimationType.class);
			provider.register(AnimationType.FLY_NORTH, ResourceManager.instance().getAnimation(CatProjectileTileset.FLY_UP, CatProjectileTileset::flyUp));
			provider.register(AnimationType.FLY_SOUTH, ResourceManager.instance().getAnimation(CatProjectileTileset.FLY_DOWN, CatProjectileTileset::flyDown));
			provider.register(AnimationType.FLY_SIDE, ResourceManager.instance().getAnimation(CatProjectileTileset.FLY_RIGHT, CatProjectileTileset::flyRight));
			provider.register(AnimationType.EXPLOSION, ResourceManager.instance().getAnimation(ProjectileTileset.WITCH_EXPLODE, ProjectileTileset::witchExplode));
			animationProvider = provider;
			setCurrentAnimation(provider.get(AnimationType.FLY_NORTH, state.getStateTime()));
			light = PROJECTILE_LIGHT;
		}
	}

	@Override
	protected Projectile createProjectile(GameState state, Vector2 origin) {
		return new Cat(state, origin, state.getStateTime());
	}

}
