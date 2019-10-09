package com.dungeon.game.viewport;

import com.dungeon.engine.viewport.ViewPort;

import java.util.Collections;
import java.util.List;

/**
 * Encapsulates active {@link ViewPort}s as well as its creation policy and camera tracking.
 */
public class GameView {

	@FunctionalInterface
	public interface CreationStrategy {
		List<View> createViews();
	}

	private List<View> activeViews = Collections.emptyList();
	private CreationStrategy creationStrategy;

	/**
	 * Set the viewport creation strategy (e.g. one for each player or one for all players)
	 */
	public void setCreationStrategy(CreationStrategy creationStrategy) {
		this.creationStrategy = creationStrategy;
	}

	/**
	 * Recreate viewports according to strategy, disposing any previous ones
	 */
	public void recreateViewPorts() {
		activeViews.forEach(View::dispose);
		activeViews = creationStrategy.createViews();
	}

	/**
	 * Update camera on all viewports
	 */
	public void updateCamera() {
		activeViews.forEach(View::updateCamera);
	}

	/**
	 * Render all viewports
	 */
	public void render() {
		activeViews.forEach(View::render);
	}

	public void dispose() {
		activeViews.forEach(View::dispose);
	}
}
