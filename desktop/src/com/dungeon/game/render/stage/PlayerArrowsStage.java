package com.dungeon.game.render.stage;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.dungeon.engine.render.ViewPortBuffer;
import com.dungeon.engine.util.Util;
import com.dungeon.engine.viewport.ViewPort;
import com.dungeon.game.player.Player;
import com.dungeon.game.player.Players;
import com.dungeon.game.resource.Resources;

public class PlayerArrowsStage implements RenderStage {

	private final ViewPort viewPort;
	private final ViewPortBuffer viewportBuffer;
	private final Texture texture;
	private boolean enabled = true;

	public PlayerArrowsStage(ViewPort viewPort, ViewPortBuffer viewportBuffer) {
		this.viewPort = viewPort;
		this.viewportBuffer = viewportBuffer;
		texture = Resources.textures.get("player_arrow.png");
	}

	@Override
	public void render() {
		if (enabled) {
			viewportBuffer.render((batch) -> {
				for (Player player : Players.all()) {
					if (!viewPort.isInViewPort(player.getAvatar())) {
						float x = Util.clamp(player.getAvatar().getOrigin().x - viewPort.cameraX, 0, viewPort.cameraWidth - 16);
						float y = Util.clamp(player.getAvatar().getOrigin().y - viewPort.cameraY, 0, viewPort.cameraHeight - 16);
						Vector2 origin = new Vector2(
							x - viewPort.cameraWidth / 2,
							y - viewPort.cameraHeight / 2
						);
						batch.setColor(player.getColor());
						batch.draw(
								texture, x, y,
								8,8,16,16, 1,1,
								origin.angle(),0,0,
								texture.getWidth(), texture.getHeight(),
								false,false);
						batch.setColor(Color.WHITE);
					}
				}
			});
		}
	}

	@Override
	public void toggle() {
		enabled = !enabled;
	}

	@Override
	public void dispose() {}

}
