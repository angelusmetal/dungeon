package com.dungeon.game.render.stage;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.dungeon.engine.Engine;
import com.dungeon.engine.render.ViewPortBuffer;
import com.dungeon.engine.ui.HLayout;
import com.dungeon.engine.util.Util;
import com.dungeon.engine.viewport.ViewPort;
import com.dungeon.game.player.Player;
import com.dungeon.game.resource.Resources;
import com.dungeon.game.ui.CoinsWidget;
import com.dungeon.game.ui.HeartWidget;
import com.dungeon.game.ui.WeaponWidget;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class HudStage implements RenderStage {

	private final ViewPort viewPort;
	private final ViewPortBuffer viewportBuffer;
	private boolean enabled = true;
	private final HLayout layout = new HLayout();
	private final CoinsWidget coinsWidget;
	private final WeaponWidget weaponWidget;
	private final HeartWidget heartWidget;

	private List<HudParticle> particles = new ArrayList<>();

	public static class HudParticle {
		Vector2 origin;
		Vector2 destination;
		Vector2 startSpeed;
		Vector2 speed;
		Animation<TextureRegion> animation;
		boolean expired;
		float ratio = 1f;
		float scale;

		public HudParticle(Vector2 origin, Vector2 destination, Vector2 speed, Animation<TextureRegion> animation) {
			this.origin = origin;
			this.destination = destination;
			this.speed = speed;
			this.startSpeed = speed.cpy();
			this.scale = speed.len();
			this.animation = animation;
		}

		public void drawAndUpdate(SpriteBatch batch) {
			origin.mulAdd(speed, Engine.frameTime());
			Vector2 head = destination.cpy().sub(origin);
			float headLen = head.len();
			if (headLen < 4) {
				expired = true;
			} else {
				ratio = Util.clamp(ratio - Engine.frameTime());
				scale += scale * Engine.frameTime();
				speed.set(startSpeed).scl(ratio).add(head.setLength(scale * (1 - ratio)));
				TextureRegion frame = animation.getKeyFrame(Engine.time());
				batch.draw(frame, origin.x - frame.getRegionWidth() / 2f, origin.y - frame.getRegionHeight() / 2f);
			}
		}
	}

	public HudStage(ViewPort viewPort, ViewPortBuffer viewportBuffer, Player player) {
		this.viewPort = viewPort;
		this.viewportBuffer = viewportBuffer;
		coinsWidget = new CoinsWidget(player);
		weaponWidget = new WeaponWidget(player);
		heartWidget = new HeartWidget(player);

		layout.pad(4);
		layout.setX(4);
		layout.setY(viewPort.cameraHeight - 4);
		layout.add(coinsWidget);
		layout.add(weaponWidget);
		layout.add(heartWidget);
	}

	public void addParticle(Vector2 origin, Vector2 destination, Vector2 speed, Animation<TextureRegion> animation) {
		HudParticle particle = new HudParticle(viewPort.worldToScreen(origin), destination, speed, animation);
		particles.add(particle);
	}

	@Override
	public void render() {
		if (enabled) {
			viewportBuffer.render(batch -> {
				layout.draw(batch);
				// Update & draw particles
				for (Iterator<HudParticle> p = particles.iterator(); p.hasNext();) {
					HudParticle particle = p.next();
					particle.drawAndUpdate(batch);
					if (particle.expired) {
						p.remove();
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

	public CoinsWidget getCoinsWidget() {
		return coinsWidget;
	}

	public WeaponWidget getWeaponWidget() {
		return weaponWidget;
	}

	public HeartWidget getHeartWidget() {
		return heartWidget;
	}
}
