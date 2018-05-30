package com.dungeon.engine.resource;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.dungeon.engine.util.ConfigUtil;
import com.moandjiezana.toml.Toml;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class ResourceManager {

	private static final Map<String, TextureResource> textures = new HashMap<>();
	private static final Map<String, Animation<TextureRegion>> animations = new HashMap<>();
	private static final Map<String, BitmapFont> fonts = new HashMap<>();

	public static void init() {
		Toml toml = new Toml().read(ResourceManager.class.getClassLoader().getResourceAsStream("animations.toml"));
		Map<String, AnimationDef> pojoMap = ConfigUtil.getPojoMap(toml, "animation", "name", AnimationDef.class);
		pojoMap.forEach((animation, def) -> def.load());
	}

	public static Texture getTexture(String name) {
		return textures.computeIfAbsent(name, TextureResource::new).get();
	}
	public static Animation<TextureRegion> getAnimation(String name) {
		return animations.get(name);
	}
	public static Animation<TextureRegion> getAnimation(String name, Supplier<Animation<TextureRegion>> getter) {
		return animations.computeIfAbsent(name, n -> getter.get());
	}
	public static void loadAnimation(String name, Animation<TextureRegion> animation) {
		if (animations.containsKey(name)) {
			throw new RuntimeException("Duplicate animation for key " + name);
		}
		System.out.println("Loading animation '" + name + "'...");
		animations.put(name, animation);
	}
	public static BitmapFont getFont(String name) {
		return fonts.computeIfAbsent(name, n -> {
			if ("".equals(n)) {
				return new BitmapFont();
			} else {
				return new BitmapFont(Gdx.files.internal(n + ".fnt"), Gdx.files.internal( n + ".png"), false, true);
			}
		});
	}

	public static void unloadAll() {
		textures.forEach((k, v) -> v.unload());
		fonts.forEach((k, v) -> v.dispose());
	}

}
