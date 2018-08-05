package com.dungeon.engine.resource.loader;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.dungeon.engine.resource.LoadingException;
import com.dungeon.engine.resource.ResourceDescriptor;
import com.dungeon.engine.resource.ResourceIdentifier;
import com.dungeon.engine.resource.ResourceLoader;
import com.dungeon.engine.resource.ResourceManager;
import com.dungeon.engine.util.ConfigUtil;
import com.moandjiezana.toml.Toml;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class AnimationLoader implements ResourceLoader<Animation<TextureRegion>> {

	private static final String TYPE = "animation";

	private final Map<String, Animation<TextureRegion>> repository;

	public AnimationLoader(Map<String, Animation<TextureRegion>> repository) {
		this.repository = repository;
	}

	@Override
	public Map<String, Animation<TextureRegion>> getRepository() {
		return repository;
	}

	@Override
	public ResourceDescriptor scan(String key, Toml toml) {
		return new ResourceDescriptor(new ResourceIdentifier(TYPE, key), toml, Collections.emptyList());
	}

	@Override
	public Animation<TextureRegion> read(Toml toml) {
		List<Integer> loop = new ArrayList<>();
		List<Integer> sequence = new ArrayList<>();
		ConfigUtil.<Number>getList(toml, "loop").ifPresent(list -> list.stream().map(Number::intValue).forEach(loop::add));
		ConfigUtil.<Number>getList(toml, "sequence").ifPresent(list -> list.stream().map(Number::intValue).forEach(sequence::add));
		int tilesize = ConfigUtil.requireInteger(toml, "tilesize");
		String texture = ConfigUtil.requireString(toml, "texture");
		float frameDuration = ConfigUtil.requireFloat(toml, "frameDuration");

		if (loop.isEmpty() == sequence.isEmpty()) {
			throw new LoadingException("must have either 'loop' or 'sequence'.", "????");
		}

		Texture tex = ResourceManager.getTexture(texture);
		if (tex.getHeight() % tilesize != 0 || tex.getWidth() % tilesize != 0) {
			throw new LoadingException("texture dimensions must be an exact multiple of the tilesize", "????");
		}
		int columns = tex.getWidth() / tilesize;
		if (!loop.isEmpty()) {
			List<TextureRegion> frames = getFrames(tex, loop, tilesize, columns);
			return loop(frameDuration, frames);
		} else {
			List<TextureRegion> frames = getFrames(tex, sequence, tilesize, columns);
			return sequence(frameDuration, frames);
		}
	}

	private static List<TextureRegion> getFrames(Texture tex, List<Integer> regions, int tilesize, int columns) {
		List<TextureRegion> frames = new ArrayList<>();
		for (int frame : regions) {
			frames.add(getFrame(tex, frame, tilesize, columns));
		}
		return frames;
	}

	private static TextureRegion getFrame(Texture tex, int frame, int tilesize, int columns) {
		return new TextureRegion(tex, tilesize * (frame % columns), tilesize * (frame / columns), tilesize, tilesize);
	}

	public static Animation<TextureRegion> loop(float frameDuration, List<TextureRegion> frames) {
		Animation<TextureRegion> animation = new Animation<>(frameDuration, asArray(frames));
		animation.setPlayMode(Animation.PlayMode.LOOP);
		return animation;
	}

	public static Animation<TextureRegion> sequence(float frameDuration, List<TextureRegion> frames) {
		return new Animation<>(frameDuration, asArray(frames));
	}

	private static TextureRegion[] asArray(List<TextureRegion> frames) {
		TextureRegion[] array = new TextureRegion[frames.size()];
		int i = 0;
		for (TextureRegion frame : frames) {
			array[i++] = frame;
		}
		return array;
	}
}
