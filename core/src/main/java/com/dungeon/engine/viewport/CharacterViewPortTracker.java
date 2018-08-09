package com.dungeon.engine.viewport;

import com.dungeon.engine.entity.Entity;

import java.util.List;

public class CharacterViewPortTracker {

	public void refresh(ViewPort viewPort, List<Entity> entities) {
		if (!entities.isEmpty()) {
			// Find min, max and avg of all character positions
			float avgX = 0, avgY = 0, minX = Float.MAX_VALUE, minY = Float.MAX_VALUE, maxX = Float.MIN_VALUE, maxY = Float.MIN_VALUE;
			for (Entity c : entities) {
				avgX += c.getOrigin().x;
				avgY += c.getOrigin().y;
				minX = Math.min(minX, c.getOrigin().x);
				minY = Math.min(minY, c.getOrigin().y);
				maxX = Math.max(maxX, c.getOrigin().x);
				maxY = Math.max(maxY, c.getOrigin().y);
			}
			avgX /= entities.size();
			avgY /= entities.size();

			// Place camera in the middle of all entities
			viewPort.cameraX = (int) (avgX - viewPort.cameraWidth / 2);
			viewPort.cameraY = (int) (avgY - viewPort.cameraHeight / 2);
		}
	}

	public void refresh(ViewPort viewPort, Entity entity) {
		if (entity != null) {
			viewPort.cameraX = (int) (entity.getOrigin().x - viewPort.cameraWidth / 2);
			viewPort.cameraY = (int) (entity.getOrigin().y - viewPort.cameraHeight / 2);
		}
	}
}
