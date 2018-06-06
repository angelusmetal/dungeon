package com.dungeon.engine.util;

import com.moandjiezana.toml.Toml;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class ConfigUtil {

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

	public static <T> Map<String, T> getPojoMap(Toml configuration, String arrayName, Function<T, String> keyField, Class<T> targetType) {
		Map<String, T> map = new HashMap<>();
		forEach(configuration, arrayName, targetType, v -> {
			if (map.put(keyField.apply(v), v) != null) {
				throw new RuntimeException("Duplicate mapping for key '" + keyField.apply(v) + "'");
			}
		});
		return map;
	}

	public static <T> void forEach(Toml configuration, String arrayName, Class<T> targetType, Consumer<T> consumer) {
		Map<String, T> map = new HashMap<>();

		for (Toml item : configuration.getTables(arrayName)) {
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
					} else if (type.isArray()) {
						if (type.getName().equals("[[F")) {
//							fieldSetArray(item, pojo, field, name, type);
							setArray2(item, pojo, field, name, new ArrayPopulator2<Number>() {
								@Override void setField(Field field) {
									float[][] array = new float[values.size()][];
									int o = 0;
									for (List<Number> inner : values) {
										int i = 0;
										for (Number n : inner) {
											array[o][i] = n.floatValue();
										}
									}
									try {
										field.set(pojo, array);
									} catch (IllegalAccessException e) {
										e.printStackTrace();
									}
								}
							});
						}
//					} else {
					}
					field.setAccessible(false);
				}
			} catch (IllegalAccessException e) {
				System.err.println("Could not instantiate POJO of type " + targetType.getSimpleName() + " for keys " + item);
				e.printStackTrace();
			}
			consumer.accept(pojo);
		}

	}

	private abstract static class ArrayPopulator<T> {
		final List<T> values = new ArrayList<>();
		void add(T value) {
			values.add(value);
		}
		abstract void setField(Field field);
	}

	private abstract static class ArrayPopulator2<T> {
		final List<List<T>> values = new ArrayList<>();
		void add(T value) {
			values.get(values.size()-1).add(value);
		}
		void outer() {
			values.add(new ArrayList<>());
		}
		abstract void setField(Field field);
	}

	public static <T,V> void setArray(Toml item, T pojo, Field field, String name, ArrayPopulator<V> populator) throws IllegalAccessException {
		item.<V>getList(name).forEach(populator::add);
		populator.setField(field);
	}

	public static <T,V> void setArray2(Toml item, T pojo, Field field, String name, ArrayPopulator2<V> populator) throws IllegalAccessException {
		item.<List<V>>getList(name).forEach(list -> {
			populator.outer();
			list.forEach(populator::add);
		});
		populator.setField(field);
	}

	public static <T> void fieldSetArray(Toml item, T pojo, Field field, String name, Class<?> type) throws IllegalAccessException {
		try {
			Class<?> innerType = Class.forName(type.getName().substring(1));
			List<List<Number>> list = item.getList(name);
			if (list != null) {
				float[][] values = (float[][]) type.cast(Array.newInstance(innerType, list.size()));
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
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
	}
}
