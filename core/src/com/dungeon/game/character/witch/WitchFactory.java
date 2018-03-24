package com.dungeon.game.character.witch;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.dungeon.engine.entity.PlayerCharacter;
import com.dungeon.engine.entity.Projectile;
import com.dungeon.engine.render.Light;
import com.dungeon.engine.resource.ResourceManager;
import com.dungeon.game.level.entity.EntityFactory;
import com.dungeon.game.object.Tombstone;
import com.dungeon.game.state.GameState;
import com.dungeon.game.tileset.CatProjectileTileset;
import com.dungeon.game.tileset.CharactersTileset32;
import com.dungeon.game.tileset.ProjectileTileset;

import java.util.function.Function;

public class WitchFactory implements EntityFactory.EntityTypeFactory {

	final GameState state;

	final Animation<TextureRegion> attackAnimation;
	final Animation<TextureRegion> idleAnimation;
	final Animation<TextureRegion> walkAnimation;
	final Animation<TextureRegion> bulletFlySideAnimation;
	final Animation<TextureRegion> bulletFlyNorthAnimation;
	final Animation<TextureRegion> bulletFlySouthAnimation;
	final Animation<TextureRegion> bulletExplodeAnimation;

	final Projectile.Builder bulletPrototype;
	final Light bulletLight;

	final Function<Vector2, Tombstone> tombstoneSpawner;

	public WitchFactory(GameState state, Tombstone.Factory tombstoneFactory) {
		this.state = state;

		idleAnimation = ResourceManager.instance().getAnimation(CharactersTileset32.WITCH_IDLE, CharactersTileset32::witchIdle);
		walkAnimation = ResourceManager.instance().getAnimation(CharactersTileset32.WITCH_WALK, CharactersTileset32::witchWalk);
		attackAnimation = ResourceManager.instance().getAnimation(CharactersTileset32.WITCH_ATTACK, CharactersTileset32::witchAttack);
		bulletFlySideAnimation = ResourceManager.instance().getAnimation(CatProjectileTileset.FLY_RIGHT, CatProjectileTileset::flyRight);
		bulletFlyNorthAnimation = ResourceManager.instance().getAnimation(CatProjectileTileset.FLY_UP, CatProjectileTileset::flyUp);
		bulletFlySouthAnimation = ResourceManager.instance().getAnimation(CatProjectileTileset.FLY_DOWN, CatProjectileTileset::flyDown);
		bulletExplodeAnimation = ResourceManager.instance().getAnimation(ProjectileTileset.WITCH_EXPLODE, ProjectileTileset::witchExplode);

		bulletPrototype = new Projectile.Builder().speed(200).timeToLive(10).autoseek(0.1f).targetRadius(60).targetPredicate(PlayerCharacter.IS_NON_PLAYER).damage(50);
		bulletLight = new Light(60, new Color(0.8f, 0.2f, 0.8f, 0.5f), Light.FLARE_TEXTURE, () -> 1f, Light::noRotate);

		tombstoneSpawner = tombstoneFactory::build;
	}

	public Witch build(Vector2 origin) {
		return new Witch(this, origin);
	}
}
