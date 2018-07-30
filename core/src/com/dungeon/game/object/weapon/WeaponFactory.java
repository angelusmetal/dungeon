package com.dungeon.game.object.weapon;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.dungeon.engine.entity.Entity;
import com.dungeon.engine.entity.EntityPrototype;
import com.dungeon.engine.entity.PlayerEntity;
import com.dungeon.engine.entity.Traits;
import com.dungeon.engine.render.Light;
import com.dungeon.engine.resource.ResourceManager;
import com.dungeon.game.combat.CatStaffWeapon;
import com.dungeon.game.combat.GreenStaffWeapon;
import com.dungeon.game.combat.SwordWeapon;
import com.dungeon.game.combat.Weapon;

public class WeaponFactory {

	private static final String SWORD = "weapon_sword";
	private static final String CAT_STAFF = "weapon_cat_staff";
	private static final String GREEN_STAFF = "weapon_green_staff";

	private static final Vector2 BOUNDING_BOX = new Vector2(20, 20);
	private static final Vector2 DRAW_OFFSET = new Vector2(10, 10);

	private Animation<TextureRegion> swordAnimation;
	private Animation<TextureRegion> catStaffAnimation;
	private Animation<TextureRegion> greenStaffAnimation;

	private final EntityPrototype sword;
	private final EntityPrototype catStaff;
	private final EntityPrototype greenStaff;

	private final Light light = new Light(96, new Color(0.1f, 0.8f, 0.7f, 1), Light.RAYS, Light.torchlight(), Light.rotateFast());

	public WeaponFactory() {
		swordAnimation = ResourceManager.getAnimation(SWORD);
		catStaffAnimation = ResourceManager.getAnimation(CAT_STAFF);
		greenStaffAnimation = ResourceManager.getAnimation(GREEN_STAFF);

		sword = weaponPrototype().animation(swordAnimation);
		catStaff = weaponPrototype().animation(catStaffAnimation);
		greenStaff = weaponPrototype().animation(greenStaffAnimation);
	}

	private EntityPrototype weaponPrototype() {
		return new EntityPrototype()
				.boundingBox(BOUNDING_BOX)
				.drawOffset(DRAW_OFFSET)
				.light(light)
				.with(Traits.zOscillate(3, 8f));
	}

	private Entity buildWeaponEntity(Vector2 origin, EntityPrototype prototype, Weapon weapon, Animation<TextureRegion> animation) {
		weapon.setAnimation(animation);
		return new Entity(prototype, origin) {
			@Override public boolean onEntityCollision(Entity other) {
				if (!expired && other instanceof PlayerEntity) {
					PlayerEntity character = (PlayerEntity) other;
					character.getPlayer().setWeapon(weapon);
					character.getPlayer().getConsole().log("Picked up " + weapon.getName() + "!", Color.GOLD);
					expire();
					return true;
				}
				return false;
			}
		};
	}

	public Entity buildSword(Vector2 origin) {
		return buildWeaponEntity(origin, sword, new SwordWeapon(), swordAnimation);
	}

	public Entity buildCatStaff(Vector2 origin) {
		return buildWeaponEntity(origin, catStaff, new CatStaffWeapon(), catStaffAnimation);
	}

	public Entity buildGreenStaff(Vector2 origin) {
		return buildWeaponEntity(origin, greenStaff, new GreenStaffWeapon(), greenStaffAnimation);
	}

}
