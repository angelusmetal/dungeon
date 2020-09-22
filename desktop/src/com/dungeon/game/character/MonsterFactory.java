package com.dungeon.game.character;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.math.Vector2;
import com.dungeon.engine.Engine;
import com.dungeon.engine.entity.Entity;
import com.dungeon.engine.entity.EntityPrototype;
import com.dungeon.engine.entity.factory.EntityTypeFactory;
import com.dungeon.engine.render.Material;
import com.dungeon.engine.resource.Resources;
import com.dungeon.game.Game;
import com.dungeon.game.character.monster.AcidSlime;
import com.dungeon.game.character.monster.DarkMinion;
import com.dungeon.game.character.monster.FireSlime;
import com.dungeon.game.character.monster.FireSlimeBoss;
import com.dungeon.game.character.monster.Ghost;
import com.dungeon.game.character.monster.IceSlime;
import com.dungeon.game.character.monster.Slime;
import com.dungeon.game.character.monster.SlimeSpawn;
import com.dungeon.game.combat.Attack;
import com.dungeon.game.combat.DamageType;
import com.dungeon.game.combat.Weapon;
import com.dungeon.game.entity.DungeonEntity;
import com.dungeon.game.entity.PlayerEntity;
import com.dungeon.game.object.weapon.WeaponFactory;
import com.dungeon.game.player.Players;
import com.dungeon.game.resource.DungeonResources;

public class MonsterFactory {

	public static final String POOL_DRY = "slime_pool_dry";

	private static final float ACID_POOL_DAMAGE = 10f;

	private final Animation<Material> poolDryAnimation = Resources.animations.get(POOL_DRY);
	private final EntityTypeFactory acidPoolFactory;
	private final EntityTypeFactory icePoolFactory;
	private final Weapon fireball;
	private final Weapon minionWeapon;

	public MonsterFactory() {
		WeaponFactory weaponFactory = new WeaponFactory();
		fireball = weaponFactory.buildFireballStaff(Game.getDifficultyTier());
		minionWeapon = weaponFactory.buildMinionMace(Game.getDifficultyTier());

		// Factory for acid pools
		final EntityPrototype acidPoolPrototype = DungeonResources.prototypes.get("creature_slime_acid_pool");
		acidPoolFactory = origin -> new DungeonEntity(acidPoolPrototype, origin) {
			@Override public void think() {
				if (Engine.time() > expirationTime - 0.5f && getAnimation() != poolDryAnimation) {
					startAnimation(poolDryAnimation);
				}
			}

			@Override protected boolean onEntityCollision(DungeonEntity entity) {
				if (entity instanceof PlayerEntity) {
					Attack attack = new Attack(this, ACID_POOL_DAMAGE, DamageType.ELEMENTAL, 0);
					entity.hit(attack);
					return true;
				} else {
					return false;
				}
			}
		};

		// Factory for ice pools
		final EntityPrototype icePoolPrototype = DungeonResources.prototypes.get("ice_pool");
		icePoolFactory = origin -> new DungeonEntity(icePoolPrototype, origin) {
			@Override public void think() {
				if (Engine.time() > expirationTime - 0.5f && getAnimation() != poolDryAnimation) {
					startAnimation(poolDryAnimation);
				}
			}

		};
	}

	public Entity acidSlime(Vector2 origin, EntityPrototype prototype) {
		return new AcidSlime(origin, prototype, acidPoolFactory);
	}

	public Entity darkMinion(Vector2 origin, EntityPrototype prototype) {
		return new DarkMinion(origin, prototype, minionWeapon);
	}

	public Entity fireSlimeBoss(Vector2 origin, EntityPrototype prototype) {
		return new FireSlimeBoss(origin, prototype);
	}

	public Entity fireSlime(Vector2 origin, EntityPrototype prototype) {
		return new FireSlime(origin, prototype);
	}

	public Entity fireSlimeExplosion(Vector2 origin, EntityPrototype prototype) {
		return new Entity(prototype, origin) {
			@Override
			protected void onExpire() {
				int bullets = (Players.count() + Game.getLevelCount()) * 2;
				Vector2 aim = new Vector2(0, 1);
				for (int i = 0; i < bullets; ++i) {
					fireball.attack(getOrigin(), aim);
					aim.rotate(360f / bullets);
				}
			}
		};
	}


	public Entity ghost(Vector2 origin, EntityPrototype prototype) {
		return new Ghost(origin, prototype);
	}

	public Entity iceSlime(Vector2 origin, EntityPrototype prototype) {
		return new IceSlime(origin, prototype, icePoolFactory);
	}
	public Entity slime(Vector2 origin, EntityPrototype prototype) {
		return new Slime(origin, prototype);
	}

	public Entity slimeSpawn(Vector2 origin, EntityPrototype prototype) {
		return new SlimeSpawn(origin, prototype);
	}
}
