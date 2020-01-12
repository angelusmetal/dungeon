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
import com.typesafe.config.Config;
import com.typesafe.config.ConfigException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class TilesetLoader implements ResourceLoader<Tileset> {

	private static final String TYPE = "tileset";

	private final ResourceRepository<Tileset> repository;

	public TilesetLoader(ResourceRepository<Tileset> repository) {
		this.repository = repository;
	}

	@Override
	public ResourceRepository<Tileset> getRepository() {
		return repository;
	}

	@Override
	public ResourceDescriptor scan(String key, Config descriptor) {
		return new ResourceDescriptor(new ResourceIdentifier(TYPE, key), descriptor, Collections.emptyList());
	}

	@Override
	public Tileset read(String identifier, Config descriptor) {
		String texture = ConfigUtil.requireString(descriptor, "texture");
		Texture tex = Resources.textures.get(texture);
		int tile_width;
		int tile_height;
		try {
			List<Integer> tilesize = ConfigUtil.requireIntList(descriptor, "tilesize");
			tile_width = tilesize.get(0);
			tile_height = tilesize.get(1);
		} catch (ConfigException.WrongType e) {
			tile_width = ConfigUtil.requireInteger(descriptor, "tilesize");
			tile_height = tile_width;
		}
		int columns = tex.getWidth() / tile_width;
		Boolean animated = ConfigUtil.getBoolean(descriptor, "animated").orElse(false);
		List<Animation<TextureRegion>> down = getAnimations(descriptor, "down", tex, tile_width, tile_height, columns, animated);
		List<Animation<TextureRegion>> left = getAnimations(descriptor, "left", tex, tile_width, tile_height, columns, animated);
		List<Animation<TextureRegion>> up = getAnimations(descriptor, "up", tex, tile_width, tile_height, columns, animated);
		List<Animation<TextureRegion>> right = getAnimations(descriptor, "right", tex, tile_width, tile_height, columns, animated);
		List<Animation<TextureRegion>> downRight = getAnimations(descriptor, "downRight", tex, tile_width, tile_height, columns, animated);
		List<Animation<TextureRegion>> downLeft = getAnimations(descriptor, "downLeft", tex, tile_width, tile_height, columns, animated);
		List<Animation<TextureRegion>> upLeft = getAnimations(descriptor, "upLeft", tex, tile_width, tile_height, columns, animated);
		List<Animation<TextureRegion>> upRight = getAnimations(descriptor, "upRight", tex, tile_width, tile_height, columns, animated);
		List<Animation<TextureRegion>> upDown = getAnimations(descriptor, "upDown", tex, tile_width, tile_height, columns, animated);
		List<Animation<TextureRegion>> leftRight = getAnimations(descriptor, "leftRight", tex, tile_width, tile_height, columns, animated);
		List<Animation<TextureRegion>> downLeftRight = getAnimations(descriptor, "downLeftRight", tex, tile_width, tile_height, columns, animated);
		List<Animation<TextureRegion>> upDownLeft = getAnimations(descriptor, "upDownLeft", tex, tile_width, tile_height, columns, animated);
		List<Animation<TextureRegion>> upLeftRight = getAnimations(descriptor, "upLeftRight", tex, tile_width, tile_height, columns, animated);
		List<Animation<TextureRegion>> upDownRight = getAnimations(descriptor, "upDownRight", tex, tile_width, tile_height, columns, animated);
		List<Animation<TextureRegion>> all = getAnimations(descriptor, "all", tex, tile_width, tile_height, columns, animated);
		List<Animation<TextureRegion>> cornerA = getAnimations(descriptor, "cornerA", tex, tile_width, tile_height, columns, animated);
		List<Animation<TextureRegion>> cornerB = getAnimations(descriptor, "cornerB", tex, tile_width, tile_height, columns, animated);
		List<Animation<TextureRegion>> cornerAB = getAnimations(descriptor, "cornerAB", tex, tile_width, tile_height, columns, animated);
		List<Animation<TextureRegion>> cornerC = getAnimations(descriptor, "cornerC", tex, tile_width, tile_height, columns, animated);
		List<Animation<TextureRegion>> cornerAC = getAnimations(descriptor, "cornerAC", tex, tile_width, tile_height, columns, animated);
		List<Animation<TextureRegion>> cornerBC = getAnimations(descriptor, "cornerBC", tex, tile_width, tile_height, columns, animated);
		List<Animation<TextureRegion>> cornerABC = getAnimations(descriptor, "cornerABC", tex, tile_width, tile_height, columns, animated);
		List<Animation<TextureRegion>> cornerD = getAnimations(descriptor, "cornerD", tex, tile_width, tile_height, columns, animated);
		List<Animation<TextureRegion>> cornerAD = getAnimations(descriptor, "cornerAD", tex, tile_width, tile_height, columns, animated);
		List<Animation<TextureRegion>> cornerBD = getAnimations(descriptor, "cornerBD", tex, tile_width, tile_height, columns, animated);
		List<Animation<TextureRegion>> cornerABD = getAnimations(descriptor, "cornerABD", tex, tile_width, tile_height, columns, animated);
		List<Animation<TextureRegion>> cornerCD = getAnimations(descriptor, "cornerCD", tex, tile_width, tile_height, columns, animated);
		List<Animation<TextureRegion>> cornerACD = getAnimations(descriptor, "cornerACD", tex, tile_width, tile_height, columns, animated);
		List<Animation<TextureRegion>> cornerBCD = getAnimations(descriptor, "cornerBCD", tex, tile_width, tile_height, columns, animated);
		List<Animation<TextureRegion>> cornerABCD = getAnimations(descriptor, "cornerABCD", tex, tile_width, tile_height, columns, animated);
		List<Animation<TextureRegion>> none = getAnimations(descriptor, "none", tex, tile_width, tile_height, columns, animated);
		List<Animation<TextureRegion>> cornerABRight = getAnimations(descriptor, "cornerABRight", tex, tile_width, tile_height, columns, animated);
		List<Animation<TextureRegion>> cornerBCDown = getAnimations(descriptor, "cornerBCDown", tex, tile_width, tile_height, columns, animated);
		List<Animation<TextureRegion>> cornerCDLeft = getAnimations(descriptor, "cornerCDLeft", tex, tile_width, tile_height, columns, animated);
		List<Animation<TextureRegion>> cornerADUp = getAnimations(descriptor, "cornerADUp", tex, tile_width, tile_height, columns, animated);
		List<Animation<TextureRegion>> cornerARight = getAnimations(descriptor, "cornerARight", tex, tile_width, tile_height, columns, animated);
		List<Animation<TextureRegion>> cornerBRight = getAnimations(descriptor, "cornerBRight", tex, tile_width, tile_height, columns, animated);
		List<Animation<TextureRegion>> cornerBDown = getAnimations(descriptor, "cornerBDown", tex, tile_width, tile_height, columns, animated);
		List<Animation<TextureRegion>> cornerCDown = getAnimations(descriptor, "cornerCDown", tex, tile_width, tile_height, columns, animated);
		List<Animation<TextureRegion>> cornerDLeft = getAnimations(descriptor, "cornerDLeft", tex, tile_width, tile_height, columns, animated);
		List<Animation<TextureRegion>> cornerCLeft = getAnimations(descriptor, "cornerCLeft", tex, tile_width, tile_height, columns, animated);
		List<Animation<TextureRegion>> cornerAUp = getAnimations(descriptor, "cornerAUp", tex, tile_width, tile_height, columns, animated);
		List<Animation<TextureRegion>> cornerDUp = getAnimations(descriptor, "cornerDUp", tex, tile_width, tile_height, columns, animated);
		List<Animation<TextureRegion>> cornerBDownRight = getAnimations(descriptor, "cornerBDownRight", tex, tile_width, tile_height, columns, animated);
		List<Animation<TextureRegion>> cornerCDownLeft = getAnimations(descriptor, "cornerCDownLeft", tex, tile_width, tile_height, columns, animated);
		List<Animation<TextureRegion>> cornerDUpLeft = getAnimations(descriptor, "cornerDUpLeft", tex, tile_width, tile_height, columns, animated);
		List<Animation<TextureRegion>> cornerAUpRight = getAnimations(descriptor, "cornerAUpRight", tex, tile_width, tile_height, columns, animated);
		return new Tileset() {
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
		};

	}

	private static List<Animation<TextureRegion>> getAnimations(Config config, String key, Texture tex, int tile_width, int tile_height, int columns, boolean animated) {
		List<Integer> regions = ConfigUtil.requireIntList(config, key).stream().map(Number::intValue).collect(Collectors.toList());
		List<Animation<TextureRegion>> animations = new ArrayList<>();
		if (animated) {
			// If 'animated' is true, all frames are considered part of a single animation
			List<TextureRegion> frames = new ArrayList<>();
			for (int frame : regions) {
				frames.add(getFrame(tex, frame, tile_width, tile_height, columns));
			}
			animations.add(new Animation<>(0.15f, frames.toArray(new TextureRegion[0])));
		} else {
			// If 'animated' is false, each frame is an animation (single frame) on its own
			for (int frame : regions) {
				animations.add(getAnimation(tex, frame, tile_width, tile_height, columns));
			}
		}
		return animations;
	}

	private static Animation<TextureRegion> getFrame(Texture tex, int frame, int tilesize, int columns) {
		// TODO Use Resources here to reuse previously loaded animations??
		return new Animation<>(0f, new TextureRegion(tex, tilesize * (frame % columns), tilesize * (frame / columns), tilesize, tilesize));
	}

	private static TextureRegion getFrame(Texture tex, int frame, int tile_width, int tile_height, int columns) {
		// TODO Use Resources here to reuse previously loaded animations??
		return new TextureRegion(tex, tile_width * (frame % columns), tile_height * (frame / columns), tile_width, tile_height);
	}
	private static Animation<TextureRegion> getAnimation(Texture tex, int frame, int tile_width, int tile_height, int columns) {
		// TODO Use Resources here to reuse previously loaded animations??
		return new Animation<>(0f, new TextureRegion(tex, tile_width * (frame % columns), tile_height * (frame / columns), tile_width, tile_height));
	}

}
