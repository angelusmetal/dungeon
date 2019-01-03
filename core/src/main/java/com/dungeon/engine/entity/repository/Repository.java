package com.dungeon.engine.entity.repository;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class Repository<T> {

	private final List<T> newObjects = new ArrayList<>();
	private final List<T> objects = new ArrayList<>();

	public void add(T newOne) {
		newObjects.add(newOne);
	}

	public void update(Consumer<T> updateAction, Predicate<T> removePredicate) {
		objects.addAll(newObjects);
		newObjects.clear();
		for (Iterator<T> iterator = objects.iterator(); iterator.hasNext();) {
			T object = iterator.next();
			updateAction.accept(object);
			if (removePredicate.test(object)) {
				iterator.remove();
			}
		}
	}

	public Stream<T> stream() {
		return objects.stream();
	}

}
