package com.dungeon.game.resource.loader;

import com.badlogic.gdx.math.Vector2;
import com.dungeon.engine.Engine;
import com.dungeon.engine.entity.Entity;
import com.dungeon.engine.entity.EntityPrototype;
import com.dungeon.engine.entity.Timer;
import com.dungeon.engine.entity.TraitSupplier;
import com.dungeon.engine.entity.Traits;
import com.dungeon.engine.resource.LoadingException;
import com.dungeon.engine.util.ConfigUtil;
import com.dungeon.engine.util.Rand;
import com.dungeon.game.resource.Resources;
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
		if (type.equals("generate")) {
			return generate(config);
		} else {
			throw new LoadingException("Unknown type " + type);
		}
	}

	private static <T extends Entity> TraitSupplier<T> generate(Config config) {
		Optional<Float> frequency = ConfigUtil.getFloat(config, "frequency");
		Supplier<Integer> count = ConfigUtil.getIntegerRange(config, "count").orElse(() -> 1);

		// The particle factory generates a single particle
		Function<Vector2, Entity> factory = getParticleGenerator(config);
		Consumer<T> generator;

		generator = e -> {
			int amount = count.get();
			for (int i = 0; i < amount; ++i) {
				Engine.entities.add(factory.apply(e.getOrigin()));
			}
		};

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

	private static Function<Vector2, Entity> getParticleGenerator(Config config) {
		String prototype = ConfigUtil.getString(config, "prototype").orElseThrow(() -> new RuntimeException("Missing prototype parameter"));
		Optional<Supplier<Integer>> x = ConfigUtil.getIntegerRange(config, "x");
		Optional<Supplier<Integer>> y = ConfigUtil.getIntegerRange(config, "y");
		Optional<Supplier<Integer>> z = ConfigUtil.getIntegerRange(config, "z");
		Optional<Supplier<Integer>> impulseX = ConfigUtil.getIntegerRange(config, "impulseX");
		Optional<Supplier<Integer>> impulseY = ConfigUtil.getIntegerRange(config, "impulseY");
		Optional<Supplier<Integer>> impulseZ = ConfigUtil.getIntegerRange(config, "impulseZ");
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
		impulseZ.ifPresent(v -> traits.add(particle -> particle.setZSpeed(v.get())));
		EntityPrototype proto = Resources.prototypes.get(prototype);
		return origin -> {
			Entity particle = new Entity(proto, origin);
			// Run initialization steps
			traits.forEach(trait -> trait.accept(particle));
			return particle;
		};
	}


}
