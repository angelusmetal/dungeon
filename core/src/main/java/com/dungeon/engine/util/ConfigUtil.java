package com.dungeon.engine.util;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Vector2;
import com.dungeon.engine.resource.LoadingException;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigException;
import com.typesafe.config.ConfigValueType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class ConfigUtil {

	public static Optional<Long> getLong(Config config, String key) {
		if (config.hasPath(key)) {
			return Optional.of(config.getLong(key));
		} else {
			return Optional.empty();
		}
	}

	public static long requireLong(Config config, String key) {
		return config.getLong(key);
	}

	public static Optional<Integer> getInteger(Config config, String key) {
		if (config.hasPath(key)) {
			return Optional.of(config.getInt(key));
		} else {
			return Optional.empty();
		}
	}

	public static int requireInteger(Config config, String key) {
		return config.getInt(key);
	}

	public static Optional<Double> getDouble(Config config, String key) {
		if (config.hasPath(key)) {
			return Optional.of(config.getDouble(key));
		} else {
			return Optional.empty();
		}
	}

	public static double requireDouble(Config config, String key) {
		return config.getDouble(key);
	}

	public static Optional<Float> getFloat(Config config, String key) {
		if (config.hasPath(key)) {
			return Optional.of((float) config.getDouble(key));
		} else {
			return Optional.empty();
		}
	}

	public static float requireFloat(Config config, String key) {
		return (float) config.getDouble(key);
	}

	public static Optional<Boolean> getBoolean(Config config, String key) {
		if (config.hasPath(key)) {
			return Optional.of(config.getBoolean(key));
		} else {
			return Optional.empty();
		}
	}

	public static boolean requireBoolean(Config config, String key) {
		return config.getBoolean(key);
	}

	public static Optional<String> getString(Config config, String key) {
		if (config.hasPath(key)) {
			return Optional.of(config.getString(key));
		} else {
			return Optional.empty();
		}
	}

	public static String requireString(Config config, String key) {
		return config.getString(key);
	}

	public static <T extends Enum<T>> Optional<T> getEnum(Config config, String key, Class<T> enumType) {
		if (config.hasPath(key)) {
			String rawValue = config.getString(key).toUpperCase();
			for (T constant : enumType.getEnumConstants()) {
				if (constant.name().equals(rawValue)) {
					return Optional.of(constant);
				}
			}
			return Optional.empty();
		} else {
			return Optional.empty();
		}
	}

	public static <T extends Enum<T>> T requireEnum(Config config, String key, Class<T> enumType) {
		return getEnum(config, key, enumType).orElseThrow(missing(key));
	}

	public static Optional<List<Float>> getFloatList(Config config, String key) {
		if (config.hasPath(key)) {
			return Optional.of(config.getDoubleList(key).stream().map(Double::floatValue).collect(Collectors.toList()));
		} else {
			return Optional.empty();
		}
	}

	public static List<Float> requireFloatList(Config config, String key) {
		return config.getDoubleList(key).stream().map(Double::floatValue).collect(Collectors.toList());
	}

	public static Optional<List<Integer>> getIntList(Config config, String key) {
		if (config.hasPath(key)) {
			return Optional.of(config.getIntList(key));
		} else {
			return Optional.empty();
		}
	}

	public static List<Integer> requireIntList(Config config, String key) {
		return config.getIntList(key);
	}

	public static Optional<List<String>> getStringList(Config config, String key) {
		if (config.hasPath(key)) {
			return Optional.of(config.getStringList(key));
		} else {
			return Optional.empty();
		}
	}

	public static List<String> requireStringList(Config config, String key) {
		try {
			return config.getStringList(key);
		} catch (ConfigException.WrongType e) {
			return Collections.singletonList(config.getString(key));
		}
	}

	public static Optional<List<Vector2>> getVector2List(Config config, String key) {
		if (config.hasPath(key)) {
			List<Float> floats = requireFloatList(config, key);
			if (floats.size() % 2 != 0) {
				throw new RuntimeException("Expected an even number of floats in Vector2 list at key '" + key + "'");
			}
			ArrayList<Vector2> vector2s = new ArrayList<>(floats.size() / 2);
			for (int i = 0; i < floats.size(); i += 2) {
				vector2s.add(new Vector2(floats.get(i), floats.get(i+1)));
			}
			return Optional.of(vector2s);
		} else {
			return Optional.empty();
		}
	}

	public static List<Vector2> requireVector2List(Config config, String key) {
		return getVector2List(config, key).get();
	}

	public static Optional<List<? extends Config>> getConfigList(Config config, String key) {
		if (config.hasPath(key)) {
			return Optional.of(config.getConfigList(key));
		} else {
			return Optional.empty();
		}
	}

	public static List<? extends Config> requireConfigList(Config config, String key) {
		return config.getConfigList(key);
	}

	public static Optional<Vector2> getVector2(Config config, String key) {
		if (config.hasPath(key)) {
			List<Double> vector2 = config.getDoubleList(key);
			if (vector2.size() != 2) {
				throw new RuntimeException("Expected Vector2 (2 numerical values) at key '" + key + "'");
			}
			return Optional.of(new Vector2(vector2.get(0).floatValue(), vector2.get(1).floatValue()));
		} else {
			return Optional.empty();
		}
	}

	public static Vector2 requireVector2(Config config, String key) {
		return getVector2(config, key).orElseThrow(missing(key));
	}

	public static Optional<GridPoint2> getGridPoint2(Config config, String key) {
		if (config.hasPath(key)) {
			List<Integer> ints = config.getIntList(key);
			if (ints.size() != 2) {
				throw new RuntimeException("Expected GridPoint2 (2 numerical values) at key '" + key + "'");
			}
			return Optional.of(new GridPoint2(ints.get(0), ints.get(1)));
		} else {
			return Optional.empty();
		}
	}

	public static GridPoint2 requireGridPoint2(Config config, String key) {
		return getGridPoint2(config, key).orElseThrow(missing(key));
	}

	public static Optional<Supplier<Float>> getFloatRange(Config config, String key) {
		if (!config.hasPath(key)) {
			return Optional.empty();
		}
		ConfigValueType type = config.getValue(key).valueType();
		if (type == ConfigValueType.NUMBER) {
			float value = requireFloat(config, key);
			return Optional.of(() -> value);
		} else if (type == ConfigValueType.LIST) {
			Vector2 range = requireVector2(config, key);
			float min = Math.min(range.x, range.y);
			float max = Math.max(range.x, range.y);
			return min != max ? Optional.of(() -> Rand.between(min, max)) : Optional.of(() -> min);
		} else {
			throw new LoadingException("Invalid type '" + type + "' for key '" + key + "'");
		}
	}

	public static Supplier<Float> requireFloatRange(Config config, String key) {
		return getFloatRange(config, key).orElseThrow(missing(key));
	}

	public static Optional<Supplier<Integer>> getIntegerRange(Config config, String key) {
		if (!config.hasPath(key)) {
			return Optional.empty();
		}
		ConfigValueType type = config.getValue(key).valueType();
		if (type == ConfigValueType.NUMBER) {
			int value = requireInteger(config, key);
			return Optional.of(() -> value);
		} else if (type == ConfigValueType.LIST) {
			Vector2 range = requireVector2(config, key);
			int min = (int) Math.min(range.x, range.y);
			int max = (int) Math.max(range.x, range.y);
			return min != max ? Optional.of(() -> Rand.between(min, max)) : Optional.of(() -> min);
		} else {
			throw new LoadingException("Invalid type '" + type + "' for key '" + key + "'");
		}
	}

	public static Supplier<Integer> requireIntegerRange(Config config, String key) {
		return getIntegerRange(config, key).orElseThrow(missing(key));
	}

	public static Optional<Color> getColor(Config config, String key) {
		// Attempt to get a hex string
		if (!config.hasPath(key)) {
			return Optional.empty();
		}
		if (config.getValue(key).valueType() == ConfigValueType.NUMBER) {
			return getInteger(config, key).map(num -> String.format("%06d", num)).map(Color::valueOf);
		}
		if (config.getValue(key).valueType() == ConfigValueType.STRING) {
			return getString(config, key).map(Color::valueOf);
		}
		// Otherwise, attempt to get a table
		if (config.getValue(key).valueType() == ConfigValueType.OBJECT) {
			Config color = config.getConfig(key);
			// Attempt to get rgb/rgba
			Optional<Float> r = ConfigUtil.getFloat(color, "r");
			Optional<Float> g = ConfigUtil.getFloat(color, "g");
			Optional<Float> b = ConfigUtil.getFloat(color, "b");
			Optional<Float> a = ConfigUtil.getFloat(color, "a");
			if (r.isPresent() && g.isPresent() && b.isPresent()) {
				return Optional.of(a.map(aFloat -> new Color(r.get(), g.get(), b.get(), aFloat))
						.orElseGet(() -> new Color(r.get(), g.get(), b.get(), 1)));
			}
			// Attempt to get hsv/hsva
			Optional<Float> h = ConfigUtil.getFloat(color, "h");
			Optional<Float> s = ConfigUtil.getFloat(color, "s");
			Optional<Float> v = ConfigUtil.getFloat(color, "v");
			if (h.isPresent() && s.isPresent() && v.isPresent()) {
				return Optional.of(a.map(aFloat -> Util.hsvaToColor(h.get(), s.get(), v.get(), aFloat))
						.orElseGet(() -> Util.hsvaToColor(h.get(), s.get(), v.get(), 1)));
			}
		}
		return Optional.empty();
	}

	public static Color requireColor(Config config, String key) {
		return getColor(config, key).orElseThrow(missing(key));
	}

	private static Supplier<RuntimeException> missing(String property) {
		return () -> new LoadingException("Missing property '" + property + "'");
	}

}
