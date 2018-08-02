package com.dungeon.engine.resource;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.dungeon.engine.entity.EntityPrototype;
import com.dungeon.game.tileset.LevelTileset;
import com.moandjiezana.toml.Toml;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.regex.Pattern;

public class ResourceManager {

	private static final Map<String, TextureResource> textures = new HashMap<>();
	private static final Map<String, Animation<TextureRegion>> animations = new HashMap<>();
	private static final Map<String, EntityPrototype> prototypes = new HashMap<>();
	private static final Map<String, BitmapFont> fonts = new HashMap<>();
	private static final Map<String, LevelTileset> tilesets = new HashMap<>();

	private static final Map<String, BiConsumer<String, Toml>> loaders = new HashMap<>();
	public static final String ASSETS_PATH = "assets/";

	public static void init() {
		addResourceLoader("animation", animations, AnimationReader::read);
		addResourceLoader("prototype", prototypes, EntityPrototypeReader::read);
		addResourceLoader("tileset", tilesets, TilesetReader::read);

		List<String> assetFiles = scanPath(ASSETS_PATH, ".*\\.toml");
		/*
		 * TODO Create a dependency tree and load by dependency order
		 */
		loaders.entrySet().stream().sorted(Comparator.comparing(Map.Entry::getKey)).forEach(entry -> {
				assetFiles.forEach(asset -> loadFile(ASSETS_PATH + asset, entry.getKey(), entry.getValue()));
		});
	}

	/**
	 * Scans a path to get the list of resources that match a certain regex
	 * @return List of strings with the resource filenames
	 */
	private static List<String> scanPath(String path, String pattern) {
		List<String> filenames = new ArrayList<>();

		Predicate<String> matches = Pattern.compile(pattern).asPredicate();
		try (
				InputStream in = ResourceManager.class.getClassLoader().getResourceAsStream(path);
				BufferedReader br = new BufferedReader(new InputStreamReader(in))) {
			String resource;

			while ((resource = br.readLine()) != null) {
				if (matches.test(resource)) {
					filenames.add(resource);
				}
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		return filenames;
	}

	/**
	 * Load a certain resource type from a file using a type loader
	 * @param filename   Filename to read
	 * @param type       Type of resource
	 * @param typeLoader Loader for type
	 */
	private static void loadFile(String filename, String type, BiConsumer<String, Toml> typeLoader) {
		System.out.println("From '" + filename + "':");
		InputStream stream = ResourceManager.class.getClassLoader().getResourceAsStream(filename);
		Toml toml = new Toml().read(stream);

		Toml typeAssets = toml.getTable(type);
		if (typeAssets != null) {
			for (Map.Entry<String, Object> typeAsset : typeAssets.entrySet()) {
				System.out.println("  Loading " + type + " '" + typeAsset.getKey() + "'...");
				typeLoader.accept(typeAsset.getKey(), typeAssets.getTable(typeAsset.getKey()));
			}
		}
	}

	private static <T> void addResourceLoader(String type, Map<String, T> resourceMap, Function<Toml, T> resourceLoader) {
		loaders.put(type, (key, toml) -> {
			if (resourceMap.containsKey(key)) {
				throw new RuntimeException("Duplicate " + type + " for key " + key);
			}
			resourceMap.put(key, resourceLoader.apply(toml));
		});
	}

	public static Animation<TextureRegion> getAnimation(String name) {
		return getResource(name, "animation", animations);
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
		return getResource(name, "prototype", prototypes);
	}

	public static LevelTileset getLevelTileset(String name) {
		return getResource(name, "tileset", tilesets);
	}

	private static <T> T getResource(String name, String type, Map<String, T> storage) {
		T resource = storage.get(name);
		if (resource == null) {
			throw new RuntimeException("No " + type + " with key '" + name + "'");
		}
		return resource;
	}

	public static Texture getTexture(String name) {
		return textures.computeIfAbsent(name, TextureResource::new).get();
	}

	public static void unloadAll() {
		textures.forEach((k, v) -> v.unload());
		fonts.forEach((k, v) -> v.dispose());
	}

}
