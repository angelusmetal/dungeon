package com.dungeon.engine.resource.loader;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.dungeon.engine.render.Material;
import com.dungeon.engine.resource.LoadingException;
import com.dungeon.engine.resource.ResourceDescriptor;
import com.dungeon.engine.resource.ResourceIdentifier;
import com.dungeon.engine.resource.ResourceLoader;
import com.dungeon.engine.resource.ResourceRepository;
import com.dungeon.engine.resource.Resources;
import com.dungeon.engine.util.ConfigUtil;
import com.typesafe.config.Config;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class AnimationLoader implements ResourceLoader<Animation<Material>> {

	private static final String TYPE = "animation";

	private final ResourceRepository<Animation<Material>> repository;

	public AnimationLoader(ResourceRepository<Animation<Material>> repository) {
		this.repository = repository;
	}

	@Override
	public ResourceRepository<Animation<Material>> getRepository() {
		return repository;
	}

	@Override
	public Animation<Material> read(String identifier, Config config) {
		return AnimationLoader.readFrom(config);
	}

	@Override
	public ResourceDescriptor scan(String key, Config config) {
		return new ResourceDescriptor(new ResourceIdentifier(TYPE, key), config, Collections.emptyList());
	}

	public static Animation<Material> readFrom(Config config) {
		String texture = ConfigUtil.requireString(config, "texture");
		List<Material> loop = ConfigUtil.getIntList(config, "loop")
				.orElse(Collections.emptyList())
				.stream().map(index -> {
					Sprite diffuse = Resources.loadSprite(texture, index);
					Sprite normal = Resources.loadSprite("normal_map/" + texture, index);
					return new Material(diffuse, normal);
				})
				.collect(Collectors.toList());
		List<Material> sequence = ConfigUtil.getIntList(config, "sequence")
				.orElse(Collections.emptyList())
				.stream().map(index -> {
					Sprite diffuse = Resources.loadSprite(texture, index);
					Sprite normal = Resources.loadSprite("normal_map/" + texture, index);
					return new Material(diffuse, normal);
				})
				.collect(Collectors.toList());
		float frameDuration = ConfigUtil.getFloat(config, "frameDuration").orElse(1f);

		if (loop.isEmpty() && sequence.isEmpty()) {
			Sprite diffuse = Resources.loadSprite(texture);
			Sprite normal = Resources.loadSprite("normal_map/" + texture);
			loop = Collections.singletonList(new Material(diffuse, normal));
		} else if (loop.size() == sequence.size()) {
			throw new LoadingException("must have either 'loop' or 'sequence', but not both");
		}

		if (!loop.isEmpty()) {
			return loop(frameDuration, loop);
		} else {
			return sequence(frameDuration, sequence);
		}
	}
	public static Animation<Material> loop(float frameDuration, List<Material> frames) {
		Animation<Material> animation = new Animation<>(frameDuration, asArray(frames));
		animation.setPlayMode(Animation.PlayMode.LOOP);
		return animation;
	}

	public static Animation<Material> sequence(float frameDuration, List<Material> frames) {
		return new Animation<>(frameDuration, asArray(frames));
	}

	private static Material[] asArray(List<Material> frames) {
		Material[] array = new Material[frames.size()];
		int i = 0;
		for (Material frame : frames) {
			array[i++] = frame;
		}
		return array;
	}
}
