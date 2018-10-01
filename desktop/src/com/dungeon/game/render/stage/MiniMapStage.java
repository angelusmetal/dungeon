package com.dungeon.game.render.stage;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.dungeon.engine.render.ViewPortBuffer;
import com.dungeon.engine.util.Util;
import com.dungeon.engine.viewport.ViewPort;
import com.dungeon.game.Game;

// TODO Make this a UI element inside the HudStage
public class MiniMapStage implements RenderStage {

	private final ViewPort viewPort;
	private final ViewPortBuffer viewportBuffer;
	private final Pixmap miniMap;
	private final Texture miniMapTexture;
	private boolean enabled = true;
	private final int width = 100;
	private final int height = 100;
	private final Color background =  Color.valueOf("000000c0");
	private final Color floor = Color.valueOf("a66620c0");

	public MiniMapStage(ViewPort viewPort, ViewPortBuffer viewportBuffer) {
		this.viewPort = viewPort;
		this.viewportBuffer = viewportBuffer;
		// 100x100
		this.miniMap = new Pixmap(width, height, Pixmap.Format.RGBA8888);
		this.miniMapTexture = new Texture(miniMap);
	}

	@Override
	public void render() {
		if (enabled) {
			// Draw map
			viewportBuffer.render(this::drawMap);
		}
	}

	@Override
	public void toggle() {
		enabled = !enabled;
	}

	private void drawMap(SpriteBatch batch) {
		int tSize = Game.getEnvironment().getTileset().tile_size;
		int minX = (viewPort.cameraX + viewPort.cameraWidth / 2) / tSize - width / 2;
		int maxX = minX + width;
		int minY = (viewPort.cameraY + viewPort.cameraHeight / 2) / tSize - height / 2;
		int maxY = minY + height;
		Color color = new Color(0, 0, 0, 1);
		float maxDst = Math.min(width / 2f, height / 2f);
		Vector2 midpoint = new Vector2((viewPort.cameraX + viewPort.cameraWidth / 2f) / tSize, (viewPort.cameraY + viewPort.cameraHeight / 2f) / tSize);
		miniMap.setColor(0, 0, 0, 0);
		miniMap.fill();
		for (int x = minX, xm = 0; x < maxX; x++, xm++) {
			for (int y = minY, ym = 0; y < maxY; y++, ym++) {
				if (x < 0 || x >= Game.getLevel().getWidth() || y < 0 || y >= Game.getLevel().getHeight()) {
					color.set(background);
				} else if (Game.getLevel().isSolid(x, y)) {
					color.set(background);
				} else {
					color.set(floor);
				}
				color.a *= Util.clamp(1 - (midpoint.dst(x, y) / maxDst));
				miniMap.setColor(color);
				miniMap.drawPixel(xm, height - ym);
			}
		}
		// Draw player
		miniMap.setColor(Color.RED);
		miniMap.drawPixel(width / 2, height / 2);

		miniMapTexture.draw(miniMap, 0, 0);
		batch.setColor(1, 1, 1, 1f);
		batch.draw(miniMapTexture, 0, 0);
		batch.setColor(Color.WHITE);
	}

	@Override
	public void dispose() {
		miniMapTexture.dispose();
		miniMap.dispose();
	}

}
