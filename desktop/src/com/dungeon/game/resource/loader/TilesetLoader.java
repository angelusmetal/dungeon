package com.dungeon.game.resource.loader;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.dungeon.engine.render.Material;
import com.dungeon.engine.resource.*;
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
		List<Animation<Material>> down = getAnimations(descriptor, texture, "down", animated);
		List<Animation<Material>> left = getAnimations(descriptor, texture, "left", animated);
		List<Animation<Material>> up = getAnimations(descriptor, texture, "up", animated);
		List<Animation<Material>> right = getAnimations(descriptor, texture, "right", animated);
		List<Animation<Material>> downRight = getAnimations(descriptor, texture, "downRight", animated);
		List<Animation<Material>> downLeft = getAnimations(descriptor, texture, "downLeft", animated);
		List<Animation<Material>> upLeft = getAnimations(descriptor, texture, "upLeft", animated);
		List<Animation<Material>> upRight = getAnimations(descriptor, texture, "upRight", animated);
		List<Animation<Material>> upDown = getAnimations(descriptor, texture, "upDown", animated);
		List<Animation<Material>> leftRight = getAnimations(descriptor, texture, "leftRight", animated);
		List<Animation<Material>> downLeftRight = getAnimations(descriptor, texture, "downLeftRight", animated);
		List<Animation<Material>> upDownLeft = getAnimations(descriptor, texture, "upDownLeft", animated);
		List<Animation<Material>> upLeftRight = getAnimations(descriptor, texture, "upLeftRight", animated);
		List<Animation<Material>> upDownRight = getAnimations(descriptor, texture, "upDownRight", animated);
		List<Animation<Material>> all = getAnimations(descriptor, texture, "all", animated);
		List<Animation<Material>> cornerA = getAnimations(descriptor, texture, "cornerA", animated);
		List<Animation<Material>> cornerB = getAnimations(descriptor, texture, "cornerB", animated);
		List<Animation<Material>> cornerAB = getAnimations(descriptor, texture, "cornerAB", animated);
		List<Animation<Material>> cornerC = getAnimations(descriptor, texture, "cornerC", animated);
		List<Animation<Material>> cornerAC = getAnimations(descriptor, texture, "cornerAC", animated);
		List<Animation<Material>> cornerBC = getAnimations(descriptor, texture, "cornerBC", animated);
		List<Animation<Material>> cornerABC = getAnimations(descriptor, texture, "cornerABC", animated);
		List<Animation<Material>> cornerD = getAnimations(descriptor, texture, "cornerD", animated);
		List<Animation<Material>> cornerAD = getAnimations(descriptor, texture, "cornerAD", animated);
		List<Animation<Material>> cornerBD = getAnimations(descriptor, texture, "cornerBD", animated);
		List<Animation<Material>> cornerABD = getAnimations(descriptor, texture, "cornerABD", animated);
		List<Animation<Material>> cornerCD = getAnimations(descriptor, texture, "cornerCD", animated);
		List<Animation<Material>> cornerACD = getAnimations(descriptor, texture, "cornerACD", animated);
		List<Animation<Material>> cornerBCD = getAnimations(descriptor, texture, "cornerBCD", animated);
		List<Animation<Material>> cornerABCD = getAnimations(descriptor, texture, "cornerABCD", animated);
		List<Animation<Material>> none = getAnimations(descriptor, texture, "none", animated);
		List<Animation<Material>> cornerABRight = getAnimations(descriptor, texture, "cornerABRight", animated);
		List<Animation<Material>> cornerBCDown = getAnimations(descriptor, texture, "cornerBCDown", animated);
		List<Animation<Material>> cornerCDLeft = getAnimations(descriptor, texture, "cornerCDLeft", animated);
		List<Animation<Material>> cornerADUp = getAnimations(descriptor, texture, "cornerADUp", animated);
		List<Animation<Material>> cornerARight = getAnimations(descriptor, texture, "cornerARight", animated);
		List<Animation<Material>> cornerBRight = getAnimations(descriptor, texture, "cornerBRight", animated);
		List<Animation<Material>> cornerBDown = getAnimations(descriptor, texture, "cornerBDown", animated);
		List<Animation<Material>> cornerCDown = getAnimations(descriptor, texture, "cornerCDown", animated);
		List<Animation<Material>> cornerDLeft = getAnimations(descriptor, texture, "cornerDLeft", animated);
		List<Animation<Material>> cornerCLeft = getAnimations(descriptor, texture, "cornerCLeft", animated);
		List<Animation<Material>> cornerAUp = getAnimations(descriptor, texture, "cornerAUp", animated);
		List<Animation<Material>> cornerDUp = getAnimations(descriptor, texture, "cornerDUp", animated);
		List<Animation<Material>> cornerBDownRight = getAnimations(descriptor, texture, "cornerBDownRight", animated);
		List<Animation<Material>> cornerCDownLeft = getAnimations(descriptor, texture, "cornerCDownLeft", animated);
		List<Animation<Material>> cornerDUpLeft = getAnimations(descriptor, texture, "cornerDUpLeft", animated);
		List<Animation<Material>> cornerAUpRight = getAnimations(descriptor, texture, "cornerAUpRight", animated);
		return new Tileset() {
			@Override public Animation<Material> down() {
				return Rand.pick(down);
			}
			@Override public Animation<Material> left() {
				return Rand.pick(left);
			}
			@Override public Animation<Material> up() {
				return Rand.pick(up);
			}
			@Override public Animation<Material> right() {
				return Rand.pick(right);
			}
			@Override public Animation<Material> downRight() {
				return Rand.pick(downRight);
			}
			@Override public Animation<Material> downLeft() {
				return Rand.pick(downLeft);
			}
			@Override public Animation<Material> upLeft() {
				return Rand.pick(upLeft);
			}
			@Override public Animation<Material> upRight() {
				return Rand.pick(upRight);
			}
			@Override public Animation<Material> upDown() {
				return Rand.pick(upDown);
			}
			@Override public Animation<Material> leftRight() {
				return Rand.pick(leftRight);
			}
			@Override public Animation<Material> downLeftRight() {
				return Rand.pick(downLeftRight);
			}
			@Override public Animation<Material> upDownLeft() {
				return Rand.pick(upDownLeft);
			}
			@Override public Animation<Material> upLeftRight() {
				return Rand.pick(upLeftRight);
			}
			@Override public Animation<Material> upDownRight() {
				return Rand.pick(upDownRight);
			}
			@Override public Animation<Material> all() {
				return Rand.pick(all);
			}
			@Override public Animation<Material> cornerA() {
				return Rand.pick(cornerA);
			}
			@Override public Animation<Material> cornerB() {
				return Rand.pick(cornerB);
			}
			@Override public Animation<Material> cornerAB() {
				return Rand.pick(cornerAB);
			}
			@Override public Animation<Material> cornerC() {
				return Rand.pick(cornerC);
			}
			@Override public Animation<Material> cornerAC() {
				return Rand.pick(cornerAC);
			}
			@Override public Animation<Material> cornerBC() {
				return Rand.pick(cornerBC);
			}
			@Override public Animation<Material> cornerABC() {
				return Rand.pick(cornerABC);
			}
			@Override public Animation<Material> cornerD() {
				return Rand.pick(cornerD);
			}
			@Override public Animation<Material> cornerAD() {
				return Rand.pick(cornerAD);
			}
			@Override public Animation<Material> cornerBD() {
				return Rand.pick(cornerBD);
			}
			@Override public Animation<Material> cornerABD() {
				return Rand.pick(cornerABD);
			}
			@Override public Animation<Material> cornerCD() {
				return Rand.pick(cornerCD);
			}
			@Override public Animation<Material> cornerACD() {
				return Rand.pick(cornerACD);
			}
			@Override public Animation<Material> cornerBCD() {
				return Rand.pick(cornerBCD);
			}
			@Override public Animation<Material> cornerABCD() {
				return Rand.pick(cornerABCD);
			}
			@Override public Animation<Material> none() {
				return Rand.pick(none);
			}
			@Override public Animation<Material> cornerABRight() {
				return Rand.pick(cornerABRight);
			}
			@Override public Animation<Material> cornerBCDown() {
				return Rand.pick(cornerBCDown);
			}
			@Override public Animation<Material> cornerCDLeft() {
				return Rand.pick(cornerCDLeft);
			}
			@Override public Animation<Material> cornerADUp() {
				return Rand.pick(cornerADUp);
			}
			@Override public Animation<Material> cornerARight() {
				return Rand.pick(cornerARight);
			}
			@Override public Animation<Material> cornerBRight() {
				return Rand.pick(cornerBRight);
			}
			@Override public Animation<Material> cornerBDown() {
				return Rand.pick(cornerBDown);
			}
			@Override public Animation<Material> cornerCDown() {
				return Rand.pick(cornerCDown);
			}
			@Override public Animation<Material> cornerDLeft() {
				return Rand.pick(cornerDLeft);
			}
			@Override public Animation<Material> cornerCLeft() {
				return Rand.pick(cornerCLeft);
			}
			@Override public Animation<Material> cornerAUp() {
				return Rand.pick(cornerAUp);
			}
			@Override public Animation<Material> cornerDUp() {
				return Rand.pick(cornerDUp);
			}
			@Override public Animation<Material> cornerBDownRight() {
				return Rand.pick(cornerBDownRight);
			}
			@Override public Animation<Material> cornerCDownLeft() {
				return Rand.pick(cornerCDownLeft);
			}
			@Override public Animation<Material> cornerDUpLeft() {
				return Rand.pick(cornerDUpLeft);
			}
			@Override public Animation<Material> cornerAUpRight() {
				return Rand.pick(cornerAUpRight);
			}
		};

	}

	private static List<Animation<Material>> getAnimations(Config config, String name, String key, boolean animated) {
		List<Integer> regions = ConfigUtil.requireIntList(config, key).stream().map(Number::intValue).collect(Collectors.toList());
		List<Animation<Material>> animations = new ArrayList<>();
		if (animated) {
			// If 'animated' is true, all frames are considered part of a single animation
			List<Material> frames = new ArrayList<>();
			for (int frame : regions) {
				Sprite diffuse = Resources.loadSprite(name, frame);
				Sprite normal = Resources.loadSprite("normal_map/" + name, frame);
				frames.add(new Material(diffuse, normal));
			}
			animations.add(new Animation<>(0.15f, frames.toArray(new Material[0])));
		} else {
			// If 'animated' is false, each frame is an animation (single frame) on its own
			for (int frame : regions) {
				Sprite diffuse = Resources.loadSprite(name, frame);
				Sprite normal = Resources.loadSprite("normal_map/" + name, frame);
				animations.add(new Animation<>(0f, new Material(diffuse, normal)));
			}
		}
		return animations;
	}

}
