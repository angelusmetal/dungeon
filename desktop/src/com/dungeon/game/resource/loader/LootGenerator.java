package com.dungeon.game.resource.loader;

import com.dungeon.engine.util.Rand;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

public class LootGenerator {

	static class Element {
		final float chance;
		final String prototype;
		final Supplier<Integer> count;
		public Element(float chance, String prototype, Supplier<Integer> count) {
			this.chance = chance;
			this.prototype = prototype;
			this.count = count;
		}
	}

	static class Group {
		final int weight;
		final List<Element> elements;
		public Group(int weight, List<Element> elements) {
			this.weight = weight;
			this.elements = elements;
		}
	}

	final List<Group> groups;

	public LootGenerator(List<Group> groups) {
		this.groups = groups;
	}

	public List<String> generate() {
		List<String> loot = new ArrayList<>();
		int totalWeight = groups.stream().map(group -> group.weight).reduce(0, Integer::sum);
		int targetWeight = Rand.nextInt(totalWeight);
		int accumulated = 1;
		Optional<Group> targetGroup = Optional.empty();
		for (Group group : groups) {
			if (accumulated <= targetWeight && accumulated + group.weight >= targetWeight) {
				targetGroup = Optional.of(group);
				break;
			}
			accumulated += group.weight;
		}
		targetGroup.ifPresent(group -> {
			for (Element element : group.elements) {
				if (Rand.chance(element.chance)) {
					int count = element.count.get();
					for (int i = 0; i < count; ++i) {
						loot.add(element.prototype);
					}
				}
			}
		});
		return loot;
	}

}
