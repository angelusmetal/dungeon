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
		int tilesize = ConfigUtil.requireInteger(descriptor, "tilesize");
		int columns = tex.getWidth() / tilesize;
		List<Animation<TextureRegion>> out = getFrames(descriptor, "out", tex, tilesize, columns);
		List<Animation<TextureRegion>> floor = getFrames(descriptor, "floor", tex, tilesize, columns);
		List<Animation<TextureRegion>> convexDL = getFrames(descriptor, "convexDL", tex, tilesize, columns);
		List<Animation<TextureRegion>> convexDR = getFrames(descriptor, "convexDR", tex, tilesize, columns);
		List<Animation<TextureRegion>> convexUL = getFrames(descriptor, "convexUL", tex, tilesize, columns);
		List<Animation<TextureRegion>> convexUR = getFrames(descriptor, "convexUR", tex, tilesize, columns);
		List<Animation<TextureRegion>> concaveDL = getFrames(descriptor, "concaveDL", tex, tilesize, columns);
		List<Animation<TextureRegion>> concaveDR = getFrames(descriptor, "concaveDR", tex, tilesize, columns);
		List<Animation<TextureRegion>> concaveUL = getFrames(descriptor, "concaveUL", tex, tilesize, columns);
		List<Animation<TextureRegion>> concaveUR = getFrames(descriptor, "concaveUR", tex, tilesize, columns);
		List<Animation<TextureRegion>> concaveDown = getFrames(descriptor, "concaveDown", tex, tilesize, columns);
		List<Animation<TextureRegion>> concaveUp = getFrames(descriptor, "concaveUp", tex, tilesize, columns);
		List<Animation<TextureRegion>> concaveLeft = getFrames(descriptor, "concaveLeft", tex, tilesize, columns);
		List<Animation<TextureRegion>> concaveRight = getFrames(descriptor, "concaveRight", tex, tilesize, columns);
		List<Animation<TextureRegion>> wallDecor1 = getFrames(descriptor, "wallDecor1", tex, tilesize, columns);
		List<Animation<TextureRegion>> wallDecor2 = getFrames(descriptor, "wallDecor2", tex, tilesize, columns);
		List<Animation<TextureRegion>> wallDecor3 = getFrames(descriptor, "wallDecor3", tex, tilesize, columns);
		List<Animation<TextureRegion>> wallDecor4 = getFrames(descriptor, "wallDecor4", tex, tilesize, columns);
		return new Tileset(tex, tilesize) {
			@Override public Animation<TextureRegion> out() {
				return Rand.pick(out);
			}

			@Override public Animation<TextureRegion> floor() {
				return Rand.pick(floor);
			}

			@Override public Animation<TextureRegion> convexLowerLeft() {
				return Rand.pick(convexDL);
			}

			@Override public Animation<TextureRegion> convexLowerRight() {
				return Rand.pick(convexDR);
			}

			@Override public Animation<TextureRegion> convexUpperLeft() {
				return Rand.pick(convexUL);
			}

			@Override public Animation<TextureRegion> convexUpperRight() {
				return Rand.pick(convexUR);
			}

			@Override public Animation<TextureRegion> concaveLowerLeft() {
				return Rand.pick(concaveDL);
			}

			@Override public Animation<TextureRegion> concaveLowerRight() {
				return Rand.pick(concaveDR);
			}

			@Override public Animation<TextureRegion> concaveUpperLeft() {
				return Rand.pick(concaveUL);
			}

			@Override public Animation<TextureRegion> concaveUpperRight() {
				return Rand.pick(concaveUR);
			}

			@Override public Animation<TextureRegion> concaveLower() {
				return Rand.pick(concaveDown);
			}

			@Override public Animation<TextureRegion> concaveUpper() {
				return Rand.pick(concaveUp);
			}

			@Override public Animation<TextureRegion> concaveLeft() {
				return Rand.pick(concaveLeft);
			}

			@Override public Animation<TextureRegion> concaveRight() {
				return Rand.pick(concaveRight);
			}

			@Override public Animation<TextureRegion> wallDecoration1() {
				return Rand.pick(wallDecor1);
			}

			@Override public Animation<TextureRegion> wallDecoration2() {
				return Rand.pick(wallDecor2);
			}

			@Override public Animation<TextureRegion> wallDecoration3() {
				return Rand.pick(wallDecor3);
			}

			@Override public Animation<TextureRegion> wallDecoration4() {
				return Rand.pick(wallDecor4);
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
		return new Animation<>(0f, new TextureRegion(tex, tilesize * (frame % columns), tilesize * (frame / columns), tilesize, tilesize));
	}

}
