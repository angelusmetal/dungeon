package com.dungeon.game.character.assassin;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.dungeon.engine.entity.Projectile;
import com.dungeon.engine.render.Light;
import com.dungeon.engine.resource.ResourceManager;
import com.dungeon.game.level.entity.EntityFactory;
import com.dungeon.game.object.Tombstone;
import com.dungeon.game.state.GameState;
import com.dungeon.game.tileset.CharactersTileset32;
import com.dungeon.game.tileset.ProjectileTileset;

import java.util.function.Function;

public class AssassinFactory implements EntityFactory.EntityTypeFactory {

	final GameState state;

	final Animation<TextureRegion> attackAnimation;
	final Animation<TextureRegion> idleAnimation;
	final Animation<TextureRegion> walkAnimation;
	final Animation<TextureRegion> bulletFlyAnimation;
	final Animation<TextureRegion> bulletExplodeAnimation;

	final Projectile.Builder bulletPrototype;
	final Light bulletLight;

	final Function<Vector2, Tombstone> tombstoneSpawner;

	public AssassinFactory(GameState state, Tombstone.Factory tombstoneFactory) {
		this.state = state;

		attackAnimation = ResourceManager.instance().getAnimation(CharactersTileset32.ASSASSIN_ATTACK, CharactersTileset32::assasinAttack);
		idleAnimation = ResourceManager.instance().getAnimation(CharactersTileset32.ASSASSIN_IDLE, CharactersTileset32::assassinIdle);
		walkAnimation = ResourceManager.instance().getAnimation(CharactersTileset32.ASSASSIN_WALK, CharactersTileset32::assassinWalk);
		bulletFlyAnimation = ResourceManager.instance().getAnimation(ProjectileTileset.ASSASSIN_FLY, ProjectileTileset::assasinFly);
		bulletExplodeAnimation = ResourceManager.instance().getAnimation(ProjectileTileset.ASSASSIN_EXPLODE, ProjectileTileset::assasinExplode);

		bulletLight = new Light(60, new Color(0.8f, 0.3f, 0.2f, 0.5f), Light.NORMAL_TEXTURE, () -> 1f, Light::noRotate);
		bulletPrototype = new Projectile.Builder().speed(80).timeToLive(10).damage(100);

		tombstoneSpawner = tombstoneFactory::build;
	}

	public Assassin build(Vector2 origin) {
		return new Assassin(this, origin);
	}
}
