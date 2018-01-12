package com.dungeon.game.character;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.dungeon.game.GameState;
import com.dungeon.engine.animation.AnimationProvider;
import com.dungeon.engine.entity.Character;
import com.dungeon.engine.entity.Entity;
import com.dungeon.engine.entity.PlayerCharacter;

public class Ghost extends Character {

	private static final float MIN_TARGET_DISTANCE = 500 * 500;

	public Ghost(GameState state, Vector2 pos) {
		super(pos);
		AnimationProvider<AnimationType> provider = new AnimationProvider<>(AnimationType.class, state);
		provider.register(AnimationType.IDLE, state.getTilesetManager().getGhostTileset().HOVER_ANIMATION);
		provider.register(AnimationType.WALK, state.getTilesetManager().getGhostTileset().HOVER_ANIMATION);
		provider.register(AnimationType.JUMP, state.getTilesetManager().getGhostTileset().HOVER_ANIMATION);
		provider.register(AnimationType.HIT, state.getTilesetManager().getGhostTileset().HOVER_ANIMATION, this::onSelfMovementUpdate);
		provider.register(AnimationType.SLASH, state.getTilesetManager().getGhostTileset().HOVER_ANIMATION, this::onSelfMovementUpdate);
		provider.register(AnimationType.PUNCH, state.getTilesetManager().getGhostTileset().HOVER_ANIMATION, this::onSelfMovementUpdate);
		provider.register(AnimationType.RUN, state.getTilesetManager().getGhostTileset().HOVER_ANIMATION);
		provider.register(AnimationType.CLIMB, state.getTilesetManager().getGhostTileset().HOVER_ANIMATION);
		setAnimationProvider(provider);
		setCurrentAnimation(provider.get(AnimationType.IDLE));
		setHitBox(16, 30);
		maxSpeed = 1f;
	}

	@Override
	public void think(GameState state) {
		Vector2 closestPlayer = new Vector2();
		for (PlayerCharacter playerCharacter : state.getPlayerCharacters()) {
			Vector2 v = playerCharacter.getPos().cpy().sub(getPos());
			float len = v.len2();
			if (len < MIN_TARGET_DISTANCE && (closestPlayer.len2() == 0 || len < closestPlayer.len2())) {
				closestPlayer = v.clamp(maxSpeed, maxSpeed);
			}
			setLinearVelocity(closestPlayer);
		}
	}

	@Override
	public void beginContact(GameState state, Entity<?> entity) {
		if (entity instanceof PlayerCharacter) {
			entity.hit(state, 1);
		}
	}
}
