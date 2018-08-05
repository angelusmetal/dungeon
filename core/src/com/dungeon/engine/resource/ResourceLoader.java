package com.dungeon.engine.resource;

import com.moandjiezana.toml.Toml;

import java.util.Map;

public interface ResourceLoader<T> {

	Map<String, T> getRepository();

	/**
	 * Scan a toml blob describing a resource
	 * @param key Toml key
	 * @param toml Toml blob
	 * @return ResourceNode describing the resource and its dependencies
	 */
	ResourceDescriptor scan(String key, Toml toml);

	/**
	 * Read an actual resource from a toml blob into an instance of its type
	 * @param toml Toml blob
	 * @return An instance of the appropriate type
	 */
	T read(Toml toml);

	default void load(ResourceDescriptor descriptor) {
		System.out.println("Loading " + descriptor.getIdentifier() + "...");
		getRepository().put(descriptor.getIdentifier().getKey(), read(descriptor.getToml()));
	}

}
