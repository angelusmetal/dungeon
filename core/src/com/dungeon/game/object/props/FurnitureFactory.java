package com.dungeon.game.object.props;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.dungeon.engine.entity.Entity;
import com.dungeon.engine.entity.EntityPrototype;
import com.dungeon.engine.entity.PlayerEntity;
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
	private final EntityPrototype chestPrototype;
	private final EntityPrototype painting1Prototype;
	private final EntityPrototype painting2Prototype;
	private final EntityPrototype painting3Prototype;
	private final EntityPrototype coinPrototype;

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
		chestPrototype = ResourceManager.getPrototype("chest");
		painting1Prototype = ResourceManager.getPrototype("prop_painting_1");
		painting2Prototype = ResourceManager.getPrototype("prop_painting_2");
		painting3Prototype = ResourceManager.getPrototype("prop_painting_3");
		coinPrototype = ResourceManager.getPrototype("coin");
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

	public Entity buildChest(Vector2 origin) {
		return new Entity(chestPrototype, origin) {
			@Override public void onHit() {
				Rand.doBetween(1, 2, () ->
						GameState.addEntity(GameState.build(EntityType.WOOD_PARTICLE, getOrigin()))
				);
				if (health < 1) {
					health = 1;
					canBeHit = false;
					setCurrentAnimation(ResourceManager.getAnimation("chest_opening"));
					Entity loot = GameState.build(GameState.createLoot(), getOrigin());
					loot.setZPos(15);
					GameState.addEntity(loot);
				}
			}
		};
	}

	public Entity buildPainting1(Vector2 origin) {
		return new Entity(painting1Prototype, origin);
	}

	public Entity buildPainting2(Vector2 origin) {
		return new Entity(painting2Prototype, origin);
	}

	public Entity buildPainting3(Vector2 origin) {
		return new Entity(painting3Prototype, origin);
	}

	public Entity buildCoin(Vector2 origin) {
		Entity coin = new Entity(coinPrototype, origin) {
			@Override public boolean onEntityCollision(Entity entity) {
				if (!expired && entity instanceof PlayerEntity) {
					PlayerEntity character = (PlayerEntity) entity;
					character.getPlayer().addGold(1);
					character.getPlayer().getConsole().log("Picked up gold!", Color.GOLD);
					expire();
					return true;
				}
				return false;
			}
		};
		coin.impulse(Rand.between(-20, 20), Rand.between(-20, 20));
		return coin;
	}

	private Entity buildProp(Vector2 origin, EntityPrototype prototype, EntityType particle) {
		return buildProp(origin, prototype, particle, 6);
	}

	private Entity buildProp(Vector2 origin, EntityPrototype prototype, EntityType particle, int count) {
		return new Entity(prototype, origin) {
			@Override public void onHit() {
				Rand.doBetween(count / 2, count, () ->
						GameState.addEntity(GameState.build(particle, getOrigin()))
				);
			}
			@Override public void onExpire() {
				Rand.doBetween(count * 2, count * 5, () ->
						GameState.addEntity(GameState.build(particle, getOrigin()))
				);
			}
		};
	}
}
