package com.dungeon.engine.entity;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.dungeon.engine.entity.repository.EntityRepository;
import com.dungeon.engine.util.Rand;
import com.dungeon.engine.util.StopWatch;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import static org.mockito.MockitoAnnotations.initMocks;

public class EntityManagerPerfTest {

	public static final int ENTITY_COUNT = 250_000;
	public static final int ITERATIONS = 50_000_000;

	EntityRepository repository = new EntityRepository();
	@Mock
	Animation<Sprite> animation;

	@Before
	public void init() {
		initMocks(this);
		repository.clear(100_000, 100_000);
		for (int i = 0; i < ENTITY_COUNT; ++i) {
			repository.add(getRandomEntity());
		}
		repository.commit(false);
		System.out.println("Done initializing");
	}

	@Test
	public void testRadius() {
		Vector2 point = new Vector2(10, 10);
		StopWatch watch = new StopWatch();
		watch.measure(ITERATIONS, () -> repository.radius(point, 100f));
		System.out.println("Radius: " + ITERATIONS + " iterations took " + watch);
	}

	@Test
	public void testEntity() {
		Entity randomEntity = getRandomEntity();
		StopWatch watch = new StopWatch();
		watch.measure(ITERATIONS, () -> repository.colliding(randomEntity));
		System.out.println("Entity: " + ITERATIONS + " iterations took " + watch);
	}

	private Entity getRandomEntity() {
		EntityPrototype prototype = new EntityPrototype();
//		prototype.animation(animation);
		prototype.boundingBox(new Vector2(Rand.between(4, 20), Rand.between(4, 20)));
		prototype.isStatic(true);
		return new Entity(prototype, new Vector2(Rand.between(0, 100_000), Rand.between(0, 100_000))) {
			@Override public void spawn() {}
		};
	}

}
