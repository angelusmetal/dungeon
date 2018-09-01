package com.dungeon.game.player;

import java.util.Collections;
import java.util.List;

public class Players {

	private static List<Player> players = Collections.emptyList();

	private Players() {}

	public static List<Player> all() {
		return players;
	}

	public static Player get(int i) {
		return players.get(i);
	}

	public static void set(List<Player> players) {
		Players.players = players;
	}

	public static int count() {
		return players.size();
	}
}
