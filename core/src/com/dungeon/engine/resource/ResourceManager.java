package com.dungeon.engine.resource;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class ResourceManager {

	private static final ResourceManager INSTANCE = new ResourceManager();

	public static ResourceManager instance() {
		return INSTANCE;
	}

	private Map<String, TextureResource> textures = new HashMap<>();
	private Map<String, Animation<TextureRegion>> animations = new HashMap<>();
	private Map<String, BitmapFont> fonts = new HashMap<>();

	public Texture getTexture(String name) {
		return textures.computeIfAbsent(name, TextureResource::new).get();
	}
	public Animation<TextureRegion> getAnimation(String name, Supplier<Animation<TextureRegion>> getter) {
		return animations.computeIfAbsent(name, n -> getter.get());
	}
	public BitmapFont getFont(String name) {
		return fonts.computeIfAbsent(name, n -> {
			if ("".equals(n)) {
				return new BitmapFont();
			} else {
				return new BitmapFont(Gdx.files.internal(n + ".fnt"), Gdx.files.internal( n + ".png"), false, true);
			}
		});
	}

	public void unloadAll() {
		textures.forEach((k, v) -> v.unload());
	}

}
