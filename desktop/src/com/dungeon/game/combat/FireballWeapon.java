package com.dungeon.game.combat;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.math.Vector2;
import com.dungeon.engine.Engine;
import com.dungeon.engine.entity.EntityPrototype;
import com.dungeon.engine.resource.Resources;
import com.dungeon.engine.util.Rand;
import com.dungeon.game.Game;
import com.dungeon.game.resource.DungeonResources;

import java.util.function.Supplier;

public class FireballWeapon extends ProjectileWeapon {

	private final EntityPrototype projectile;

	Sound soundFirebolt = Resources.sounds.get("audio/sound/firebolt.ogg");

	public FireballWeapon() {
		super("Fireball", damageSupplier(), DamageType.ELEMENTAL, 0);
		projectile = DungeonResources.prototypes.get("slime_fireball");
	}

	@Override
	public void spawnEntities(Vector2 origin, Vector2 aim) {
		createProjectile(projectile, this::createAttack, origin, aim);
		Engine.audio.playSound(soundFirebolt, origin, 1f, 0.05f);
	}

	private static Supplier<Float> damageSupplier() {
		float tier = Game.getDifficultyTier();
		return () -> tier * Rand.between(2f, 5f);
	}

}
