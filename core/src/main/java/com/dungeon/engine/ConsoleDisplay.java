package com.dungeon.engine;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class ConsoleDisplay {
	private final int logSize;
	private final float messageExpiration;
	private final List<LogLine> log;
	private final Map<String, Supplier<String>> watches;

	public static class LogLine {
		public String message;
		public Color color;
		public float expiration;
		public LogLine(String message, Color color, float expiration) {
			this.message = message;
			this.color = color;
			this.expiration = expiration;
		}
	}

	public ConsoleDisplay(int logSize, float messageExpiration) {
		this.logSize = logSize;
		this.messageExpiration = messageExpiration;
		this.log = new ArrayList<>(logSize);
		this.watches = new LinkedHashMap<>();
	}

	public void log(String log) {
		log(log, Color.WHITE);
	}

	public void log(String log, Color color) {
		if (this.log.size() == logSize) {
			this.log.remove(0);
		}
		this.log.add(new LogLine(log, color.cpy(), Engine.time() + messageExpiration));
		Gdx.app.log("Console", log);
	}

	public List<LogLine> getLog() {
		// Purge expired lines
		log.removeIf(logLine -> logLine.expiration <= Engine.time());
		return log;
	}

	public void watch(String key, Supplier<String> value) {
		watches.put(key, value);
	}

	public Map<String, Supplier<String>> getWatches() {
		return watches;
	}

	public float getMessageExpiration() {
		return messageExpiration;
	}

}
