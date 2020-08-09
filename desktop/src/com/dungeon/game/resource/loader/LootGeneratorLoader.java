package com.dungeon.game.resource.loader;

import com.dungeon.engine.resource.ResourceDescriptor;
import com.dungeon.engine.resource.ResourceIdentifier;
import com.dungeon.engine.resource.ResourceLoader;
import com.dungeon.engine.resource.ResourceRepository;
import com.dungeon.engine.util.ConfigUtil;
import com.typesafe.config.Config;

import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class LootGeneratorLoader implements ResourceLoader<LootGenerator> {

	private static final String TYPE = "loot";
	private final ResourceRepository<LootGenerator> repository;

	public LootGeneratorLoader(ResourceRepository<LootGenerator> repository) {
		this.repository = repository;
	}

	@Override
	public ResourceRepository<LootGenerator> getRepository() {
		return repository;
	}

	@Override
	public ResourceDescriptor scan(String key, Config descriptor) {
//		List<ResourceIdentifier> dependencies = ConfigUtil
//				.getConfigList(descriptor, "groups")
//				.orElse(Collections.emptyList())
//				.stream()
//				.flatMap(group -> ConfigUtil
//						.getConfigList(group, "elements")
//						.orElse(Collections.emptyList())
//						.stream()
//						.map(element -> new ResourceIdentifier("prototype", key)))
//				.collect(Collectors.toList());
//		return new ResourceDescriptor(new ResourceIdentifier(TYPE, key), descriptor, dependencies);
		return new ResourceDescriptor(new ResourceIdentifier(TYPE, key), descriptor, Collections.emptyList());
	}

	@Override
	public LootGenerator read(String identifier, Config descriptor) {
		List<LootGenerator.Group> groups = ConfigUtil.getConfigList(descriptor, "groups").orElse(Collections.emptyList()).stream().map(group -> {
			int weight = ConfigUtil.getInteger(group, "weight").orElse(1);
			List<LootGenerator.Element> elements = ConfigUtil.getConfigList(group, "elements").orElse(Collections.emptyList()).stream().map(element -> {
				float chance = ConfigUtil.getFloat(element, "chance").orElse(1f);
				String prototype = ConfigUtil.requireString(element, "prototype");
				Supplier<Integer> count = ConfigUtil.getIntegerRange(element, "count").orElse(() -> 1);
				return new LootGenerator.Element(chance, prototype, count);
			}).collect(Collectors.toList());
			return new LootGenerator.Group(weight, elements);
		}).collect(Collectors.toList());
		return new LootGenerator(groups);
	}
}
