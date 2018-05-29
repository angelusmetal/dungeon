package com.dungeon.game.object.props;

import com.badlogic.gdx.math.Vector2;
import com.dungeon.engine.entity.Entity;
import com.dungeon.engine.entity.EntityPrototype;
import com.dungeon.engine.util.Rand;
import com.dungeon.game.level.entity.EntityType;
import com.dungeon.game.state.GameState;

public class FurnitureFactory {

	private final EntityPrototype bookshelfProtoype;
	private final EntityPrototype tablePrototype;
	private final EntityPrototype table2Prototype;
	private final EntityPrototype cagePrototype;

	public FurnitureFactory() {
		bookshelfProtoype = new EntityPrototype()
				.animation(() -> GameState.getLevelTileset().bookshelf().animation)
				.boundingBox(new Vector2(48, 38))
				.drawOffset(new Vector2(24, 24))
				.health(500);
		tablePrototype = new EntityPrototype()
				.animation(() -> GameState.getLevelTileset().table().animation)
				.boundingBox(new Vector2(42, 20))
				.drawOffset(new Vector2(24, 28))
				.health(500);
		table2Prototype = new EntityPrototype()
				.animation(() -> GameState.getLevelTileset().table2().animation)
				.boundingBox(new Vector2(42, 20))
				.drawOffset(new Vector2(24, 28))
				.health(500);
		cagePrototype = new EntityPrototype()
				.animation(() -> GameState.getLevelTileset().cage().animation)
				.boundingBox(new Vector2(20, 36))
				.drawOffset(new Vector2(24, 24))
				.health(1000);
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
