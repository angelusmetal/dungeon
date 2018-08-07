package com.dungeon.game.resource;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.dungeon.engine.entity.EntityPrototype;
import com.dungeon.engine.resource.ResourceManagerLoader;
import com.dungeon.engine.resource.ResourceRepository;
import com.dungeon.game.level.RoomPrototype;
import com.dungeon.game.resource.loader.AnimationLoader;
import com.dungeon.game.resource.loader.EntityPrototypeLoader;
import com.dungeon.game.resource.loader.EnvironmentLoader;
import com.dungeon.game.resource.loader.RoomPrototypeLoader;
import com.dungeon.game.resource.loader.TilesetLoader;
import com.dungeon.game.tileset.Environment;
import com.dungeon.game.tileset.Tileset;

public class Resources<T> {
	public static final ResourceRepository<Texture> textures = new ResourceRepository<>(Texture::new, Texture::dispose);
	public static final ResourceRepository<Animation<TextureRegion>> animations = new ResourceRepository<>();
	public static final ResourceRepository< EntityPrototype> prototypes = new ResourceRepository<>();
	public static final ResourceRepository<BitmapFont> fonts = new ResourceRepository<>(Resources::computeFont, BitmapFont::dispose);
	public static final ResourceRepository<Tileset> tilesets = new ResourceRepository<>();
	public static final ResourceRepository<RoomPrototype> rooms = new ResourceRepository<>();
	public static final ResourceRepository<Environment> environments = new ResourceRepository<>();

	public static void load() {
		ResourceManagerLoader loader = new ResourceManagerLoader();
		loader.registerLoader("animation", new AnimationLoader(animations));
		loader.registerLoader("prototype", new EntityPrototypeLoader(prototypes));
		loader.registerLoader("tileset", new TilesetLoader(tilesets));
		loader.registerLoader("room", new RoomPrototypeLoader(rooms));
		loader.registerLoader("level", new EnvironmentLoader(environments));
		loader.load();
	}

	private static BitmapFont computeFont(String name) {
		return new BitmapFont(Gdx.files.internal(name + ".fnt"), Gdx.files.internal( name + ".png"), false, true);
	}

	public static void dispose() {
		textures.dispose();
		animations.dispose();
		prototypes.dispose();
		fonts.dispose();
		tilesets.dispose();
		rooms.dispose();
		environments.dispose();
	}

}
