package com.dungeon.engine.controller;

public class ControllerConfig {
	public String id;
	public int povControl;
	public int analogControlX;
	public int analogControlY;

	public static final ControllerConfig DEFAULT = new ControllerConfig("default", 0, 3, -2);

	public ControllerConfig() {}

	public ControllerConfig(String id, int povControl, int analogControlX, int analogControlY) {
		this.id = id;
		this.povControl = povControl;
		this.analogControlX = analogControlX;
		this.analogControlY = analogControlY;
	}

	public String getId() {
		return id;
	}
}
