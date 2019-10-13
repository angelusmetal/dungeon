package com.dungeon.game.viewport;

import com.badlogic.gdx.Gdx;
import com.dungeon.engine.Engine;
import com.dungeon.engine.entity.Entity;
import com.dungeon.engine.util.Util;
import com.dungeon.engine.viewport.ViewPort;
import com.dungeon.game.player.Player;
import com.dungeon.game.player.Players;
import com.dungeon.game.render.stage.ViewPortRenderer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Creates a single shared view for all players. Camera zooms in and out to frame them all.
 */
public class SharedScreenCreationStrategy implements GameView.CreationStrategy {

	private final float baseScale;

	public SharedScreenCreationStrategy(float baseScale) {
		this.baseScale = baseScale;
	}

	@Override
	public List<View> createViews() {
		float scale = baseScale;

		ViewPort viewPort = new ViewPort(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), scale);
		ViewPortRenderer renderer = new ViewPortRenderer(viewPort, Players.all());
		renderer.initialize();
		View view = new View(viewPort, renderer, getCamera(viewPort, Players.all().stream().map(Player::getAvatar).collect(Collectors.toList())));
		List<View> views = Collections.singletonList(view);
		for (Player p : Players.all()) {
			p.setViewPort(viewPort);
			p.setRenderer(renderer);
		}
		Engine.setMainViewport(viewPort);
		return views;
	}

	ViewPortCamera getCamera(ViewPort viewPort, List<Entity> entities) {
		return () -> {
			if (!entities.isEmpty()) {
				// Find min, max and avg of all character positions
				float minX = Float.MAX_VALUE, minY = Float.MAX_VALUE, maxX = Float.MIN_VALUE, maxY = Float.MIN_VALUE;
				for (Entity c : entities) {
					minX = Math.min(minX, c.getOrigin().x);
					minY = Math.min(minY, c.getOrigin().y);
					maxX = Math.max(maxX, c.getOrigin().x);
					maxY = Math.max(maxY, c.getOrigin().y);
				}
				float width = maxX - minX;
				float height = maxY - minY;
				int centerX = (int) (minX + width / 2f);
				int centerY = (int) (minY + height / 2f);

				// Place camera in the middle of all entities
				float newScale = Util.clamp(viewPort.width / width, 1, baseScale);
				viewPort.setScale(newScale);
				viewPort.centerAt(centerX, centerY);
			}
		};
	}


}