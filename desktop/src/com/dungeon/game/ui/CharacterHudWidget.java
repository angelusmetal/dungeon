package com.dungeon.game.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Vector2;
import com.dungeon.engine.Engine;
import com.dungeon.engine.render.Material;
import com.dungeon.engine.resource.Resources;
import com.dungeon.engine.ui.widget.AbstractWidget;
import com.dungeon.engine.ui.widget.Widget;
import com.dungeon.engine.viewport.ViewPort;
import com.dungeon.game.player.Player;
import com.dungeon.game.resource.DungeonResources;

public class CharacterHudWidget extends AbstractWidget implements Widget {
	private final Animation<Material> coins;
	private final Animation<Material> face;
	private final Animation<Material> bars;
	private final Animation<Material> nameplate;
	private final Animation<Material> health;
	private final Animation<Material> energy;
	private final Animation<Material> experience;
	private final BitmapFont font;
	private final BitmapFont chubbyFont;
	private final Player player;
	private final ShaderProgram shaderOutline;
	private final Color outlineColor;
	private final ViewPort viewPort;

	private final float healthWidth;
	private final float energyWidth;
	private final float experienceWidth;

	public CharacterHudWidget(Player player, ViewPort viewPort) {
		face = getFace(player);
		coins = Resources.animations.get("coin");
 		bars = Resources.animations.get("hud_bars");
		nameplate = Resources.animations.get("hud_nameplate");
		health = Resources.animations.get("hud_health");
		energy = Resources.animations.get("hud_energy");
		experience = Resources.animations.get("hud_experience");
		font = Resources.fonts.get(DungeonResources.DEFAULT_FONT);
		chubbyFont = Resources.fonts.get("chubby-9");
		this.player = player;
		shaderOutline = Resources.shaders.get("df_vertex.glsl|outline_border_fragment.glsl");
		height = 48;
		width = 144;
		this.viewPort = viewPort;
		outlineColor = new Color(0x504057ff);
		healthWidth = health.getKeyFrame(0).getDiffuse().getWidth();
		energyWidth = energy.getKeyFrame(0).getDiffuse().getWidth();
		experienceWidth = experience.getKeyFrame(0).getDiffuse().getWidth();
	}

	private Animation<Material> getFace(Player player) {
		if (player.getCharacterId() == 0) {
			return Resources.animations.get("hud_kara");
		} else if (player.getCharacterId() == 1) {
			return Resources.animations.get("hud_jack");
		} else if (player.getCharacterId() == 2) {
			return Resources.animations.get("hud_mort");
		} else if (player.getCharacterId() == 3) {
			return Resources.animations.get("hud_alma");
		} else {
			return Resources.animations.get("hud_alma");
		}
	}

	@Override
	public void draw(SpriteBatch batch) {
		// Display bars background
		Sprite frame = bars.getKeyFrame(Engine.time()).getDiffuse();
		frame.setPosition(x, y);
		frame.draw(batch);

		// Display health
		float healthFill = player.getAvatar().getHealth() / player.getAvatar().getMaxHealth();
		frame = new Sprite(health.getKeyFrame(Engine.time()).getDiffuse());
		frame.setRegionX((int) (frame.getRegionX() + frame.getRegionWidth() * (1f - healthFill)));
		frame.setSize((int) (frame.getWidth() * healthFill), frame.getHeight());
		frame.setPosition(x + 43, y + 22);
		frame.draw(batch);
		String healthText = "" + (int) player.getAvatar().getHealth() + " / " + player.getAvatar().getMaxHealth();
		GlyphLayout layout = new GlyphLayout(font, healthText);
		font.draw(batch, layout, x + 83 - layout.width / 2, y + 22 + font.getLineHeight());

		// Display energy
		float energyFill = player.getAvatar().getEnergy() / player.getAvatar().getMaxEnergy();
		frame = new Sprite(energy.getKeyFrame(Engine.time()).getDiffuse());
		frame.setRegionX((int) (frame.getRegionX() + frame.getRegionWidth() * (1f - energyFill)));
		frame.setSize((int) (frame.getWidth() * energyFill), frame.getHeight());
		frame.setPosition(x + 40, y + 15);
		frame.draw(batch);

		// Display experience
		frame = experience.getKeyFrame(Engine.time()).getDiffuse();
		frame.setPosition(x, y);
		frame.draw(batch);

		// Display nameplate
		frame = nameplate.getKeyFrame(Engine.time()).getDiffuse();
		frame.setColor(player.getColor());
		frame.setPosition(x, y);
		frame.draw(batch);

		// Display mug
		frame = face.getKeyFrame(Engine.time()).getDiffuse();
		frame.setPosition(x, y);
		frame.draw(batch);

		// Display coins
		frame = coins.getKeyFrame(Engine.time()).getDiffuse();
		frame.setColor(Color.WHITE);
		frame.setPosition(x + 42, y + 4);
		frame.draw(batch);
		font.draw(batch, Integer.toString(player.getGold()), x + 51, y + 4 + font.getLineHeight());

		// Display weapon
		if (player.getWeapon().getHudAnimation() != null) {
			TextureRegion weapon = player.getWeapon().getHudAnimation().getKeyFrame(Engine.time()).getDiffuse();
			batch.draw(weapon, x + 120, y + 8);
		}

		batch.end();
		shaderOutline.bind();
		shaderOutline.setUniformf("u_color", outlineColor);
		shaderOutline.setUniformf("u_viewportInverse", new Vector2(1f / viewPort.width, 1f / viewPort.height));
		batch.setShader(shaderOutline);
		batch.begin();
		chubbyFont.draw(batch, player.getName() + " LVL 1", x + 60, y + 45);
		batch.end();
		batch.setShader(null);
		batch.begin();
	}

	@Override
	public Vector2 getCenter() {
		return null;
	}

	public Vector2 getCoinCenter() {
		return new Vector2(x + 42 + coins.getKeyFrame(0).getDiffuse().getRegionWidth() / 2f, y + 4 + coins.getKeyFrame(0).getDiffuse().getRegionHeight() / 2f);
	}

	public Vector2 getHealthCenter() {
		return new Vector2(x + 43 + health.getKeyFrame(0).getDiffuse().getRegionWidth() / 2f, y + 22 + health.getKeyFrame(0).getDiffuse().getRegionHeight() / 2f);
	}
}
