package com.dungeon.engine.resource;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.dungeon.engine.entity.EntityPrototype;
import com.dungeon.engine.resource.legacy.TextureResource;
import com.dungeon.engine.resource.loader.AnimationLoader;
import com.dungeon.engine.resource.loader.EntityPrototypeLoader;
import com.dungeon.engine.resource.loader.TilesetLoader;
import com.dungeon.game.tileset.Tileset;
import com.moandjiezana.toml.Toml;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Predicate;
import java.util.regex.Pattern;

public class ResourceManager {

	private static final Map<String, TextureResource> textures = new HashMap<>();
	private static final Map<String, Animation<TextureRegion>> animations = new HashMap<>();
	private static final Map<String, EntityPrototype> prototypes = new HashMap<>();
	private static final Map<String, BitmapFont> fonts = new HashMap<>();
	private static final Map<String, Tileset> tilesets = new HashMap<>();

	private static final Map<String, BiConsumer<String, Toml>> loaders = new HashMap<>();
	private static final Map<String, ResourceLoader<?>> readers = new HashMap<>();
	public static final String ASSETS_PATH = "assets";

	public static void init() {
		readers.put("animation", new AnimationLoader(animations));
		readers.put("prototype", new EntityPrototypeLoader(prototypes));
		readers.put("tileset", new TilesetLoader(tilesets));

		// Read all descriptor files in the assets directory
		List<String> assetFiles = scanPath(ASSETS_PATH, ".*\\.toml");

		// Load all their contents into the descriptors dictionary
		Map<ResourceIdentifier, ResourceDescriptor> descriptors = new HashMap<>();
		assetFiles.forEach(file -> loadFile(file, descriptors));

		// Scan dependencies and build the correct load order
		Set<ResourceDescriptor> sequence = new LinkedHashSet<>();
		descriptors.values().forEach(descriptor -> descriptor.scanDependencies(descriptors, sequence));

		// Load resources in the correct order
		sequence.forEach(descriptor -> readers.get(descriptor.getIdentifier().getType()).load(descriptor));
	}

	/**
	 * Recursively scans a path to get the list of resources that match a certain regex
	 * @return List of strings with the resource filenames
	 */
	private static List<String> scanPath(String path, String pattern) {
		List<String> filenames = new ArrayList<>();

		Predicate<String> matches = Pattern.compile(pattern).asPredicate();
		try (
				InputStream in = streamFile(path);
				BufferedReader br = new BufferedReader(new InputStreamReader(in))) {
			String entry;

			while ((entry = br.readLine()) != null) {
				String resource = path + "/" + entry;
				System.out.println(entry + "(" + resource + ")");
				if (new File(ResourceManager.class.getClassLoader().getResource(resource).getFile()).isDirectory()) {
					filenames.addAll(scanPath(resource, pattern));
				}
				if (matches.test(entry)) {
					filenames.add(resource);
				}
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		return filenames;
	}

	private static void loadFile(String filename, Map<ResourceIdentifier, ResourceDescriptor> descriptors) {
		System.out.println("Reading '" + filename + "'...");
		Toml toml = new Toml().read(streamFile(filename));

		// Iterate types
		for (Map.Entry<String, Object> typeEntry : toml.entrySet()) {
			String type = typeEntry.getKey();
			ResourceLoader<?> resourceLoader = readers.get(type);
			if (resourceLoader == null) {
				System.out.println("Ignoring unknown resource type '" + type + "'");
			} else {
				// Iterate entries within type
				Toml typeTable = toml.getTable(type);
				for (Map.Entry<String, Object> entry : typeTable.entrySet()) {
					String key = entry.getKey();
					ResourceIdentifier identifier = new ResourceIdentifier(type, key);
					ResourceDescriptor descriptor = resourceLoader.scan(key, typeTable.getTable(key));
					System.out.println("  - Adding descriptor for " + identifier + "...");
					ResourceDescriptor duplicate = descriptors.put(identifier, descriptor);
					if (duplicate != null) {
						throw new RuntimeException("Found duplicate descriptor for identifier: '" + identifier + "'");
					}
				}
			}
		}
	}

	private static InputStream streamFile(String filename) {
		return ResourceManager.class.getClassLoader().getResourceAsStream(filename);
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

	public static Tileset getLevelTileset(String name) {
		return getResource(name, "tileset", tilesets);
	}

	private static <T> T getResource(String name, String type, Map<String, T> repository) {
		T resource = repository.get(name);
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
