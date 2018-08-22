package com.dungeon.game.object.weapon;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.dungeon.engine.entity.Entity;
import com.dungeon.engine.entity.EntityPrototype;
import com.dungeon.engine.entity.PlayerEntity;
import com.dungeon.engine.entity.Traits;
import com.dungeon.engine.entity.factory.EntityTypeFactory;
import com.dungeon.engine.render.Light;
import com.dungeon.game.combat.CatStaffWeapon;
import com.dungeon.game.combat.GreenStaffWeapon;
import com.dungeon.game.combat.SwordWeapon;
import com.dungeon.game.combat.Weapon;
import com.dungeon.game.resource.Resources;
import com.dungeon.game.state.GameState;

import java.util.function.Supplier;

public class WeaponFactory {

	public final EntityTypeFactory sword;
	public final EntityTypeFactory catStaff;
	public final EntityTypeFactory greenStaff;

	public WeaponFactory() {
		sword = origin -> buildWeaponEntity(origin, Resources.prototypes.get("weapon_sword"), SwordWeapon::new);
		catStaff = origin -> buildWeaponEntity(origin, Resources.prototypes.get("weapon_cat_staff"), CatStaffWeapon::new);
		greenStaff = origin -> buildWeaponEntity(origin, Resources.prototypes.get("weapon_green_staff"), GreenStaffWeapon::new);
	}

	private Entity buildWeaponEntity(Vector2 origin, EntityPrototype prototype, Supplier<Weapon> weaponSupplier) {
		Weapon weapon = weaponSupplier.get();
		return new Entity(prototype, origin) {
			@Override public boolean onEntityCollision(Entity other) {
				if (!expired && other instanceof PlayerEntity) {
					PlayerEntity character = (PlayerEntity) other;
					weapon.setAnimation(getCurrentAnimation().getAnimation());
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
