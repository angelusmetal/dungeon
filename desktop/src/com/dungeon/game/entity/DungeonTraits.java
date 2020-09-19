package com.dungeon.game.entity;

import com.dungeon.engine.Engine;
import com.dungeon.engine.entity.Entity;
import com.dungeon.engine.entity.TraitSupplier;
import com.dungeon.engine.util.Metronome;
import com.dungeon.game.combat.Attack;

import java.util.function.Function;

import static com.dungeon.engine.entity.Traits.withExpiration;

public class DungeonTraits {
	public static <T extends Entity> TraitSupplier<T> damageEffect(Attack attack, float damageInterval, float duration, float particleInterval, Function<T, Entity> particleProvider) {
		return entityAtStart -> {
			// TODO Not very nice to do this
			if (entityAtStart instanceof DungeonEntity) {
				Metronome particleMetronome = new Metronome(particleInterval, () -> Engine.entities.add(particleProvider.apply(entityAtStart)));
				Metronome hitMetronome = new Metronome(damageInterval, () -> ((DungeonEntity) entityAtStart).hit(attack));
				return withExpiration(duration, entityAtRuntime -> {
					particleMetronome.doAtInterval();
					hitMetronome.doAtInterval();
				});
			} else {
				return entityAtRuntime -> {};
			}
		};
	}

}
