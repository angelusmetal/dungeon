package com.dungeon.game.resource.loader;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.dungeon.engine.Engine;
import com.dungeon.engine.entity.Entity;
import com.dungeon.engine.entity.TraitSupplier;
import com.dungeon.engine.entity.Traits;
import com.dungeon.engine.render.Material;
import com.dungeon.engine.resource.LoadingException;
import com.dungeon.engine.resource.Resources;
import com.dungeon.engine.util.ConfigUtil;
import com.dungeon.engine.util.Metronome;
import com.dungeon.engine.util.Rand;
import com.dungeon.engine.util.Util;
import com.dungeon.game.Game;
import com.dungeon.game.entity.CreatureEntity;
import com.dungeon.game.entity.DungeonEntity;
import com.dungeon.game.resource.DungeonResources;
import com.typesafe.config.Config;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

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
			return generateLoot(config);
		} else if (type.equals("xInvert")){
			return xInvert(config);
		} else if (type.equals("deathClone")){
			return deathClone(config);
		} else if (type.equals("fadeIn")) {
			return fadeIn(config);
		} else if (type.equals("sound")) {
			return sound(config);
		} else if (type.equals("setAnimation")) {
			return setAnimation(config);
		} else if (type.equals("disableSignals")) {
			return e -> entity -> entity.setAcceptsSignals(false);
		} else if (type.equals("shout")) {
			return shout(config);
		} else {
			throw new LoadingException("Unknown type " + type);
		}
	}

	private static <T extends Entity> TraitSupplier<T> shout(Config config) {
		List<String> phrases = ConfigUtil.getStringList(config, "phrases").orElse(Collections.emptyList());
		float chance = ConfigUtil.getFloat(config, "chance").orElse(1f);
		return e -> entity -> {
			if (Rand.chance(chance)) {
				Game.shout(entity, Rand.pick(phrases));
			}
		};
	}

	private static <T extends Entity> TraitSupplier<T> setAnimation(Config config) {
		String name = ConfigUtil.requireString(config, "animation");
		Animation<Material> animation = Resources.animations.get(name);
		return e -> entity -> {
			entity.setAnimation(animation, Engine.time());
		};
	}

	private static <T extends Entity> TraitSupplier<T> generateLoot(Config config) {
		String lootName = ConfigUtil.requireString(config, "loot");
		return e -> entity -> {
			LootGenerator generator = DungeonResources.loots.get(lootName);
			List<String> lootList = generator.generate();
			Vector2 origin = entity.getOrigin().cpy().add(0, -5);
			if (lootList.size() == 1) {
				// Spawn a single piece of loot
				Entity loot = Game.build(lootList.get(0), origin);
				loot.setZPos(15);
				// TODO Is this really ok?
				loot.getTraits().add(Traits.fadeIn(1f, 1f).get(loot));
				Engine.entities.add(loot);
			} else {
				// Spawn multiple pieces of loot
				lootList.forEach(piece -> {
					Entity loot = Game.build(piece, origin);
					loot.setZPos(15);
					// Scatter them around! (this only works if speed is at least 50)
					loot.impulse(Rand.between(-20, 20), Rand.between(-5, -1));
					loot.setZSpeed(Rand.between(50, 150));
					Engine.entities.add(loot);
				});
			}
		};
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
		} else if (vector.equals("aim")) {
			return Traits.xInvertByVector(
					e -> e instanceof CreatureEntity ? ((CreatureEntity) e).getAim() : e.getMovement());
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
		boolean inheritZ = ConfigUtil.getBoolean(config, "inheritZ").orElse(false);
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
			}
			if (inheritZ) {
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

	private static <T extends Entity> TraitSupplier<T> sound(Config config) {
		List<Sound> sounds = ConfigUtil.requireStringList(config, "file").stream().map(Resources.sounds::get).collect(Collectors.toList());
		float chance = ConfigUtil.getFloat(config, "chance").orElse(1f);
		float volume = ConfigUtil.getFloat(config, "volume").orElse(1f);
		float pitchVariance = Util.clamp(ConfigUtil.getFloat(config, "pitchVariance").orElse(0f));
		float zspeedAttn = ConfigUtil.getFloat(config, "zspeedAttn").orElse(0f);
		return Traits.playSound(sounds, volume, pitchVariance, zspeedAttn, chance);
	}


}
