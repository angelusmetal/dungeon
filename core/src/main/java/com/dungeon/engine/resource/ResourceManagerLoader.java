package com.dungeon.engine.resource;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import com.typesafe.config.ConfigValue;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/**
 * Takes care of scanning a resource folder recursively and load all the contained resource through the
 * registered loaders. It will load all descriptors on the first scan and detect dependencies; on a second
 * pass the resources are loaded from the previously read descriptors (without reading the files a second time)
 */
public class ResourceManagerLoader {

	private final Map<String, ResourceLoader<?>> loaders = new HashMap<>();

	/**
	 * Register a resource loader and associate it with a specific resource identifier name
	 *
	 * @param type   Identifier key for the resource type
	 * @param loader Loader class to detect dependencies and load the resource
	 */
	public void registerLoader(String type, ResourceLoader<?> loader) {
		loaders.put(type, loader);
	}

	/**
	 * Load resources from a custom path and mask
	 */
	public void load(String path) {
		Config config = ConfigFactory.load(path);

		// Load all contents into the descriptors dictionary
		Map<ResourceIdentifier, ResourceDescriptor> descriptors = new HashMap<>();

		// Iterate types
		config.root().entrySet().stream().map(Map.Entry::getKey).forEach(type -> {
			// Get the corresponding loader
			ResourceLoader<?> resourceLoader = loaders.get(type);
			if (resourceLoader == null) {
				System.out.println("Ignoring unknown resource type '" + type + "'");
			} else {
				// Iterate entries within type
				Config typeTable = config.getConfig(type);
				for (Map.Entry<String, ConfigValue> entry : typeTable.root().entrySet()) {
					String key = entry.getKey();
					ResourceIdentifier identifier = new ResourceIdentifier(type, key);
					ResourceDescriptor descriptor = resourceLoader.scan(key, typeTable.getConfig(key));
					System.out.println("  - Adding descriptor for " + identifier + "...");
					ResourceDescriptor duplicate = descriptors.put(identifier, descriptor);
					if (duplicate != null) {
						throw new LoadingException("Found duplicate descriptor for identifier: '" + identifier + "'");
					}
				}
			}
		});

		// Scan dependencies and build the correct put order
		Set<ResourceDescriptor> sequence = new LinkedHashSet<>();
		descriptors.values().forEach(descriptor -> descriptor.scanDependencies(descriptor, descriptors, sequence));

		// Load resources in the correct order
		sequence.forEach(descriptor -> loaders.get(descriptor.getIdentifier().getType()).load(descriptor));
	}

//	/**
//	 * Recursively scans a path to get the list of resources that match a certain regex
//	 *
//	 * @return List of strings with the resource filenames
//	 */
//	private List<String> scanPath(String path, String pattern) {
//		List<String> filenames = new ArrayList<>();
//
//		Predicate<String> matches = Pattern.compile(pattern).asPredicate();
//		try (
//				InputStream in = streamFile(path);
//				BufferedReader br = new BufferedReader(new InputStreamReader(in))) {
//			String entry;
//
//			while ((entry = br.readLine()) != null) {
//				String resource = path + "/" + entry;
//				if (new File(ResourceManagerLoader.class.getClassLoader().getResource(resource).getFile()).isDirectory()) {
//					filenames.addAll(scanPath(resource, pattern));
//				}
//				if (matches.test(entry)) {
//					filenames.add(resource);
//				}
//			}
//		} catch (IOException e) {
//			throw new LoadingException(e);
//		}
//
//		return filenames;
//	}
//
//
//	/**
//	 * Stream a file using the classloader
//	 */
//	private InputStream streamFile(String filename) {
//		return ResourceManagerLoader.class.getClassLoader().getResourceAsStream(filename);
//	}

}
