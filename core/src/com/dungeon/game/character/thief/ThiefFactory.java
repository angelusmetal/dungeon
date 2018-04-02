package com.dungeon.game.character.thief;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.dungeon.engine.entity.Particle;
import com.dungeon.engine.entity.PlayerCharacter;
import com.dungeon.engine.entity.Projectile;
import com.dungeon.engine.render.Light;
import com.dungeon.engine.resource.ResourceManager;
import com.dungeon.game.level.entity.EntityFactory;
import com.dungeon.game.object.tombstone.Tombstone;
import com.dungeon.game.state.GameState;
import com.dungeon.game.tileset.CharactersTileset32;
import com.dungeon.game.tileset.ProjectileTileset;

import java.util.function.Function;

public class ThiefFactory implements EntityFactory.EntityTypeFactory {

	final GameState state;

	final Animation<TextureRegion> attackAnimation;
	final Animation<TextureRegion> idleAnimation;
	final Animation<TextureRegion> walkAnimation;
	final Animation<TextureRegion> bulletFlyAnimation;
	final Animation<TextureRegion> bulletExplodeAnimation;

	final Projectile.Builder bullet;
	final Particle.Builder bulletExplosion;
	final Light bulletLight;

	final Function<Vector2, Tombstone> tombstoneSpawner;

	public ThiefFactory(GameState state, Tombstone.Factory tombstoneFactory) {
		this.state = state;

		idleAnimation = ResourceManager.instance().getAnimation(CharactersTileset32.THIEF_IDLE, CharactersTileset32::thiefIdle);
		walkAnimation = ResourceManager.instance().getAnimation(CharactersTileset32.THIEF_WALK, CharactersTileset32::thiefWalk);
		attackAnimation = ResourceManager.instance().getAnimation(CharactersTileset32.THIEF_ATTACK, CharactersTileset32::thiefAttack);
		bulletFlyAnimation = ResourceManager.instance().getAnimation(ProjectileTileset.THIEF_FLY, ProjectileTileset::thiefFly);
		bulletExplodeAnimation = ResourceManager.instance().getAnimation(ProjectileTileset.THIEF_EXPLODE, ProjectileTileset::thiefExplode);

		bullet = new Projectile.Builder()
				.speed(400)
				.timeToLive(10)
				.bounciness(10)
				.targetPredicate(PlayerCharacter.IS_NON_PLAYER)
				.damage(15);
		bulletExplosion = new Particle.Builder()
				.timeToLive(bulletExplodeAnimation.getAnimationDuration());
		bulletLight = new Light(60, new Color(0.3f, 0.9f, 0.2f, 0.5f), Light.NORMAL_TEXTURE, () -> 1f, Light::noRotate);

		tombstoneSpawner = tombstoneFactory::build;
	}

	public Thief build(Vector2 origin) {
		return new Thief(this, origin);
	}
}
