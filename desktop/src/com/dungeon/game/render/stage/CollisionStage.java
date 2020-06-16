package com.dungeon.game.render.stage;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.dungeon.engine.Engine;
import com.dungeon.engine.render.Renderer;
import com.dungeon.engine.render.ViewPortBuffer;
import com.dungeon.engine.viewport.ViewPort;

import java.util.List;

public class CollisionStage implements Renderer {

	private final ViewPort viewPort;
	private final ViewPortBuffer viewportBuffer;
	private final Color collision = new Color(1f, 0.2f, 0.2f, 0.3f);
	private final Color origin = new Color(0.2f, 1f, 0.2f, 0.3f);
	private final Color geometry = new Color(1f, 1f, 0.2f, 0.3f);
//	private final TextureRegion fill;
	ShapeRenderer shapeRenderer;

	public CollisionStage(ViewPort viewPort, ViewPortBuffer viewportBuffer) {
		this.viewPort = viewPort;
		this.viewportBuffer = viewportBuffer;
		this.shapeRenderer = new ShapeRenderer();
//		this.fill = new TextureRegion(Resources.textures.get("fill.png"));
	}

	@Override
	public void render() {
		viewportBuffer.getFrameBuffer().begin();
		Gdx.gl.glEnable(GL20.GL_BLEND);
		Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
		shapeRenderer.setProjectionMatrix(shapeRenderer.getProjectionMatrix().setToOrtho2D(viewPort.cameraX, viewPort.cameraY, viewPort.width, viewPort.height));
		shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
		// Bounding box & origin
		Engine.entities.inViewPort(viewPort).filter(viewPort::isInViewPort).forEach(e -> {
			shapeRenderer.setColor(collision);
			shapeRenderer.rect(
					(int) (e.getBody().getBottomLeft().x),
					(int) (e.getBody().getBottomLeft().y),
					(int) (e.getBody().getBoundingBox().x),
					(int) (e.getBody().getBoundingBox().y));
			shapeRenderer.setColor(origin);
			shapeRenderer.rect(
					(int) (e.getOrigin().x),
					(int) (e.getOrigin().y),
					1f,
					1f);
		});
		shapeRenderer.end();
		// Geometry (for occluding light)
		shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
		shapeRenderer.setColor(geometry);
		Engine.entities.inViewPort(viewPort).filter(viewPort::isInViewPort).forEach(entity -> {
			List<Vector2> vertexes = entity.getOcclusionSegments();
			for (int i = 0; i < vertexes.size(); i += 2) {
				shapeRenderer.line(
						(int) (vertexes.get(i).x + entity.getOrigin().x),
						(int) (vertexes.get(i).y + entity.getOrigin().y),
						(int) (vertexes.get(i+1).x + entity.getOrigin().x),
						(int) (vertexes.get(i+1).y + entity.getOrigin().y));
			}
		});
		shapeRenderer.end();
		Gdx.gl.glDisable(GL20.GL_BLEND);
		viewportBuffer.getFrameBuffer().end();
	}

	@Override
	public void dispose() {
		shapeRenderer.dispose();
	}

}
