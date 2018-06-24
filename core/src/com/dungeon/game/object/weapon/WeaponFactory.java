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
import com.dungeon.game.state.GameState;

public class WeaponFactory {

	private static final String SWORD = "weapon_sword";
	private static final String CAT_STAFF = "weapon_cat_staff";
	private static final String GREEN_STAFF = "weapon_green_staff";

	private static final Vector2 BOUNDING_BOX = new Vector2(20, 20);
	private static final Vector2 DRAW_OFFSET = new Vector2(10, 10);

	private final EntityPrototype sword;
	private final EntityPrototype catStaff;
	private final EntityPrototype greenStaff;

	private final Light light = new Light(96, new Color(0.1f, 0.8f, 0.7f, 1), Light.RAYS_TEXTURE, Light::torchlight, Light::rotateFast);

	public WeaponFactory() {
		Animation<TextureRegion> swordAnimation = ResourceManager.getAnimation(SWORD);
		Animation<TextureRegion> catStaffAnimation = ResourceManager.getAnimation(CAT_STAFF);
		Animation<TextureRegion> greenStaffAnimation = ResourceManager.getAnimation(GREEN_STAFF);

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

	private Entity buildWeaponEntity(Vector2 origin, EntityPrototype prototype, Weapon weapon) {
		return new Entity(origin, prototype) {
			@Override public boolean onEntityCollision(Entity other) {
				if (!expired && other instanceof PlayerEntity) {
					((PlayerEntity) other).getPlayer().setWeapon(weapon);
					expire();
					GameState.console().log("Picked up " + weapon.getClass().getSimpleName(), Color.GOLD);
					return true;
				}
				return false;
			}
		};
	}

	public Entity buildSword(Vector2 origin) {
		return buildWeaponEntity(origin, sword, new SwordWeapon());
	}

	public Entity buildCatStaff(Vector2 origin) {
		return buildWeaponEntity(origin, catStaff, new CatStaffWeapon());
	}

	public Entity buildGreenStaff(Vector2 origin) {
		return buildWeaponEntity(origin, greenStaff, new GreenStaffWeapon());
	}

}
