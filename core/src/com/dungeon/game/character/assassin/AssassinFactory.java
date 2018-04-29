package com.dungeon.game.character.assassin;

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
import com.dungeon.game.tileset.CharactersSheet32;
import com.dungeon.game.tileset.ProjectileSheet;

import java.util.function.Function;

public class AssassinFactory implements EntityFactory.EntityTypeFactory {

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

	public AssassinFactory(GameState state, Tombstone.Factory tombstoneFactory) {
		this.state = state;

		attackAnimation = ResourceManager.instance().getAnimation(CharactersSheet32.ASSASSIN_ATTACK, CharactersSheet32::assasinAttack);
		idleAnimation = ResourceManager.instance().getAnimation(CharactersSheet32.ASSASSIN_IDLE, CharactersSheet32::assassinIdle);
		walkAnimation = ResourceManager.instance().getAnimation(CharactersSheet32.ASSASSIN_WALK, CharactersSheet32::assassinWalk);
		bulletFlyAnimation = ResourceManager.instance().getAnimation(ProjectileSheet.ASSASSIN_FLY, ProjectileSheet::assasinFly);
		bulletExplodeAnimation = ResourceManager.instance().getAnimation(ProjectileSheet.ASSASSIN_EXPLODE, ProjectileSheet::assasinExplode);

		bulletLight = new Light(60, new Color(0.8f, 0.3f, 0.2f, 0.5f), Light.NORMAL_TEXTURE, () -> 1f, Light::noRotate);
		bulletTrailLight = new Light(60, new Color(0.8f, 0.3f, 0.2f, 0.2f), Light.NORMAL_TEXTURE, () -> 1f, Light::noRotate);
		bullet = new Projectile.Builder()
				.speed(180)
				.timeToLive(10)
				.targetPredicate(PlayerCharacter.IS_NON_PLAYER)
				.damage(35)
				.trailFrequency(0.1f);
		bulletExplosion = new Particle.Builder()
				.timeToLive(bulletExplodeAnimation.getAnimationDuration());
		bulletTrail = new Particle.Builder()
				.timeToLive(bulletExplodeAnimation.getAnimationDuration())
				.mutate(Particle.fadeOut(1f))
				.zSpeed(10);

		tombstoneSpawner = tombstoneFactory::build;
	}

	public Assassin build(Vector2 origin) {
		return new Assassin(this, origin);
	}
}
