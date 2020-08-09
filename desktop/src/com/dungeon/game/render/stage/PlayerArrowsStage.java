package com.dungeon.game.render.stage;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.dungeon.engine.render.Renderer;
import com.dungeon.engine.render.ViewPortBuffer;
import com.dungeon.engine.resource.Resources;
import com.dungeon.engine.util.Util;
import com.dungeon.engine.viewport.ViewPort;
import com.dungeon.game.player.Player;
import com.dungeon.game.player.Players;

public class PlayerArrowsStage implements Renderer {

	private final ViewPort viewPort;
	private final ViewPortBuffer viewportBuffer;
	private final Sprite arrow;

	public PlayerArrowsStage(ViewPort viewPort, ViewPortBuffer viewportBuffer) {
		this.viewPort = viewPort;
		this.viewportBuffer = viewportBuffer;
		arrow = Resources.loadSprite("player_arrow");
	}

	@Override
	public void render() {
		viewportBuffer.render(batch -> {
			for (Player player : Players.all()) {
				if (!viewPort.isInViewPort(player.getAvatar())) {
					float x = Util.clamp(player.getAvatar().getOrigin().x - viewPort.cameraX, 0, viewPort.cameraWidth - 16);
					float y = Util.clamp(player.getAvatar().getOrigin().y - viewPort.cameraY, 0, viewPort.cameraHeight - 16);
					Vector2 origin = new Vector2(
							x - viewPort.cameraWidth / 2,
							y - viewPort.cameraHeight / 2
					);
					batch.setColor(player.getColor());
					arrow.setOrigin(8, 8);
					arrow.setRotation(origin.angle());
					arrow.setPosition(x, y);
					arrow.draw(batch);
//					batch.draw(
//							texture, x, y,
//							8,8,16,16, 1,1,
//							origin.angle(),0,0,
//							texture.getWidth(), texture.getHeight(),
//							false,false);
					batch.setColor(Color.WHITE);
				}
			}
		});
	}

	@Override
	public void dispose() {}

}
