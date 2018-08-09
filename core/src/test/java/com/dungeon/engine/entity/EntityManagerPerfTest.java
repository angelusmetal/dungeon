package com.dungeon.engine.entity;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.dungeon.engine.util.Rand;
import com.dungeon.engine.util.StopWatch;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import static org.mockito.MockitoAnnotations.initMocks;

public class EntityManagerPerfTest {

	public static final int ENTITY_COUNT = 2000;
	public static final int ITERATIONS = 50_000_000;

	EntityManager manager = new EntityManager();
	@Mock
	Animation<TextureRegion> animation;

	@Before
	public void init() {
		initMocks(this);
		for (int i = 0; i < ENTITY_COUNT; ++i) {
			manager.add(getRandomEntity());
		}
	}

	@Test
	public void testRadius() {
		Vector2 point = new Vector2(10, 10);
		StopWatch watch = new StopWatch();
		watch.measure(ITERATIONS, () -> manager.radius(point, 100f));
		System.out.println("Radius: " + ITERATIONS + " iterations took " + watch);
	}

	private Entity getRandomEntity() {
		EntityPrototype prototype = new EntityPrototype();
		prototype.animation(animation);
		prototype.boundingBox(new Vector2(Rand.between(4, 20), Rand.between(4, 20)));
		return new Entity(prototype, new Vector2(Rand.between(-2000, 2000), Rand.between(-2000, 2000)));
	}

}
