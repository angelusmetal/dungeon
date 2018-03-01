package com.dungeon.game.character;

import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector2;
import com.dungeon.engine.animation.AnimationProvider;
import com.dungeon.engine.entity.Character;
import com.dungeon.engine.entity.Entity;
import com.dungeon.engine.entity.PlayerCharacter;
import com.dungeon.engine.entity.Projectile;
import com.dungeon.engine.physics.Body;
import com.dungeon.game.GameState;

public class Witch extends PlayerCharacter {

	public static Projectile.Builder CAT_PROTOTYPE = new Projectile.Builder().speed(200).timeToLive(10).autoseek(0.1f).targetRadius(100).targetPredicate(PlayerCharacter.IS_NON_PLAYER);
	public static Quaternion PROJECTILE_LIGHT = new Quaternion(0.8f, 0.2f, 0.8f, 0.5f);

	public Witch(GameState state, Vector2 pos) {
		super(new Body(pos, new Vector2(14, 28)));
		AnimationProvider<AnimationType> provider = new AnimationProvider<>(AnimationType.class, state);
		provider.register(AnimationType.IDLE, state.getTilesetManager().getCharactersTileset().WITCH_IDLE_ANIMATION);
		provider.register(AnimationType.WALK, state.getTilesetManager().getCharactersTileset().WITCH_WALK_ANIMATION);
		provider.register(AnimationType.JUMP, state.getTilesetManager().getCharactersTileset().WITCH_JUMP_ANIMATION);
		provider.register(AnimationType.HIT, state.getTilesetManager().getCharactersTileset().WITCH_HIT_ANIMATION, this::onSelfMovementUpdate);
		provider.register(AnimationType.SLASH, state.getTilesetManager().getCharactersTileset().WITCH_SLASH_ANIMATION, this::onSelfMovementUpdate);
		provider.register(AnimationType.PUNCH, state.getTilesetManager().getCharactersTileset().WITCH_PUNCH_ANIMATION, this::onSelfMovementUpdate);
		provider.register(AnimationType.RUN, state.getTilesetManager().getCharactersTileset().WITCH_RUN_ANIMATION);
		provider.register(AnimationType.CLIMB, state.getTilesetManager().getCharactersTileset().WITCH_CLIMB_ANIMATION);
		setAnimationProvider(provider);
		setCurrentAnimation(provider.get(AnimationType.IDLE));
		health = 90;
		maxSpeed = 60;
		dmg = 50;
	}

	public class Cat extends Projectile {

		public Cat(GameState state, Vector2 origin, float startTime) {
			super(new Body(origin, new Vector2(6, 6)), startTime, CAT_PROTOTYPE);
			// TODO We shouldn't do this every time a projectile is built
			AnimationProvider<AnimationType> provider = new AnimationProvider<>(AnimationType.class, state);
			provider.register(AnimationType.FLY_NORTH, state.getTilesetManager().getCatProjectileTileset().PROJECTILE_FLY_ANIMATION_UP);
			provider.register(AnimationType.FLY_SOUTH, state.getTilesetManager().getCatProjectileTileset().PROJECTILE_FLY_ANIMATION_DOWN);
			provider.register(AnimationType.FLY_SIDE, state.getTilesetManager().getCatProjectileTileset().PROJECTILE_FLY_ANIMATION_RIGHT);
			provider.register(AnimationType.EXPLOSION, state.getTilesetManager().getProjectileTileset().PROJECTILE_WITCH_EXPLODE_ANIMATION);
			animationProvider = provider;
			setCurrentAnimation(provider.get(AnimationType.FLY_NORTH));
			lightRadius = 15;
			lightColor = PROJECTILE_LIGHT;
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
		return new Cat(state, origin, state.getStateTime());
	}

}
