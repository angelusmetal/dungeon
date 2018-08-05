package com.dungeon.engine.resource.loader;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.dungeon.engine.render.Tile;
import com.dungeon.engine.resource.ResourceDescriptor;
import com.dungeon.engine.resource.ResourceIdentifier;
import com.dungeon.engine.resource.ResourceLoader;
import com.dungeon.engine.resource.ResourceManager;
import com.dungeon.engine.util.ConfigUtil;
import com.dungeon.engine.util.Rand;
import com.dungeon.game.tileset.Tileset;
import com.moandjiezana.toml.Toml;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class TilesetLoader implements ResourceLoader<Tileset> {

	private static final String TYPE = "tileset";

	private final Map<String, Tileset> repository;

	public TilesetLoader(Map<String, Tileset> repository) {
		this.repository = repository;
	}

	@Override
	public Map<String, Tileset> getRepository() {
		return repository;
	}

	@Override
	public ResourceDescriptor scan(String key, Toml descriptor) {
		return new ResourceDescriptor(new ResourceIdentifier(TYPE, key), descriptor, Collections.emptyList());
	}

	@Override
	public Tileset read(Toml descriptor) {
		String texture = ConfigUtil.requireString(descriptor, "texture");
		Texture tex = ResourceManager.getTexture(texture);
		int tilesize = ConfigUtil.requireInteger(descriptor, "tilesize");
		int columns = tex.getWidth() / tilesize;
		List<Tile> out = getFrames(descriptor, "out", tex, tilesize, columns);
		List<Tile> floor = getFrames(descriptor, "floor", tex, tilesize, columns);
		List<Tile> convexDL = getFrames(descriptor, "convexDL", tex, tilesize, columns);
		List<Tile> convexDR = getFrames(descriptor, "convexDR", tex, tilesize, columns);
		List<Tile> convexUL = getFrames(descriptor, "convexUL", tex, tilesize, columns);
		List<Tile> convexUR = getFrames(descriptor, "convexUR", tex, tilesize, columns);
		List<Tile> concaveDL = getFrames(descriptor, "concaveDL", tex, tilesize, columns);
		List<Tile> concaveDR = getFrames(descriptor, "concaveDR", tex, tilesize, columns);
		List<Tile> concaveUL = getFrames(descriptor, "concaveUL", tex, tilesize, columns);
		List<Tile> concaveUR = getFrames(descriptor, "concaveUR", tex, tilesize, columns);
		List<Tile> concaveDown = getFrames(descriptor, "concaveDown", tex, tilesize, columns);
		List<Tile> concaveUp = getFrames(descriptor, "concaveUp", tex, tilesize, columns);
		List<Tile> concaveLeft = getFrames(descriptor, "concaveLeft", tex, tilesize, columns);
		List<Tile> concaveRight = getFrames(descriptor, "concaveRight", tex, tilesize, columns);
		List<Tile> wallDecor1 = getFrames(descriptor, "wallDecor1", tex, tilesize, columns);
		List<Tile> wallDecor2 = getFrames(descriptor, "wallDecor2", tex, tilesize, columns);
		List<Tile> wallDecor3 = getFrames(descriptor, "wallDecor3", tex, tilesize, columns);
		List<Tile> wallDecor4 = getFrames(descriptor, "wallDecor4", tex, tilesize, columns);
		return new Tileset(tex, tilesize) {
			@Override public Tile out() {
				return Rand.pick(out);
			}

			@Override public Tile floor() {
				return Rand.pick(floor);
			}

			@Override public Tile convexLowerLeft() {
				return Rand.pick(convexDL);
			}

			@Override public Tile convexLowerRight() {
				return Rand.pick(convexDR);
			}

			@Override public Tile convexUpperLeft() {
				return Rand.pick(convexUL);
			}

			@Override public Tile convexUpperRight() {
				return Rand.pick(convexUR);
			}

			@Override public Tile concaveLowerLeft() {
				return Rand.pick(concaveDL);
			}

			@Override public Tile concaveLowerRight() {
				return Rand.pick(concaveDR);
			}

			@Override public Tile concaveUpperLeft() {
				return Rand.pick(concaveUL);
			}

			@Override public Tile concaveUpperRight() {
				return Rand.pick(concaveUR);
			}

			@Override public Tile concaveLower() {
				return Rand.pick(concaveDown);
			}

			@Override public Tile concaveUpper() {
				return Rand.pick(concaveUp);
			}

			@Override public Tile concaveLeft() {
				return Rand.pick(concaveLeft);
			}

			@Override public Tile concaveRight() {
				return Rand.pick(concaveRight);
			}

			@Override public Tile wallDecoration1() {
				return Rand.pick(wallDecor1);
			}

			@Override public Tile wallDecoration2() {
				return Rand.pick(wallDecor2);
			}

			@Override public Tile wallDecoration3() {
				return Rand.pick(wallDecor3);
			}

			@Override public Tile wallDecoration4() {
				return Rand.pick(wallDecor4);
			}
		};

	}

	private static List<Tile> getFrames(Toml toml, String key, Texture tex, int tilesize, int columns) {
		List<Integer> regions = ConfigUtil.<Number>requireList(toml, key).stream().map(Number::intValue).collect(Collectors.toList());
		List<Tile> frames = new ArrayList<>();
		for (int frame : regions) {
			frames.add(getFrame(tex, frame, tilesize, columns));
		}
		return frames;
	}

	private static Tile getFrame(Texture tex, int frame, int tilesize, int columns) {
		return new Tile(new TextureRegion(tex, tilesize * (frame % columns), tilesize * (frame / columns), tilesize, tilesize));
	}

}
