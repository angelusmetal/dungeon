package com.dungeon.game.render.stage;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.dungeon.engine.Engine;
import com.dungeon.engine.entity.Entity;
import com.dungeon.engine.render.BlendFunctionContext;
import com.dungeon.engine.render.ViewPortBuffer;
import com.dungeon.engine.util.Util;
import com.dungeon.engine.viewport.ViewPort;
import com.dungeon.game.Game;
import com.dungeon.game.resource.Resources;

public class LightStage implements RenderStage {

	private final ViewPort viewPort;
	private final ViewPortBuffer viewportBuffer;
	private final ViewPortBuffer currentLightBuffer;
	private final ViewPortBuffer lightsBuffer;
	private final BlendFunctionContext combineLights;
	private final BlendFunctionContext blendLights;
	private final float gamma;
	private boolean enabled = true;
	private final TextureRegion shadow;

	private static final float SHADOW_INTENSITY = 0.6f;
	private final Color color = new Color(1, 1, 1, SHADOW_INTENSITY);
	private static final float MAX_HEIGHT_ATTENUATION = 100;
	private static final int VERTICAL_OFFSET = -2;

	public LightStage(ViewPort viewPort, ViewPortBuffer viewportBuffer) {
		this.viewPort = viewPort;
		this.viewportBuffer = viewportBuffer;
		this.currentLightBuffer = new ViewPortBuffer(viewPort);
		this.currentLightBuffer.reset();
		this.currentLightBuffer.getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
		this.lightsBuffer = new ViewPortBuffer(viewPort);
		this.lightsBuffer.reset();
		//this.lightsBuffer.getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
		this.combineLights = new BlendFunctionContext(GL20.GL_ONE, GL20.GL_ONE);
		this.blendLights = new BlendFunctionContext(GL20.GL_DST_COLOR, GL20.GL_ZERO);
		this.gamma = Game.getConfiguration().getDouble("viewport.gamma", 1.0d).floatValue();
		this.shadow = new TextureRegion(Resources.textures.get("shadow.png"));
	}

	@Override
	public void render() {
		if (enabled) {
			// Clear lights buffer
			lightsBuffer.render(batch -> {
				Gdx.gl.glClearColor(Engine.getBaseLight().r, Engine.getBaseLight().g, Engine.getBaseLight().b, 1f);
				Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
			});
			// Render lights in a separate frame buffer
			Engine.entities.inViewPort(viewPort, 100f).filter(viewPort::lightIsInViewPort).filter(e -> e.getLight() != null).forEach(e -> drawLight(e, viewPort));
			// Draw lighting on top of scene
			viewportBuffer.render(batch -> {
//				batch.setColor(GameState.getBaseLight());
				blendLights.run(batch, () -> lightsBuffer.draw(batch));
//				batch.setColor(Color.WHITE);
			});
		}
	}

	private void drawLight(Entity e, ViewPort viewPort) {
		// Draw each light in a separate buffer
		currentLightBuffer.render(batch -> {
			// Clear the buffer (black)
			Gdx.gl.glClearColor(0f, 0f, 0f, 1f);
			Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
			// Set light color
			batch.setColor(
					Util.clamp(e.getLight().color.r * e.getLight().dim),
					Util.clamp(e.getLight().color.g * e.getLight().dim),
					Util.clamp(e.getLight().color.b * e.getLight().dim),
					Util.clamp(e.getLight().color.a * e.getLight().dim));
			// Draw light texture
			viewPort.draw(batch, e.getLight().texture, e.getOrigin().x, e.getOrigin().y + e.getZPos(), e.getLight().diameter * e.getLight().dim, e.getLight().angle);
			// Draw shadows
			Vector2 origin = e.getOrigin().cpy().add(0, e.getZPos());
			Engine.entities.radius(origin, e.getLight().diameter / 2).filter(Entity::castsShadow).forEach(blocker -> {
				color.a = SHADOW_INTENSITY * blocker.getColor().a;
				batch.setColor(color);
				float attenuation = 1 - Math.min(blocker.getZPos(), MAX_HEIGHT_ATTENUATION) / MAX_HEIGHT_ATTENUATION;
				float width = blocker.getBody().getBoundingBox().x * attenuation;
				float height = width / 3 * attenuation;
				viewPort.draw(batch,
						shadow,
						blocker.getBody().getBottomLeft().x,
						blocker.getBody().getBottomLeft().y + VERTICAL_OFFSET,
						width,
						height);
			});
		});
		// And then combine that into the main buffer
		lightsBuffer.render(batch -> combineLights.run(batch, () -> currentLightBuffer.draw(batch)));
	}

	@Override
	public void toggle() {
		enabled = !enabled;
	}

	@Override
	public void dispose() {
		lightsBuffer.dispose();
	}

}
