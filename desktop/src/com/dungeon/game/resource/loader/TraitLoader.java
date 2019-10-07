package com.dungeon.game.resource.loader;

import com.badlogic.gdx.math.Vector2;
import com.dungeon.engine.Engine;
import com.dungeon.engine.entity.Entity;
import com.dungeon.engine.util.Metronome;
import com.dungeon.engine.entity.TraitSupplier;
import com.dungeon.engine.entity.Traits;
import com.dungeon.engine.resource.LoadingException;
import com.dungeon.engine.util.ConfigUtil;
import com.dungeon.game.Game;
import com.dungeon.game.entity.DungeonEntity;
import com.dungeon.game.entity.PlayerEntity;
import com.typesafe.config.Config;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class TraitLoader {

	public static <T extends Entity> TraitSupplier<T> load(Config config) {
		String type = ConfigUtil.requireString(config, "do");
		if (type.equals("expire")) {
			return e -> Entity::expire;
		} else if (type.equals("stop")) {
			return e -> Entity::stop;
		} else if (type.equals("rotate")) {
			return rotate(config);
		} else if (type.equals("rotateRandom")) {
			return rotateRandom(config);
		} else if (type.equals("generate")) {
			return generate(config);
		} else if (type.equals("generateLoot")) {
			return DungeonEntity.generateLoot();
		} else if (type.equals("xInvert")){
			return xInvert(config);
		} else if (type.equals("deathClone")){
			return deathClone(config);
		} else if (type.equals("fadeIn")){
			return fadeIn(config);
		} else {
			throw new LoadingException("Unknown type " + type);
		}
	}

	private static <T extends Entity> TraitSupplier<T> fadeIn(Config config) {
		float alpha = ConfigUtil.getFloat(config, "alpha").orElse(1f);
		Optional<Float> duration = ConfigUtil.getFloat(config, "duration");
		return duration.<TraitSupplier<T>>map(aFloat -> Traits.fadeIn(alpha, aFloat)).orElseGet(() -> Traits.fadeIn(alpha));
	}

	private static <T extends Entity> TraitSupplier<T> rotate(Config config) {
		float speed = ConfigUtil.requireFloat(config, "speed");
		return Traits.rotateFixed(speed);
	}

	private static <T extends Entity> TraitSupplier<T> rotateRandom(Config config) {
		Supplier<Integer> speed = ConfigUtil.requireIntegerRange(config, "speed");
		return Traits.rotateRandom(speed);
	}

	private static <T extends Entity> TraitSupplier<T> xInvert(Config config) {
		String vector = ConfigUtil.requireString(config, "vector");
		if (vector.equals("movement")) {
			return Traits.xInvertByVector(Entity::getMovement);
//		} else if (vector.equals("aimAndImpulse")) {
//			return Traits.xInvertByVector(
//					e -> e instanceof PlayerEntity ? ((PlayerEntity) e).getAim() : e.getMovement(),
//					Entity::getMovement);
		} else {
			throw new LoadingException("Invalid vector type " + vector);
		}
	}

	private static <T extends Entity> TraitSupplier<T> deathClone(Config config) {
		float timeToLive = ConfigUtil.requireFloat(config, "timeToLive");
		return Traits.deathClone(timeToLive);
	}

	private static <T extends Entity> TraitSupplier<T> generate(Config config) {
		Optional<Supplier<Float>> frequency = ConfigUtil.getFloatRange(config, "frequency");
		boolean strongUpdate = ConfigUtil.getBoolean(config, "strongUpdate").orElse(false);
		Supplier<Integer> count = ConfigUtil.getIntegerRange(config, "count").orElse(() -> 1);

		// The particle factory generates a single particle
		Function<Entity, Entity> factory = getParticleGenerator(config);
		Consumer<T> generator;

		generator = e -> {
			int amount = count.get();
			for (int i = 0; i < amount; ++i) {
				Engine.entities.add(factory.apply(e));
			}
		};

		// If frequency is specified, wrap in a timer object
		if (frequency.isPresent()) {
			Supplier<Float> freq = frequency.get();
			if (strongUpdate) {
				return e -> {
					Metronome metronome = new Metronome(freq.get(), () -> generator.accept(e));
					return entity -> metronome.doAtInterval();
				};
			} else {
				return e -> {
					Metronome metronome = new Metronome(freq.get(), () -> {
						// Unless strongUpdate is set, generator only applies within player viewport
						if (e instanceof DungeonEntity && ((DungeonEntity) e).inPlayerViewport()) {
							generator.accept(e);
						}
					});
					return entity -> metronome.doAtInterval();
				};
			}
		} else {
			// TODO Apply strongUpdate here as well
			return e -> generator::accept;
		}
	}

	private static Function<Entity, Entity> getParticleGenerator(Config config) {
		String prototype = ConfigUtil.getString(config, "prototype").orElseThrow(() -> new RuntimeException("Missing prototype parameter"));
		Optional<Supplier<Integer>> x = ConfigUtil.getIntegerRange(config, "x");
		Optional<Supplier<Integer>> y = ConfigUtil.getIntegerRange(config, "y");
		Optional<Supplier<Integer>> z = ConfigUtil.getIntegerRange(config, "z");
		Optional<Supplier<Integer>> impulseX = ConfigUtil.getIntegerRange(config, "impulseX");
		Optional<Supplier<Integer>> impulseY = ConfigUtil.getIntegerRange(config, "impulseY");
		Optional<Supplier<Integer>> impulseZ = ConfigUtil.getIntegerRange(config, "impulseZ");
		Optional<Vector2> impulseAngle = ConfigUtil.getVector2(config, "impulseAngle");
		boolean inheritColor = ConfigUtil.getBoolean(config, "inheritColor").orElse(false);
		String rotation = ConfigUtil.getString(config, "rotation").orElse("none");
		// List of initialization steps (like offset & impulse)
		List<Consumer<Entity>> traits = new ArrayList<>();
		x.ifPresent(v -> traits.add(particle -> particle.getOrigin().x += v.get()));
		y.ifPresent(v -> traits.add(particle -> particle.getOrigin().y += v.get()));
		z.ifPresent(v -> traits.add(particle -> particle.setZPos(particle.getZPos() + v.get())));
		if (impulseX.isPresent() || impulseY.isPresent()) {
			Supplier<Integer> xSupplier = impulseX.orElse(() -> 0);
			Supplier<Integer> ySupplier = impulseY.orElse(() -> 0);
			traits.add(particle -> particle.impulse(xSupplier.get(), ySupplier.get()));
		}
		impulseZ.ifPresent(v -> traits.add(particle -> particle.setZSpeed(particle.getZSpeed() + v.get())));
		if (rotation.equals("movement")) {
			traits.add(particle -> particle.setRotation(particle.getMovement().angle()));
		}
		return generator -> {
			Entity particle = Game.build(prototype, generator.getOrigin());
			// Inherit generator properties
			particle.getDrawScale().set(generator.getDrawScale());
			if (inheritColor) {
				particle.setColor(generator.getColor());
				if (particle.getLight() != null) {
					particle.getLight().color.set(generator.getColor());
				}
				// TODO This probably doesn't belong here
				particle.setZPos(generator.getZPos());
			}
			// Apply angle
			impulseAngle.ifPresent(v -> {
				particle.impulse(generator.getMovement().cpy().rotate(v.x).setLength(v.y));
			});
			// Run initialization steps
			traits.forEach(trait -> trait.accept(particle));
			return particle;
		};
	}


}
