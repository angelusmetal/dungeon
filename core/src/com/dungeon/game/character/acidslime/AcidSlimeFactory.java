package com.dungeon.game.character.acidslime;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.dungeon.engine.entity.Traits;
import com.dungeon.engine.entity.Entity;
import com.dungeon.engine.entity.EntityPrototype;
import com.dungeon.engine.util.Rand;
import com.dungeon.engine.render.Light;
import com.dungeon.engine.resource.ResourceManager;
import com.dungeon.game.character.slime.SlimeBlobsSheet;
import com.dungeon.game.level.entity.EntityFactory;
import com.dungeon.game.state.GameState;

public class AcidSlimeFactory implements EntityFactory.EntityTypeFactory {

	final Animation<TextureRegion> idleAnimation;
	final Animation<TextureRegion> attackAnimation;
	final Animation<TextureRegion> dieAnimation;
	final Animation<TextureRegion> poolFloodAnimation;
	final Animation<TextureRegion> poolDryAnimation;
	final Animation<TextureRegion> blobAnimation;
	final Animation<TextureRegion> splatAnimation;

	final EntityPrototype character;
	final EntityPrototype death;
	final EntityPrototype pool;
	final EntityPrototype blob;
	final EntityPrototype splat;

	public AcidSlimeFactory() {
		// Character animations
		idleAnimation = ResourceManager.instance().getAnimation(AcidSlimeSheet.IDLE, AcidSlimeSheet::idle);
		attackAnimation = ResourceManager.instance().getAnimation(AcidSlimeSheet.ATTACK, AcidSlimeSheet::attack);
		dieAnimation = ResourceManager.instance().getAnimation(AcidSlimeSheet.DIE, AcidSlimeSheet::die);
		// Pool animations
		poolFloodAnimation = ResourceManager.instance().getAnimation(SlimeBlobsSheet.POOL_FLOOD, SlimeBlobsSheet::poolFlood);
		poolDryAnimation = ResourceManager.instance().getAnimation(SlimeBlobsSheet.POOL_DRY, SlimeBlobsSheet::poolDry);
		// Blob animations
		blobAnimation = ResourceManager.instance().getAnimation(SlimeBlobsSheet.BLOB, SlimeBlobsSheet::blob);
		splatAnimation = ResourceManager.instance().getAnimation(SlimeBlobsSheet.SPLAT, SlimeBlobsSheet::splat);

		Color color = new Color(1, 1, 1, 0.5f);
		Color lightColor = new Color(0, 1, 0, 0.5f);
		Color blobLightColor = new Color(0, 1, 0, 0.2f);
		Color blobColor = new Color(0.25f, 0.75f, 0.2f, 0.5f);

		Light characterLight = new Light(100, lightColor, Light.RAYS_TEXTURE, () -> 1f, Light::rotateMedium);
		Light poolLight = new Light(100, lightColor, Light.NORMAL_TEXTURE, () -> 1f, Light::noRotate);
		Light blobLight = new Light(30, blobLightColor, Light.NORMAL_TEXTURE, () -> 1f, Light::noRotate);

		Vector2 characterBoundingBox = new Vector2(22, 12);
		Vector2 characterDrawOffset = new Vector2(16, 11);
		Vector2 poolBoundingBox = new Vector2(22, 12);
		Vector2 poolDrawOffset = new Vector2(16, 3);
		Vector2 blobBouncingBox = new Vector2(6, 6);
		Vector2 blobDrawOffset = new Vector2(8, 8);

		character = new EntityPrototype()
				.boundingBox(characterBoundingBox)
				.drawOffset(characterDrawOffset)
				.color(color)
				.light(characterLight);
		death = new EntityPrototype()
				.animation(dieAnimation)
				.boundingBox(characterBoundingBox)
				.drawOffset(characterDrawOffset)
				.timeToLive(dieAnimation.getAnimationDuration())
				.color(color)
				.light(characterLight);
		pool = new EntityPrototype()
				.animation(poolFloodAnimation)
				.boundingBox(poolBoundingBox)
				.drawOffset(poolDrawOffset)
				.color(blobColor)
				.light(poolLight)
				.timeToLive(5f)
				.with(Traits.fadeOutLight())
				.zIndex(-1);
		blob = new EntityPrototype()
				.animation(blobAnimation)
				.boundingBox(blobBouncingBox)
				.drawOffset(blobDrawOffset)
				.color(blobColor)
				.light(blobLight)
				.speed(50)
				.zSpeed(() -> Rand.between(50f, 100f))
				.with(Traits.zAccel(-200))
				.timeToLive(10);
		splat = new EntityPrototype()
				.animation(splatAnimation)
				.boundingBox(blobBouncingBox)
				.drawOffset(blobDrawOffset)
				.color(blobColor)
				.light(blobLight)
				.timeToLive(splatAnimation.getAnimationDuration());

	}

	@Override
	public Entity build(Vector2 origin) {
		return new AcidSlime(origin, this);
	}

	public Entity createBlob(Vector2 origin) {
		Entity entity = new Entity(origin, blob) {
			@Override
			protected void onExpire() {
				GameState.addEntity(new Entity(getPos(), splat));
			}
			@Override
			protected void onGroundRest() {
				expire();
			}
		};
		entity.setZPos(8);
		entity.impulse(Rand.between(-50f, 50f), Rand.between(-10f, 10f));
		return entity;
	}

}
