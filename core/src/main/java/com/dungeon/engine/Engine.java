package com.dungeon.engine;

import com.badlogic.gdx.graphics.Color;
import com.dungeon.engine.entity.EntityManager;
import com.dungeon.engine.physics.LevelTiles;
import com.dungeon.engine.render.effect.RenderEffect;
import com.dungeon.engine.util.Rand;
import com.dungeon.engine.util.Util;

import java.util.ArrayList;
import java.util.List;

public class Engine {

	public static EntityManager entities = new EntityManager();

	private static float stateTime = 0;
	private static float frameTime;

	private static List<RenderEffect> renderEffects = new ArrayList<>();
	private static List<RenderEffect> newRenderEffects = new ArrayList<>();

	private static List<OverlayText> overlayTexts = new ArrayList<>();
	private static List<OverlayText> newOverelayTexts = new ArrayList<>();

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
	}

	public static void setLevelTiles(LevelTiles levelTiles) {
		Engine.levelTiles = levelTiles;
	}

	public static LevelTiles getLevelTiles() {
		return levelTiles;
	}

	public static void addOverlayText(OverlayText overlayText) {
		newOverelayTexts.add(overlayText);
	}

	public static List<OverlayText> getOverlayTexts() {
		return overlayTexts;
	}

	public static void addRenderEffect(RenderEffect effect) {
		newRenderEffects.add(effect);
	}

	public static List<RenderEffect> getRenderEffects() {
		return renderEffects;
	}

	public static void refresh() {
		// Dispose and remove old effects, and then add new ones
		renderEffects.stream().filter(RenderEffect::isExpired).forEach(RenderEffect::dispose);
		renderEffects.removeIf(RenderEffect::isExpired);
		renderEffects.addAll(newRenderEffects);
		newRenderEffects.clear();

		entities.commit();
		overlayTexts.addAll(newOverelayTexts);
		newOverelayTexts.clear();
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
