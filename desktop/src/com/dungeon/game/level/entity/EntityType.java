package com.dungeon.game.level.entity;

public interface EntityType {
	// Exit altar
	String EXIT = "exit_platform";
	// Player characters
	String WITCH = "player_witch";
	String THIEF = "player_thief";
	String ASSASSIN = "player_assassin";
	// Creatures
	String GHOST = "creature_ghost";
	String SLIME = "creature_slime";
	String SLIME_ACID = "creature_slime_acid";
	String SLIME_FIRE = "creature_slime_fire";
	// Items
	String HEALTH_POWERUP = "health_powerup";
	String WEAPON_SWORD = "weapon_sword";
	String WEAPON_CAT_STAFF = "weapon_cat_staff";
	String WEAPON_GREEN_STAFF = "weapon_green_staff";
	// Props
	String TOMBSTONE = "tombstone";
	String DOOR_VERTICAL = "door_vertical";
	String DOOR_HORIZONTAL = "door_horizontal";
	String CHEST = "chest";
	String COIN = "coin";
}
