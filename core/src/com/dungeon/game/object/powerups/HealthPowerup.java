package com.dungeon.game.object.powerups;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.dungeon.engine.animation.GameAnimation;
import com.dungeon.engine.entity.Entity;
import com.dungeon.engine.entity.Mutators;
import com.dungeon.engine.entity.Particle;
import com.dungeon.engine.entity.PlayerCharacter;
import com.dungeon.engine.physics.Body;
import com.dungeon.engine.random.Rand;
import com.dungeon.engine.render.Light;
import com.dungeon.engine.resource.ResourceManager;
import com.dungeon.game.level.entity.EntityFactory;
import com.dungeon.game.state.GameState;
import com.dungeon.game.tileset.FillSheet;

import java.util.ArrayList;

public class HealthPowerup extends Entity<Entity> {

	private static final Vector2 BOUNDING_BOX = new Vector2(10, 10);
	private static final Vector2 DRAW_OFFSET = new Vector2(10, 10);

	public static class Factory implements EntityFactory.EntityTypeFactory {

		final GameState state;
		final Light light;
		final Light vanishLight;
		final Animation<TextureRegion> animation;
		final Animation<TextureRegion> specAnimation;
		final Particle.Builder spec;
		final Particle.Builder vanish;

		public Factory(GameState state) {
			this.state = state;
			light = new Light(192, new Color(1, 0.1f, 0.2f, 1), Light.RAYS_TEXTURE, Light::oscillating, Light::rotateFast);
			animation = ResourceManager.instance().getAnimation(PowerupsSheet.HEALTH, PowerupsSheet::health);
			specAnimation = ResourceManager.instance().getAnimation(FillSheet.FILL, FillSheet::fill);
			spec = new Particle.Builder()
					.zSpeed(50)
					.timeToLive(1f)
					.color(Color.RED)
					.mutate(Mutators.fadeOut(0.8f))
					.mutate(Mutators.zAccel(100))
					.mutate(Mutators.hOscillate(10, 5f));
			vanishLight = new Light(192, new Color(1, 0.1f, 0.2f, 1), Light.RAYS_TEXTURE, Light::torchlight, Light::rotateFast);
			vanish = new Particle.Builder()
					.timeToLive(1f)
					.light(vanishLight)
					.mutate(Mutators.fadeOut(0.5f))
					.mutate(Mutators.fadeOutLight())
					.mutate(Mutators.zAccel(10));
		}

		@Override
		public HealthPowerup build(Vector2 origin) {
			return new HealthPowerup(this, origin);
		}
	}

	private final Factory factory;
	private float nextSpawn = 0;

	private HealthPowerup(Factory factory, Vector2 position) {
		super(new Body(position, BOUNDING_BOX), DRAW_OFFSET);
		setCurrentAnimation(new GameAnimation(factory.animation, factory.state.getStateTime()));
		light = factory.light;
		this.factory = factory;
		// TODO Please clean this crap up...
		z = 4;
		mutator = new ArrayList<>();
		mutator.add(Mutators.zOscillate(3, 8f).get(this));
	}

	@Override
	public void think(GameState state) {
		if (nextSpawn <= state.getStateTime()) {
			nextSpawn = state.getStateTime() + 0.05f;
			Spec spec = new Spec(factory, getPos(), state.getStateTime());
			spec.getPos().x += Rand.between(-8, 8);
			spec.setZPos(Rand.between(2, 10));
			spec.impulse(Rand.between(-10, 10), 0);
			state.addEntity(spec);
		}
		super.think(state);
	}

	@Override
	public boolean isExpired(float time) {
		return expired;
	}

	@Override
	public boolean isSolid() {
		return false;
	}

	@Override
	protected boolean onEntityCollision(GameState state, Entity entity) {
		if (entity instanceof PlayerCharacter) {
			PlayerCharacter character = (PlayerCharacter) entity;
			character.heal(25);
			expire(state);
			return true;
		} else {
			return false;
		}
	}

	private void expire(GameState state) {
		if (!expired) {
			Vanish vanish = new Vanish(factory, getPos(), state.getStateTime());
			vanish.setZPos(z);
			state.addEntity(vanish);
			for (int i = 0; i < 50; ++i) {
				Spec spec = new Spec(factory, getPos(), state.getStateTime());
				spec.getPos().x += Rand.between(-8, 8);
				spec.setZPos(Rand.between(2, 10));
				spec.impulse(Rand.between(-100, 100), Rand.between(-100, 100));
				state.addEntity(spec);
			}
			expired = true;
		}
	}

	private static class Spec extends Particle {
		private static final Vector2 BOUNDING_BOX = new Vector2(1, 1);
		private static final Vector2 DRAW_OFFSET = new Vector2(1, 1);

		public Spec(HealthPowerup.Factory factory, Vector2 origin, float startTime) {
			super(new Body(origin, BOUNDING_BOX), DRAW_OFFSET, startTime, factory.spec);
			setCurrentAnimation(new GameAnimation(factory.specAnimation, startTime));
		}
	}

	private static class Vanish extends Particle {

		public Vanish(HealthPowerup.Factory factory, Vector2 origin, float startTime) {
			super(new Body(origin, BOUNDING_BOX), DRAW_OFFSET, startTime, factory.vanish);
			setCurrentAnimation(new GameAnimation(factory.animation, startTime));
		}
	}

}
