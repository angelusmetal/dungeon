package com.dungeon.engine.resource.loader;

import com.dungeon.engine.audio.LayeredMusic;
import com.dungeon.engine.resource.ResourceDescriptor;
import com.dungeon.engine.resource.ResourceIdentifier;
import com.dungeon.engine.resource.ResourceLoader;
import com.dungeon.engine.resource.ResourceRepository;
import com.dungeon.engine.util.ConfigUtil;
import com.typesafe.config.Config;

import java.util.Collections;

public class LayeredMusicLoader implements ResourceLoader<LayeredMusic> {

	private static final String TYPE = "music";

	private final ResourceRepository<LayeredMusic> repository;

	public LayeredMusicLoader(ResourceRepository<LayeredMusic> repository) {
		this.repository = repository;
	}

	@Override
	public ResourceRepository<LayeredMusic> getRepository() {
		return repository;
	}

	@Override
	public LayeredMusic read(String identifier, Config config) {
		return LayeredMusicLoader.readFrom(config);
	}

	@Override
	public ResourceDescriptor scan(String key, Config config) {
		return new ResourceDescriptor(new ResourceIdentifier(TYPE, key), config, Collections.emptyList());
	}

	public static LayeredMusic readFrom(Config config) {
		String intro = ConfigUtil.getString(config, "intro").orElse(null);
		String loop = ConfigUtil.getString(config, "loop").orElse(null);
		int bpm = ConfigUtil.getInteger(config, "bpm").orElse(0);
		return new LayeredMusic(intro, loop, bpm);
	}
}
