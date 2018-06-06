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
	float[][] loop;
	float[][] sequence;

	public String getName() {
		return name;
	}

	public void load() {
		if (loop != null && sequence != null) {
			throw new RuntimeException("Error while loading animation '" + name + "': cannot have both 'loop' AND 'sequence'");
		}
		if (loop == null && sequence == null) {
			throw new RuntimeException("Error while loading animation '" + name + "': must have either 'loop' or 'sequence'.");
		}

		Texture tex = ResourceManager.getTexture(this.texture);
		if (loop != null) {
			List<TextureRegion> frames = getFrames(tex, loop);
			ResourceManager.loadAnimation(name, loop(frameDuration, frames));
		} else {
			List<TextureRegion> frames = getFrames(tex, sequence);
			ResourceManager.loadAnimation(name, sequence(frameDuration, frames));
		}

	}

	private List<TextureRegion> getFrames(Texture tex, float[][] regions) {
		List<TextureRegion> frames = new ArrayList<>();
		for (float[] regionDef : regions) {
			if (regionDef.length == 2) {
				frames.add(getTile(tex, regionDef[0], regionDef[1]));
			} else if (regionDef.length == 4) {
				frames.add(getTile(tex, regionDef[0], regionDef[1], regionDef[2], regionDef[3]));
			} else {
				throw new RuntimeException("Error while loading animation '" + name + "': each region must have either 2 or 4 parameters.");
			}
		}
		return frames;
	}

	private TextureRegion getTile(Texture tex, float x, float y) {
		return new TextureRegion(tex, (int) (tilesize * x), (int) (tilesize * y), tilesize, tilesize);
	}

	private TextureRegion getTile(Texture tex, float x, float y, float x_tiles, float y_tiles) {
		return new TextureRegion(tex, (int) (tilesize * x), (int) (tilesize * y), (int) (tilesize * x_tiles), (int) (tilesize * y_tiles));
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
