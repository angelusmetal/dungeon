package com.dungeon.engine.util;

import com.moandjiezana.toml.Toml;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class ConfigUtil {

	public static <T> Map<String, T> getPojoMap(Toml configuration, String arrayName, String keyFieldName, Class<T> targetType) {

		Map<String, T> map = new HashMap<>();

		for (Toml item : configuration.getTables(arrayName)) {
			// Create POJO instance
			T pojo;
			try {
				pojo = targetType.newInstance();
			} catch (InstantiationException | IllegalAccessException e) {
				throw new RuntimeException("Cannot instantiate POJO of type " + targetType.getSimpleName(), e);
			}
			// Get key field
			String key = item.getString(keyFieldName);
			if (key == null) {
				continue;
			}
			map.put(item.getString(key), pojo);
			try {
				for (Field field : targetType.getDeclaredFields()) {
					String name = field.getName();
					Class<?> type = field.getType();
					if (type == Long.class || type == long.class) {
						Long value = getLong(item, name);
						if (value != null) {
							field.set(pojo, value);
						}
					} else if (type == Integer.class || type == int.class) {
						Integer value = getInteger(item, name);
						if (value != null) {
							field.set(pojo, value);
						}
					} else if (type == Double.class || type == double.class) {
						Double value = getDouble(item, name);
						if (value != null) {
							field.set(pojo, value);
						}
					} else if (type == Float.class || type == float.class) {
						Float value = getFloat(item, name);
						if (value != null) {
							field.set(pojo, value);
						}
					} else if (type == String.class) {
						field.set(pojo, item.getString(name));
					}
				}
			} catch (IllegalAccessException e) {
				System.err.println("Could not instantiate POJO of type " + targetType.getSimpleName() + " for keys " + item);
				e.printStackTrace();
			}
		}

		return map;
	}

	public static Long getLong(Toml configuration, String key) {
		return configuration.getLong(key);
	}

	public static long getLong(Toml configuration, String key, long def) {
		return configuration.getLong(key, def);
	}

	public static Integer getInteger(Toml configuration, String key) {
		Long value = configuration.getLong(key);
		return value == null ? null : value.intValue();
	}

	public static int getInteger(Toml configuration, String key, int def) {
		return configuration.getLong(key, (long) def).intValue();
	}

	public static Double getDouble(Toml configuration, String key) {
		try {
			return configuration.getDouble(key);
		} catch (ClassCastException e) {
			return configuration.getLong(key).doubleValue();
		}
	}

	public static double getDouble(Toml configuration, String key, double def) {
		try {
			return configuration.getDouble(key, def);
		} catch (ClassCastException e) {
			// No need to take default into account, because it already found a value (and failed)
			return configuration.getLong(key).doubleValue();
		}
	}

	public static Float getFloat(Toml configuration, String key) {
		try {
			Double value = configuration.getDouble(key);
			return value == null ? null : value.floatValue();
		} catch (ClassCastException e) {
			// No need to take null into account, because it already found a value (and failed)
			return configuration.getLong(key).floatValue();
		}
	}

	public static float getFloat(Toml configuration, String key, float def) {
		try {
			return configuration.getDouble(key, (double) def).floatValue();
		} catch (ClassCastException e) {
			// No need to take default into account, because it already found a value (and failed)
			return configuration.getLong(key).floatValue();
		}
	}

}
