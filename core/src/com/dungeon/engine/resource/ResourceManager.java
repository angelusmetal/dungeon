package com.dungeon.engine.resource;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.dungeon.engine.entity.EntityPrototype;
import com.dungeon.engine.util.ConfigUtil;
import com.moandjiezana.toml.Toml;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class ResourceManager {

	private static final Map<String, TextureResource> textures = new HashMap<>();
	private static final Map<String, Animation<TextureRegion>> animations = new HashMap<>();
	private static final Map<String,EntityPrototype> prototypes = new HashMap<>();
	private static final Map<String, BitmapFont> fonts = new HashMap<>();

	public static void init() {
		Toml toml = new Toml().read(ResourceManager.class.getClassLoader().getResourceAsStream("animations.toml"));
		ConfigUtil.getMapOf(toml, AnimationDef.class).forEach((name, def) -> def.load(name));
		toml = new Toml().read(ResourceManager.class.getClassLoader().getResourceAsStream("prototypes.toml"));
		ConfigUtil.getMapOf(toml, PrototypeDef.class).forEach((name, def) -> def.load(name));
	}

	public static Animation<TextureRegion> getAnimation(String name) {
		Animation<TextureRegion> animation = animations.get(name);
		if (animation == null) {
			throw new RuntimeException("No animation with key '" + name + "'");
		}
		return animation;
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

	public static EntityPrototype getPrototype(String name) {
		EntityPrototype prototype = prototypes.get(name);
		if (prototype == null) {
			throw new RuntimeException("No prototype with key '" + name + "'");
		}
		return prototype;
	}

	public static Texture getTexture(String name) {
		return textures.computeIfAbsent(name, TextureResource::new).get();
	}

	public static void loadAnimation(String name, Animation<TextureRegion> animation) {
		if (animations.containsKey(name)) {
			throw new RuntimeException("Duplicate animation for key " + name);
		}
		System.out.println("Loading animation '" + name + "'...");
		animations.put(name, animation);
	}

	public static void loadPrototype(String name, EntityPrototype prototype) {
		if (prototypes.containsKey(name)) {
			throw new RuntimeException("Duplicate prototype for key " + name);
		}
		System.out.println("Loading prototype '" + name + "'...");
		prototypes.put(name, prototype);
	}

	public static void unloadAll() {
		textures.forEach((k, v) -> v.unload());
		fonts.forEach((k, v) -> v.dispose());
	}

}
