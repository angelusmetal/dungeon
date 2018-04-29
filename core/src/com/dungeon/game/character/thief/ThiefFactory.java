package com.dungeon.game.character.thief;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.dungeon.engine.entity.Mutators;
import com.dungeon.engine.entity.Particle;
import com.dungeon.engine.entity.PlayerCharacter;
import com.dungeon.engine.entity.Projectile;
import com.dungeon.engine.render.Light;
import com.dungeon.engine.resource.ResourceManager;
import com.dungeon.game.level.entity.EntityFactory;
import com.dungeon.game.object.tombstone.Tombstone;
import com.dungeon.game.state.GameState;
import com.dungeon.game.tileset.CharactersSheet32;
import com.dungeon.game.tileset.ProjectileSheet;

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
	final Particle.Builder bulletTrail;
	final Light bulletLight;
	final Light bulletTrailLight;

	final Function<Vector2, Tombstone> tombstoneSpawner;

	public ThiefFactory(GameState state, Tombstone.Factory tombstoneFactory) {
		this.state = state;

		idleAnimation = ResourceManager.instance().getAnimation(CharactersSheet32.THIEF_IDLE, CharactersSheet32::thiefIdle);
		walkAnimation = ResourceManager.instance().getAnimation(CharactersSheet32.THIEF_WALK, CharactersSheet32::thiefWalk);
		attackAnimation = ResourceManager.instance().getAnimation(CharactersSheet32.THIEF_ATTACK, CharactersSheet32::thiefAttack);
		bulletFlyAnimation = ResourceManager.instance().getAnimation(ProjectileSheet.THIEF_FLY, ProjectileSheet::thiefFly);
		bulletExplodeAnimation = ResourceManager.instance().getAnimation(ProjectileSheet.THIEF_EXPLODE, ProjectileSheet::thiefExplode);

		bullet = new Projectile.Builder()
				.speed(400)
				.timeToLive(10)
				.bounciness(10)
				.targetPredicate(PlayerCharacter.IS_NON_PLAYER)
				.damage(15)
				.trailFrequency(0.02f);
		bulletExplosion = new Particle.Builder()
				.timeToLive(bulletExplodeAnimation.getAnimationDuration());
		bulletTrail = new Particle.Builder()
				.timeToLive(bulletExplodeAnimation.getAnimationDuration())
				.mutate(Mutators.fadeOut(1f))
				.zSpeed(0);
		bulletLight = new Light(60, new Color(0.3f, 0.9f, 0.2f, 0.5f), Light.NORMAL_TEXTURE, () -> 1f, Light::noRotate);
		bulletTrailLight = new Light(20, new Color(0.3f, 0.9f, 0.2f, 0.1f), Light.NORMAL_TEXTURE, () -> 1f, Light::noRotate);

		tombstoneSpawner = tombstoneFactory::build;
	}

	public Thief build(Vector2 origin) {
		return new Thief(this, origin);
	}
}
