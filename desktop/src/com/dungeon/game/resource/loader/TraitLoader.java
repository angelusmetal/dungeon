package com.dungeon.game.resource.loader;

import com.dungeon.engine.Engine;
import com.dungeon.engine.entity.Entity;
import com.dungeon.engine.entity.Timer;
import com.dungeon.engine.entity.TraitSupplier;
import com.dungeon.engine.entity.Traits;
import com.dungeon.engine.resource.LoadingException;
import com.dungeon.engine.util.ConfigUtil;
import com.dungeon.game.Game;
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
		} else if (type.equals("xInvert")){
			return xInvert(config);
		} else if (type.equals("deathClone")){
			return deathClone(config);
		} else {
			throw new LoadingException("Unknown type " + type);
		}
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
//		} else if (vector.equals("aim")) {
//			return Traits.xInvertByVector(PlayerEntity::getAim);
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
			return e -> {
				Timer timer = new Timer(freq.get());
				return entity -> timer.doAtInterval(() -> generator.accept(entity));
			};
		} else {
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
		boolean inheritColor = ConfigUtil.getBoolean(config, "inheritColor").orElse(false);
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
			// Run initialization steps
			traits.forEach(trait -> trait.accept(particle));
			return particle;
		};
	}


}
