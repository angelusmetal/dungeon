package com.dungeon.engine.resource;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.dungeon.engine.entity.Traits;
import com.dungeon.engine.util.ConfigUtil;
import com.moandjiezana.toml.Toml;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class AnimationReader {

	public static Animation<TextureRegion> read(Toml toml) {
		List<Integer> loop = new ArrayList<>();
		List<Integer> sequence = new ArrayList<>();
		ConfigUtil.<Number>getList(toml, "loop").ifPresent(list -> list.stream().map(Number::intValue).forEach(loop::add));
		ConfigUtil.<Number>getList(toml, "sequence").ifPresent(list -> list.stream().map(Number::intValue).forEach(sequence::add));
		int tilesize = ConfigUtil.getInteger(toml, "tilesize").orElseThrow(missing("tilesize"));
		String texture = ConfigUtil.getString(toml, "texture").orElseThrow(missing("texture"));
		float frameDuration = ConfigUtil.getFloat(toml, "frameDuration").orElseThrow(missing("frameDuration"));

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

	private static Supplier<LoadingException> missing(String property) {
		return () -> new LoadingException("Missing property '" + property + "'", "???");
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
