package com.dungeon.engine.resource;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import java.util.ArrayList;
import java.util.List;

public class AnimationDef {
	String texture;
	String name;
	int tilesize;
	float frameDuration;
	int[] loop;
	int[] sequence;

	private int columns;

	public void load(String name) {
		this.name = name;
		if (loop != null && sequence != null) {
			throw new RuntimeException("Error while loading animation '" + name + "': cannot have both 'loop' AND 'sequence'");
		}
		if (loop == null && sequence == null) {
			throw new RuntimeException("Error while loading animation '" + name + "': must have either 'loop' or 'sequence'.");
		}

		Texture tex = ResourceManager.getTexture(this.texture);
		if (tex.getHeight() % tilesize != 0 || tex.getWidth() % tilesize != 0) {
			throw new RuntimeException("Error while loading animation '" + name + "': texture dimensions must be an exact multiple of the tilesize");
		}
		columns = tex.getWidth() / tilesize;
		if (loop != null) {
			List<TextureRegion> frames = getFrames(tex, loop);
			ResourceManager.loadAnimation(name, loop(frameDuration, frames));
		} else {
			List<TextureRegion> frames = getFrames(tex, sequence);
			ResourceManager.loadAnimation(name, sequence(frameDuration, frames));
		}
	}

	private List<TextureRegion> getFrames(Texture tex, int[] regions) {
		List<TextureRegion> frames = new ArrayList<>();
		for (int frame : regions) {
			frames.add(getFrame(tex, frame));
		}
		return frames;
	}

	private TextureRegion getFrame(Texture tex, int frame) {
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
