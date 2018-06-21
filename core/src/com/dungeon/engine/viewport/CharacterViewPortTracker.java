package com.dungeon.engine.viewport;

import com.dungeon.engine.entity.Entity;

import java.util.List;

public class CharacterViewPortTracker {

	public void refresh(ViewPort viewPort, List<Entity> entities) {
		if (!entities.isEmpty()) {
			// Find min, max and avg of all character positions
			float avgX = 0, avgY = 0, minX = Float.MAX_VALUE, minY = Float.MAX_VALUE, maxX = Float.MIN_VALUE, maxY = Float.MIN_VALUE;
			for (Entity c : entities) {
				avgX += c.getPos().x;
				avgY += c.getPos().y;
				minX = Math.min(minX, c.getPos().x);
				minY = Math.min(minY, c.getPos().y);
				maxX = Math.max(maxX, c.getPos().x);
				maxY = Math.max(maxY, c.getPos().y);
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
			viewPort.cameraX = (int) (entity.getPos().x - viewPort.cameraWidth / 2);
			viewPort.cameraY = (int) (entity.getPos().y - viewPort.cameraHeight / 2);
		}
	}
}
