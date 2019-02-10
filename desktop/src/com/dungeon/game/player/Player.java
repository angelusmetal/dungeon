package com.dungeon.game.player;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Disposable;
import com.dungeon.engine.Console;
import com.dungeon.engine.Engine;
import com.dungeon.engine.viewport.ViewPort;
import com.dungeon.game.Game;
import com.dungeon.game.combat.CatStaffWeapon;
import com.dungeon.game.combat.GreenStaffWeapon;
import com.dungeon.game.combat.SwordWeapon;
import com.dungeon.game.combat.Weapon;
import com.dungeon.game.controller.PlayerControlBundle;
import com.dungeon.game.entity.PlayerEntity;
import com.dungeon.game.level.entity.EntityType;
import com.dungeon.game.render.stage.ViewPortRenderer;

public class Player implements Disposable {

	private static Color[] PLAYER_COLORS = {Color.BLUE, Color.RED, Color.GREEN, Color.YELLOW};
	private static Weapon[] PLAYER_WEAPONS = {new CatStaffWeapon(), new GreenStaffWeapon(), new GreenStaffWeapon()};

	private int playerId;
	private int characterId;
	private String name;
	private PlayerControlBundle control;
	private ViewPort viewPort;
	private ViewPortRenderer renderer;
	private PlayerEntity avatar;
	private PlayerStats stats;
	private Color color;
	private Weapon weapon;
	private Console console;
	private int gold;

	public Player(int playerId, int characterId, PlayerControlBundle control) {
		this.playerId = playerId;
		this.characterId = characterId;
		this.control = control;
		this.stats = new PlayerStats();
		this.color = PLAYER_COLORS[playerId];
		this.weapon = PLAYER_WEAPONS[characterId];
		this.console = new Console(10, 3f);
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
		control.setEntity(avatar);
	}

	private PlayerEntity createAvatar(int characterId, Vector2 origin) {
		if (characterId == 0) {
			return (PlayerEntity) Game.build(EntityType.WITCH, origin);
		} else if (characterId == 1) {
			return (PlayerEntity) Game.build(EntityType.THIEF, origin);
		} else {
			return (PlayerEntity) Game.build(EntityType.ASSASSIN, origin);
		}
	}

	public void setViewPort(ViewPort viewPort) {
		this.viewPort = viewPort;
	}

	public ViewPort getViewPort() {
		return viewPort;
	}

	public void setRenderer(ViewPortRenderer renderer) {
		if (this.renderer != null) {
			this.renderer.dispose();
		}
		this.renderer = renderer;
	}

	public ViewPortRenderer getRenderer() {
		return renderer;
	}

	public Color getColor() {
		return color;
	}

	@Override
	public void dispose() {
		if (renderer != null) {
			renderer.dispose();
		}
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

	public Console getConsole() {
		return console;
	}

	public void addGold(int amount) {
		gold += amount;
	}

	public int getGold() {
		return gold;
	}
}
