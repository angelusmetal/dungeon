package com.dungeon.game.character.fireslime;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.dungeon.engine.entity.Traits;
import com.dungeon.engine.entity.Entity;
import com.dungeon.engine.entity.EntityPrototype;
import com.dungeon.engine.entity.PlayerCharacter;
import com.dungeon.engine.render.Light;
import com.dungeon.engine.resource.ResourceManager;
import com.dungeon.game.level.entity.EntityFactory;
import com.dungeon.game.state.GameState;

public class FireSlimeFactory implements EntityFactory.EntityTypeFactory {

	final Animation<TextureRegion> idleAnimation;
	final Animation<TextureRegion> projectileAnimation;
	final Animation<TextureRegion> explosionAnimation;

	final EntityPrototype character;
	final EntityPrototype bullet;
	final EntityPrototype bulletExplosion;
	final EntityPrototype bulletTrail;

	final Light characterLight;
	final Light bulletLight;
	final Light bulletTrailLight;

	public FireSlimeFactory() {
		// Character animations
		idleAnimation = ResourceManager.instance().getAnimation(FireSlimeSheet.IDLE, FireSlimeSheet::idle);
		projectileAnimation = ResourceManager.instance().getAnimation(FireSlimeSheet.PROJECTILE, FireSlimeSheet::projectile);
		explosionAnimation = ResourceManager.instance().getAnimation(FireSlimeSheet.EXPLOSION, FireSlimeSheet::explosion);

		characterLight = new Light(100, new Color(1, 0.5f, 0, 0.8f), Light.NORMAL_TEXTURE, Light::torchlight, Light::noRotate);
		bulletLight = new Light(50, new Color(1, 0.5f, 0, 0.5f), Light.NORMAL_TEXTURE, Light::torchlight, Light::noRotate);
		bulletTrailLight = new Light(20, new Color(1, 0.5f, 0, 0.5f), Light.NORMAL_TEXTURE, Light::torchlight, Light::noRotate);

		Vector2 characterBoundingBox = new Vector2(22, 12);
		Vector2 characterDrawOffset = new Vector2(16, 11);

		Vector2 bulletBoundingBox = new Vector2(6, 6);
		Vector2 bulletDrawOffset = new Vector2(4, 4);

		character = new EntityPrototype()
				.animation(idleAnimation)
				.boundingBox(characterBoundingBox)
				.drawOffset(characterDrawOffset)
				.color(new Color(1, 1, 1, 0.8f))
				.light(characterLight)
				.speed(20f);
		bullet = new EntityPrototype()
				.animation(projectileAnimation)
				.boundingBox(bulletBoundingBox)
				.drawOffset(bulletDrawOffset)
				.light(bulletLight)
				.speed(100)
				.timeToLive(10)
				.targetPredicate(PlayerCharacter.IS_PLAYER)
				.damage(() -> 5f * (GameState.getPlayerCount() + GameState.getLevelCount()))
				.with(Traits.generator(0.1f, this::createBulletTrail));
		bulletExplosion = new EntityPrototype()
				.animation(explosionAnimation)
				.light(bulletLight)
				.timeToLive(explosionAnimation.getAnimationDuration());
		bulletTrail = new EntityPrototype()
				.animation(explosionAnimation)
				.light(bulletTrailLight)
				.timeToLive(explosionAnimation.getAnimationDuration())
				.with(Traits.fadeOut(1f))
				.zSpeed(10);
	}

	@Override
	public Entity build(Vector2 origin) {
		return new FireSlime(origin, this);
	}

	private Entity createBulletTrail(Entity entity) {
		return new Entity(entity.getPos(), bulletTrail);
	}

}
