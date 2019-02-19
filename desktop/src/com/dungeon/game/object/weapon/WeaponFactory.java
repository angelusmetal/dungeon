package com.dungeon.game.object.weapon;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.dungeon.engine.entity.Entity;
import com.dungeon.engine.entity.EntityPrototype;
import com.dungeon.game.combat.CatStaffWeapon;
import com.dungeon.game.combat.VenomStaffWeapon;
import com.dungeon.game.combat.SwordWeapon;
import com.dungeon.game.combat.Weapon;
import com.dungeon.game.entity.DungeonEntity;
import com.dungeon.game.entity.PlayerEntity;

import java.util.function.Supplier;

public class WeaponFactory {

	public Entity sword(Vector2 origin, EntityPrototype prototype) {
		return buildWeaponEntity(origin, prototype, SwordWeapon::new);
	}

	public Entity catStaff(Vector2 origin, EntityPrototype prototype) {
		return buildWeaponEntity(origin, prototype, CatStaffWeapon::new);
	}

	public Entity greenStaff(Vector2 origin, EntityPrototype prototype) {
		return buildWeaponEntity(origin, prototype, VenomStaffWeapon::new);
	}

	private Entity buildWeaponEntity(Vector2 origin, EntityPrototype prototype, Supplier<Weapon> weaponSupplier) {
		Weapon weapon = weaponSupplier.get();
		return new DungeonEntity(prototype, origin) {
			@Override public boolean onEntityCollision(Entity other) {
				if (!expired && other instanceof PlayerEntity) {
					PlayerEntity character = (PlayerEntity) other;
					weapon.setAnimation(getAnimation());
					character.getPlayer().setWeapon(weapon);
					character.getPlayer().getConsole().log("Picked up " + weapon.getName() + "!", Color.GOLD);
					expire();
					return true;
				}
				return false;
			}
		};
	}

}
