package com.dungeon.engine.resource;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.dungeon.engine.render.Tile;
import com.dungeon.engine.util.ConfigUtil;
import com.dungeon.engine.util.Rand;
import com.dungeon.game.tileset.LevelTileset;
import com.moandjiezana.toml.Toml;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class TilesetReader {

	public static LevelTileset read(Toml toml) {
		String texture = ConfigUtil.getString(toml, "texture").orElseThrow(missing("texture"));
		Texture tex = ResourceManager.getTexture(texture);
		int tilesize = ConfigUtil.getInteger(toml, "tilesize").orElseThrow(missing("tilesize"));
		int columns = tex.getWidth() / tilesize;
		List<Tile> out = getFrames(toml, "out", tex, tilesize, columns);
		List<Tile> floor = getFrames(toml, "floor", tex, tilesize, columns);
		List<Tile> convexDL = getFrames(toml, "convexDL", tex, tilesize, columns);
		List<Tile> convexDR = getFrames(toml, "convexDR", tex, tilesize, columns);
		List<Tile> convexUL = getFrames(toml, "convexUL", tex, tilesize, columns);
		List<Tile> convexUR = getFrames(toml, "convexUR", tex, tilesize, columns);
		List<Tile> concaveDL = getFrames(toml, "concaveDL", tex, tilesize, columns);
		List<Tile> concaveDR = getFrames(toml, "concaveDR", tex, tilesize, columns);
		List<Tile> concaveUL = getFrames(toml, "concaveUL", tex, tilesize, columns);
		List<Tile> concaveUR = getFrames(toml, "concaveUR", tex, tilesize, columns);
		List<Tile> concaveDown = getFrames(toml, "concaveDown", tex, tilesize, columns);
		List<Tile> concaveUp = getFrames(toml, "concaveUp", tex, tilesize, columns);
		List<Tile> concaveLeft = getFrames(toml, "concaveLeft", tex, tilesize, columns);
		List<Tile> concaveRight = getFrames(toml, "concaveRight", tex, tilesize, columns);
		List<Tile> wallDecor1 = getFrames(toml, "wallDecor1", tex, tilesize, columns);
		List<Tile> wallDecor2 = getFrames(toml, "wallDecor2", tex, tilesize, columns);
		List<Tile> wallDecor3 = getFrames(toml, "wallDecor3", tex, tilesize, columns);
		List<Tile> wallDecor4 = getFrames(toml, "wallDecor4", tex, tilesize, columns);
		return new LevelTileset(tex, tilesize) {
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

	private static Supplier<RuntimeException> missing(String property) {
		return () -> new RuntimeException("Missing property '" + property + "'");
	}

	private static List<Tile> getFrames(Toml toml, String key, Texture tex, int tilesize, int columns) {
		List<Integer> regions = ConfigUtil.<Number>getList(toml, key).orElseThrow(missing(key)).stream().map(Number::intValue).collect(Collectors.toList());
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
