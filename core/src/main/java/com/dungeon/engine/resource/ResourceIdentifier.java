package com.dungeon.engine.resource;

import java.util.Objects;

/**
 * Uniquely identifies a resource, globally. An identifier is made up of a type and a key
 */
public class ResourceIdentifier {
	private final String type;
	private final String key;

	public ResourceIdentifier(String type, String key) {
		this.type = type;
		this.key = key;
	}

	public String getType() {
		return type;
	}

	public String getKey() {
		return key;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		ResourceIdentifier that = (ResourceIdentifier) o;
		return Objects.equals(type, that.type) &&
				Objects.equals(key, that.key);
	}

	@Override
	public int hashCode() {
		return Objects.hash(type, key);
	}

	@Override
	public String toString() {
		return type + '.' + key;
	}
}
