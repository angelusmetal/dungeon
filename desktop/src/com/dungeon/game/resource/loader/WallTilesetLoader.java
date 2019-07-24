package com.dungeon.game.resource.loader;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.dungeon.engine.resource.ResourceDescriptor;
import com.dungeon.engine.resource.ResourceIdentifier;
import com.dungeon.engine.resource.ResourceLoader;
import com.dungeon.engine.resource.ResourceRepository;
import com.dungeon.engine.util.ConfigUtil;
import com.dungeon.engine.util.Rand;
import com.dungeon.game.resource.Resources;
import com.dungeon.game.tileset.Tileset;
import com.dungeon.game.tileset.WallTileset;
import com.typesafe.config.Config;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class WallTilesetLoader implements ResourceLoader<WallTileset> {

	private static final String TYPE = "wallTileset";

	private final ResourceRepository<WallTileset> repository;

	public WallTilesetLoader(ResourceRepository<WallTileset> repository) {
		this.repository = repository;
	}

	@Override
	public ResourceRepository<WallTileset> getRepository() {
		return repository;
	}

	@Override
	public ResourceDescriptor scan(String key, Config descriptor) {
		return new ResourceDescriptor(new ResourceIdentifier(TYPE, key), descriptor, Collections.emptyList());
	}

	@Override
	public WallTileset read(String identifier, Config descriptor) {
		String texture = ConfigUtil.requireString(descriptor, "texture");
		Texture tex = Resources.textures.get(texture);
		int tilesize = ConfigUtil.requireInteger(descriptor, "tilesize");
		int columns = tex.getWidth() / tilesize;
		List<Animation<TextureRegion>> down = getFrames(descriptor, "down", tex, tilesize, columns);
		List<Animation<TextureRegion>> left = getFrames(descriptor, "left", tex, tilesize, columns);
		List<Animation<TextureRegion>> up = getFrames(descriptor, "up", tex, tilesize, columns);
		List<Animation<TextureRegion>> right = getFrames(descriptor, "right", tex, tilesize, columns);
		List<Animation<TextureRegion>> downRight = getFrames(descriptor, "downRight", tex, tilesize, columns);
		List<Animation<TextureRegion>> downLeft = getFrames(descriptor, "downLeft", tex, tilesize, columns);
		List<Animation<TextureRegion>> upLeft = getFrames(descriptor, "upLeft", tex, tilesize, columns);
		List<Animation<TextureRegion>> upRight = getFrames(descriptor, "upRight", tex, tilesize, columns);
		List<Animation<TextureRegion>> upDown = getFrames(descriptor, "upDown", tex, tilesize, columns);
		List<Animation<TextureRegion>> leftRight = getFrames(descriptor, "leftRight", tex, tilesize, columns);
		List<Animation<TextureRegion>> downLeftRight = getFrames(descriptor, "downLeftRight", tex, tilesize, columns);
		List<Animation<TextureRegion>> upDownLeft = getFrames(descriptor, "upDownLeft", tex, tilesize, columns);
		List<Animation<TextureRegion>> upLeftRight = getFrames(descriptor, "upLeftRight", tex, tilesize, columns);
		List<Animation<TextureRegion>> upDownRight = getFrames(descriptor, "upDownRight", tex, tilesize, columns);
		List<Animation<TextureRegion>> all = getFrames(descriptor, "all", tex, tilesize, columns);
		List<Animation<TextureRegion>> cornerA = getFrames(descriptor, "cornerA", tex, tilesize, columns);
		List<Animation<TextureRegion>> cornerB = getFrames(descriptor, "cornerB", tex, tilesize, columns);
		List<Animation<TextureRegion>> cornerAB = getFrames(descriptor, "cornerAB", tex, tilesize, columns);
		List<Animation<TextureRegion>> cornerC = getFrames(descriptor, "cornerC", tex, tilesize, columns);
		List<Animation<TextureRegion>> cornerAC = getFrames(descriptor, "cornerAC", tex, tilesize, columns);
		List<Animation<TextureRegion>> cornerBC = getFrames(descriptor, "cornerBC", tex, tilesize, columns);
		List<Animation<TextureRegion>> cornerABC = getFrames(descriptor, "cornerABC", tex, tilesize, columns);
		List<Animation<TextureRegion>> cornerD = getFrames(descriptor, "cornerD", tex, tilesize, columns);
		List<Animation<TextureRegion>> cornerAD = getFrames(descriptor, "cornerAD", tex, tilesize, columns);
		List<Animation<TextureRegion>> cornerBD = getFrames(descriptor, "cornerBD", tex, tilesize, columns);
		List<Animation<TextureRegion>> cornerABD = getFrames(descriptor, "cornerABD", tex, tilesize, columns);
		List<Animation<TextureRegion>> cornerCD = getFrames(descriptor, "cornerCD", tex, tilesize, columns);
		List<Animation<TextureRegion>> cornerACD = getFrames(descriptor, "cornerACD", tex, tilesize, columns);
		List<Animation<TextureRegion>> cornerBCD = getFrames(descriptor, "cornerBCD", tex, tilesize, columns);
		List<Animation<TextureRegion>> cornerABCD = getFrames(descriptor, "cornerABCD", tex, tilesize, columns);
		List<Animation<TextureRegion>> none = getFrames(descriptor, "none", tex, tilesize, columns);
		List<Animation<TextureRegion>> cornerABRight = getFrames(descriptor, "cornerABRight", tex, tilesize, columns);
		List<Animation<TextureRegion>> cornerBCDown = getFrames(descriptor, "cornerBCDown", tex, tilesize, columns);
		List<Animation<TextureRegion>> cornerCDLeft = getFrames(descriptor, "cornerCDLeft", tex, tilesize, columns);
		List<Animation<TextureRegion>> cornerADUp = getFrames(descriptor, "cornerADUp", tex, tilesize, columns);
		List<Animation<TextureRegion>> cornerARight = getFrames(descriptor, "cornerARight", tex, tilesize, columns);
		List<Animation<TextureRegion>> cornerBRight = getFrames(descriptor, "cornerBRight", tex, tilesize, columns);
		List<Animation<TextureRegion>> cornerBDown = getFrames(descriptor, "cornerBDown", tex, tilesize, columns);
		List<Animation<TextureRegion>> cornerCDown = getFrames(descriptor, "cornerCDown", tex, tilesize, columns);
		List<Animation<TextureRegion>> cornerDLeft = getFrames(descriptor, "cornerDLeft", tex, tilesize, columns);
		List<Animation<TextureRegion>> cornerCLeft = getFrames(descriptor, "cornerCLeft", tex, tilesize, columns);
		List<Animation<TextureRegion>> cornerAUp = getFrames(descriptor, "cornerAUp", tex, tilesize, columns);
		List<Animation<TextureRegion>> cornerDUp = getFrames(descriptor, "cornerDUp", tex, tilesize, columns);
		List<Animation<TextureRegion>> cornerBDownRight = getFrames(descriptor, "cornerBDownRight", tex, tilesize, columns);
		List<Animation<TextureRegion>> cornerCDownLeft = getFrames(descriptor, "cornerCDownLeft", tex, tilesize, columns);
		List<Animation<TextureRegion>> cornerDUpLeft = getFrames(descriptor, "cornerDUpLeft", tex, tilesize, columns);
		List<Animation<TextureRegion>> cornerAUpRight = getFrames(descriptor, "cornerAUpRight", tex, tilesize, columns);
		List<Animation<TextureRegion>> decor1 = getFrames(descriptor, "decor1", tex, tilesize, columns);
		List<Animation<TextureRegion>> decor2 = getFrames(descriptor, "decor2", tex, tilesize, columns);
		List<Animation<TextureRegion>> decor3 = getFrames(descriptor, "decor3", tex, tilesize, columns);
		return new WallTileset(tex, tilesize) {
			@Override public Animation<TextureRegion> down() {
				return Rand.pick(down);
			}
			@Override public Animation<TextureRegion> left() {
				return Rand.pick(left);
			}
			@Override public Animation<TextureRegion> up() {
				return Rand.pick(up);
			}
			@Override public Animation<TextureRegion> right() {
				return Rand.pick(right);
			}
			@Override public Animation<TextureRegion> downRight() {
				return Rand.pick(downRight);
			}
			@Override public Animation<TextureRegion> downLeft() {
				return Rand.pick(downLeft);
			}
			@Override public Animation<TextureRegion> upLeft() {
				return Rand.pick(upLeft);
			}
			@Override public Animation<TextureRegion> upRight() {
				return Rand.pick(upRight);
			}
			@Override public Animation<TextureRegion> upDown() {
				return Rand.pick(upDown);
			}
			@Override public Animation<TextureRegion> leftRight() {
				return Rand.pick(leftRight);
			}
			@Override public Animation<TextureRegion> downLeftRight() {
				return Rand.pick(downLeftRight);
			}
			@Override public Animation<TextureRegion> upDownLeft() {
				return Rand.pick(upDownLeft);
			}
			@Override public Animation<TextureRegion> upLeftRight() {
				return Rand.pick(upLeftRight);
			}
			@Override public Animation<TextureRegion> upDownRight() {
				return Rand.pick(upDownRight);
			}
			@Override public Animation<TextureRegion> all() {
				return Rand.pick(all);
			}
			@Override public Animation<TextureRegion> cornerA() {
				return Rand.pick(cornerA);
			}
			@Override public Animation<TextureRegion> cornerB() {
				return Rand.pick(cornerB);
			}
			@Override public Animation<TextureRegion> cornerAB() {
				return Rand.pick(cornerAB);
			}
			@Override public Animation<TextureRegion> cornerC() {
				return Rand.pick(cornerC);
			}
			@Override public Animation<TextureRegion> cornerAC() {
				return Rand.pick(cornerAC);
			}
			@Override public Animation<TextureRegion> cornerBC() {
				return Rand.pick(cornerBC);
			}
			@Override public Animation<TextureRegion> cornerABC() {
				return Rand.pick(cornerABC);
			}
			@Override public Animation<TextureRegion> cornerD() {
				return Rand.pick(cornerD);
			}
			@Override public Animation<TextureRegion> cornerAD() {
				return Rand.pick(cornerAD);
			}
			@Override public Animation<TextureRegion> cornerBD() {
				return Rand.pick(cornerBD);
			}
			@Override public Animation<TextureRegion> cornerABD() {
				return Rand.pick(cornerABD);
			}
			@Override public Animation<TextureRegion> cornerCD() {
				return Rand.pick(cornerCD);
			}
			@Override public Animation<TextureRegion> cornerACD() {
				return Rand.pick(cornerACD);
			}
			@Override public Animation<TextureRegion> cornerBCD() {
				return Rand.pick(cornerBCD);
			}
			@Override public Animation<TextureRegion> cornerABCD() {
				return Rand.pick(cornerABCD);
			}
			@Override public Animation<TextureRegion> none() {
				return Rand.pick(none);
			}
			@Override public Animation<TextureRegion> cornerABRight() {
				return Rand.pick(cornerABRight);
			}
			@Override public Animation<TextureRegion> cornerBCDown() {
				return Rand.pick(cornerBCDown);
			}
			@Override public Animation<TextureRegion> cornerCDLeft() {
				return Rand.pick(cornerCDLeft);
			}
			@Override public Animation<TextureRegion> cornerADUp() {
				return Rand.pick(cornerADUp);
			}
			@Override public Animation<TextureRegion> cornerARight() {
				return Rand.pick(cornerARight);
			}
			@Override public Animation<TextureRegion> cornerBRight() {
				return Rand.pick(cornerBRight);
			}
			@Override public Animation<TextureRegion> cornerBDown() {
				return Rand.pick(cornerBDown);
			}
			@Override public Animation<TextureRegion> cornerCDown() {
				return Rand.pick(cornerCDown);
			}
			@Override public Animation<TextureRegion> cornerDLeft() {
				return Rand.pick(cornerDLeft);
			}
			@Override public Animation<TextureRegion> cornerCLeft() {
				return Rand.pick(cornerCLeft);
			}
			@Override public Animation<TextureRegion> cornerAUp() {
				return Rand.pick(cornerAUp);
			}
			@Override public Animation<TextureRegion> cornerDUp() {
				return Rand.pick(cornerDUp);
			}
			@Override public Animation<TextureRegion> cornerBDownRight() {
				return Rand.pick(cornerBDownRight);
			}
			@Override public Animation<TextureRegion> cornerCDownLeft() {
				return Rand.pick(cornerCDownLeft);
			}
			@Override public Animation<TextureRegion> cornerDUpLeft() {
				return Rand.pick(cornerDUpLeft);
			}
			@Override public Animation<TextureRegion> cornerAUpRight() {
				return Rand.pick(cornerAUpRight);
			}
			@Override public Animation<TextureRegion> wallDecoration1() {
				return Rand.pick(decor1);
			}
			@Override public Animation<TextureRegion> wallDecoration2() {
				return Rand.pick(decor2);
			}
			@Override public Animation<TextureRegion> wallDecoration3() {
				return Rand.pick(decor3);
			}
		};

	}

	private static List<Animation<TextureRegion>> getFrames(Config config, String key, Texture tex, int tilesize, int columns) {
		List<Integer> regions = ConfigUtil.requireIntList(config, key).stream().map(Number::intValue).collect(Collectors.toList());
		List<Animation<TextureRegion>> frames = new ArrayList<>();
		for (int frame : regions) {
			frames.add(getFrame(tex, frame, tilesize, columns));
		}
		return frames;
	}

	private static Animation<TextureRegion> getFrame(Texture tex, int frame, int tilesize, int columns) {
		// TODO Use Resources here to reuse previously loaded animations??
		return new Animation<>(0f,
				new TextureRegion(
						tex,
						tilesize * (frame % columns),
						tilesize * (frame / columns),
						tilesize,
						tilesize * 2)); // Wall tilesets are 2 tiles tall
	}

}
