package com.dungeon.game.viewport;

import com.badlogic.gdx.Gdx;
import com.dungeon.engine.Engine;
import com.dungeon.engine.entity.Entity;
import com.dungeon.engine.viewport.ViewPort;
import com.dungeon.game.player.Player;
import com.dungeon.game.player.Players;
import com.dungeon.game.render.stage.ViewPortRenderer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Creates a view for each player, in split screen fashion
 */
public class SplitScreenCreationStrategy implements GameView.CreationStrategy {

	private final float baseScale;

	public SplitScreenCreationStrategy(float baseScale) {
		this.baseScale = baseScale;
	}

	@Override
	public List<View> createViews() {
		float scale = baseScale;

		LinkedList<ViewPort> viewPorts = new LinkedList<>();
		if (Players.count() == 1) {
			viewPorts.push(new ViewPort(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), scale));
		} else if (Players.count() == 2) {
			scale -= 1;
			int width = Gdx.graphics.getWidth() / 2;
			int height = Gdx.graphics.getHeight();
			viewPorts.add(new ViewPort(0, 0, width, height, scale));
			viewPorts.add(new ViewPort(width, 0, width, height, scale));
		} else {
			scale -= 2;
			int width = Gdx.graphics.getWidth() / 2;
			int height = Gdx.graphics.getHeight() / 2;
			viewPorts.add(new ViewPort(0, height, width, height, scale));
			viewPorts.add(new ViewPort(width, height, width, height, scale));
			viewPorts.add(new ViewPort(0, 0, width, height, scale));
			if (Players.count() == 4) {
				viewPorts.add(new ViewPort(width, 0, width, height, scale));
			}
		}
		int i = 0;
		List<View> views = new ArrayList<>(Players.count());
		for (Player p : Players.all()) {
			p.setViewPort(viewPorts.get(i++));
			p.setRenderer(new ViewPortRenderer(p.getViewPort(), Collections.singletonList(p)));
			p.getRenderer().initialize();
			views.add(new View(p.getViewPort(), p.getRenderer(), getCamera(p.getViewPort(), p.getAvatar())));
		}
		Engine.setMainViewport(Players.get(0).getViewPort());
		return views;
	}

	ViewPortCamera getCamera(ViewPort viewPort, Entity entity) {
		return () -> {
			if (entity != null) {
				viewPort.cameraX = (int) (entity.getOrigin().x - viewPort.cameraWidth / 2);
				viewPort.cameraY = (int) (entity.getOrigin().y - viewPort.cameraHeight / 2);
			}
		};
	}


}
