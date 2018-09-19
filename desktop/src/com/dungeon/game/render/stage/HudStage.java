package com.dungeon.game.render.stage;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.dungeon.engine.Engine;
import com.dungeon.engine.entity.Entity;
import com.dungeon.engine.render.ViewPortBuffer;
import com.dungeon.engine.util.Util;
import com.dungeon.engine.viewport.ViewPort;
import com.dungeon.game.player.Player;
import com.dungeon.game.resource.Resources;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class HudStage implements RenderStage {

	private static float HEALTH_PER_HEART = 50f;
	private static int MARGIN = 4;
	private static int HEART_TAB = 12 + 2;

	private final ViewPort viewPort;
	private final ViewPortBuffer viewportBuffer;
	private final Player player;
	private boolean enabled = true;
	private final BitmapFont font;
	private final Animation<TextureRegion> hearts;
	private final Animation<TextureRegion> coins;

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
				System.out.println("ratio: " + ratio);
				TextureRegion frame = animation.getKeyFrame(Engine.time());
				batch.draw(frame, origin.x - frame.getRegionWidth() / 2f, origin.y - frame.getRegionHeight() / 2f);
			}
		}
	}

	public HudStage(ViewPort viewPort, ViewPortBuffer viewportBuffer, Player player) {
		this.viewPort = viewPort;
		this.viewportBuffer = viewportBuffer;
		this.player = player;
		font = Resources.fonts.get(Resources.DEFAULT_FONT);
		hearts = Resources.animations.get("heart_container");
		coins = Resources.animations.get("coin");
	}

	public void addParticle(Vector2 origin, Vector2 destination, Vector2 speed, Animation<TextureRegion> animation) {
		HudParticle particle = new HudParticle(viewPort.worldToScreen(origin), destination, speed, animation);
		particles.add(particle);
	}

	@Override
	public void render() {
		if (enabled) {
			viewportBuffer.render(batch -> {
				int x = MARGIN;
				int y = viewPort.cameraHeight - MARGIN;
				// Display coins
				TextureRegion coin = coins.getKeyFrame(Engine.time());
				y -= coin.getRegionHeight();
				batch.draw(coin, x, y);
				font.draw(batch, Integer.toString(player.getGold()), x, y);
				x += 12;
				// Display weapon
				y = viewPort.cameraHeight - MARGIN;
				if (player.getWeapon().getAnimation() != null) {
					TextureRegion weapon = player.getWeapon().getAnimation().getKeyFrame(Engine.time());
					y -= weapon.getRegionHeight();
					batch.draw(weapon, x, y);
					x += weapon.getRegionWidth();
				}
				// Display heart containers
				y = viewPort.cameraHeight - MARGIN - HEART_TAB;
				int containers = (int) Math.ceil(player.getAvatar().getMaxHealth() / HEALTH_PER_HEART);
				float remaining = player.getAvatar().getHealth();
				for (int i = 0; i < containers; ++i) {
					TextureRegion heart = hearts.getKeyFrame(Util.clamp(remaining / HEALTH_PER_HEART));
					batch.draw(heart, x + i * HEART_TAB, y);
					remaining -= HEALTH_PER_HEART;
				}
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

}
