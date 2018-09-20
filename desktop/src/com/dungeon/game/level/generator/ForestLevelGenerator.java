package com.dungeon.game.level.generator;

import com.badlogic.gdx.math.Vector2;
import com.dungeon.engine.noise.OpenSimplexNoise;
import com.dungeon.engine.util.Rand;
import com.dungeon.engine.util.Util;
import com.dungeon.game.level.Level;
import com.dungeon.game.level.entity.EntityPlaceholder;
import com.dungeon.game.level.entity.EntityType;
import com.dungeon.game.tileset.Environment;

public class ForestLevelGenerator implements LevelGenerator {

	private final OpenSimplexNoise noise;
	private final Environment environment;
	private final int width;
	private final int height;
	private final double featureSize;

	public ForestLevelGenerator(Environment environment, int width, int height, double featureSize) {
		this.noise = new OpenSimplexNoise(Rand.nextInt(Integer.MAX_VALUE));
		this.environment = environment;
		this.width = width;
		this.height = height;
		this.featureSize = featureSize;
	}

	@Override
	public Level generateLevel() {
		Level level = new Level(width, height);
		for (int x = 0; x < width; ++x) {
			for (int y = 0; y < height; ++y) {
//				double value = noise.eval((double) x / featureSize, (double) y / featureSize);
//				level.setSolid(x, y, value < 0);
//				level.setAnimation(x, y, value < 0 ? environment.getTileset().out() : environment.getTileset().floor());
				level.setSolid(x, y, false);
				level.setAnimation(x, y, environment.getTileset().floor());
			}
		}
		placePlayerSpawns(level);

		for (float x = 0; x < width; x += 0.5f) {
			for (float y = 0; y < height; y += 0.5f) {
				double value = getNoise(x, y);
				placeVegetation(level, x, y, value);
			}
		}

		return level;
	}

	private void placePlayerSpawns(Level level) {
		int startX, startY;
		do {
			startX = Rand.between(1, width - 2);
			startY = Rand.between(1, height - 2);

		} while (getNoise(startX, startY) < 0.5d);
		level.getEntityPlaceholders().add(new EntityPlaceholder(EntityType.PLAYER_SPAWN, new Vector2(startX, startY)));
		// TODO Need to keep looking to place other players
	}

	private void placeVegetation(Level level, float x, float y, double value) {
		String plant;
		if (value < -0.8) {
			plant = "prop_bush_red";
		} else if (value < -0.7) {
			plant = "prop_bush_red_small";
		} else if (value < -0.6) {
			plant = "prop_bush_purple";
		} else if (value < -0.5) {
			plant = "prop_bush_purple_small";
		} else if (value < -0.4) {
			plant = "prop_bush_cyan";
		} else if (value < -0.3) {
			plant = "prop_bush_cyan_small";
		} else if (value < -0.2) {
			plant = "prop_bush_green";
		} else if (value < -0.1) {
			plant = "prop_bush_green_small";
		} else if (value < 0) {
			plant = "prop_bush_gold";
		} else if (value < 0.1) {
			plant = "prop_bush_gold_small";
		} else if (value < 0.2) {
			plant = "prop_grass_1";
		} else if (value < 0.3) {
			plant = "prop_grass_2";
		} else if (value < 0.4) {
			plant = "prop_grass_3";
		} else if (value < 0.5) {
			plant = "prop_flower_1";
		} else {
			return;
		}

		float offsetX = Rand.between(-0.25f, 0.25f);
		float offsetY = Rand.between(-0.25f, 0.25f);

		level.getEntityPlaceholders().add(new EntityPlaceholder(plant, new Vector2(x + offsetX, y + offsetY)));
	}

	private double getNoise(double x, double y) {
		double value = noise.eval(x / featureSize, y / featureSize);
		// Distance to center is used to increase density towards the edges
		double distanceToCenter = ((width / 2d - x) * (width / 2d - x) + (height / 2d - y) * (height / 2d - y)) / ((width / 2d) * (width / 2d));
		return Util.clamp(value + distanceToCenter * -1, -1, 1);
	}

}
