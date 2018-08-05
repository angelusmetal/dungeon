package com.dungeon.engine.util;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.moandjiezana.toml.Toml;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class ConfigUtil {

	public static Map<String, Toml> getTomlMap(Toml configuration, String arrayName, String keyFieldName) {
		Map<String, Toml> map = new HashMap<>();

		for (Toml item : configuration.getTables(arrayName)) {
			// Get key field
			String key = item.getString(keyFieldName);
			if (key == null) {
				continue;
			}
			map.put(key, item);
		}

		return map;
	}

	public static <T> List<T> getListOf(Toml configuration, String arrayName, Class<T> targetType) {
		return configuration.getTables(arrayName).stream().map(item -> readPojo(item, targetType)).collect(Collectors.toList());
	}

	public static <T> Map<String, T> getMapOf(Toml configuration, Class<T> targetType) {
		return configuration.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, entry -> readPojo(configuration.getTable(entry.getKey()), targetType)));
	}

	private static <T> T readPojo(Toml item, Class<T> targetType) {
		// Create POJO instance
		T pojo;
		try {
			pojo = targetType.newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			throw new RuntimeException("Cannot instantiate POJO of type " + targetType.getSimpleName(), e);
		}
		try {
			for (Field field : targetType.getDeclaredFields()) {
				String name = field.getName();
				Class<?> type = field.getType();
				field.setAccessible(true);
				if (type == Long.class || type == long.class) {
					getLong(item, name).ifPresent(v -> set(field, pojo, v));
				} else if (type == Integer.class || type == int.class) {
					getInteger(item, name).ifPresent(v -> set(field, pojo, v));
				} else if (type == Double.class || type == double.class) {
					getDouble(item, name).ifPresent(v -> set(field, pojo, v));
				} else if (type == Float.class || type == float.class) {
					getFloat(item, name).ifPresent(v -> set(field, pojo, v));
				} else if (type == Boolean.class || type == boolean.class) {
					getBoolean(item, name).ifPresent(v -> set(field, pojo, v));
				} else if (type == String.class) {
					field.set(pojo, item.getString(name));
				} else if (type.isArray()) {
					if (type.getName().equals("[I")) {
						List<Number> list = item.getList(name);
						if (list != null) {
							int i = 0;
							int[] values = new int[list.size()];
							for (Number number : list) {
								values[i++] = number.intValue();
							}
							field.set(pojo, values);
						}
					}
					else if (type.getName().equals("[F")) {
						List<Number> list = item.getList(name);
						if (list != null) {
							int i = 0;
							float[] values = new float[list.size()];
							for (Number number : list) {
								values[i++] = number.floatValue();
							}
							field.set(pojo, values);
						}
					}
					else if (type.getName().equals("[Ljava.lang.String;")) {
						List<String> list = item.getList(name);
						if (list != null) {
							int i = 0;
							String[] values = new String[list.size()];
							for (String string : list) {
								values[i++] = string;
							}
							field.set(pojo, values);
						}
					}
					else if (type.getName().equals("[[F")) {
						List<List<Number>> list = item.getList(name);
						if (list != null) {
							float[][] values = new float[list.size()][];
							int o = 0;
							for (List<Number> inner : list) {
								int i = 0;
								float[] innerValues = new float[inner.size()];
								for (Number number : inner) {
									innerValues[i++] = number.floatValue();
								}
								values[o++] = innerValues;
							}
							field.set(pojo, values);
						}
					}
					else {
						System.out.println("->>" + type.getName());
					}
				} else {
					System.out.println("->>" + type.getName());
//						System.err.println("Found unknown type for field " + field.getName() + ": " + type);
				}
				field.setAccessible(false);
			}
		} catch (IllegalAccessException e) {
			System.err.println("Could not instantiate POJO of type " + targetType.getSimpleName() + " for keys " + item);
			e.printStackTrace();
		}
		return pojo;
	}

	private static <T> void set(Field field, T obj, Object value) {
		try {
			field.set(obj, value);
		} catch (IllegalAccessException e) {
			throw new RuntimeException("Cannot set value '" + value + "' on field " + field, e);
		}
	}

	public static Optional<Long> getLong(Toml configuration, String key) {
		return Optional.ofNullable(configuration.getLong(key));
	}

	public static long requireLong(Toml configuration, String key) {
		return getLong(configuration, key).orElseThrow(missing(key));
	}

	public static Optional<Integer> getInteger(Toml configuration, String key) {
		Long value = configuration.getLong(key);
		return value == null ? Optional.empty() : Optional.of(value.intValue());
	}

	public static int requireInteger(Toml configuration, String key) {
		return getInteger(configuration, key).orElseThrow(missing(key));
	}

	public static Optional<Double> getDouble(Toml configuration, String key) {
		try {
			return Optional.ofNullable(configuration.getDouble(key));
		} catch (ClassCastException e) {
			return Optional.of(configuration.getLong(key).doubleValue());
		}
	}

	public static double requireDouble(Toml configuration, String key) {
		return getDouble(configuration, key).orElseThrow(missing(key));
	}

	public static Optional<Float> getFloat(Toml configuration, String key) {
		try {
			Double value = configuration.getDouble(key);
			return value == null ? Optional.empty() : Optional.of(value.floatValue());
		} catch (ClassCastException e) {
			// No need to take null into account, because it already found a value (and failed)
			return Optional.of(configuration.getLong(key).floatValue());
		}
	}

	public static float requireFloat(Toml configuration, String key) {
		return getFloat(configuration, key).orElseThrow(missing(key));
	}

	public static Optional<Boolean> getBoolean(Toml configuration, String key) {
		try {
			return configuration.contains(key) ? Optional.of(configuration.getBoolean(key)) : Optional.empty();
		} catch (ClassCastException e) {
			return Optional.of(Boolean.getBoolean(configuration.getString(key)));
		}
	}

	public static boolean requireBoolean(Toml configuration, String key) {
		return getBoolean(configuration, key).orElseThrow(missing(key));
	}

	public static Optional<String> getString(Toml toml, String key) {
		try {
			return Optional.ofNullable(toml.getString(key));
		} catch (ClassCastException e) {
			return Optional.empty();
		}
	}

	public static String requireString(Toml configuration, String key) {
		return getString(configuration, key).orElseThrow(missing(key));
	}

	public static <T> Optional<List<T>> getList(Toml toml, String key) {
		return Optional.ofNullable(toml.getList(key));
	}

	public static <T> List<T> requireList(Toml configuration, String key) {
		return ConfigUtil.<T>getList(configuration, key).orElseThrow(missing(key));
	}

	public static Optional<Vector2> getVector2(Toml toml, String key) {
		List<Number> vector2 = toml.getList(key);
		if (vector2 != null) {
			if (vector2.size() != 2) {
				throw new RuntimeException("Expected Vector2 (2 numerical values) at key '" + key + "'");
			}
			return Optional.of(new Vector2(vector2.get(0).floatValue(), vector2.get(1).floatValue()));
		} else {
			return Optional.empty();
		}
	}

	public static Vector2 requireVector2(Toml configuration, String key) {
		return getVector2(configuration, key).orElseThrow(missing(key));
	}

	public static Optional<Color> getColor(Toml toml, String key) {
		// Attempt to get a hex string
		Optional<Color> fromString = getString(toml, key).map(Color::valueOf);
		if (fromString.isPresent()) {
			return fromString;
		}
		// Otherwise, attempt to get a table
		Toml color = toml.getTable(key);
		if (color != null) {
			// Attempt to get rgb/rgba
			Optional<Float> r = ConfigUtil.getFloat(color, "r");
			Optional<Float> g = ConfigUtil.getFloat(color, "g");
			Optional<Float> b = ConfigUtil.getFloat(color, "b");
			Optional<Float> a = ConfigUtil.getFloat(color, "a");
			if (r.isPresent() && g.isPresent() && b.isPresent()) {
				return a.map(aFloat -> Optional.of(new Color(r.get(), g.get(), b.get(), aFloat)))
						.orElseGet(() -> Optional.of(new Color(r.get(), g.get(), b.get(), 1)));
			}
			// Attempt to get hsv/hsva
			Optional<Float> h = ConfigUtil.getFloat(color, "h");
			Optional<Float> s = ConfigUtil.getFloat(color, "s");
			Optional<Float> v = ConfigUtil.getFloat(color, "v");
			if (h.isPresent() && s.isPresent() && v.isPresent()) {
				return a.map(aFloat -> Optional.of(new Color(h.get(), s.get(), v.get(), aFloat)))
						.orElseGet(() -> Optional.of(new Color(h.get(), s.get(), v.get(), 1)));
			}
		}
		return Optional.empty();
	}

	public static Color requireColor(Toml configuration, String key) {
		return getColor(configuration, key).orElseThrow(missing(key));
	}

	private static Supplier<RuntimeException> missing(String property) {
		return () -> new RuntimeException("Missing property '" + property + "'");
	}

}
