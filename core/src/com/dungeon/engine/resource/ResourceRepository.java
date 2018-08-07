package com.dungeon.engine.resource;

import com.badlogic.gdx.utils.Disposable;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * A repository that hosts a specific resource type. Can optionally have a compute function to load
 * resources on demand (based on their name) and/or a cleanup function to apply to resources when the
 * repository is disposed of (for resources that require disposing)
 * @param <T> Type of resources
 */
public class ResourceRepository<T> implements Disposable {

	private final Map<String, T> resources = new HashMap<>();
	private final Function<? super String, ? extends T> compute;
	private final BiConsumer<? super String, ? super T> clean;

	public ResourceRepository() {
		this.compute = k -> null;
		this.clean = (k, v) -> {};
	}

	public ResourceRepository(Function<? super String, ? extends T> compute) {
		this.compute = compute;
		this.clean = (k, v) -> {};
	}

	public ResourceRepository(Function<? super String, ? extends T> compute, BiConsumer<? super String, ? super T> clean) {
		this.compute = compute;
		this.clean = clean;
	}

	public ResourceRepository(Function<? super String, ? extends T> compute, Consumer<? super T> clean) {
		this.compute = compute;
		this.clean = (k, v) -> clean.accept(v);
	}

	public void put(String key, T resource) {
		// Duplicate check is already performed during file loading
		resources.put(key, resource);
	}

	public T get(String key) {
		T resource = resources.computeIfAbsent(key, compute);
		if (resource == null) {
			throw new LoadingException("No resource with key '" + key + "'");
		}
		return resource;
	}

	public void dispose() {
		resources.clear();
	}

}
