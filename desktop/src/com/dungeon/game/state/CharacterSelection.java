package com.dungeon.game.state;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.dungeon.engine.Engine;
import com.dungeon.game.Game;
import com.dungeon.game.character.player.PlayerCharacterFactory;
import com.dungeon.game.controller.ControlBundle;
import com.dungeon.game.player.Player;
import com.dungeon.game.render.effect.FadeEffect;
import com.dungeon.game.resource.Resources;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class CharacterSelection {

	private static final int CHARACTER_COUNT = 3;
	private int currentPlayer = 0;
	private List<Slot> slots = new ArrayList<>(4);

	/**
	 * Each slot in the character selection with a unique player id, the selected character, and the associated
	 * control bundle.
	 */
	public static class Slot {
		public ControlBundle control;
		int playerId;
		int characterId = 0;
		Slot(ControlBundle control, int playerId) {
			this.control = control;
			this.playerId = playerId;
		}
	}

	// Rendering stuff
	private SpriteBatch batch;
	private Texture playerCharacterScreen;

	public void initialize() {
		batch = new SpriteBatch();
		playerCharacterScreen = Resources.textures.get("character_selection.png");
	}

	public void dispose() {
		batch.dispose();
	}

	public boolean addControl(ControlBundle control) {
		if (slots.size() < 4) {
			slots.add(new Slot(control, currentPlayer++));
			return true;
		} else {
			return false;
		}
	}

	public void selectNextCharacter(ControlBundle control) {
		getSlot(control).ifPresent(s -> s.characterId = (s.characterId + 1) % CHARACTER_COUNT);
	}

	public void selectPrevCharacter(ControlBundle control) {
		getSlot(control).ifPresent(s -> s.characterId = (s.characterId - 1 + CHARACTER_COUNT) % CHARACTER_COUNT);
	}

	public void confirmSelection(ControlBundle control) {
		// TODO Only confirm when all active slots have confirmed
		List<Player> players = slots.stream().map(slot -> new Player(slot.playerId, slot.characterId, slot.control)).collect(Collectors.toList());
		Engine.renderEffects.add(FadeEffect.fadeOut(Engine.time(), () -> Game.startNewGame(players)));
	}

	private Optional<Slot> getSlot(ControlBundle control) {
		for (Slot s : slots) {
			if (s.control == control) {
				return Optional.of(s);
			}
		}
		return Optional.empty();
	}

	private Animation<TextureRegion> getAnimation(int characterId) {
		if (characterId == 0) {
			return Resources.animations.get(PlayerCharacterFactory.WITCH_WALK);
		} else if (characterId == 1) {
			return Resources.animations.get(PlayerCharacterFactory.THIEF_WALK);
		} else {
			return Resources.animations.get(PlayerCharacterFactory.ASSASSIN_WALK);
		}
	}

	public void render() {
		batch.begin();
		Gdx.gl.glClearColor(1, 1, 1, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		batch.draw(playerCharacterScreen, 0, 0, playerCharacterScreen.getWidth() * 4, playerCharacterScreen.getHeight() * 4);
		int x = 50, y = 80;
		for (Slot s : slots) {
			TextureRegion keyFrame = getAnimation(s.characterId).getKeyFrame(Engine.time());
			batch.draw(keyFrame, (x - keyFrame.getRegionWidth() / 2) * 4, (y - keyFrame.getRegionHeight() / 2) * 4, keyFrame.getRegionWidth() * 4, keyFrame.getRegionHeight() * 4);
			x += 100;
		}
		batch.end();
	}

}
