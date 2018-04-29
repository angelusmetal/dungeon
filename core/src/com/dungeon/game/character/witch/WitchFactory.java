package com.dungeon.game.character.witch;

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
import com.dungeon.game.tileset.CatProjectileSheet;
import com.dungeon.game.tileset.CharactersSheet32;
import com.dungeon.game.tileset.ProjectileSheet;

import java.util.function.Function;

public class WitchFactory implements EntityFactory.EntityTypeFactory {

	final GameState state;

	final Animation<TextureRegion> attackAnimation;
	final Animation<TextureRegion> idleAnimation;
	final Animation<TextureRegion> walkAnimation;
	final Animation<TextureRegion> bulletFlySideAnimation;
	final Animation<TextureRegion> bulletFlyNorthAnimation;
	final Animation<TextureRegion> bulletFlySouthAnimation;
	final Animation<TextureRegion> bulletExplodeAnimation;

	final Projectile.Builder bullet;
	final Particle.Builder bulletExplosion;
	final Particle.Builder bulletTrail;
	final Light bulletLight;

	final Function<Vector2, Tombstone> tombstoneSpawner;

	public WitchFactory(GameState state, Tombstone.Factory tombstoneFactory) {
		this.state = state;

		idleAnimation = ResourceManager.instance().getAnimation(CharactersSheet32.WITCH_IDLE, CharactersSheet32::witchIdle);
		walkAnimation = ResourceManager.instance().getAnimation(CharactersSheet32.WITCH_WALK, CharactersSheet32::witchWalk);
		attackAnimation = ResourceManager.instance().getAnimation(CharactersSheet32.WITCH_ATTACK, CharactersSheet32::witchAttack);
		bulletFlySideAnimation = ResourceManager.instance().getAnimation(CatProjectileSheet.FLY_RIGHT, CatProjectileSheet::flyRight);
		bulletFlyNorthAnimation = ResourceManager.instance().getAnimation(CatProjectileSheet.FLY_UP, CatProjectileSheet::flyUp);
		bulletFlySouthAnimation = ResourceManager.instance().getAnimation(CatProjectileSheet.FLY_DOWN, CatProjectileSheet::flyDown);
		bulletExplodeAnimation = ResourceManager.instance().getAnimation(ProjectileSheet.WITCH_EXPLODE, ProjectileSheet::witchExplode);

		bullet = new Projectile.Builder()
				.speed(200)
				.timeToLive(10)
				.targetPredicate(PlayerCharacter.IS_NON_PLAYER)
				.damage(25)
				.trailFrequency(0.05f)
				.mutate(Mutators.autoSeek(0.1f, 60, PlayerCharacter.IS_NON_PLAYER));
		bulletExplosion = new Particle.Builder()
				.timeToLive(bulletExplodeAnimation.getAnimationDuration());
		bulletTrail = new Particle.Builder()
				.timeToLive(bulletExplodeAnimation.getAnimationDuration())
				.mutate(Mutators.fadeOut(0.8f))
				.mutate(Mutators.zAccel(100f));
		bulletLight = new Light(60, new Color(0.8f, 0.2f, 0.8f, 0.5f), Light.FLARE_TEXTURE, () -> 1f, Light::noRotate);

		tombstoneSpawner = tombstoneFactory::build;
	}

	public Witch build(Vector2 origin) {
		return new Witch(this, origin);
	}
}
