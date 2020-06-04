package com.dungeon.engine.resource.repository;

import com.badlogic.gdx.graphics.Texture;
import com.dungeon.engine.resource.ResourceRepository;

public class Textures {

	public static final ResourceRepository<Texture> repository = new ResourceRepository<>(Texture::new, Texture::dispose);

	public static Texture get(String key) {
		return repository.get(key);
	}

	public void put(String key, Texture resource) {
		repository.put(key, resource);
	}

}
