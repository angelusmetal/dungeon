package com.dungeon.game.render.stage;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.dungeon.engine.Engine;
import com.dungeon.engine.render.ViewPortBuffer;
import com.dungeon.engine.util.Util;
import com.dungeon.engine.viewport.ViewPort;
import com.dungeon.game.resource.Resources;

public class TitleStage implements RenderStage {

	private final ViewPort viewPort;
	private final ViewPortBuffer viewportBuffer;
	private final ViewPortBuffer textBuffer;
	private boolean enabled = true;
	private BitmapFont titleFont;
	private BitmapFont subtitleFont;
//	private Texture ornament;
	private Color color = Color.WHITE.cpy();
	private float expiration;

	public TitleStage(ViewPort viewPort, ViewPortBuffer viewportBuffer) {
		this.viewPort = viewPort;
		this.viewportBuffer = viewportBuffer;
		this.textBuffer = new ViewPortBuffer(viewPort, Pixmap.Format.RGBA8888);
		textBuffer.reset();
		titleFont = Resources.fonts.get("chantelli-antiqua-32");
		subtitleFont = Resources.fonts.get(Resources.DEFAULT_FONT);
//		ornament = Resources.textures.get("title_ornament.png");
	}

	@Override
	public void render() {
		if (enabled) {
			if (expiration > Engine.time()) {
				viewportBuffer.render(batch -> {
					color.a = Util.clamp((expiration - Engine.time()) / 2f);
					batch.setColor(color);
					textBuffer.draw(batch);
					batch.setColor(Color.WHITE);
				});
			}
		}
	}

	@Override
	public void toggle() {
		enabled = !enabled;
	}

	@Override
	public void dispose() {

	}
	
	public void display(String title) {
		this.expiration = Engine.time() + 4;
		textBuffer.render(batch -> {
			Gdx.gl.glClearColor(0f, 0f, 0f, 0f);
			Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
			GlyphLayout titleLayout = new GlyphLayout(titleFont, title);
			titleFont.draw(batch, title,  viewPort.cameraWidth / 2f - titleLayout.width / 2, viewPort.cameraHeight / 2f + titleLayout.height);
		});
	}

	public void display(String title, String subtitle) {
		this.expiration = Engine.time() + 4;
		textBuffer.render(batch -> {
			Gdx.gl.glClearColor(0f, 0f, 0f, 0f);
			Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
			GlyphLayout titleLayout = new GlyphLayout(titleFont, title);
			GlyphLayout subtitleLayout = new GlyphLayout(subtitleFont, subtitle);
			titleFont.draw(batch, title,  viewPort.cameraWidth / 2f - titleLayout.width / 2, viewPort.cameraHeight / 2f + titleLayout.height);
			subtitleFont.draw(batch, subtitle,  viewPort.cameraWidth / 2f - subtitleLayout.width / 2, viewPort.cameraHeight / 2f - subtitleLayout.height);
//			batch.draw(ornament, viewPort.cameraWidth / 2 - ornament.getWidth() / 2, viewPort.cameraHeight / 2 - subtitleLayout.height * 2 - ornament.getHeight());
		});
	}

}
