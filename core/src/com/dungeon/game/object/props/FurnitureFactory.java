package com.dungeon.game.object.props;

import com.badlogic.gdx.math.Vector2;
import com.dungeon.engine.entity.Entity;
import com.dungeon.engine.entity.EntityPrototype;
import com.dungeon.engine.resource.ResourceManager;
import com.dungeon.engine.util.Rand;
import com.dungeon.game.level.entity.EntityType;
import com.dungeon.game.state.GameState;

public class FurnitureFactory {

	private final EntityPrototype bookshelfProtoype;
	private final EntityPrototype tablePrototype;
	private final EntityPrototype table2Prototype;
	private final EntityPrototype cagePrototype;
	private final EntityPrototype bushGreenPrototype;
	private final EntityPrototype bushGreenSmallPrototype;
	private final EntityPrototype bushGoldPrototype;
	private final EntityPrototype bushGoldSmallPrototype;
	private final EntityPrototype bushRedPrototype;
	private final EntityPrototype bushRedSmallPrototype;
	private final EntityPrototype bushCyanPrototype;
	private final EntityPrototype bushCyanSmallPrototype;
	private final EntityPrototype bushPurplePrototype;
	private final EntityPrototype bushPurpleSmallPrototype;
	private final EntityPrototype grass1Prototype;
	private final EntityPrototype grass2Prototype;
	private final EntityPrototype grass3Prototype;
	private final EntityPrototype flower1Prototype;

	public FurnitureFactory() {
		bookshelfProtoype = ResourceManager.getPrototype("prop_bookshelf");
		tablePrototype = ResourceManager.getPrototype("prop_table_1");
		table2Prototype = ResourceManager.getPrototype("prop_table_2");
		cagePrototype = ResourceManager.getPrototype("prop_cage");
		bushGreenPrototype = ResourceManager.getPrototype("prop_bush_green");
		bushGreenSmallPrototype = ResourceManager.getPrototype("prop_bush_green_small");
		bushGoldPrototype = ResourceManager.getPrototype("prop_bush_gold");
		bushGoldSmallPrototype = ResourceManager.getPrototype("prop_bush_gold_small");
		bushRedPrototype = ResourceManager.getPrototype("prop_bush_red");
		bushRedSmallPrototype = ResourceManager.getPrototype("prop_bush_red_small");
		bushCyanPrototype = ResourceManager.getPrototype("prop_bush_cyan");
		bushCyanSmallPrototype = ResourceManager.getPrototype("prop_bush_cyan_small");
		bushPurplePrototype = ResourceManager.getPrototype("prop_bush_purple");
		bushPurpleSmallPrototype = ResourceManager.getPrototype("prop_bush_purple_small");
		grass1Prototype = ResourceManager.getPrototype("prop_grass_1");
		grass2Prototype = ResourceManager.getPrototype("prop_grass_2");
		grass3Prototype = ResourceManager.getPrototype("prop_grass_3");
		flower1Prototype = ResourceManager.getPrototype("prop_flower_1");
	}

	public Entity buildBookshelf(Vector2 origin) {
		return buildProp(origin, bookshelfProtoype, EntityType.WOOD_PARTICLE);
	}

	public Entity buildTable(Vector2 origin) {
		return buildProp(origin, tablePrototype, EntityType.WOOD_PARTICLE);
	}

	public Entity buildTable2(Vector2 origin) {
		return buildProp(origin, table2Prototype, EntityType.WOOD_PARTICLE);
	}

	public Entity buildCage(Vector2 origin) {
		return buildProp(origin, cagePrototype, EntityType.STONE_PARTICLE);
	}

	public Entity buildBushGreen(Vector2 origin) {
		// TODO Make leave particles
		return buildProp(origin, bushGreenPrototype, EntityType.LEAVE_PARTICLE, 1);
	}

	public Entity buildBushGold(Vector2 origin) {
		return buildProp(origin, bushGoldPrototype, EntityType.LEAVE_PARTICLE, 1);
	}

	public Entity buildBushRed(Vector2 origin) {
		return buildProp(origin, bushRedPrototype, EntityType.LEAVE_PARTICLE, 1);
	}

	public Entity buildBushCyan(Vector2 origin) {
		return buildProp(origin, bushCyanPrototype, EntityType.LEAVE_PARTICLE, 1);
	}

	public Entity buildBushPurple(Vector2 origin) {
		return buildProp(origin, bushPurplePrototype, EntityType.LEAVE_PARTICLE, 1);
	}

	public Entity buildBushGreenSmall(Vector2 origin) {
		return buildProp(origin, bushGreenSmallPrototype, EntityType.LEAVE_PARTICLE, 1);
	}

	public Entity buildBushGoldSmall(Vector2 origin) {
		return buildProp(origin, bushGoldSmallPrototype, EntityType.LEAVE_PARTICLE, 1);
	}

	public Entity buildBushRedSmall(Vector2 origin) {
		return buildProp(origin, bushRedSmallPrototype, EntityType.LEAVE_PARTICLE, 1);
	}

	public Entity buildBushCyanSmall(Vector2 origin) {
		return buildProp(origin, bushCyanSmallPrototype, EntityType.LEAVE_PARTICLE, 1);
	}

	public Entity buildBushPurpleSmall(Vector2 origin) {
		return buildProp(origin, bushPurpleSmallPrototype, EntityType.LEAVE_PARTICLE, 1);
	}

	public Entity buildGrass1(Vector2 origin) {
		return buildProp(origin, grass1Prototype, EntityType.LEAVE_PARTICLE, 1);
	}

	public Entity buildGrass2(Vector2 origin) {
		return buildProp(origin, grass2Prototype, EntityType.LEAVE_PARTICLE, 1);
	}

	public Entity buildGrass3(Vector2 origin) {
		return buildProp(origin, grass3Prototype, EntityType.LEAVE_PARTICLE, 1);
	}

	public Entity buildFlower1(Vector2 origin) {
		return buildProp(origin, flower1Prototype, EntityType.LEAVE_PARTICLE, 1);
	}

	private Entity buildProp(Vector2 origin, EntityPrototype prototype, EntityType particle) {
		return buildProp(origin, prototype, particle, 6);
	}

	private Entity buildProp(Vector2 origin, EntityPrototype prototype, EntityType particle, int count) {
		Entity entity = new Entity(origin, prototype) {
			@Override public boolean isSolid() {
				return true;
			}
			@Override public void onHit() {
				Rand.doBetween(count / 2, count, () ->
						GameState.addEntity(GameState.build(particle, getPos()))
				);
			}
			@Override public void onExpire() {
				Rand.doBetween(count * 2, count * 5, () ->
						GameState.addEntity(GameState.build(particle, getPos()))
				);
			}
		};
		return entity;
	}
}
