package com.dungeon.engine.resource.loader;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
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

public class AnimationLoader implements ResourceLoader<Animation<Sprite>> {

	private static final String TYPE = "animation";

	private final ResourceRepository<Animation<Sprite>> repository;

	public AnimationLoader(ResourceRepository<Animation<Sprite>> repository) {
		this.repository = repository;
	}

	@Override
	public ResourceRepository<Animation<Sprite>> getRepository() {
		return repository;
	}

	@Override
	public Animation<Sprite> read(String identifier, Config config) {
		return AnimationLoader.readFrom(config);
	}

	@Override
	public ResourceDescriptor scan(String key, Config config) {
		return new ResourceDescriptor(new ResourceIdentifier(TYPE, key), config, Collections.emptyList());
	}

	public static Animation<Sprite> readFrom(Config config) {
		String texture = ConfigUtil.requireString(config, "texture");
		List<Sprite> loop = ConfigUtil.getIntList(config, "loop")
				.orElse(Collections.emptyList())
				.stream().map(index -> Resources.loadSprite(texture, index))
				.collect(Collectors.toList());
		List<Sprite> sequence = ConfigUtil.getIntList(config, "sequence")
				.orElse(Collections.emptyList())
				.stream().map(index -> Resources.loadSprite(texture, index))
				.collect(Collectors.toList());
		float frameDuration = ConfigUtil.getFloat(config, "frameDuration").orElse(1f);

		if (loop.isEmpty() && sequence.isEmpty()) {
			loop = Collections.singletonList(Resources.loadSprite(texture));
		} else if (loop.size() == sequence.size()) {
			throw new LoadingException("must have either 'loop' or 'sequence', but not both");
		}

		if (!loop.isEmpty()) {
			return loop(frameDuration, loop);
		} else {
			return sequence(frameDuration, sequence);
		}
	}
	public static Animation<Sprite> loop(float frameDuration, List<Sprite> frames) {
		Animation<Sprite> animation = new Animation<>(frameDuration, asArray(frames));
		animation.setPlayMode(Animation.PlayMode.LOOP);
		return animation;
	}

	public static Animation<Sprite> sequence(float frameDuration, List<Sprite> frames) {
		return new Animation<>(frameDuration, asArray(frames));
	}

	private static Sprite[] asArray(List<Sprite> frames) {
		Sprite[] array = new Sprite[frames.size()];
		int i = 0;
		for (Sprite frame : frames) {
			array[i++] = frame;
		}
		return array;
	}
}
