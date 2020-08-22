package com.dungeon.engine;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.utils.BufferUtils;
import com.dungeon.engine.audio.AudioManager;
import com.dungeon.engine.console.Console;
import com.dungeon.engine.controller.InputProcessorStack;
import com.dungeon.engine.controller.KeyboardProcessor;
import com.dungeon.engine.entity.repository.EntityRepository;
import com.dungeon.engine.entity.repository.Repository;
import com.dungeon.engine.physics.LevelTiles;
import com.dungeon.engine.render.effect.RenderEffect;
import com.dungeon.engine.resource.Resources;
import com.dungeon.engine.util.Rand;
import com.dungeon.engine.util.Util;
import com.dungeon.engine.viewport.ViewPort;

import java.nio.IntBuffer;

public class Engine {

	public static void dispose() {
		Resources.dispose();
	}

	private static float stateTime = 0;
	private static float frameTime;

	public static EntityRepository entities = new EntityRepository();
	public static AudioManager audio = new AudioManager();
	public static InputMultiplexer inputMultiplexer = new InputMultiplexer();
	public static InputProcessorStack inputStack = new InputProcessorStack();
	public static KeyboardProcessor mainKeyboardProcessor = new KeyboardProcessor();
	public static ApplicationListenerStack appListenerStack = new ApplicationListenerStack();

	public static Repository<OverlayText> overlayTexts = new Repository<>();
	public static Repository<RenderEffect> renderEffects = new Repository<>();

	private static LevelTiles levelTiles;
	private static ViewPort mainViewport;

	public static Console console = new Console();

	// FIXME Does this belong here?
	private static Color baseLight = Color.WHITE.cpy();
	private static float specular = 1f;
	private static boolean normalMapEnabled = true;

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

	/**
	 * Gets the "main" viewport. Positional sound will be relative to the main viewport
	 */
	public static ViewPort getMainViewport() {
		return mainViewport;
	}

	/**
	 * Sets the "main" viewport.
	 */
	public static void setMainViewport(ViewPort mainViewport) {
		Engine.mainViewport = mainViewport;
	}

	public static int getMaxTextureSize () {
		IntBuffer buffer = BufferUtils.newIntBuffer(16);
		Gdx.gl.glGetIntegerv(GL20.GL_MAX_TEXTURE_SIZE, buffer);
		return buffer.get(0);
	}

	public static float getSpecular() {
		return specular;
	}

	public static void setSpecular(float specular) {
		Engine.specular = specular;
	}

	public static boolean isNormalMapEnabled() {
		return normalMapEnabled;
	}

	public static void setNormalMapEnabled(boolean renderNormalMap) {
		Engine.normalMapEnabled = renderNormalMap;
	}
}
