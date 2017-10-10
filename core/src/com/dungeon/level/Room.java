package com.dungeon.level;

import java.util.List;

public class Room {
	public final ProceduralLevelGenerator.Coords topLeft = new ProceduralLevelGenerator.Coords(0,0);
	public final ProceduralLevelGenerator.Coords bottomRight = new ProceduralLevelGenerator.Coords(0,0);
	List<ProceduralLevelGenerator.ConnectionPoint> connectionPoints;

	@Override
	public String toString() {
		return "topLeft: " + topLeft + ", bottomRight: " + bottomRight + ", connectionPoints: [" + connectionPoints + "]";
	}
}
