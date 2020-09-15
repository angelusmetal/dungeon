package com.dungeon.engine.util;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.dungeon.engine.Engine;
import com.dungeon.engine.console.ConsoleVar;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class SettingsUtil {

	/**
	 * Reads the contents of a Preferences into a provided POJO
	 */
	public static <T> void readPreferences(Preferences preferences, T pojo) {
		try {
			BeanInfo beanInfo = Introspector.getBeanInfo(pojo.getClass());
			for (PropertyDescriptor property : beanInfo.getPropertyDescriptors()) {
				Method readMethod = property.getReadMethod();
				Method writeMethod = property.getWriteMethod();
				// If no getter or no setter, sorry
				if (readMethod == null || writeMethod == null) {
					continue;
				}
				Class<?> propertyType = property.getPropertyType();
				// Get current value to use as default
				Object currentVal = readMethod.invoke(pojo);
				// Good ol' if else to handle each type
				if (propertyType.isAssignableFrom(boolean.class) || propertyType.isAssignableFrom(Boolean.class)) {
					Boolean newVal = preferences.getBoolean(property.getName(), (Boolean) currentVal);
					writeMethod.invoke(pojo, newVal);
				} else if (propertyType.isAssignableFrom(int.class) || propertyType.isAssignableFrom(Integer.class)) {
					Integer newVal = preferences.getInteger(property.getName(), (Integer) currentVal);
					writeMethod.invoke(pojo, newVal);
				} else if (propertyType.isAssignableFrom(long.class) || propertyType.isAssignableFrom(Long.class)) {
					Long newVal = preferences.getLong(property.getName(), (Long) currentVal);
					writeMethod.invoke(pojo, newVal);
				} else if (propertyType.isAssignableFrom(float.class) || propertyType.isAssignableFrom(Float.class)) {
					Float newVal = preferences.getFloat(property.getName(), (Float) currentVal);
					writeMethod.invoke(pojo, newVal);
				} else if (propertyType.isAssignableFrom(String.class)) {
					String newVal = preferences.getString(property.getName(), (String) currentVal);
					writeMethod.invoke(pojo, newVal);
				}
			}
		} catch (IllegalAccessException | IntrospectionException | InvocationTargetException e) {
			Gdx.app.error("Preferences", "Error while loading preferences", e);
		}
	}

	/**
	 * Writes the contents of a POJO into a Preferences
	 */
	public static <T> void writePreferences(Preferences preferences, T pojo) {
		try {
			BeanInfo beanInfo = Introspector.getBeanInfo(pojo.getClass());
			for (PropertyDescriptor property : beanInfo.getPropertyDescriptors()) {
				Method readMethod = property.getReadMethod();
				// If no getter, sorry
				if (readMethod == null) {
					continue;
				}
				Class<?> propertyType = property.getPropertyType();
				// Get current value to use as default
				Object currentVal = readMethod.invoke(pojo);
				// Good ol' if else to handle each type
				if (propertyType.isAssignableFrom(boolean.class) || propertyType.isAssignableFrom(Boolean.class)) {
					preferences.putBoolean(property.getName(), (Boolean) currentVal);
				} else if (propertyType.isAssignableFrom(int.class) || propertyType.isAssignableFrom(Integer.class)) {
					preferences.putInteger(property.getName(), (Integer) currentVal);
				} else if (propertyType.isAssignableFrom(long.class) || propertyType.isAssignableFrom(Long.class)) {
					preferences.putLong(property.getName(), (Long) currentVal);
				} else if (propertyType.isAssignableFrom(float.class) || propertyType.isAssignableFrom(Float.class)) {
					preferences.putFloat(property.getName(), (Float) currentVal);
				} else if (propertyType.isAssignableFrom(String.class)) {
					preferences.putString(property.getName(), (String) currentVal);
				}
			}
		} catch (IllegalAccessException | IntrospectionException | InvocationTargetException e) {
			Gdx.app.error("Preferences", "Error while saving preferences", e);
		}
	}

	public static <T> void bindAsVariables(T pojo) {
		try {
			BeanInfo beanInfo = Introspector.getBeanInfo(pojo.getClass());
			for (PropertyDescriptor property : beanInfo.getPropertyDescriptors()) {
				Method readMethod = property.getReadMethod();
				Method writeMethod = property.getWriteMethod();
				// If no getter or no setter, sorry
				if (readMethod == null || writeMethod == null) {
					continue;
				}
				Class<?> propertyType = property.getPropertyType();
				// Get current value to use as default
				Object currentVal = readMethod.invoke(pojo);
				// Good ol' if else to handle each type
				if (propertyType.isAssignableFrom(boolean.class) || propertyType.isAssignableFrom(Boolean.class)) {
					Engine.console.bindVar(ConsoleVar.mutableBoolean(property.getName(), () -> {
						try {
							return (Boolean) readMethod.invoke(pojo);
						} catch (IllegalAccessException | InvocationTargetException e) {
							Gdx.app.error("Preferences", "Error reading setting", e);
							return false;
						}
					}, newVal -> {
						try {
							writeMethod.invoke(pojo, newVal);
						} catch (IllegalAccessException | InvocationTargetException e) {
							Gdx.app.error("Preferences", "Error updating setting", e);
						}
					}));
				} else if (propertyType.isAssignableFrom(int.class) || propertyType.isAssignableFrom(Integer.class)) {
					Engine.console.bindVar(ConsoleVar.mutableInt(property.getName(), () -> {
						try {
							return (Integer) readMethod.invoke(pojo);
						} catch (IllegalAccessException | InvocationTargetException e) {
							Gdx.app.error("Preferences", "Error reading setting", e);
							return 0;
						}
					}, newVal -> {
						try {
							writeMethod.invoke(pojo, newVal);
						} catch (IllegalAccessException | InvocationTargetException e) {
							Gdx.app.error("Preferences", "Error updating setting", e);
						}
					}));
				} else if (propertyType.isAssignableFrom(long.class) || propertyType.isAssignableFrom(Long.class)) {
					Engine.console.bindVar(ConsoleVar.mutableLong(property.getName(), () -> {
						try {
							return (Long) readMethod.invoke(pojo);
						} catch (IllegalAccessException | InvocationTargetException e) {
							Gdx.app.error("Preferences", "Error reading setting", e);
							return 0L;
						}
					}, newVal -> {
						try {
							writeMethod.invoke(pojo, newVal);
						} catch (IllegalAccessException | InvocationTargetException e) {
							Gdx.app.error("Preferences", "Error updating setting", e);
						}
					}));
				} else if (propertyType.isAssignableFrom(float.class) || propertyType.isAssignableFrom(Float.class)) {
					Engine.console.bindVar(ConsoleVar.mutableFloat(property.getName(), () -> {
						try {
							return (Float) readMethod.invoke(pojo);
						} catch (IllegalAccessException | InvocationTargetException e) {
							Gdx.app.error("Preferences", "Error reading setting", e);
							return 0f;
						}
					}, newVal -> {
						try {
							writeMethod.invoke(pojo, newVal);
						} catch (IllegalAccessException | InvocationTargetException e) {
							Gdx.app.error("Preferences", "Error updating setting", e);
						}
					}));
				} else if (propertyType.isAssignableFrom(String.class)) {
					Engine.console.bindVar(ConsoleVar.mutableString(property.getName(), () -> {
						try {
							return (String) readMethod.invoke(pojo);
						} catch (IllegalAccessException | InvocationTargetException e) {
							Gdx.app.error("Preferences", "Error reading setting", e);
							return "";
						}
					}, newVal -> {
						try {
							writeMethod.invoke(pojo, newVal);
						} catch (IllegalAccessException | InvocationTargetException e) {
							Gdx.app.error("Preferences", "Error updating setting", e);
						}
					}));
				}
			}
		} catch (IllegalAccessException | IntrospectionException | InvocationTargetException e) {
			Gdx.app.error("Preferences", "Error while loading preferences", e);
		}
	}
}
