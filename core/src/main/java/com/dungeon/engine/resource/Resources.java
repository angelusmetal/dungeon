package com.dungeon.engine.resource;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.dungeon.engine.entity.EntityPrototype;
import com.dungeon.engine.resource.loader.AnimationLoader;

import java.util.ArrayList;
import java.util.List;

public class Resources {

	public static final String DEFAULT_FONT = "alegreya-sans-sc-9";

	private static final List<ResourceRepository<?>> repositories = new ArrayList<>();

	public static final ResourceRepository<Texture> textures = new ResourceRepository<>(Texture::new, Texture::dispose);
	public static final ResourceRepository<Animation<TextureRegion>> animations = new ResourceRepository<>();
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

	public static void dispose() {
		repositories.forEach(Disposable::dispose);
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
