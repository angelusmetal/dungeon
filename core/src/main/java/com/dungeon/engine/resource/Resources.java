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
import com.dungeon.engine.entity.EntityPrototype;
import com.dungeon.engine.resource.loader.AnimationLoader;

import java.util.ArrayList;
import java.util.List;

public class Resources {

	public static final String DEFAULT_FONT = "alegreya-sans-sc-9";

	private static final List<ResourceRepository<?>> repositories = new ArrayList<>();
	private static TextureAtlas atlas;

	public static final ResourceRepository<Texture> textures = new ResourceRepository<>(Texture::new, Texture::dispose);
	public static final ResourceRepository<Animation<Sprite>> animations = new ResourceRepository<>();
	public static final ResourceRepository<EntityPrototype> prototypes = new ResourceRepository<>();
	public static final ResourceRepository<BitmapFont> fonts = new ResourceRepository<>(Resources::computeFont, BitmapFont::dispose);
	public static final ResourceRepository<ShaderProgram> shaders = new ResourceRepository<>(Resources::computeShader, ShaderProgram::dispose);
	public static final ResourceRepository<Sound> sounds = new ResourceRepository<>(Resources::computeSound, Sound::dispose);

	public static final ResourceManagerLoader loader = new ResourceManagerLoader();

	static {
		loader.registerLoader("animation", new AnimationLoader(animations));
	}

	public static void registerRepository(ResourceRepository<?> repository) {
		repositories.add(repository);
	}

	public static void initAtlas() {
		// Regenerate atlas if missing or outdated
		TexturePacker.Settings settings = new TexturePacker.Settings();
		settings.maxWidth = settings.maxHeight = Engine.getMaxTextureSize();
		settings.ignoreBlankImages = false;
		if (TexturePacker.isModified("gfx", ".", "pack", settings)) {
			Gdx.app.log("Resources", "Updating texture atlas...");
			TexturePacker.process(settings, "gfx", ".", "pack");
		}
		// Load atlas
		Gdx.app.log("Resources", "Loading texture atlas...");
		atlas = new TextureAtlas(Gdx.files.internal("pack.atlas"));
	}

	public static void dispose() {
		repositories.forEach(Disposable::dispose);
		atlas.dispose();
	}

	public static Sprite loadSprite(String name) {
		if (atlas == null) {
			throw new RuntimeException("Texture atlas not initialized; did you forget to call Resources.initAtlas()?");
		}
		Sprite sprite = atlas.createSprite(name);
		if (sprite == null) {
			System.err.println("No image found in atlas for name '" + name + "'");
		}
		return sprite;
	}

	public static Sprite loadSprite(String name, int index) {
		if (atlas == null) {
			throw new RuntimeException("Texture atlas not initialized; did you forget to call Resources.initAtlas()?");
		}
		Sprite sprite = atlas.createSprite(name, index);
		if (sprite == null) {
			System.err.println("No image found in atlas for name '" + name + "' and index '" + index + "'");
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
