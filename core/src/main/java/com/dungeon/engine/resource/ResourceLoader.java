package com.dungeon.engine.resource;

import com.typesafe.config.Config;

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
	 * @param key  Config key
	 * @param descriptor Config blob
	 * @return ResourceDescriptor describing the resource and its dependencies
	 */
	ResourceDescriptor scan(String key, Config descriptor);
	/**
	 * Read an actual resource from a config blob into an instance of its type
	 *
	 * @param descriptor Config blob
	 * @return An instance of the appropriate type
	 */
	T read(Config descriptor);

	default void load(ResourceDescriptor descriptor) {
		System.out.println("Loading " + descriptor.getIdentifier().getType() + " '" + descriptor.getIdentifier().getKey() + "'...");
		getRepository().put(descriptor.getIdentifier().getKey(), read(descriptor.getBlob()));
	}

}
