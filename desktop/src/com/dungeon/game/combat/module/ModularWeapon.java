package com.dungeon.game.combat.module;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.dungeon.game.combat.Weapon;

import java.util.List;

public class ModularWeapon extends Weapon {

	private final List<WeaponModule> modules;
	private final Animation<TextureRegion> animation;
	private final float cooldown;
	private final float energyDrain;

	public ModularWeapon(String name, Animation<TextureRegion> animation, List<WeaponModule> modules, float cooldown, float energyDrain) {
		super(name);
		this.animation = animation;
		this.modules = modules;
		this.cooldown = cooldown;
		this.energyDrain = energyDrain;
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
	public Animation<TextureRegion> getAnimation() {
		return animation;
	}
}
