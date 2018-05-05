package com.dungeon.game.object.exit;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.dungeon.engine.entity.Entity;
import com.dungeon.engine.entity.EntityPrototype;
import com.dungeon.engine.entity.PlayerCharacter;
import com.dungeon.engine.resource.ResourceManager;
import com.dungeon.game.level.entity.EntityFactory;
import com.dungeon.game.state.GameState;

public class ExitPlatformFactory implements EntityFactory.EntityTypeFactory {

	private static final Vector2 BOUNDING_BOX = new Vector2(64, 64);
	private static final Vector2 DRAW_OFFSET = new Vector2(32, 32);

	final Animation<TextureRegion> animation;
	final EntityPrototype prototype;

	public ExitPlatformFactory() {
		animation = ResourceManager.instance().getAnimation(ExitPlatformSheet.IDLE, ExitPlatformSheet::idle);
		prototype = new EntityPrototype()
				.animation(animation)
				.boundingBox(BOUNDING_BOX)
				.drawOffset(DRAW_OFFSET)
				.zIndex(-1);
	}

	@Override
	public Entity build(Vector2 origin) {
		return new Entity(origin, prototype) {
			boolean exited = false;
			@Override
			protected boolean onEntityCollision(Entity entity) {
				if (!exited && entity instanceof PlayerCharacter) {
					exited = true;
					GameState.exitLevel();
				}
				return true;
			}
		};
	}

}
