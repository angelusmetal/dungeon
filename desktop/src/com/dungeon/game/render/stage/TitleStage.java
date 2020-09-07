package com.dungeon.game.render.stage;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Vector2;
import com.dungeon.engine.render.Renderer;
import com.dungeon.engine.render.ViewPortBuffer;
import com.dungeon.engine.resource.Resources;
import com.dungeon.engine.util.automation.Automation;
import com.dungeon.engine.util.automation.AutomationInstance;
import com.dungeon.engine.viewport.ViewPort;
import com.dungeon.game.resource.DungeonResources;

public class TitleStage implements Renderer {

	private static final float FADE_DURATION = 2f;
	private static final float LEGEND_DURATION = 2f;

	private final ViewPort viewPort;
	private final ViewPortBuffer viewportBuffer;
	private final ViewPortBuffer textBuffer;
	private final ShaderProgram shaderProgram;
	private BitmapFont titleFont;
	private BitmapFont subtitleFont;
//	private Texture ornament;
	private Color color = Color.WHITE.cpy();
	private String title;
	private String subtitle;
	private AutomationInstance titleFade;
	private AutomationInstance subtitleFade;
	private final Vector2 texelSize;
	private final float blurSamples = 6;

	public TitleStage(ViewPort viewPort, ViewPortBuffer viewportBuffer) {
		this.viewPort = viewPort;
		this.viewportBuffer = viewportBuffer;
		this.textBuffer = new ViewPortBuffer(viewPort, Pixmap.Format.RGBA8888);
		shaderProgram = Resources.shaders.get("df_vertex.glsl|processing/blur.glsl");
		textBuffer.reset();
		textBuffer.projectToZeroFull();
		titleFont = Resources.fonts.get("chantelli-antiqua-32");
		subtitleFont = Resources.fonts.get(DungeonResources.DEFAULT_FONT);
//		ornament = Resources.textures.get("title_ornament.png");
		// Both automation start up empty
		titleFade = new AutomationInstance(Automation.of().build());
		subtitleFade = new AutomationInstance(Automation.of().build());
		texelSize = new Vector2(1f / viewPort.width, 1f / viewPort.height);
	}

	@Override
	public void render() {
		// Render title
		if (!titleFade.isFinished()) {
			float fade = titleFade.get();
			textBuffer.render(batch -> {
				Gdx.gl.glClearColor(0f, 0f, 0f, 0f);
				Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
				GlyphLayout titleLayout = new GlyphLayout(titleFont, title);
				titleFont.draw(batch, title,  viewPort.cameraWidth / 2f - titleLayout.width / 2f, viewPort.cameraHeight / 2f + titleLayout.height);
			});
			viewportBuffer.projectToZero();
			displayTextbuffer(fade);
		}
		// Render subtitle
		if (!subtitleFade.isFinished()) {
			float fade = subtitleFade.get();
			textBuffer.render(batch -> {
				Gdx.gl.glClearColor(0f, 0f, 0f, 0f);
				Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
				GlyphLayout subtitleLayout = new GlyphLayout(subtitleFont, subtitle);
				subtitleFont.draw(batch, subtitle,  viewPort.cameraWidth / 2f - subtitleLayout.width / 2f, viewPort.cameraHeight / 2f - subtitleLayout.height - 5 * (1f - fade));
			});
			viewportBuffer.projectToZero();
			displayTextbuffer(fade);
		}
	}

	/**
	 * Displays the previously rendered text buffer with a certain amount of blur and fade (both throttled with the
	 * same value)
	 * @param fade 1: fully opaque & focused, 0: fully transparent and blurred
	 */
	public void displayTextbuffer(float fade) {
		viewportBuffer.renderNoWrap(batch -> {
			shaderProgram.begin();
			shaderProgram.setUniformf("u_texelSize", texelSize);
			shaderProgram.setUniformf("u_samples", blurSamples);
			shaderProgram.setUniformf("u_blur", 1f - fade);
			shaderProgram.end();
			batch.setShader(shaderProgram);
			batch.begin();
			color.a = fade;
			batch.setColor(color);
			textBuffer.draw(batch);
			batch.setColor(Color.WHITE);
			batch.end();
		});
	}

	@Override
	public void dispose() {
		textBuffer.dispose();
	}
	
	public void display(String title) {
		this.title = title;
		// Add automation only for the title
		titleFade = new AutomationInstance(Automation.of()
				.linear(0f, 1f, FADE_DURATION)
				.linear(1f, 1f, LEGEND_DURATION)
				.linear(1f, 0f, FADE_DURATION)
				.build());
	}

	public void display(String title, String subtitle) {
		this.title = title;
		this.subtitle = subtitle;
		// Add automation for both title and subtitle
		titleFade = new AutomationInstance(Automation.of()
				.linear(0f, 1f, FADE_DURATION)
				.linear(1f, 1f, LEGEND_DURATION * 2f)
				.linear(1f, 0f, FADE_DURATION)
				.build());
		subtitleFade = new AutomationInstance(Automation.of()
				.linear(0f, 0f, FADE_DURATION)
				.linear(0f, 1f, FADE_DURATION)
				.linear(1f, 1f, LEGEND_DURATION)
				.linear(1f, 0f, FADE_DURATION)
				.build());
	}

}
