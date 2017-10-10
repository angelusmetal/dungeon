package com.dungeon.movement;

import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.ControllerListener;
import com.badlogic.gdx.controllers.PovDirection;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class MovableControllerAdapter implements ControllerListener {

	private final EnumMap<PovDirection, Vector2> povVectorMapper = new EnumMap<>(PovDirection.class);
	private final Map<Integer, Consumer<Float>> axisControllers = new HashMap<>();
	private final Map<Integer, Consumer<PovDirection>> povControllers = new HashMap<>();
	private final Map<Integer, Consumer<Integer>> buttonControllers = new HashMap<>();
	private static final float MIN_AXIS_THRESHOLD = 0.2f;

	public MovableControllerAdapter() {
		povVectorMapper.put(PovDirection.north, new Vector2(0, 1));
		povVectorMapper.put(PovDirection.northEast, new Vector2(1, 1));
		povVectorMapper.put(PovDirection.east, new Vector2(1, 0));
		povVectorMapper.put(PovDirection.southEast, new Vector2(1, -1));
		povVectorMapper.put(PovDirection.south, new Vector2(0, -1));
		povVectorMapper.put(PovDirection.southWest, new Vector2(-1, -1));
		povVectorMapper.put(PovDirection.west, new Vector2(-1, 0));
		povVectorMapper.put(PovDirection.northWest, new Vector2(-1, 1));
		povVectorMapper.put(PovDirection.center, new Vector2(0, 0));
	}

	public void addAxisController(int xAxisCode, int yAxisCode, Movable movable) {
		axisControllers.put(xAxisCode, value -> movable.setSelfXMovement(Math.abs(value) > MIN_AXIS_THRESHOLD ? value : 0));
		axisControllers.put(yAxisCode, value -> movable.setSelfYMovement(Math.abs(value) > MIN_AXIS_THRESHOLD ? -value : 0));
	}

	public void addPovController(int povCode, Movable movable) {
		povControllers.put(povCode, value -> {
			Vector2 vector = povVectorMapper.get(value);
			movable.setSelfXMovement(vector.x);
			movable.setSelfYMovement(vector.y);
		});
	}

	public void addButtonController(int buttonCode, Consumer<Integer> action) {
		this.buttonControllers.put(buttonCode, action);
	}

	@Override
	public void connected(Controller controller) {

	}

	@Override
	public void disconnected(Controller controller) {

	}

	@Override
	public boolean buttonDown(Controller controller, int buttonCode) {
		buttonControllers.getOrDefault(buttonCode, (v) -> {}).accept(buttonCode);
		return false;
	}

	@Override
	public boolean buttonUp(Controller controller, int buttonCode) {
		return false;
	}

	@Override
	public boolean axisMoved(Controller controller, int axisCode, float value) {
		axisControllers.getOrDefault(axisCode, (v) -> {}).accept(value);
		return true;
	}

	@Override
	public boolean povMoved(Controller controller, int povCode, PovDirection value) {
		povControllers.getOrDefault(povCode, (v) -> {}).accept(value);
		return true;
	}

	@Override
	public boolean xSliderMoved(Controller controller, int sliderCode, boolean value) {
		return false;
	}

	@Override
	public boolean ySliderMoved(Controller controller, int sliderCode, boolean value) {
		return false;
	}

	@Override
	public boolean accelerometerMoved(Controller controller, int accelerometerCode, Vector3 value) {
		return false;
	}
}
