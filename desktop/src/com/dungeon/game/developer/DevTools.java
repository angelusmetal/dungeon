package com.dungeon.game.developer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.dungeon.engine.controller.AbstractInputProcessor;
import com.dungeon.engine.controller.toggle.KeyboardToggle;
import com.dungeon.engine.controller.trigger.Trigger;
import com.dungeon.engine.ui.widget.SamplerVisualizer;
import com.dungeon.engine.ui.widget.VLayout;
import com.dungeon.engine.util.CyclicProfiler;
import com.dungeon.engine.util.StopWatch;
import com.dungeon.engine.util.Util;
import com.dungeon.engine.viewport.ViewPort;
import com.dungeon.game.player.Player;
import com.dungeon.game.player.Players;
import com.dungeon.game.render.stage.ViewPortRenderer;

import java.util.LinkedHashMap;

public class DevTools {

	public static CyclicProfiler movementSampler = new CyclicProfiler(200);
	public static CyclicProfiler entitiesSampler = new CyclicProfiler(200);
	public static CyclicProfiler renderSampler = new CyclicProfiler(200);
	public static CyclicProfiler profilerSampler = new CyclicProfiler(200);

	public static LinkedHashMap<String, CyclicProfiler> namedProfilers = new LinkedHashMap<>();

	private final InputMultiplexer inputMultiplexer;
	private StopWatch stopWatch = new StopWatch();
	private VLayout profilerWidget = new VLayout();
	public boolean drawProfiler = false;
	private Vector2 mouseOrigin = new Vector2();

	public DevTools(InputMultiplexer inputMultiplexer) {
		this.inputMultiplexer = inputMultiplexer;
		inputMultiplexer.addProcessor(new AbstractInputProcessor() {
			@Override public boolean mouseMoved(int screenX, int screenY) {
				mouseOrigin.set(screenX, Gdx.graphics.getHeight() - screenY);
				return true;
			}
		});
	}

	public void draw() {
		if (drawProfiler) {
			stopWatch.start();
			SpriteBatch batch = new SpriteBatch();
			batch.begin();
			profilerWidget.draw(batch);
			batch.end();
			profilerSampler.sample((int) stopWatch.getAndReset());
		}
	}

	public void addDeveloperHotkeys() {
		addDeveloperHotkey(Input.Keys.F1, () -> Players.all().stream().map(Player::getRenderer).forEach(ViewPortRenderer::toggleTiles));
		addDeveloperHotkey(Input.Keys.F2, () -> Players.all().stream().map(Player::getRenderer).forEach(ViewPortRenderer::toggleEntities));
		addDeveloperHotkey(Input.Keys.F3, () -> Players.all().stream().map(Player::getRenderer).forEach(ViewPortRenderer::toggleLighting));
		addDeveloperHotkey(Input.Keys.F4, () -> Players.all().stream().map(Player::getRenderer).forEach(ViewPortRenderer::toggleShadows));
		addDeveloperHotkey(Input.Keys.F5, () -> Players.all().stream().map(Player::getRenderer).forEach(ViewPortRenderer::toggleBoundingBox));
		addDeveloperHotkey(Input.Keys.F6, () -> Players.all().stream().map(Player::getRenderer).forEach(ViewPortRenderer::toggleNoise));
		addDeveloperHotkey(Input.Keys.F7, () -> Players.all().stream().map(Player::getRenderer).forEach(ViewPortRenderer::toggleConsole));
		addDeveloperHotkey(Input.Keys.F11, () -> {
			Players.all().stream().map(Player::getRenderer).forEach(ViewPortRenderer::toggleProfiler);
			drawProfiler = !drawProfiler;
		});
	}

	public void refreshProfilerWidgets() {
		// Add visual profiler stuff
		profilerWidget.add(
				new SamplerVisualizer(entitiesSampler, "upd")
						.color(new Color(0f, 0f, 1f, 0.5f))
						.formatter(Util::nanosToString));
		profilerWidget.add(
				new SamplerVisualizer(renderSampler, "ren")
						.color(new Color(1f, 0f, 0f, 0.5f))
						.formatter(Util::nanosToString));
		profilerWidget.add(
				new SamplerVisualizer(movementSampler, "mov")
						.color(new Color(0f, 1f, 0f, 0.5f))
						.formatter(String::valueOf));
		// Add a profiler widget for each stage
		namedProfilers.forEach(
				(label, sampler) -> profilerWidget.add(
						new SamplerVisualizer(sampler, label)
								.color(new Color(0.7f, 0.7f, 0.7f, 0.5f))
								.formatter(Util::nanosToString)));
		profilerWidget.add(
				new SamplerVisualizer(profilerSampler, "profiler")
						.color(new Color(0.4f, 0.4f, 0.4f, 0.5f))
						.formatter(Util::nanosToString));
		profilerWidget.setX(Gdx.graphics.getWidth() - 150);
		profilerWidget.setY(0);
	}

	public void addDeveloperHotkey(int keycode, Runnable runnable) {
		KeyboardToggle keyboardToggle = new KeyboardToggle(keycode);
		inputMultiplexer.addProcessor(keyboardToggle);
		Trigger trigger = new Trigger(keyboardToggle);
		trigger.addListener(runnable);
	}

	public Vector2 mouseAt() {
		ViewPort viewPort = Players.get(0).getViewPort();
		return mouseOrigin.cpy().scl(1 / viewPort.getScale()).add(viewPort.cameraX, viewPort.cameraY);
	}

}
