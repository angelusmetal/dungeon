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

	public void load() {
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
		System.out.println("Loaded animation " + name);
	}

	private List<TextureRegion> getFrames(Texture tex, int[] regions) {
		List<TextureRegion> frames = new ArrayList<>();
		for (int frame : regions) {
			frames.add(getFrame(tex, frame));
//			if (regionDef.length == 2) {
//				frames.add(getTile(tex, regionDef[0], regionDef[1]));
//			} else if (regionDef.length == 4) {
//				frames.add(getTile(tex, regionDef[0], regionDef[1], regionDef[2], regionDef[3]));
//			} else {
//				throw new RuntimeException("Error while loading animation '" + name + "': each region must have either 2 or 4 parameters.");
//			}
		}
		return frames;
	}

	private TextureRegion getFrame(Texture tex, int frame) {
		System.out.println("    Getting frame " + frame + " - " + (tilesize * (frame % columns)) + ", " + (tilesize * (frame / columns)));
		return new TextureRegion(tex, tilesize * (frame % columns), tilesize * (frame / columns), tilesize, tilesize);
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
