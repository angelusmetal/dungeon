package com.dungeon.engine.console;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Colors;

import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Wrapper for a mutable console variable. This allows any variable to be published and accessed through the console.
 */
public class ConsoleVar {
	private final String name;
	private final Supplier<String> getter;
	private final Consumer<String> setter;

	public ConsoleVar(String name, Supplier<String> getter, Consumer<String> setter) {
		this.name = name;
		this.getter = getter;
		this.setter = setter;
	}

	public String getName() {
		return name;
	}

	public Supplier<String> getter() {
		return getter;
	}

	public Consumer<String> setter() {
		return setter;
	}

	/// Factory methods...

	public static ConsoleVar mutableBoolean(String name, Supplier<Boolean> getter, Consumer<Boolean> setter) {
		return new ConsoleVar(name, () -> getter.get().toString(), (value) -> setter.accept(Boolean.parseBoolean(value)));
	}

	public static ConsoleVar readOnlyBoolean(String name, Supplier<Boolean> getter) {
		return new ConsoleVar(name, () -> getter.get().toString(), readOnlyError(name));
	}

	public static ConsoleVar mutableInt(String name, Supplier<Integer> getter, Consumer<Integer> setter) {
		return new ConsoleVar(name, () -> getter.get().toString(), (value) -> setter.accept(Integer.parseInt(value)));
	}

	public static ConsoleVar readOnlyInt(String name, Supplier<Integer> getter) {
		return new ConsoleVar(name, () -> getter.get().toString(), readOnlyError(name));
	}

	public static ConsoleVar mutableFloat(String name, Supplier<Float> getter, Consumer<Float> setter) {
		return new ConsoleVar(name, () -> getter.get().toString(), (value) -> setter.accept(Float.parseFloat(value)));
	}

	public static ConsoleVar readOnlyFloat(String name, Supplier<Float> getter) {
		return new ConsoleVar(name, () -> getter.get().toString(), readOnlyError(name));
	}

	public static ConsoleVar mutableLong(String name, Supplier<Long> getter, Consumer<Long> setter) {
		return new ConsoleVar(name, () -> getter.get().toString(), (value) -> setter.accept(Long.parseLong(value)));
	}

	public static ConsoleVar readOnlyLong(String name, Supplier<Long> getter) {
		return new ConsoleVar(name, () -> getter.get().toString(), readOnlyError(name));
	}

	public static ConsoleVar mutableDouble(String name, Supplier<Double> getter, Consumer<Double> setter) {
		return new ConsoleVar(name, () -> getter.get().toString(), (value) -> setter.accept(Double.parseDouble(value)));
	}

	public static ConsoleVar readOnlyDouble(String name, Supplier<Double> getter) {
		return new ConsoleVar(name, () -> getter.get().toString(), readOnlyError(name));
	}

	public static ConsoleVar mutableColor(String name, Supplier<Color> getter, Consumer<Color> setter) {
		return new ConsoleVar(name, () -> getter.get().toString(), (value) -> {
			String[] tokens = value.split(",+");
			if (tokens.length == 1) {
				Color colorByName = Colors.get(tokens[0]);
				if (colorByName != null) {
					setter.accept(colorByName);
				} else {
					setter.accept(Color.valueOf(tokens[0])); // assume hex
				}
			} else if (tokens.length == 4) {
				float r = Float.parseFloat(tokens[0]);
				float g = Float.parseFloat(tokens[1]);
				float b = Float.parseFloat(tokens[2]);
				float a = Float.parseFloat(tokens[3]);
				setter.accept(new Color(r, g, b, a));
			}
		});
	}

	public static ConsoleVar readOnlyColor(String name, Supplier<Color> getter) {
		return new ConsoleVar(name, () -> getter.get().toString(), readOnlyError(name));
	}

	static private Consumer<String> readOnlyError(String varName) {
		return value -> {
			throw new RuntimeException("'" + varName + "' is read-only.");
		};
	}

}
