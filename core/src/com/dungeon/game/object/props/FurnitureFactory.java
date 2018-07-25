package com.dungeon.game.object.props;

import com.badlogic.gdx.math.Vector2;
import com.dungeon.engine.entity.Entity;
import com.dungeon.engine.entity.EntityPrototype;
import com.dungeon.engine.resource.ResourceManager;
import com.dungeon.engine.util.Rand;
import com.dungeon.game.level.entity.EntityType;
import com.dungeon.game.state.GameState;

public class FurnitureFactory {

	private static final String BOOKSHELF = "prop_bookshelf";
	private static final String TABLE_1 = "prop_table_1";
	private static final String TABLE_2 = "prop_table_2";
	private static final String CAGE = "prop_cage";

	private final EntityPrototype bookshelfProtoype;
	private final EntityPrototype tablePrototype;
	private final EntityPrototype table2Prototype;
	private final EntityPrototype cagePrototype;

	public FurnitureFactory() {
		bookshelfProtoype = ResourceManager.getPrototype(BOOKSHELF);
		tablePrototype = ResourceManager.getPrototype(TABLE_1);
		table2Prototype = ResourceManager.getPrototype(TABLE_2);
		cagePrototype = ResourceManager.getPrototype(CAGE);
	}

	public Entity buildBookshelf(Vector2 origin) {
		return buildFurniture(origin, bookshelfProtoype, EntityType.WOOD_PARTICLE);
	}

	public Entity buildTable(Vector2 origin) {
		return buildFurniture(origin, tablePrototype, EntityType.WOOD_PARTICLE);
	}

	public Entity buildTable2(Vector2 origin) {
		return buildFurniture(origin, table2Prototype, EntityType.WOOD_PARTICLE);
	}

	public Entity buildCage(Vector2 origin) {
		return buildFurniture(origin, cagePrototype, EntityType.STONE_PARTICLE);
	}

	private Entity buildFurniture(Vector2 origin, EntityPrototype prototype, EntityType particle) {
		Entity entity = new Entity(origin, prototype) {
			@Override public boolean isSolid() {
				return true;
			}
			@Override public void onHit() {
				Rand.doBetween(3, 6, () ->
						GameState.addEntity(GameState.build(particle, getPos()))
				);
			}
			@Override public void onExpire() {
				Rand.doBetween(20, 30, () ->
						GameState.addEntity(GameState.build(particle, getPos()))
				);
			}
		};
		return entity;
	}
}
