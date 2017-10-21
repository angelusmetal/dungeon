package com.dungeon.engine.viewport;

import com.dungeon.engine.entity.Character;
import com.dungeon.engine.entity.PlayerCharacter;

import java.util.List;

public class CharacterViewPortTracker {

	private final List<PlayerCharacter> characters;

	public CharacterViewPortTracker(List<PlayerCharacter> characters) {
		this.characters = characters;
	}

	public void refresh(ViewPort viewPort) {
		// Find min, max and avg of all character positions
		float avgX = 0, avgY = 0, minX = Float.MAX_VALUE, minY = Float.MAX_VALUE, maxX = Float.MIN_VALUE, maxY = Float.MIN_VALUE;
		for (Character c : characters) {
			avgX += c.getPos().x;
			avgY += c.getPos().y;
			minX = Math.min(minX, c.getPos().x);
			minY = Math.min(minY, c.getPos().y);
			maxX = Math.max(maxX, c.getPos().x);
			maxY = Math.max(maxY, c.getPos().y);
		}
		avgX /= characters.size();
		avgY /= characters.size();

		// Place camera in the middle of all characters
		viewPort.xOffset = (int) (avgX - viewPort.width / viewPort.scale / 2);
		viewPort.yOffset = (int) (avgY - viewPort.height / viewPort.scale / 2);
	}
}
