package com.dungeon.game.resource.loader;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.dungeon.engine.resource.LoadingException;
import com.dungeon.engine.resource.ResourceDescriptor;
import com.dungeon.engine.resource.ResourceIdentifier;
import com.dungeon.engine.resource.ResourceLoader;
import com.dungeon.engine.resource.ResourceRepository;
import com.dungeon.game.resource.Resources;
import com.typesafe.config.Config;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AnimationLoader implements ResourceLoader<Animation<TextureRegion>> {

	private static final String TYPE = "animation";

	private final ResourceRepository<Animation<TextureRegion>> repository;

	public AnimationLoader(ResourceRepository<Animation<TextureRegion>> repository) {
		this.repository = repository;
	}

	@Override
	public ResourceRepository<Animation<TextureRegion>> getRepository() {
		return repository;
	}

	@Override
	public Animation<TextureRegion> read(Config config) {
		return AnimationLoader.readFrom(config);
	}

	@Override
	public ResourceDescriptor scan(String key, Config config) {
		return new ResourceDescriptor(new ResourceIdentifier(TYPE, key), config, Collections.emptyList());
	}

	public static Animation<TextureRegion> readFrom(Config config) {
		List<Integer> loop = Collections.emptyList();
		List<Integer> sequence = new ArrayList<>();
		if (config.hasPath("loop")) {
			loop = config.getIntList("loop");
		}
		if (config.hasPath("sequence")) {
			sequence = config.getIntList("sequence");
		}
		int tilesize = config.getInt("tilesize");
		String texture = config.getString("texture");
		float frameDuration = (float) config.getDouble("frameDuration");

		if (loop.isEmpty() == sequence.isEmpty()) {
			throw new LoadingException("must have either 'loop' or 'sequence'.");
		}

		Texture tex = Resources.textures.get(texture);
		if (tex.getHeight() % tilesize != 0 || tex.getWidth() % tilesize != 0) {
			throw new LoadingException("texture dimensions must be an exact multiple of the tilesize");
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
