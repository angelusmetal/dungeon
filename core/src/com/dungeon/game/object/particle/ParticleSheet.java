package com.dungeon.game.object.particle;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.dungeon.engine.render.TileSheet;
import com.dungeon.engine.resource.ResourceManager;

public class ParticleSheet extends TileSheet {

	private final TextureRegion WOOD_1 = getTile(0, 0);
	private final TextureRegion WOOD_2 = getTile(1, 0);
	private final TextureRegion WOOD_3 = getTile(2, 0);
	private final TextureRegion WOOD_4 = getTile(3, 0);
	private final TextureRegion WOOD_5 = getTile(4, 0);
	private final TextureRegion WOOD_6 = getTile(5, 0);
	private final TextureRegion WOOD_7 = getTile(6, 0);
	private final TextureRegion WOOD_8 = getTile(7, 0);
	private final TextureRegion STONE_1 = getTile(0, 1);
	private final TextureRegion STONE_2 = getTile(1, 1);
	private final TextureRegion STONE_3 = getTile(2, 1);
	private final TextureRegion STONE_4 = getTile(3, 1);
	private final TextureRegion STONE_5 = getTile(4, 1);
	private final TextureRegion STONE_6 = getTile(5, 1);
	private final TextureRegion STONE_7 = getTile(6, 1);
	private final TextureRegion STONE_8 = getTile(7, 1);
	private final TextureRegion DROPLET_1 = getTile(0, 2);
	private final TextureRegion DROPLET_2 = getTile(1, 2);
	private final TextureRegion DROPLET_3 = getTile(2, 2);
	private final TextureRegion DROPLET_4 = getTile(3, 2);
	private final TextureRegion DROPLET_5 = getTile(4, 2);
	private final TextureRegion DROPLET_6 = getTile(5, 2);
	private final TextureRegion DROPLET_7 = getTile(6, 2);
	private final TextureRegion DROPLET_8 = getTile(7, 2);
	private final TextureRegion FIREBALL_1 = getTile(0, 3);
	private final TextureRegion FIREBALL_2 = getTile(1, 3);
	private final TextureRegion FIREBALL_3 = getTile(2, 3);
	private final TextureRegion FIREBALL_4 = getTile(3, 3);
	private final TextureRegion FIREBALL_5 = getTile(4, 3);
	private final TextureRegion FIREBALL_6 = getTile(5, 3);
	private final TextureRegion FIREBALL_7 = getTile(6, 3);
	private final TextureRegion FIREBALL_8 = getTile(7, 3);
	private final TextureRegion FLAME_1 = getTile(0, 4);
	private final TextureRegion FLAME_2 = getTile(1, 4);
	private final TextureRegion FLAME_3 = getTile(2, 4);
	private final TextureRegion FLAME_4 = getTile(3, 4);
	private final TextureRegion FLAME_5 = getTile(4, 4);
	private final TextureRegion FLAME_6 = getTile(5, 4);
	private final TextureRegion FLAME_7 = getTile(6, 4);
	private final TextureRegion FLAME_8 = getTile(7, 4);
	private final TextureRegion CANDLE_1 = getTile(0, 4);
	private final TextureRegion CANDLE_2 = getTile(1, 4);
	private final TextureRegion CANDLE_3 = getTile(2, 4);
	private final TextureRegion CANDLE_4 = getTile(3, 4);
	private final TextureRegion CANDLE_5 = getTile(4, 4);
	private final TextureRegion CANDLE_6 = getTile(5, 4);
	private final TextureRegion CANDLE_7 = getTile(6, 4);
	private final TextureRegion CANDLE_8 = getTile(7, 4);

	private final Animation<TextureRegion> WOOD_ANIMATION_1 = new Animation<>(0.25f, WOOD_1);
	private final Animation<TextureRegion> WOOD_ANIMATION_2 = new Animation<>(0.25f, WOOD_2);
	private final Animation<TextureRegion> WOOD_ANIMATION_3 = new Animation<>(0.25f, WOOD_3);
	private final Animation<TextureRegion> WOOD_ANIMATION_4 = new Animation<>(0.25f, WOOD_4);
	private final Animation<TextureRegion> WOOD_ANIMATION_5 = new Animation<>(0.25f, WOOD_5);
	private final Animation<TextureRegion> WOOD_ANIMATION_6 = new Animation<>(0.25f, WOOD_6);
	private final Animation<TextureRegion> WOOD_ANIMATION_7 = new Animation<>(0.25f, WOOD_7);
	private final Animation<TextureRegion> WOOD_ANIMATION_8 = new Animation<>(0.25f, WOOD_8);
	private final Animation<TextureRegion> STONE_ANIMATION_1 = new Animation<>(0.25f, STONE_1);
	private final Animation<TextureRegion> STONE_ANIMATION_2 = new Animation<>(0.25f, STONE_2);
	private final Animation<TextureRegion> STONE_ANIMATION_3 = new Animation<>(0.25f, STONE_3);
	private final Animation<TextureRegion> STONE_ANIMATION_4 = new Animation<>(0.25f, STONE_4);
	private final Animation<TextureRegion> STONE_ANIMATION_5 = new Animation<>(0.25f, STONE_5);
	private final Animation<TextureRegion> STONE_ANIMATION_6 = new Animation<>(0.25f, STONE_6);
	private final Animation<TextureRegion> STONE_ANIMATION_7 = new Animation<>(0.25f, STONE_7);
	private final Animation<TextureRegion> STONE_ANIMATION_8 = new Animation<>(0.25f, STONE_8);
	private final Animation<TextureRegion> DROPLET_START_ANIMATION = new Animation<>(0.25f, DROPLET_1, DROPLET_2, DROPLET_3, DROPLET_4);
	private final Animation<TextureRegion> DROPLET_FALL_ANIMATION = new Animation<>(0.25f,  DROPLET_5);
	private final Animation<TextureRegion> DROPLET_END_ANIMATION = new Animation<>(0.25f, DROPLET_6, DROPLET_7, DROPLET_8);
	private final Animation<TextureRegion> FIREBALL_ANIMATION = loop(0.25f, FIREBALL_1, FIREBALL_2, FIREBALL_3, FIREBALL_4, FIREBALL_5, FIREBALL_6, FIREBALL_7, FIREBALL_8);
	private final Animation<TextureRegion> FLAME_ANIMATION = loop(0.25f, FLAME_1, FLAME_2, FLAME_3, FLAME_4, FLAME_5, FLAME_6, FLAME_7, FLAME_8);
	private final Animation<TextureRegion> CANDLE_ANIMATION = loop(0.25f, CANDLE_1, CANDLE_2, CANDLE_3, CANDLE_4, CANDLE_5, CANDLE_6, CANDLE_7, CANDLE_8);

	public static final String WOOD_PARTICLE_1 = "wood_particle_1";
	public static final String WOOD_PARTICLE_2 = "wood_particle_2";
	public static final String WOOD_PARTICLE_3 = "wood_particle_3";
	public static final String WOOD_PARTICLE_4 = "wood_particle_4";
	public static final String WOOD_PARTICLE_5 = "wood_particle_5";
	public static final String WOOD_PARTICLE_6 = "wood_particle_6";
	public static final String WOOD_PARTICLE_7 = "wood_particle_7";
	public static final String WOOD_PARTICLE_8 = "wood_particle_8";
	public static final String STONE_PARTICLE_1 = "stone_particle_1";
	public static final String STONE_PARTICLE_2 = "stone_particle_2";
	public static final String STONE_PARTICLE_3 = "stone_particle_3";
	public static final String STONE_PARTICLE_4 = "stone_particle_4";
	public static final String STONE_PARTICLE_5 = "stone_particle_5";
	public static final String STONE_PARTICLE_6 = "stone_particle_6";
	public static final String STONE_PARTICLE_7 = "stone_particle_7";
	public static final String STONE_PARTICLE_8 = "stone_particle_8";
	public static final String DROPLET_START = "droplet_start";
	public static final String DROPLET_FALL = "droplet_fall";
	public static final String DROPLET_END = "droplet_end";
	public static final String FIREBALL = "fireball";
	public static final String FLAME = "flame";
	public static final String CANDLE = "candle";

	ParticleSheet() {
		super(ResourceManager.getTexture("particles.png"), 8);
	}

	public static Animation<TextureRegion> wood1() {
		return new ParticleSheet().WOOD_ANIMATION_1;
	}

	public static Animation<TextureRegion> wood2() {
		return new ParticleSheet().WOOD_ANIMATION_2;
	}

	public static Animation<TextureRegion> wood3() {
		return new ParticleSheet().WOOD_ANIMATION_3;
	}

	public static Animation<TextureRegion> wood4() {
		return new ParticleSheet().WOOD_ANIMATION_4;
	}

	public static Animation<TextureRegion> wood5() {
		return new ParticleSheet().WOOD_ANIMATION_5;
	}

	public static Animation<TextureRegion> wood6() {
		return new ParticleSheet().WOOD_ANIMATION_6;
	}

	public static Animation<TextureRegion> wood7() {
		return new ParticleSheet().WOOD_ANIMATION_7;
	}

	public static Animation<TextureRegion> wood8() {
		return new ParticleSheet().WOOD_ANIMATION_8;
	}

	public static Animation<TextureRegion> stone1() {
		return new ParticleSheet().STONE_ANIMATION_1;
	}

	public static Animation<TextureRegion> stone2() {
		return new ParticleSheet().STONE_ANIMATION_2;
	}

	public static Animation<TextureRegion> stone3() {
		return new ParticleSheet().STONE_ANIMATION_3;
	}

	public static Animation<TextureRegion> stone4() {
		return new ParticleSheet().STONE_ANIMATION_4;
	}

	public static Animation<TextureRegion> stone5() {
		return new ParticleSheet().STONE_ANIMATION_5;
	}

	public static Animation<TextureRegion> stone6() {
		return new ParticleSheet().STONE_ANIMATION_6;
	}

	public static Animation<TextureRegion> stone7() {
		return new ParticleSheet().STONE_ANIMATION_7;
	}

	public static Animation<TextureRegion> stone8() {
		return new ParticleSheet().STONE_ANIMATION_8;
	}

	public static Animation<TextureRegion> dropletStart() {
		return new ParticleSheet().DROPLET_START_ANIMATION;
	}

	public static Animation<TextureRegion> dropletFall() {
		return new ParticleSheet().DROPLET_FALL_ANIMATION;
	}

	public static Animation<TextureRegion> dropletEnd() {
		return new ParticleSheet().DROPLET_END_ANIMATION;
	}

	public static Animation<TextureRegion> fireball() {
		return new ParticleSheet().FIREBALL_ANIMATION;
	}

	public static Animation<TextureRegion> flame() {
		return new ParticleSheet().FLAME_ANIMATION;
	}

	public static Animation<TextureRegion> candle() {
		return new ParticleSheet().CANDLE_ANIMATION;
	}
}
