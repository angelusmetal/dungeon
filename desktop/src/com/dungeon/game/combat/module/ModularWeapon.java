package com.dungeon.game.combat.module;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.dungeon.engine.render.Material;
import com.dungeon.game.combat.Weapon;

import java.util.List;

/**
 * Weapon implementation that is made up of multiple modules. This modular nature allows easily composing weapons and
 * creating random weapons.
 */
public class ModularWeapon extends Weapon {

	private final List<WeaponModule> modules;
	private final Animation<Material> animation;
	private final float cooldown;
	private final float energyDrain;
	private final int price;

	public ModularWeapon(String name, Animation<Material> animation, List<WeaponModule> modules, float cooldown, float energyDrain, int price) {
		super(name);
		this.animation = animation;
		this.modules = modules;
		this.cooldown = cooldown;
		this.energyDrain = energyDrain;
		this.price = price;
	}

	@Override
	public void attack(Vector2 origin, Vector2 aim) {
		modules.forEach(module -> module.apply(origin, aim));
	}

	@Override
	public float attackCooldown() {
		return cooldown;
	}

	@Override
	public float energyDrain() {
		return energyDrain;
	}

	@Override
	public Animation<Material> getAnimation() {
		return animation;
	}

	@Override
	public int getPrice() {
		return price;
	}
}
