package com.dungeon.engine.resource.repository;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.dungeon.engine.resource.ResourceRepository;

public class Animations {

	public static final ResourceRepository<Animation<TextureRegion>> repository = new ResourceRepository<>();

	public static Animation<TextureRegion> get(String key) {
		return repository.get(key);
	}

	public void put(String key, Animation<TextureRegion> resource) {
		repository.put(key, resource);
	}

}
