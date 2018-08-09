package com.dungeon.engine.resource;

import com.moandjiezana.toml.Toml;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Describes a resource through a toml blob.
 */
public class ResourceDescriptor {

	private final ResourceIdentifier identifier;
	private final Toml toml;
	private final List<ResourceIdentifier> dependencies;

	public ResourceDescriptor(ResourceIdentifier identifier, Toml toml, List<ResourceIdentifier> dependencies) {
		this.identifier = identifier;
		this.toml = toml;
		this.dependencies = dependencies;
	}

	public ResourceIdentifier getIdentifier() {
		return identifier;
	}

	public Toml getToml() {
		return toml;
	}

	public void scanDependencies(Map<ResourceIdentifier, ResourceDescriptor> descriptors, Set<ResourceDescriptor> sequence) {
		dependencies.forEach(depId -> {
			ResourceDescriptor dependency = descriptors.get(depId);
			if (dependency == null) {
				throw new LoadingException("Dependency '" + depId + "' does not exist");
			}
			dependency.scanDependencies(descriptors, sequence);
		});
		sequence.add(this);
	}
}
