package com.dungeon.game.player;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.dungeon.engine.ConsoleDisplay;
import com.dungeon.engine.Engine;
import com.dungeon.engine.viewport.ViewPort;
import com.dungeon.game.Game;
import com.dungeon.game.combat.Weapon;
import com.dungeon.game.controller.ControlBundle;
import com.dungeon.game.entity.PlayerEntity;
import com.dungeon.game.level.entity.EntityType;
import com.dungeon.game.object.weapon.WeaponFactory;
import com.dungeon.game.render.stage.ViewPortRenderer;

public class Player {

	private static Color[] PLAYER_COLORS = {new Color(0x5f9ff1ff), new Color(0xf1735fff), new Color(0x7ee740ff), new Color(0xebba52ff)};

	private int playerId;
	private int characterId;
	private String name;
	private ControlBundle control;
	private ViewPort viewPort;
	private ViewPortRenderer renderer;
	private PlayerEntity avatar;
	private PlayerStats stats;
	private Color color;
	private Weapon weapon;
	private ConsoleDisplay console;
	private int gold;

	public Player(int playerId, int characterId, ControlBundle control) {
		this.playerId = playerId;
		this.characterId = characterId;
		this.control = control;
		this.stats = new PlayerStats();
		this.color = PLAYER_COLORS[playerId];
		this.weapon = createWeapon();
		pickName();
		this.console = new ConsoleDisplay(100, 10f);
	}

	private void pickName() {
		if (characterId == 0) {
			name = "KARA";
		} else if (characterId == 1) {
			name = "JACK";
		} else if (characterId == 2) {
			name = "MORT";
		} else if (characterId == 3) {
			name = "ALMA";
		} else {
			name = "????";
		}
	}

	private Weapon createWeapon() {
		// TODO This is awful...
		if (characterId == 0) {
			return new WeaponFactory().buildCatStaff(1f);
		} else if (characterId == 1) {
			return new WeaponFactory().buildSword(1f);
		} else if (characterId == 2) {
			return new WeaponFactory().buildVenomStaff(1f);
		}
		// Hmmm
		return new WeaponFactory().buildSword(1f);
	}

	/**
	 * Spawns an avatar entity of this player on the specified origin, adds it to the list of player characters and
	 * binds the control bundle with the avatar.
	 * @param origin
	 */
	public void spawn(Vector2 origin) {
		avatar = createAvatar(characterId, origin);
		avatar.setPlayerId(playerId);
		Engine.entities.add(avatar);
		enableAvatarControl();
	}

	public void enableAvatarControl() {
		control.setEntity(avatar);
	}

	public void disableAvatarControl() {
		control.setEntity(null);
	}

	private PlayerEntity createAvatar(int characterId, Vector2 origin) {
		if (characterId == 0) {
			return (PlayerEntity) Game.build(EntityType.KARA, origin);
		} else if (characterId == 1) {
			return (PlayerEntity) Game.build(EntityType.JACK, origin);
		} else if (characterId == 2) {
			return (PlayerEntity) Game.build(EntityType.MORT, origin);
		} else {
			return (PlayerEntity) Game.build(EntityType.ALMA, origin);
		}
	}

	public void setViewPort(ViewPort viewPort) {
		this.viewPort = viewPort;
	}

	public ViewPort getViewPort() {
		return viewPort;
	}

	public void setRenderer(ViewPortRenderer renderer) {
		this.renderer = renderer;
	}

	public ViewPortRenderer getRenderer() {
		return renderer;
	}

	public Color getColor() {
		return color;
	}

	public PlayerEntity getAvatar() {
		return avatar;
	}

	public Weapon getWeapon() {
		return weapon;
	}

	public void setWeapon(Weapon weapon) {
		this.weapon = weapon;
	}

	public ConsoleDisplay getConsole() {
		return console;
	}

	public void addGold(int amount) {
		gold += amount;
	}

	public void subtractGold(int amount) {
		gold -= amount;
	}

	public int getGold() {
		return gold;
	}

	public int getCharacterId() {
		return characterId;
	}

	public String getName() {
		return name;
	}
}
