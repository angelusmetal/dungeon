package com.dungeon.engine.resource;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.tools.texturepacker.TexturePacker;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.dungeon.engine.Engine;
import com.dungeon.engine.audio.LayeredMusic;
import com.dungeon.engine.render.Material;
import com.dungeon.engine.resource.loader.AnimationLoader;
import com.dungeon.engine.resource.loader.LayeredMusicLoader;

import java.util.ArrayList;
import java.util.List;

public class Resources {

	public static final String DEFAULT_FONT = "alegreya-sans-sc-9";
	public static final String GRAPHICS_PATH = "gfx";

	private static final List<ResourceRepository<?>> repositories = new ArrayList<>();
	private static TextureAtlas atlas;

	public static final ResourceRepository<Texture> textures = new ResourceRepository<>(Texture::new, Texture::dispose);
	public static final ResourceRepository<Animation<Material>> animations = new ResourceRepository<>();
	public static final ResourceRepository<BitmapFont> fonts = new ResourceRepository<>(Resources::computeFont, BitmapFont::dispose);
	public static final ResourceRepository<ShaderProgram> shaders = new ResourceRepository<>(Resources::computeShader, ShaderProgram::dispose);
	public static final ResourceRepository<Sound> sounds = new ResourceRepository<>(Resources::computeSound, Sound::dispose);
	public static final ResourceRepository<LayeredMusic> musics = new ResourceRepository<>();

	public static final ResourceManagerLoader loader = new ResourceManagerLoader();

	static {
		loader.registerLoader("animation", new AnimationLoader(animations));
		loader.registerLoader("music", new LayeredMusicLoader(musics));
	}

	public static void registerRepository(ResourceRepository<?> repository) {
		repositories.add(repository);
	}

	public static void initAtlas() {
		if (Engine.isAtlasForced()) {
			// Regenerate atlas if missing or outdated
			TexturePacker.Settings settings = new TexturePacker.Settings();
			settings.maxWidth = settings.maxHeight = Engine.getMaxTextureSize();
			settings.ignoreBlankImages = false;
			settings.stripWhitespaceX = settings.stripWhitespaceY = true;
			settings.paddingX = settings.paddingY = 1;
			if (TexturePacker.isModified(GRAPHICS_PATH, ".", "pack", settings)) {
				Gdx.app.log("Resources", "Updating texture atlas...");
				TexturePacker.process(settings, GRAPHICS_PATH, ".", "pack");
			}
		}
		// Load atlas
		Gdx.app.log("Resources", "Loading texture atlas...");
		atlas = new TextureAtlas(Gdx.files.internal("pack.atlas"));
	}

	public static void dispose() {
		repositories.forEach(Disposable::dispose);
		if (atlas != null) {
			atlas.dispose();
		}
	}

	public static Sprite loadSprite(String name) {
		if (atlas == null) {
			throw new RuntimeException("Texture atlas not initialized; did you forget to call Resources.initAtlas()?");
		}
		Sprite sprite = atlas.createSprite(name);
		if (sprite == null) {
			try {
				// TODO If the image is not PNG?
				Texture texture = textures.get(GRAPHICS_PATH + "/" + name + ".png");
				sprite = new Sprite(texture);
				Gdx.app.error("Resources", "No image found in atlas for name '" + name + "', loaded from disk");
			} catch (GdxRuntimeException e) {
				Gdx.app.error("Resources", "No image found in atlas nor in disk for name '" + name + "'");
			}
		}
		return sprite;
	}

	public static Sprite loadSprite(String name, int index) {
		if (atlas == null) {
			throw new RuntimeException("Texture atlas not initialized; did you forget to call Resources.initAtlas()?");
		}
		Sprite sprite = atlas.createSprite(name, index);
		if (sprite == null) {
			try {
				// TODO If the image is not PNG?
				Texture texture = textures.get(GRAPHICS_PATH + "/" + name + "_" + index + ".png");
				sprite = new Sprite(texture);
				Gdx.app.error("Resources", "No image found in atlas for name '" + name + "' and index '" + index + "', loaded from disk");
			} catch (GdxRuntimeException e) {
				Gdx.app.error("Resources", "No image found in atlas nor in disk for name '" + name + "' and index '" + index + "'");
			}
		}
		return sprite;
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

}
