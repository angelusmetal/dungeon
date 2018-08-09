package com.dungeon.engine.resource;

import com.moandjiezana.toml.Toml;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.regex.Pattern;

/**
 * Takes care of scanning a resource folder recursively and load all the contained resource through the
 * registered loaders. It will load all descriptors on the first scan and detect dependencies; on a second
 * pass the resources are loaded from the previously read descriptors (without reading the files a second time)
 */
public class ResourceManagerLoader {

	private final Map<String, ResourceLoader<?>> readers = new HashMap<>();
	public final String ASSETS_PATH = "assets";

	/**
	 * Register a resource loader and associate it with a specific resource identifier name
	 *
	 * @param type   Identifier key for the resource type
	 * @param loader Loader class to detect dependencies and load the resource
	 */
	public void registerLoader(String type, ResourceLoader<?> loader) {
		readers.put(type, loader);
	}

	/**
	 * Load resources from the standard path (assets/) and mask (*.toml)
	 */
	public void load() {
		load(ASSETS_PATH, ".*\\.toml");
	}

	/**
	 * Load resources from a custom path and mask
	 */
	public void load(String path, String pattern) {
		// Read all descriptor files in the assets directory
		List<String> assetFiles = scanPath(path, pattern);

		// Load all their contents into the descriptors dictionary
		Map<ResourceIdentifier, ResourceDescriptor> descriptors = new HashMap<>();
		assetFiles.forEach(file -> loadFile(file, descriptors));

		// Scan dependencies and build the correct put order
		Set<ResourceDescriptor> sequence = new LinkedHashSet<>();
		descriptors.values().forEach(descriptor -> descriptor.scanDependencies(descriptors, sequence));

		// Load resources in the correct order
		sequence.forEach(descriptor -> readers.get(descriptor.getIdentifier().getType()).load(descriptor));
	}

	/**
	 * Recursively scans a path to get the list of resources that match a certain regex
	 *
	 * @return List of strings with the resource filenames
	 */
	private List<String> scanPath(String path, String pattern) {
		List<String> filenames = new ArrayList<>();

		Predicate<String> matches = Pattern.compile(pattern).asPredicate();
		try (
				InputStream in = streamFile(path);
				BufferedReader br = new BufferedReader(new InputStreamReader(in))) {
			String entry;

			while ((entry = br.readLine()) != null) {
				String resource = path + "/" + entry;
				if (new File(ResourceManagerLoader.class.getClassLoader().getResource(resource).getFile()).isDirectory()) {
					filenames.addAll(scanPath(resource, pattern));
				}
				if (matches.test(entry)) {
					filenames.add(resource);
				}
			}
		} catch (IOException e) {
			throw new LoadingException(e);
		}

		return filenames;
	}

	/**
	 * Load descriptors from a file. Unknown types (with no registered loader) will be reported and ignored.
	 * Duplicate identifiers will abort with an error.
	 *
	 * @param filename    file to read from
	 * @param descriptors descriptors dictionary to populate
	 */
	private void loadFile(String filename, Map<ResourceIdentifier, ResourceDescriptor> descriptors) {
		System.out.println("Reading '" + filename + "'...");
		Toml toml = new Toml().read(streamFile(filename));

		// Iterate types
		for (Map.Entry<String, Object> typeEntry : toml.entrySet()) {
			String type = typeEntry.getKey();
			ResourceLoader<?> resourceLoader = readers.get(type);
			if (resourceLoader == null) {
				System.out.println("Ignoring unknown resource type '" + type + "'");
			} else {
				// Iterate entries within type
				Toml typeTable = toml.getTable(type);
				for (Map.Entry<String, Object> entry : typeTable.entrySet()) {
					String key = entry.getKey();
					ResourceIdentifier identifier = new ResourceIdentifier(type, key);
					ResourceDescriptor descriptor = resourceLoader.scan(key, typeTable.getTable(key));
					System.out.println("  - Adding descriptor for " + identifier + "...");
					ResourceDescriptor duplicate = descriptors.put(identifier, descriptor);
					if (duplicate != null) {
						throw new LoadingException("Found duplicate descriptor for identifier: '" + identifier + "'");
					}
				}
			}
		}
	}

	/**
	 * Stream a file using the classloader
	 */
	private InputStream streamFile(String filename) {
		return ResourceManagerLoader.class.getClassLoader().getResourceAsStream(filename);
	}

}
