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

	public static final String WOOD_PARTICLE_1 = "wood_particle_1";
	public static final String WOOD_PARTICLE_2 = "wood_particle_2";
	public static final String WOOD_PARTICLE_3 = "wood_particle_3";
	public static final String WOOD_PARTICLE_4 = "wood_particle_4";
	public static final String WOOD_PARTICLE_5 = "wood_particle_5";
	public static final String WOOD_PARTICLE_6 = "wood_particle_6";
	public static final String WOOD_PARTICLE_7 = "wood_particle_7";
	public static final String WOOD_PARTICLE_8 = "wood_particle_8";
	public static final String STONE_PARTICLE_1 = "stone_particle_1";
	public static final String STONE_PARTICLE_2 = "stone_particle_2";
	public static final String STONE_PARTICLE_3 = "stone_particle_3";
	public static final String STONE_PARTICLE_4 = "stone_particle_4";
	public static final String STONE_PARTICLE_5 = "stone_particle_5";
	public static final String STONE_PARTICLE_6 = "stone_particle_6";
	public static final String STONE_PARTICLE_7 = "stone_particle_7";
	public static final String STONE_PARTICLE_8 = "stone_particle_8";
	public static final String DROPLET_START = "droplet_start";
	public static final String DROPLET_FALL = "droplet_fall";
	public static final String DROPLET_END = "droplet_end";
	public static final String FIREBALL = "fireball";
	public static final String FLAME = "flame";
	public static final String CANDLE = "candle";

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
				ResourceManager.getAnimation(WOOD_PARTICLE_1),
				ResourceManager.getAnimation(WOOD_PARTICLE_2),
				ResourceManager.getAnimation(WOOD_PARTICLE_3),
				ResourceManager.getAnimation(WOOD_PARTICLE_4),
				ResourceManager.getAnimation(WOOD_PARTICLE_5),
				ResourceManager.getAnimation(WOOD_PARTICLE_6),
				ResourceManager.getAnimation(WOOD_PARTICLE_7),
				ResourceManager.getAnimation(WOOD_PARTICLE_8)
		);
		List<Animation<TextureRegion>> stoneParticleAnimations = Arrays.asList(
				ResourceManager.getAnimation(STONE_PARTICLE_1),
				ResourceManager.getAnimation(STONE_PARTICLE_2),
				ResourceManager.getAnimation(STONE_PARTICLE_3),
				ResourceManager.getAnimation(STONE_PARTICLE_4),
				ResourceManager.getAnimation(STONE_PARTICLE_5),
				ResourceManager.getAnimation(STONE_PARTICLE_6),
				ResourceManager.getAnimation(STONE_PARTICLE_7),
				ResourceManager.getAnimation(STONE_PARTICLE_8)
		);
		Animation<TextureRegion> dropletStartAnimation = ResourceManager.getAnimation(DROPLET_START);
		Animation<TextureRegion> dropletFallAnimation = ResourceManager.getAnimation(DROPLET_FALL);
		Animation<TextureRegion> dropletEndAnimation = ResourceManager.getAnimation(DROPLET_END);
		Animation<TextureRegion> fireballAnimation = ResourceManager.getAnimation(FIREBALL);
		Animation<TextureRegion> flameAnimation = ResourceManager.getAnimation(FLAME);
		Animation<TextureRegion> candleAnimation = ResourceManager.getAnimation(CANDLE);

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