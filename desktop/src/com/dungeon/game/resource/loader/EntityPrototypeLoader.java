package com.dungeon.game.resource.loader;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.dungeon.engine.entity.Entity;
import com.dungeon.engine.entity.EntityMover;
import com.dungeon.engine.entity.EntityPrototype;
import com.dungeon.engine.entity.TraitSupplier;
import com.dungeon.engine.entity.Traits;
import com.dungeon.engine.render.LightPrototype;
import com.dungeon.engine.render.ShadowType;
import com.dungeon.engine.resource.LoadingException;
import com.dungeon.engine.resource.ResourceDescriptor;
import com.dungeon.engine.resource.ResourceIdentifier;
import com.dungeon.engine.resource.ResourceLoader;
import com.dungeon.engine.resource.ResourceRepository;
import com.dungeon.engine.resource.Resources;
import com.dungeon.engine.util.ConfigUtil;
import com.dungeon.engine.util.Rand;
import com.dungeon.game.entity.PlayerEntity;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigException;
import com.typesafe.config.ConfigValueType;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Vector;

public class EntityPrototypeLoader implements ResourceLoader<EntityPrototype> {

	private static final String TYPE = "prototype";

	private final ResourceRepository<EntityPrototype> repository;

	public EntityPrototypeLoader(ResourceRepository<EntityPrototype> repository) {
		this.repository = repository;
	}

	@Override
	public ResourceRepository<EntityPrototype> getRepository() {
		return repository;
	}

	@Override
	public ResourceDescriptor scan(String key, Config descriptor) {
		List<ResourceIdentifier> dependencies = new ArrayList<>();
		try {
			ConfigUtil.getString(descriptor, "animation").ifPresent(animation ->
					dependencies.add(new ResourceIdentifier("animation", animation))
			);
		} catch (ConfigException.WrongType e) {
			ConfigUtil.getStringList(descriptor, "animation").ifPresent(animations -> animations.forEach(anim -> dependencies.add(new ResourceIdentifier("animation", anim))));
		}
		ConfigUtil.getConfigList(descriptor, "onHit").ifPresent(
				traits -> traits.forEach(conf -> getGeneratorDeps(conf, dependencies)));
		ConfigUtil.getConfigList(descriptor, "onExpire").ifPresent(
				traits -> traits.forEach(conf -> getGeneratorDeps(conf, dependencies)));
		ConfigUtil.getConfigList(descriptor, "onGroundHit").ifPresent(
				traits -> traits.forEach(conf -> getGeneratorDeps(conf, dependencies)));
		ConfigUtil.getConfigList(descriptor, "onRest").ifPresent(
				traits -> traits.forEach(conf -> getGeneratorDeps(conf, dependencies)));
		ConfigUtil.getConfigList(descriptor, "onSignal").ifPresent(
				traits -> traits.forEach(conf -> getGeneratorDeps(conf, dependencies)));
		ConfigUtil.getConfigList(descriptor, "with").ifPresent(
				traits -> traits.forEach(conf -> getGeneratorDeps(conf, dependencies)));
		return new ResourceDescriptor(new ResourceIdentifier(TYPE, key), descriptor, dependencies);
	}

	private void getGeneratorDeps(Config config, List<ResourceIdentifier> dependencies) {
		ConfigUtil.getString(config, "prototype").ifPresent(dependency -> dependencies.add(new ResourceIdentifier("prototype", dependency)));
	}

	@Override
	public EntityPrototype read(String identifier, Config descriptor) {
		EntityPrototype prototype = new EntityPrototype();

		try {
			ConfigUtil.getString(descriptor, "animation").ifPresent(animation ->
					prototype.animation(Resources.animations.get(animation))
			);
		} catch (ConfigException.WrongType e) {
			// If there are multiple animations, pick one at random
			ConfigUtil.getStringList(descriptor, "animation").ifPresent(animations -> prototype.animation(() -> Resources.animations.get(Rand.pick(animations))));
		}

		ConfigUtil.getBoolean(descriptor, "offsetAnimation").ifPresent(prototype::offsetAnimation);
		ConfigUtil.getString(descriptor, "factory").ifPresent(prototype::factory);
		ConfigUtil.getFloat(descriptor, "bounciness").ifPresent(prototype::bounciness);
		ConfigUtil.getVector2(descriptor, "boundingBox").ifPresent(prototype::boundingBox);
		ConfigUtil.getVector2(descriptor, "boundingBoxOffset").ifPresent(prototype::boundingBoxOffset);
		ConfigUtil.getEnum(descriptor, "shadow", ShadowType.class).ifPresent(prototype::shadowType);
		ConfigUtil.getColor(descriptor, "color").ifPresent(prototype::color);
		ConfigUtil.getVector2(descriptor, "drawOffset").ifPresent(prototype::drawOffset);
		ConfigUtil.getFloat(descriptor, "friction").ifPresent(prototype::friction);
		ConfigUtil.getFloat(descriptor, "airFriction").ifPresent(prototype::airFriction);
		ConfigUtil.getInteger(descriptor, "health").ifPresent(prototype::health);
		ConfigUtil.getFloat(descriptor, "knockback").ifPresent(prototype::knockback);
		ConfigUtil.getFloat(descriptor, "speed").ifPresent(prototype::speed);
		getLight(descriptor, "light").ifPresent(prototype::light);
		getLight(descriptor, "flare").ifPresent(prototype::flare);

		if (descriptor.hasPath("timeToLive")) {
			if (descriptor.getValue("timeToLive").valueType() == ConfigValueType.NUMBER) {
				prototype.timeToLive(ConfigUtil.requireFloat(descriptor, "timeToLive"));
			} else if (descriptor.getValue("timeToLive").valueType() == ConfigValueType.STRING) {
				String timeToLive = ConfigUtil.requireString(descriptor, "timeToLive");
				if ("animation".equals(timeToLive)) {
					prototype.timeToLive(prototype.getAnimation().get().getAnimationDuration());
				} else {
					throw new LoadingException("Invalid value '" + timeToLive + "' for timeToLive");
				}
			}
		}

		ConfigUtil.getFloat(descriptor, "zAccel").ifPresent(value -> prototype.with(Traits.zAccel(value)));
		ConfigUtil.getFloat(descriptor, "zSpeed").ifPresent(prototype::zSpeed);
		ConfigUtil.getInteger(descriptor, "zIndex").ifPresent(prototype::zIndex);
		ConfigUtil.getFloat(descriptor, "fadeIn").ifPresent(value -> prototype.with(Traits.fadeIn(value)));
		ConfigUtil.getFloat(descriptor, "fadeOut").ifPresent(value -> prototype.with(Traits.fadeOut(value)));
		ConfigUtil.getBoolean(descriptor, "fadeOutLight").ifPresent(value -> prototype.with(Traits.fadeOutLight()));
		ConfigUtil.getBoolean(descriptor, "fadeOutFlare").ifPresent(value -> prototype.with(Traits.fadeOutFlare()));
		ConfigUtil.getVector2(descriptor, "hOscillate").ifPresent(value -> prototype.with(Traits.hOscillate(value.x, value.y)));
		ConfigUtil.getVector2(descriptor, "vOscillate").ifPresent(value -> prototype.with(Traits.vOscillate(value.x, value.y)));
		ConfigUtil.getVector2(descriptor, "zOscillate").ifPresent(value -> prototype.with(Traits.zOscillate(value.x, value.y)));
		ConfigUtil.getVector2(descriptor, "movOscillate").ifPresent(value -> prototype.with(Traits.movOscillate(value.x, value.y)));
		ConfigUtil.getFloat(descriptor, "z").ifPresent(prototype::z);
		// Physics are enabled by default
		if (ConfigUtil.getBoolean(descriptor, "physics").orElse(true)) {
			prototype.with(EntityMover.move());
		}
		ConfigUtil.getBoolean(descriptor, "canBeBlockedByEntities").ifPresent(prototype::canBeBlockedByEntities);
		ConfigUtil.getBoolean(descriptor, "canBeBlockedByTiles").ifPresent(prototype::canBeBlockedByTiles);
		ConfigUtil.getBoolean(descriptor, "canBlock").ifPresent(value -> {
			prototype.canBlock(value);
			prototype.canBeHit(value);
			prototype.canBeHurt(value);
		});
		ConfigUtil.getBoolean(descriptor, "canBeHit").ifPresent(value -> {
			prototype.canBeHit(value);
			prototype.canBeHurt(value);
		});
		ConfigUtil.getBoolean(descriptor, "canBeHurt").ifPresent(prototype::canBeHurt);
		ConfigUtil.getBoolean(descriptor, "static").ifPresent(prototype::isStatic);

		ConfigUtil.getConfigList(descriptor, "onHit").ifPresent(
				traits -> traits.stream().map(TraitLoader::load).forEach(prototype::onHit));
		ConfigUtil.getConfigList(descriptor, "onExpire").ifPresent(
				traits -> traits.stream().map(TraitLoader::load).forEach(prototype::onExpire));
		ConfigUtil.getConfigList(descriptor, "onGroundHit").ifPresent(
				traits -> traits.stream().map(TraitLoader::load).forEach(prototype::onGroundHit));
		ConfigUtil.getConfigList(descriptor, "onRest").ifPresent(
				traits -> traits.stream().map(TraitLoader::load).forEach(prototype::onRest));
		ConfigUtil.getConfigList(descriptor, "onSignal").ifPresent(
				traits -> traits.stream().map(TraitLoader::load).forEach(prototype::onSignal));
		ConfigUtil.getConfigList(descriptor, "with").ifPresent(
				traits -> traits.stream().map(TraitLoader::load).forEach(prototype::with));

		ConfigUtil.getString(descriptor, "hits").ifPresent(predicate -> {
			if ("nonPlayers".equals(predicate)) {
				prototype.hitPredicate(PlayerEntity.HIT_NON_PLAYERS);
			} else if ("players".equals(predicate)) {
				prototype.hitPredicate(PlayerEntity.HIT_PLAYERS);
			} else {
				throw new LoadingException("Invalid value '" + predicate + "' for hits");
			}
		});

		return prototype;
	}

	private static <T extends Entity> TraitSupplier<T> getOnRest(String onRest) {
		if ("expire".equals(onRest)) {
			return e -> Entity::expire;
		} else if ("stop".equals(onRest)) {
			return e -> Entity::stop;
		}
		throw new RuntimeException("Unknown onRest type '" + onRest + "'");
	}

	public static LightPrototype getLight(Config config) {
		float diameter = ConfigUtil.requireFloat(config, "diameter");
		Color color = ConfigUtil.requireColor(config, "color");
		Texture texture = Resources.textures.get(ConfigUtil.requireString(config, "texture"));
		boolean castsShadow = ConfigUtil.getBoolean(config, "castsShadow").orElse(false);
		Vector2 offset = ConfigUtil.getVector2(config, "offset").orElse(Vector2.Zero);
		LightPrototype lightPrototype = new LightPrototype(diameter, color, texture, offset, castsShadow);
		ConfigUtil.getStringList(config, "traits").ifPresent(list -> list.forEach(trait -> getLightTrait(lightPrototype, trait)));
		return lightPrototype;
	}

	public static Optional<LightPrototype> getLight(Config config, String key) {
		if (config.hasPath(key)) {
			return Optional.of(getLight(config.getConfig(key)));
		} else {
			return Optional.empty();
		}
	}

	private static void getLightTrait(LightPrototype lightPrototype, String name) {
		if ("torchlight".equals(name)) {
			lightPrototype.torchlight();
		} else if ("rotateSlow".equals(name)) {
			lightPrototype.rotateSlow();
		} else if ("rotateMedium".equals(name)) {
			lightPrototype.rotateMedium();
		} else if ("rotateFast".equals(name)) {
			lightPrototype.rotateFast();
		} else if ("oscillate".equals(name)) {
			lightPrototype.oscillate();
		} else {
			throw new RuntimeException("light trait '" + name + "' not recognized");
		}
	}

}
