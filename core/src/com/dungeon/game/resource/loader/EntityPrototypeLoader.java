package com.dungeon.game.resource.loader;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.dungeon.engine.entity.Entity;
import com.dungeon.engine.entity.EntityPrototype;
import com.dungeon.engine.entity.TraitSupplier;
import com.dungeon.engine.entity.Traits;
import com.dungeon.engine.render.DrawFunction;
import com.dungeon.engine.render.Light;
import com.dungeon.engine.resource.ResourceDescriptor;
import com.dungeon.engine.resource.ResourceIdentifier;
import com.dungeon.engine.resource.ResourceLoader;
import com.dungeon.engine.resource.ResourceRepository;
import com.dungeon.engine.util.ConfigUtil;
import com.dungeon.engine.util.Rand;
import com.dungeon.game.resource.Resources;
import com.moandjiezana.toml.Toml;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

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
	public ResourceDescriptor scan(String key, Toml toml) {
		List<ResourceIdentifier> dependencies = new ArrayList<>();
		ConfigUtil.getString(toml, "animation").ifPresent(dependency -> dependencies.add(new ResourceIdentifier("animation", dependency)));
		return new ResourceDescriptor(new ResourceIdentifier(TYPE, key), toml, dependencies);
	}

	@Override
	public EntityPrototype read(Toml descriptor) {
		EntityPrototype prototype = new EntityPrototype();
		ConfigUtil.getString(descriptor, "animation").ifPresent(a -> prototype.animation(Resources.animations.get(a)));
		ConfigUtil.getFloat(descriptor, "bounciness").ifPresent(prototype::bounciness);
		ConfigUtil.getVector2(descriptor, "boundingBox").ifPresent(prototype::boundingBox);
		ConfigUtil.getBoolean(descriptor, "castsShadow").ifPresent(prototype::castsShadow);
		ConfigUtil.getColor(descriptor, "color").ifPresent(prototype::color);
		getDrawFunction(descriptor, "drawFunction").ifPresent(prototype::drawFunction);
		ConfigUtil.getVector2(descriptor, "drawOffset").ifPresent(prototype::drawOffset);
		ConfigUtil.getFloat(descriptor, "friction").ifPresent(prototype::friction);
		ConfigUtil.getInteger(descriptor, "health").ifPresent(prototype::health);
		ConfigUtil.getFloat(descriptor, "knockback").ifPresent(prototype::knockback);
		getLight(descriptor, "light").ifPresent(prototype::light);
		ConfigUtil.getFloat(descriptor, "timeToLive").ifPresent(prototype::timeToLive);
		ConfigUtil.getFloat(descriptor, "zAccel").ifPresent(value -> prototype.with(Traits.zAccel(value)));
		ConfigUtil.getFloat(descriptor, "zSpeed").ifPresent(prototype::zSpeed);
		ConfigUtil.getInteger(descriptor, "zIndex").ifPresent(prototype::zIndex);
		ConfigUtil.getFloat(descriptor, "fadeOut").ifPresent(value -> prototype.with(Traits.fadeOut(value)));
		ConfigUtil.getBoolean(descriptor, "fadeOutLight").ifPresent(value -> prototype.with(Traits.fadeOutLight()));
		ConfigUtil.getVector2(descriptor, "hOscillate").ifPresent(value -> prototype.with(Traits.hOscillate(value.x, value.y)));
		ConfigUtil.getVector2(descriptor, "zOscillate").ifPresent(value -> prototype.with(Traits.zOscillate(value.x, value.y)));
		ConfigUtil.getBoolean(descriptor, "solid").ifPresent(value -> {
			prototype.solid(value);
			prototype.canBeHit(value);
			prototype.canBeHurt(value);
		});
		ConfigUtil.getBoolean(descriptor, "canBeHit").ifPresent(value -> {
			prototype.canBeHit(value);
			prototype.canBeHurt(value);
		});
		ConfigUtil.getBoolean(descriptor, "canBeHurt").ifPresent(prototype::canBeHurt);
		getGenerator(descriptor, "generate").ifPresent(prototype::with);
		return prototype;
	}

	private static Optional<Light> getLight(Toml toml, String key) {
		List<String> light = toml.getList(key);
		if (light != null) {
			if (light.size() < 3) {
				throw new RuntimeException("light must have at least 3 parameters");
			}
			float diameter = Float.parseFloat(light.get(0));
			Color color = Color.valueOf(light.get(1));
			Texture texture = getLightTexture(light.get(2));
			List<Consumer<Light>> traits = new ArrayList<>();
			for (int i = 3; i < light.size(); ++i) {
				traits.add(getLightTrait(light.get(i)));
			}
			return Optional.of(new Light(diameter, color, texture, traits));
		} else {
			return Optional.empty();
		}
	}

	private static <T extends Entity> Optional<TraitSupplier<T>> getGenerator(Toml toml, String key) {
		Toml table = toml.getTable(key);
		if (table != null) {
			float frequence = ConfigUtil.getFloat(table, "frequence").orElseThrow(() -> new RuntimeException("Missing frequence parameter"));
			String prototype = ConfigUtil.getString(table, "prototype").orElseThrow(() -> new RuntimeException("Missing prototype parameter"));
			Optional<Vector2> x = ConfigUtil.getVector2(table, "x");
			Optional<Vector2> y = ConfigUtil.getVector2(table, "y");
			Optional<Vector2> z = ConfigUtil.getVector2(table, "z");
			Optional<Vector2> impulseX = ConfigUtil.getVector2(table, "impulseX");
			Optional<Vector2> impulseY = ConfigUtil.getVector2(table, "impulseY");
			List<Consumer<Entity>> traits = new ArrayList<>();
			x.ifPresent(v -> traits.add(particle -> particle.getOrigin().x += Rand.between(v.x, v.y)));
			y.ifPresent(v -> traits.add(particle -> particle.getOrigin().y += Rand.between(v.x, v.y)));
			z.ifPresent(v -> traits.add(particle -> particle.setZPos(particle.getZPos() + Rand.between(v.x, v.y))));
			impulseX.ifPresent(v -> traits.add(particle -> particle.impulse(Rand.between(v.x, v.y), 0)));
			impulseY.ifPresent(v -> traits.add(particle -> particle.impulse(0, Rand.between(v.x, v.y))));
			return Optional.of(Traits.generator(frequence, (gen) -> {
				Entity particle = new Entity(Resources.prototypes.get(prototype), gen.getOrigin());
				traits.forEach(trait -> trait.accept(particle));
				return particle;
			}));
		}
		return Optional.empty();
	}

	private static Texture getLightTexture(String name) {
		if ("NORMAL".equals(name)) {
			return Light.NORMAL;
		} else if ("RAYS".equals(name)) {
			return Light.RAYS;
		} else if ("FLARE".equals(name)) {
			return Light.FLARE;
		} else {
			throw new RuntimeException("light texture '" + name + "' not recognized");
		}
	}

	private static Consumer<Light> getLightTrait(String name) {
		if ("torchlight".equals(name)) {
			return Light.torchlight();
		} else if ("rotateSlow".equals(name)) {
			return Light.rotateSlow();
		} else if ("rotateMedium".equals(name)) {
			return Light.rotateMedium();
		} else if ("rotateFast".equals(name)) {
			return Light.rotateFast();
		} else if ("oscillate".equals(name)) {
			return Light.oscillate();
		} else {
			throw new RuntimeException("light trait '" + name + "' not recognized");
		}
	}

	private static Optional<Supplier<DrawFunction>> getDrawFunction(Toml toml, String key) {
		Toml table = toml.getTable(key);
		if (table != null) {
			Optional<Supplier<DrawFunction>> rotateFixed = ConfigUtil.getFloat(table, "rotateFixed").map(DrawFunction::rotateFixed);
			Optional<Supplier<DrawFunction>> rotateRandom = ConfigUtil.getFloat(table, "rotateRandom").map(DrawFunction::rotateRandom);
			if (rotateFixed.isPresent() && rotateRandom.isPresent()) {
				throw new RuntimeException("Only one drawFunction can be specified");
			}
			return Optional.ofNullable(rotateFixed.orElse(rotateRandom.orElse(null)));
		}
		return Optional.empty();
	}

}
