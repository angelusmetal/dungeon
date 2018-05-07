package com.dungeon.game.character.slime;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.dungeon.engine.entity.Entity;
import com.dungeon.engine.entity.EntityPrototype;
import com.dungeon.engine.entity.Trait;
import com.dungeon.engine.entity.Traits;
import com.dungeon.engine.render.ColorContext;
import com.dungeon.engine.render.DrawContext;
import com.dungeon.engine.render.Light;
import com.dungeon.engine.resource.ResourceManager;
import com.dungeon.engine.util.Rand;
import com.dungeon.game.level.entity.EntityFactory;
import com.dungeon.game.state.GameState;

import java.util.function.Supplier;

public class SlimeFactory implements EntityFactory.EntityTypeFactory {

	final Animation<TextureRegion> idleAnimation;
	final Animation<TextureRegion> attackAnimation;
	final Animation<TextureRegion> dieAnimation;
	final Animation<TextureRegion> blobAnimation;
	final Animation<TextureRegion> splatAnimation;

	final EntityPrototype character;
	final EntityPrototype death;
	final EntityPrototype blob;
	final EntityPrototype splat;

	public SlimeFactory() {
		// Character animations
		idleAnimation = ResourceManager.instance().getAnimation(SlimeSheet.IDLE, SlimeSheet::idle);
		attackAnimation = ResourceManager.instance().getAnimation(SlimeSheet.ATTACK, SlimeSheet::attack);
		dieAnimation = ResourceManager.instance().getAnimation(SlimeSheet.DIE, SlimeSheet::die);
		// Blob animations
		blobAnimation = ResourceManager.instance().getAnimation(SlimeBlobsSheet.BLOB, SlimeBlobsSheet::blob);
		splatAnimation = ResourceManager.instance().getAnimation(SlimeBlobsSheet.SPLAT, SlimeBlobsSheet::splat);

		Supplier<Color> color = () -> new Color(
				Rand.between(0.5f, 1f),
				Rand.between(0.5f, 1f),
				Rand.between(0.5f, 1f),
				0.5f);

		Color lightColor = new Color(1, 1, 1, 0.5f);

		Light characterLight = new Light(50, lightColor, Light.NORMAL_TEXTURE, () -> 1f, Light::noRotate);
		Light deathLight = new Light(100, lightColor, Light.NORMAL_TEXTURE, () -> 1f, Light::noRotate);

		Vector2 characterBoundingBox = new Vector2(22, 12);
		Vector2 characterDrawOffset = new Vector2(16, 11);
		Vector2 blobBouncingBox = new Vector2(6, 6);
		Vector2 blobDrawOffset = new Vector2(8, 8);

		character = new EntityPrototype()
				.boundingBox(characterBoundingBox)
				.drawOffset(characterDrawOffset)
				.color(color)
				.light(characterLight)
				.speed(100f)
				.zSpeed(0)
				.friction(1);
		death = new EntityPrototype()
				.animation(dieAnimation)
				.boundingBox(characterBoundingBox)
				.drawOffset(characterDrawOffset)
				.color(new Color(1, 1, 1, 0.5f))
				.light(deathLight)
				.with(Traits.fadeOutLight())
				.timeToLive(dieAnimation.getAnimationDuration() + 1f);
		blob = new EntityPrototype()
				.animation(blobAnimation)
				.boundingBox(blobBouncingBox)
				.drawOffset(blobDrawOffset)
				.speed(50)
				.zSpeed(() -> Rand.between(50f, 100f))
				.with(Traits.zAccel(-200))
				.timeToLive(10);
		splat = new EntityPrototype()
				.animation(splatAnimation)
				.boundingBox(blobBouncingBox)
				.drawOffset(blobDrawOffset)
				.timeToLive(splatAnimation.getAnimationDuration());
	}

	@Override
	public Entity build(Vector2 origin) {
		Slime slime = new Slime(origin, this);
		slime.getLight().color.set(slime.getColor());
		return slime;
	}

	Entity createDeath(Entity dying) {
		Entity entity = new Entity(dying.getPos(), death);
		entity.setZPos(dying.getZPos());
		entity.setColor(dying.getColor());
		entity.getLight().color.set(dying.getColor());
		return entity;
	}

	Entity createBlob(Entity dying) {
		Entity entity = new Entity(dying.getPos(), blob) {
			@Override
			protected void onExpire() {
				Entity splatEntity = new Entity(getPos(), splat);
				splatEntity.setColor(dying.getColor());
				GameState.addEntity(splatEntity);
			}
			@Override
			protected void onGroundRest() {
				expire();
			}
		};
		entity.setZPos(8);
		entity.impulse(Rand.between(-50f, 50f), Rand.between(-10f, 10f));
		entity.setColor(dying.getColor());
		return entity;
	}
}
