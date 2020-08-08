package com.dungeon.game.resource.loader;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.dungeon.engine.resource.ResourceDescriptor;
import com.dungeon.engine.resource.ResourceIdentifier;
import com.dungeon.engine.resource.ResourceLoader;
import com.dungeon.engine.resource.ResourceRepository;
import com.dungeon.engine.resource.Resources;
import com.dungeon.engine.util.ConfigUtil;
import com.dungeon.engine.util.Rand;
import com.dungeon.game.tileset.Tileset;
import com.typesafe.config.Config;

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
		Boolean animated = ConfigUtil.getBoolean(descriptor, "animated").orElse(false);
		List<Animation<Sprite>> down = getAnimations(descriptor, texture, "down", animated);
		List<Animation<Sprite>> left = getAnimations(descriptor, texture, "left", animated);
		List<Animation<Sprite>> up = getAnimations(descriptor, texture, "up", animated);
		List<Animation<Sprite>> right = getAnimations(descriptor, texture, "right", animated);
		List<Animation<Sprite>> downRight = getAnimations(descriptor, texture, "downRight", animated);
		List<Animation<Sprite>> downLeft = getAnimations(descriptor, texture, "downLeft", animated);
		List<Animation<Sprite>> upLeft = getAnimations(descriptor, texture, "upLeft", animated);
		List<Animation<Sprite>> upRight = getAnimations(descriptor, texture, "upRight", animated);
		List<Animation<Sprite>> upDown = getAnimations(descriptor, texture, "upDown", animated);
		List<Animation<Sprite>> leftRight = getAnimations(descriptor, texture, "leftRight", animated);
		List<Animation<Sprite>> downLeftRight = getAnimations(descriptor, texture, "downLeftRight", animated);
		List<Animation<Sprite>> upDownLeft = getAnimations(descriptor, texture, "upDownLeft", animated);
		List<Animation<Sprite>> upLeftRight = getAnimations(descriptor, texture, "upLeftRight", animated);
		List<Animation<Sprite>> upDownRight = getAnimations(descriptor, texture, "upDownRight", animated);
		List<Animation<Sprite>> all = getAnimations(descriptor, texture, "all", animated);
		List<Animation<Sprite>> cornerA = getAnimations(descriptor, texture, "cornerA", animated);
		List<Animation<Sprite>> cornerB = getAnimations(descriptor, texture, "cornerB", animated);
		List<Animation<Sprite>> cornerAB = getAnimations(descriptor, texture, "cornerAB", animated);
		List<Animation<Sprite>> cornerC = getAnimations(descriptor, texture, "cornerC", animated);
		List<Animation<Sprite>> cornerAC = getAnimations(descriptor, texture, "cornerAC", animated);
		List<Animation<Sprite>> cornerBC = getAnimations(descriptor, texture, "cornerBC", animated);
		List<Animation<Sprite>> cornerABC = getAnimations(descriptor, texture, "cornerABC", animated);
		List<Animation<Sprite>> cornerD = getAnimations(descriptor, texture, "cornerD", animated);
		List<Animation<Sprite>> cornerAD = getAnimations(descriptor, texture, "cornerAD", animated);
		List<Animation<Sprite>> cornerBD = getAnimations(descriptor, texture, "cornerBD", animated);
		List<Animation<Sprite>> cornerABD = getAnimations(descriptor, texture, "cornerABD", animated);
		List<Animation<Sprite>> cornerCD = getAnimations(descriptor, texture, "cornerCD", animated);
		List<Animation<Sprite>> cornerACD = getAnimations(descriptor, texture, "cornerACD", animated);
		List<Animation<Sprite>> cornerBCD = getAnimations(descriptor, texture, "cornerBCD", animated);
		List<Animation<Sprite>> cornerABCD = getAnimations(descriptor, texture, "cornerABCD", animated);
		List<Animation<Sprite>> none = getAnimations(descriptor, texture, "none", animated);
		List<Animation<Sprite>> cornerABRight = getAnimations(descriptor, texture, "cornerABRight", animated);
		List<Animation<Sprite>> cornerBCDown = getAnimations(descriptor, texture, "cornerBCDown", animated);
		List<Animation<Sprite>> cornerCDLeft = getAnimations(descriptor, texture, "cornerCDLeft", animated);
		List<Animation<Sprite>> cornerADUp = getAnimations(descriptor, texture, "cornerADUp", animated);
		List<Animation<Sprite>> cornerARight = getAnimations(descriptor, texture, "cornerARight", animated);
		List<Animation<Sprite>> cornerBRight = getAnimations(descriptor, texture, "cornerBRight", animated);
		List<Animation<Sprite>> cornerBDown = getAnimations(descriptor, texture, "cornerBDown", animated);
		List<Animation<Sprite>> cornerCDown = getAnimations(descriptor, texture, "cornerCDown", animated);
		List<Animation<Sprite>> cornerDLeft = getAnimations(descriptor, texture, "cornerDLeft", animated);
		List<Animation<Sprite>> cornerCLeft = getAnimations(descriptor, texture, "cornerCLeft", animated);
		List<Animation<Sprite>> cornerAUp = getAnimations(descriptor, texture, "cornerAUp", animated);
		List<Animation<Sprite>> cornerDUp = getAnimations(descriptor, texture, "cornerDUp", animated);
		List<Animation<Sprite>> cornerBDownRight = getAnimations(descriptor, texture, "cornerBDownRight", animated);
		List<Animation<Sprite>> cornerCDownLeft = getAnimations(descriptor, texture, "cornerCDownLeft", animated);
		List<Animation<Sprite>> cornerDUpLeft = getAnimations(descriptor, texture, "cornerDUpLeft", animated);
		List<Animation<Sprite>> cornerAUpRight = getAnimations(descriptor, texture, "cornerAUpRight", animated);
		return new Tileset() {
			@Override public Animation<Sprite> down() {
				return Rand.pick(down);
			}
			@Override public Animation<Sprite> left() {
				return Rand.pick(left);
			}
			@Override public Animation<Sprite> up() {
				return Rand.pick(up);
			}
			@Override public Animation<Sprite> right() {
				return Rand.pick(right);
			}
			@Override public Animation<Sprite> downRight() {
				return Rand.pick(downRight);
			}
			@Override public Animation<Sprite> downLeft() {
				return Rand.pick(downLeft);
			}
			@Override public Animation<Sprite> upLeft() {
				return Rand.pick(upLeft);
			}
			@Override public Animation<Sprite> upRight() {
				return Rand.pick(upRight);
			}
			@Override public Animation<Sprite> upDown() {
				return Rand.pick(upDown);
			}
			@Override public Animation<Sprite> leftRight() {
				return Rand.pick(leftRight);
			}
			@Override public Animation<Sprite> downLeftRight() {
				return Rand.pick(downLeftRight);
			}
			@Override public Animation<Sprite> upDownLeft() {
				return Rand.pick(upDownLeft);
			}
			@Override public Animation<Sprite> upLeftRight() {
				return Rand.pick(upLeftRight);
			}
			@Override public Animation<Sprite> upDownRight() {
				return Rand.pick(upDownRight);
			}
			@Override public Animation<Sprite> all() {
				return Rand.pick(all);
			}
			@Override public Animation<Sprite> cornerA() {
				return Rand.pick(cornerA);
			}
			@Override public Animation<Sprite> cornerB() {
				return Rand.pick(cornerB);
			}
			@Override public Animation<Sprite> cornerAB() {
				return Rand.pick(cornerAB);
			}
			@Override public Animation<Sprite> cornerC() {
				return Rand.pick(cornerC);
			}
			@Override public Animation<Sprite> cornerAC() {
				return Rand.pick(cornerAC);
			}
			@Override public Animation<Sprite> cornerBC() {
				return Rand.pick(cornerBC);
			}
			@Override public Animation<Sprite> cornerABC() {
				return Rand.pick(cornerABC);
			}
			@Override public Animation<Sprite> cornerD() {
				return Rand.pick(cornerD);
			}
			@Override public Animation<Sprite> cornerAD() {
				return Rand.pick(cornerAD);
			}
			@Override public Animation<Sprite> cornerBD() {
				return Rand.pick(cornerBD);
			}
			@Override public Animation<Sprite> cornerABD() {
				return Rand.pick(cornerABD);
			}
			@Override public Animation<Sprite> cornerCD() {
				return Rand.pick(cornerCD);
			}
			@Override public Animation<Sprite> cornerACD() {
				return Rand.pick(cornerACD);
			}
			@Override public Animation<Sprite> cornerBCD() {
				return Rand.pick(cornerBCD);
			}
			@Override public Animation<Sprite> cornerABCD() {
				return Rand.pick(cornerABCD);
			}
			@Override public Animation<Sprite> none() {
				return Rand.pick(none);
			}
			@Override public Animation<Sprite> cornerABRight() {
				return Rand.pick(cornerABRight);
			}
			@Override public Animation<Sprite> cornerBCDown() {
				return Rand.pick(cornerBCDown);
			}
			@Override public Animation<Sprite> cornerCDLeft() {
				return Rand.pick(cornerCDLeft);
			}
			@Override public Animation<Sprite> cornerADUp() {
				return Rand.pick(cornerADUp);
			}
			@Override public Animation<Sprite> cornerARight() {
				return Rand.pick(cornerARight);
			}
			@Override public Animation<Sprite> cornerBRight() {
				return Rand.pick(cornerBRight);
			}
			@Override public Animation<Sprite> cornerBDown() {
				return Rand.pick(cornerBDown);
			}
			@Override public Animation<Sprite> cornerCDown() {
				return Rand.pick(cornerCDown);
			}
			@Override public Animation<Sprite> cornerDLeft() {
				return Rand.pick(cornerDLeft);
			}
			@Override public Animation<Sprite> cornerCLeft() {
				return Rand.pick(cornerCLeft);
			}
			@Override public Animation<Sprite> cornerAUp() {
				return Rand.pick(cornerAUp);
			}
			@Override public Animation<Sprite> cornerDUp() {
				return Rand.pick(cornerDUp);
			}
			@Override public Animation<Sprite> cornerBDownRight() {
				return Rand.pick(cornerBDownRight);
			}
			@Override public Animation<Sprite> cornerCDownLeft() {
				return Rand.pick(cornerCDownLeft);
			}
			@Override public Animation<Sprite> cornerDUpLeft() {
				return Rand.pick(cornerDUpLeft);
			}
			@Override public Animation<Sprite> cornerAUpRight() {
				return Rand.pick(cornerAUpRight);
			}
		};

	}

	private static List<Animation<Sprite>> getAnimations(Config config, String name, String key, boolean animated) {
		List<Integer> regions = ConfigUtil.requireIntList(config, key).stream().map(Number::intValue).collect(Collectors.toList());
		List<Animation<Sprite>> animations = new ArrayList<>();
		if (animated) {
			// If 'animated' is true, all frames are considered part of a single animation
			List<Sprite> frames = new ArrayList<>();
			for (int frame : regions) {
				frames.add(Resources.loadSprite(name, frame));
			}
			animations.add(new Animation<>(0.15f, frames.toArray(new Sprite[0])));
		} else {
			// If 'animated' is false, each frame is an animation (single frame) on its own
			for (int frame : regions) {
				animations.add(new Animation<>(0f, Resources.loadSprite(name, frame)));
			}
		}
		return animations;
	}

}
