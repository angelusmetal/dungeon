package com.dungeon.engine;

import com.badlogic.gdx.graphics.Color;
import com.dungeon.engine.entity.repository.EntityRepository;
import com.dungeon.engine.entity.repository.Repository;
import com.dungeon.engine.physics.LevelTiles;
import com.dungeon.engine.render.effect.RenderEffect;
import com.dungeon.engine.util.Rand;
import com.dungeon.engine.util.Util;
import com.dungeon.engine.viewport.AudioManager;

public class Engine {

	public static EntityRepository entities = new EntityRepository();
	public static AudioManager audio = new AudioManager();

	private static float stateTime = 0;
	private static float frameTime;

	public static Repository<OverlayText> overlayTexts = new Repository<>();
	public static Repository<RenderEffect> renderEffects = new Repository<>();

	private static LevelTiles levelTiles;

	// FIXME Does this belong here?
	private static Color baseLight = Color.WHITE.cpy();

	/** Time since the game started */
	public static float time() {
		return stateTime;
	}

	/** Time since last frame */
	public static float frameTime() {
		return frameTime;
	}

	public static void addTime(float frameTime) {
		stateTime += frameTime;
		Engine.frameTime = frameTime;
		// TODO is this the right place?
		audio.update();
	}

	public static void setLevelTiles(LevelTiles levelTiles) {
		Engine.levelTiles = levelTiles;
	}

	public static LevelTiles getLevelTiles() {
		return levelTiles;
	}

	public static void refresh() {
		entities.commit(false);
	}

	public static Color getBaseLight() {
		return baseLight;
	}

	public static void randomizeBaseLight() {
		baseLight.set(Util.hsvaToColor(Rand.between(0f, 1f), 0.5f, 1f, 1f));
	}

	public static void setBaseLight(Color color) {
		baseLight.set(color);
	}

}
