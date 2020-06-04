package com.dungeon.engine.resource;

import com.typesafe.config.Config;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Describes a resource through a typesafe blob.
 */
public class ResourceDescriptor {

	private final ResourceIdentifier identifier;
	private final Config blob;
	private final List<ResourceIdentifier> dependencies;

	public ResourceDescriptor(ResourceIdentifier identifier, Config blob, List<ResourceIdentifier> dependencies) {
		this.identifier = identifier;
		this.blob = blob;
		this.dependencies = dependencies;
	}

	public ResourceIdentifier getIdentifier() {
		return identifier;
	}

	public Config getBlob() {
		return blob;
	}

	public void scanDependencies(ResourceDescriptor source, Map<ResourceIdentifier, ResourceDescriptor> descriptors, Set<ResourceDescriptor> sequence) {
		dependencies.forEach(depId -> {
			ResourceDescriptor dependency = descriptors.get(depId);
			if (dependency == null) {
				throw new LoadingException("Dependency '" + depId + "' does not exist; required by '" + source.getIdentifier() + "'");
			}
			dependency.scanDependencies(dependency, descriptors, sequence);
		});
		sequence.add(this);
	}
}
