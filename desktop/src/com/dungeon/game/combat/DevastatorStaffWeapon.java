package com.dungeon.game.combat;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.math.Vector2;
import com.dungeon.engine.Engine;
import com.dungeon.engine.entity.EntityPrototype;
import com.dungeon.engine.entity.Traits;
import com.dungeon.engine.resource.Resources;
import com.dungeon.engine.util.Rand;
import com.dungeon.game.Game;
import com.dungeon.game.entity.PlayerEntity;
import com.dungeon.game.resource.DungeonResources;

import java.util.function.Supplier;

public class DevastatorStaffWeapon extends ProjectileWeapon {

	private final EntityPrototype projectile;
	private final int tier = (int) Game.getDifficultyTier();
	Sound soundAttack = Resources.sounds.get("audio/sound/magic_bolt.ogg");

	public DevastatorStaffWeapon() {
		super("Devastator staff", damageSupplier(), DamageType.ELEMENTAL, 0);
		projectile = DungeonResources.prototypes.get("devastator_bullet")
				.with(Traits.autoSeek(0.1f, 60, () -> Engine.entities.dynamic().filter(PlayerEntity.TARGET_NON_PLAYER_CHARACTERS)));
	}

	@Override
	public void spawnEntities(Vector2 origin, Vector2 aim) {
		createProjectileFan(projectile, this::createAttack, origin, aim, tier + 5, 10);
		Engine.audio.playSound(soundAttack, origin, 1f, 0.05f);
	}

	private static Supplier<Float> damageSupplier() {
		float tier = Game.getDifficultyTier();
		return () -> tier * Rand.between(200f, 300f);
	}

	public float attackCooldown() {
		return 0.35f;
	}
}
