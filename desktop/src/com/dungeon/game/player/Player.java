package com.dungeon.game.player;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Disposable;
import com.dungeon.engine.OldConsole;
import com.dungeon.engine.Engine;
import com.dungeon.engine.viewport.ViewPort;
import com.dungeon.game.Game;
import com.dungeon.game.combat.CatStaffWeapon;
import com.dungeon.game.combat.SwordWeapon;
import com.dungeon.game.combat.VenomStaffWeapon;
import com.dungeon.game.combat.Weapon;
import com.dungeon.game.controller.ControlBundle;
import com.dungeon.game.entity.PlayerEntity;
import com.dungeon.game.level.entity.EntityType;
import com.dungeon.game.object.weapon.WeaponFactory;
import com.dungeon.game.render.stage.ViewPortRenderer;
import com.dungeon.game.resource.Resources;

public class Player implements Disposable {

	private static Color[] PLAYER_COLORS = {Color.BLUE, Color.RED, Color.GREEN, Color.YELLOW};

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
	private OldConsole console;
	private int gold;

	public Player(int playerId, int characterId, ControlBundle control) {
		this.playerId = playerId;
		this.characterId = characterId;
		this.control = control;
		this.stats = new PlayerStats();
		this.color = PLAYER_COLORS[playerId];
		this.weapon = createWeapon(playerId);
		this.console = new OldConsole(10, 3f);
	}

	private Weapon createWeapon(int playerId) {
		// TODO This is awful...
		if (playerId == 0) {
			Weapon w = new CatStaffWeapon();
			Animation<TextureRegion> animation = new WeaponFactory().catStaff(Vector2.Zero, Resources.prototypes.get("weapon_cat_staff")).getAnimation();
			w.setAnimation(animation);
			return w;
		} else if (playerId == 1) {
			Weapon w = new VenomStaffWeapon();
			Animation<TextureRegion> animation = new WeaponFactory().greenStaff(Vector2.Zero, Resources.prototypes.get("weapon_green_staff")).getAnimation();
			w.setAnimation(animation);
			return w;
		} else if (playerId == 2) {
			Weapon w = new SwordWeapon();
			Animation<TextureRegion> animation = new WeaponFactory().sword(Vector2.Zero, Resources.prototypes.get("weapon_sword")).getAnimation();
			w.setAnimation(animation);
			return w;
		}
		// Hmmm
		return new SwordWeapon();
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

	public OldConsole getConsole() {
		return console;
	}

	public void addGold(int amount) {
		gold += amount;
	}

	public int getGold() {
		return gold;
	}
}
