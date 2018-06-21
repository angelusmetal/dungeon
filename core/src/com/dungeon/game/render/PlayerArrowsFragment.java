package com.dungeon.game.render;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.dungeon.engine.render.ViewPortBuffer;
import com.dungeon.engine.resource.ResourceManager;
import com.dungeon.engine.util.Util;
import com.dungeon.engine.viewport.ViewPort;
import com.dungeon.game.player.Player;
import com.dungeon.game.state.GameState;
import com.dungeon.game.state.OverlayText;

import java.util.Comparator;

public class PlayerArrowsFragment implements RenderFragment {

	private final ViewPort viewPort;
	private final ViewPortBuffer viewportBuffer;
	private final Texture texture;
	private boolean enabled = true;

	public PlayerArrowsFragment(ViewPort viewPort, ViewPortBuffer viewportBuffer) {
		this.viewPort = viewPort;
		this.viewportBuffer = viewportBuffer;
		texture = ResourceManager.getTexture("player_arrow.png");
	}

	@Override
	public void render() {
		if (enabled) {
			viewportBuffer.render((batch) -> {
				for (Player player : GameState.getPlayers()) {
					if (!viewPort.isInViewPort(player.getAvatar())) {
						float x = Util.clamp(player.getAvatar().getPos().x - viewPort.cameraX, 0, viewPort.cameraWidth - 16);
						float y = Util.clamp(player.getAvatar().getPos().y - viewPort.cameraY, 0, viewPort.cameraHeight - 16);
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
