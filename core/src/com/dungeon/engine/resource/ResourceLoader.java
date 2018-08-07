package com.dungeon.engine.resource;

import com.moandjiezana.toml.Toml;

/**
 * Interface through which the ResourceManagerLoader interacts to load each resource type.
 *
 * @param <T> Type of resource to be loaded.
 */
public interface ResourceLoader<T> {

	/**
	 * Returns the resource repository with which this loader is associated (and onto which it will load resources)
	 *
	 * @return
	 */
	ResourceRepository<T> getRepository();

	/**
	 * Scan a toml blob describing a resource
	 *
	 * @param key  Toml key
	 * @param toml Toml blob
	 * @return ResourceNode describing the resource and its dependencies
	 */
	ResourceDescriptor scan(String key, Toml toml);

	/**
	 * Read an actual resource from a toml blob into an instance of its type
	 *
	 * @param toml Toml blob
	 * @return An instance of the appropriate type
	 */
	T read(Toml toml);

	default void load(ResourceDescriptor descriptor) {
		System.out.println("Loading " + descriptor.getIdentifier() + "...");
		getRepository().put(descriptor.getIdentifier().getKey(), read(descriptor.getToml()));
	}

}
