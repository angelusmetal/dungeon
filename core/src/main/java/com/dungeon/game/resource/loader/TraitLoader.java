package com.dungeon.game.resource.loader;

import com.badlogic.gdx.math.Vector2;
import com.dungeon.engine.entity.Entity;
import com.dungeon.engine.entity.EntityPrototype;
import com.dungeon.engine.entity.Timer;
import com.dungeon.engine.entity.TraitSupplier;
import com.dungeon.engine.entity.Traits;
import com.dungeon.engine.resource.LoadingException;
import com.dungeon.engine.util.ConfigUtil;
import com.dungeon.engine.util.Rand;
import com.dungeon.game.resource.Resources;
import com.dungeon.game.state.GameState;
import com.typesafe.config.Config;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

public class TraitLoader {

	public static <T extends Entity> TraitSupplier<T> load(Config config) {
		String type = ConfigUtil.requireString(config, "do");
		if (type.equals("generate")) {
			return generate(config);
		} else {
			throw new LoadingException("Unknown type " + type);
		}
	}

	private static <T extends Entity> TraitSupplier<T> generate(Config config) {
		Optional<Float> frequency = ConfigUtil.getFloat(config, "frequency");
		Vector2 count = ConfigUtil.getVector2(config, "count").orElse(new Vector2(1, 1));
		int minCount = (int) count.x;
		int maxCount = (int) count.y;

		// The particle factory generates a single particle
		Function<Vector2, Entity> factory = getParticleGenerator(config);
		Consumer<T> generator;
		// Generate a random amount of particles
		if (minCount != maxCount) {
			generator = e -> {
				int amount = Rand.between(minCount, maxCount);
				for (int i = 1; i < amount; ++i) {
					GameState.entities.add(factory.apply(e.getOrigin()));
				}
			};
		} else {
			if (minCount == 1) {
				// Generate a single particle
				generator = e -> GameState.entities.add(factory.apply(e.getOrigin()));
			} else {
				// Generate a fixed amount of particles
				generator = e -> {
					for (int i = 1; i < minCount; ++i) {
						GameState.entities.add(factory.apply(e.getOrigin()));
					}
				};
			}
		}

		// If frequency is specified, wrap in a timer object
		if (frequency.isPresent()) {
			Float freq = frequency.get();
			return e -> {
				Timer timer = new Timer(freq);
				return entity -> timer.doAtInterval(() -> generator.accept(entity));
			};
		} else {
			return e -> generator::accept;
		}
	}

	/**
	 * A generator that continuously generates particles, in the specified frequency
	 */
	private static <T extends Entity> Optional<TraitSupplier<T>> getContinuousGenerator(Config config, String key) {
		if (config.hasPath(key)) {
			Config table = config.getConfig(key);
			float frequency = ConfigUtil.getFloat(table, "frequency").orElseThrow(() -> new RuntimeException("Missing frequency parameter"));
			Function<Vector2, Entity> generator = getParticleGenerator(table);
			return Optional.of(Traits.generator(frequency, gen -> generator.apply(gen.getOrigin())));
		}
		return Optional.empty();
	}
	/**
	 * A generator that generates particles once, with the (optionally) specified particle count
	 */
	private static <T extends Entity> Optional<TraitSupplier<T>> getInstantGenerator(Config config) {
		Vector2 count = ConfigUtil.getVector2(config, "count").orElse(new Vector2(1, 1));
		Function<Vector2, Entity> generator = getParticleGenerator(config);
		return Optional.of(Traits.generatorMultiple((int)count.x, (int)count.y, gen -> generator.apply(gen.getOrigin())));
	}

	private static Function<Vector2, Entity> getParticleGenerator(Config config) {
		String prototype = ConfigUtil.getString(config, "prototype").orElseThrow(() -> new RuntimeException("Missing prototype parameter"));
		Optional<Vector2> x = ConfigUtil.getVector2(config, "x");
		Optional<Vector2> y = ConfigUtil.getVector2(config, "y");
		Optional<Vector2> z = ConfigUtil.getVector2(config, "z");
		Optional<Vector2> impulseX = ConfigUtil.getVector2(config, "impulseX");
		Optional<Vector2> impulseY = ConfigUtil.getVector2(config, "impulseY");
		Optional<Vector2> impulseZ = ConfigUtil.getVector2(config, "impulseZ");
		// List of initialization steps (like offset & impulse)
		List<Consumer<Entity>> traits = new ArrayList<>();
		x.ifPresent(v -> traits.add(particle -> particle.getOrigin().x += Rand.between(v.x, v.y)));
		y.ifPresent(v -> traits.add(particle -> particle.getOrigin().y += Rand.between(v.x, v.y)));
		z.ifPresent(v -> traits.add(particle -> particle.setZPos(particle.getZPos() + Rand.between(v.x, v.y))));
		impulseX.ifPresent(v -> traits.add(particle -> particle.impulse(Rand.between(v.x, v.y), 0)));
		impulseY.ifPresent(v -> traits.add(particle -> particle.impulse(0, Rand.between(v.x, v.y))));
		impulseZ.ifPresent(v -> traits.add(particle -> particle.setZSpeed(Rand.between(v.x, v.y))));
		EntityPrototype proto = Resources.prototypes.get(prototype);
		return origin -> {
			Entity particle = new Entity(proto, origin);
			// Run initialization steps
			traits.forEach(trait -> trait.accept(particle));
			return particle;
		};
	}


}
