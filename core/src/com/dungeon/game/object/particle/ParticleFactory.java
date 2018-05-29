package com.dungeon.game.object.particle;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.dungeon.engine.entity.Entity;
import com.dungeon.engine.entity.EntityPrototype;
import com.dungeon.engine.entity.Traits;
import com.dungeon.engine.resource.ResourceManager;
import com.dungeon.engine.util.Rand;
import com.dungeon.game.state.GameState;

import java.util.Arrays;
import java.util.List;

public class ParticleFactory {

	private static final Vector2 BOUNDING_BOX = new Vector2(1, 1);
	private static final Vector2 DRAW_OFFSET = new Vector2(4, 4);

	private final EntityPrototype woodParticlePrototype;
	private final EntityPrototype stoneParticlePrototype;
	private final EntityPrototype dropletStartPrototype;
	private final EntityPrototype dropletFallPrototype;
	private final EntityPrototype dropletEndPrototype;
	private final EntityPrototype fireballPrototype;
	private final EntityPrototype flamePrototype;
	private final EntityPrototype candlePrototype;

	public ParticleFactory() {
		List<Animation<TextureRegion>> woodParticleAnimations = Arrays.asList(
				ResourceManager.getAnimation(ParticleSheet.WOOD_PARTICLE_1, ParticleSheet::wood1),
				ResourceManager.getAnimation(ParticleSheet.WOOD_PARTICLE_2, ParticleSheet::wood2),
				ResourceManager.getAnimation(ParticleSheet.WOOD_PARTICLE_3, ParticleSheet::wood3),
				ResourceManager.getAnimation(ParticleSheet.WOOD_PARTICLE_4, ParticleSheet::wood4),
				ResourceManager.getAnimation(ParticleSheet.WOOD_PARTICLE_5, ParticleSheet::wood5),
				ResourceManager.getAnimation(ParticleSheet.WOOD_PARTICLE_6, ParticleSheet::wood6),
				ResourceManager.getAnimation(ParticleSheet.WOOD_PARTICLE_7, ParticleSheet::wood7),
				ResourceManager.getAnimation(ParticleSheet.WOOD_PARTICLE_8, ParticleSheet::wood8)
		);
		List<Animation<TextureRegion>> stoneParticleAnimations = Arrays.asList(
				ResourceManager.getAnimation(ParticleSheet.STONE_PARTICLE_1, ParticleSheet::stone1),
				ResourceManager.getAnimation(ParticleSheet.STONE_PARTICLE_2, ParticleSheet::stone2),
				ResourceManager.getAnimation(ParticleSheet.STONE_PARTICLE_3, ParticleSheet::stone3),
				ResourceManager.getAnimation(ParticleSheet.STONE_PARTICLE_4, ParticleSheet::stone4),
				ResourceManager.getAnimation(ParticleSheet.STONE_PARTICLE_5, ParticleSheet::stone5),
				ResourceManager.getAnimation(ParticleSheet.STONE_PARTICLE_6, ParticleSheet::stone6),
				ResourceManager.getAnimation(ParticleSheet.STONE_PARTICLE_7, ParticleSheet::stone7),
				ResourceManager.getAnimation(ParticleSheet.STONE_PARTICLE_8, ParticleSheet::stone8)
		);
		Animation<TextureRegion> dropletStartAnimation = ResourceManager.getAnimation(ParticleSheet.DROPLET_START, ParticleSheet::dropletStart);
		Animation<TextureRegion> dropletFallAnimation = ResourceManager.getAnimation(ParticleSheet.DROPLET_FALL, ParticleSheet::dropletFall);
		Animation<TextureRegion> dropletEndAnimation = ResourceManager.getAnimation(ParticleSheet.DROPLET_END, ParticleSheet::dropletEnd);
		Animation<TextureRegion> fireballAnimation = ResourceManager.getAnimation(ParticleSheet.FIREBALL, ParticleSheet::fireball);
		Animation<TextureRegion> flameAnimation = ResourceManager.getAnimation(ParticleSheet.FLAME, ParticleSheet::flame);
		Animation<TextureRegion> candleAnimation = ResourceManager.getAnimation(ParticleSheet.CANDLE, ParticleSheet::candle);

		woodParticlePrototype = new EntityPrototype()
				.animation(() -> Rand.pick(woodParticleAnimations))
				.boundingBox(BOUNDING_BOX)
				.drawOffset(DRAW_OFFSET)
				.timeToLive(3f)
				.with(Traits.fadeOut(1f))
				.with(Traits.zAccel(-200))
				.zSpeed(() -> Rand.between(50f, 100f))
				.bounciness(0.8f);
		stoneParticlePrototype = new EntityPrototype()
				.animation(() -> Rand.pick(stoneParticleAnimations))
				.boundingBox(BOUNDING_BOX)
				.drawOffset(DRAW_OFFSET)
				.timeToLive(3f)
				.with(Traits.fadeOut(1f))
				.with(Traits.zAccel(-200))
				.zSpeed(() -> Rand.between(50f, 100f))
				.bounciness(0.8f);
		dropletStartPrototype = new EntityPrototype()
				.animation(dropletStartAnimation)
				.boundingBox(BOUNDING_BOX)
				.drawOffset(DRAW_OFFSET)
				.timeToLive(dropletStartAnimation.getAnimationDuration());
		dropletFallPrototype = new EntityPrototype()
				.animation(dropletFallAnimation)
				.boundingBox(BOUNDING_BOX)
				.drawOffset(DRAW_OFFSET)
				.timeToLive(3f)
				.with(Traits.zAccel(-200));
		dropletEndPrototype = new EntityPrototype()
				.animation(dropletEndAnimation)
				.boundingBox(BOUNDING_BOX)
				.drawOffset(DRAW_OFFSET)
				.timeToLive(dropletEndAnimation.getAnimationDuration());
		fireballPrototype = new EntityPrototype()
				.animation(fireballAnimation)
				.boundingBox(BOUNDING_BOX)
				.drawOffset(DRAW_OFFSET)
				.timeToLive(1f)
				.with(Traits.fadeOut(1f))
				.zSpeed(10f);
		flamePrototype = new EntityPrototype()
				.animation(flameAnimation)
				.boundingBox(BOUNDING_BOX)
				.drawOffset(DRAW_OFFSET);
		candlePrototype = new EntityPrototype()
				.animation(candleAnimation)
				.boundingBox(BOUNDING_BOX)
				.drawOffset(DRAW_OFFSET);
	}

	public Entity buildWoodParticle(Vector2 origin) {
		Entity particle = new Entity(origin, woodParticlePrototype) {
			@Override public void onGroundRest() {
				stop();
			}
		};
		particle.impulse(Rand.between(-50, 50), Rand.between(-20, 20));
		return particle;
	}

	public Entity buildStoneParticle(Vector2 origin) {
		Entity particle = new Entity(origin, stoneParticlePrototype) {
			@Override public void onGroundRest() {
				stop();
			}
		};
		particle.impulse(Rand.between(-50, 50), Rand.between(-20, 20));
		return particle;
	}

	public Entity buildDroplet(Vector2 origin) {
		return new Entity(origin, dropletStartPrototype) {
			@Override public void onExpire() {
				Entity fall = new Entity(getPos(), dropletFallPrototype) {
					@Override public void onGroundRest() {
						expire();
					}
					@Override public void onExpire() {
						GameState.addEntity(new Entity(getPos(), dropletEndPrototype));
					}
				};
				GameState.addEntity(fall);
			}
		};
	}

	public Entity buildFireball(Vector2 origin) {
		return new Entity(origin, fireballPrototype);
	}

	public Entity buildFlame(Vector2 origin) {
		return new Entity(origin, flamePrototype);
	}

	public Entity buildCandle(Vector2 origin) {
		return new Entity(origin, candlePrototype);
	}

}
