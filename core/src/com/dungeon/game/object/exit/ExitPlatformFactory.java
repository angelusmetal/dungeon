package com.dungeon.game.object.exit;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.dungeon.engine.resource.ResourceManager;
import com.dungeon.game.level.entity.EntityFactory;
import com.dungeon.game.state.GameState;

public class ExitPlatformFactory implements EntityFactory.EntityTypeFactory {

	final GameState state;
	final Animation<TextureRegion> animation;

	public ExitPlatformFactory(GameState state) {
		this.state = state;
		animation = ResourceManager.instance().getAnimation(ExitPlatformSheet.IDLE, ExitPlatformSheet::idle);
	}

	@Override
	public ExitPlatform build(Vector2 origin) {
		return new ExitPlatform(this, origin);
	}

}
