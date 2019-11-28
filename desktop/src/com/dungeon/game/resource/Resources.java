package com.dungeon.game.resource;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.dungeon.engine.entity.EntityPrototype;
import com.dungeon.engine.resource.ResourceManagerLoader;
import com.dungeon.engine.resource.ResourceRepository;
import com.dungeon.game.level.RoomPrototype;
import com.dungeon.game.level.TilePrototype;
import com.dungeon.game.resource.loader.AnimationLoader;
import com.dungeon.game.resource.loader.EntityPrototypeLoader;
import com.dungeon.game.resource.loader.EnvironmentLoader;
import com.dungeon.game.resource.loader.RoomPrototypeLoader;
import com.dungeon.game.resource.loader.TilePrototypeLoader;
import com.dungeon.game.resource.loader.TilesetLoader;
import com.dungeon.game.tileset.Environment;
import com.dungeon.game.tileset.Tileset;

public class Resources {
	public static final String DEFAULT_FONT = "alegreya-sans-sc-9";

	public static final ResourceRepository<Texture> textures = new ResourceRepository<>(Texture::new, Texture::dispose);
	public static final ResourceRepository<Animation<TextureRegion>> animations = new ResourceRepository<>();
	public static final ResourceRepository< EntityPrototype> prototypes = new ResourceRepository<>();
	public static final ResourceRepository<BitmapFont> fonts = new ResourceRepository<>(Resources::computeFont, BitmapFont::dispose);
	public static final ResourceRepository<Tileset> tilesets = new ResourceRepository<>();
	public static final ResourceRepository<TilePrototype> tiles = new ResourceRepository<>();
	public static final ResourceRepository<RoomPrototype> rooms = new ResourceRepository<>();
	public static final ResourceRepository<Environment> environments = new ResourceRepository<>();
	public static final ResourceRepository<ShaderProgram> shaders = new ResourceRepository<>(Resources::computeShader, ShaderProgram::dispose);
	public static final ResourceRepository<Sound> sounds = new ResourceRepository<>(Resources::computeSound, Sound::dispose);

	public static void load() {
		ResourceManagerLoader loader = new ResourceManagerLoader();
		loader.registerLoader("animation", new AnimationLoader(animations));
		loader.registerLoader("prototype", new EntityPrototypeLoader(prototypes));
		loader.registerLoader("tileset", new TilesetLoader(tilesets));
		loader.registerLoader("tile", new TilePrototypeLoader(tiles));
		loader.registerLoader("room", new RoomPrototypeLoader(rooms));
		loader.registerLoader("environment", new EnvironmentLoader(environments));
		loader.load("assets/assets.conf");
	}

	private static BitmapFont computeFont(String name) {
		return new BitmapFont(Gdx.files.internal(name + ".fnt"), Gdx.files.internal( name + ".png"), false, true);
	}

	private static ShaderProgram computeShader(String name) {
		String[] scripts = name.split("\\|");
		ShaderProgram shaderProgram = new ShaderProgram(Gdx.files.internal("shaders/" + scripts[0]).readString(), Gdx.files.internal("shaders/" + scripts[1]).readString());
		if (!shaderProgram.isCompiled()) {
			throw new GdxRuntimeException("Couldn't compile shader: " + shaderProgram.getLog());
		}
		return shaderProgram;
	}

	private static Sound computeSound(String name) {
		return Gdx.audio.newSound(Gdx.files.internal(name));
	}

	public static void dispose() {
		textures.dispose();
		animations.dispose();
		prototypes.dispose();
		fonts.dispose();
		tilesets.dispose();
		tiles.dispose();
		rooms.dispose();
		environments.dispose();
		shaders.dispose();
		sounds.dispose();
	}

}
