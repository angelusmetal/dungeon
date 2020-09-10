package com.dungeon.engine.audio;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.dungeon.engine.Engine;
import com.dungeon.engine.util.Rand;
import com.dungeon.engine.util.automation.TimeGradient;
import com.dungeon.engine.util.Util;
import com.dungeon.engine.viewport.ViewPort;

import java.util.Iterator;
import java.util.LinkedList;

public class AudioManager {

	boolean enabled = true;
	private float musicVolume = 0.5f;

	private static class MusicTrack {
		Music music;
		TimeGradient fade;
		boolean ending;
	}

	private LinkedList<MusicTrack> currentTracks = new LinkedList<>();

	public void playMusic(LayeredMusic layeredMusic) {
		if (layeredMusic.getIntro() != null) {
			Music.OnCompletionListener completionListener = null;
			if (layeredMusic.getLoop() != null) {
				completionListener = endingTrack -> playMusic(Gdx.files.internal(layeredMusic.getLoop()), true, 0f, null);
			}
			playMusic(Gdx.files.internal(layeredMusic.getIntro()), false, 0f, completionListener);
		} else {
			playMusic(Gdx.files.internal(layeredMusic.getLoop()), true, 0f, null);
		}
	}

	public void playMusic(FileHandle file) {
		playMusic(file, true, 4f, null);
	}

	public void playMusic(FileHandle file, float fade) {
		playMusic(file, true, fade, null);
	}

	public void playMusic(FileHandle file, boolean loop, float fade, Music.OnCompletionListener completionListener) {
		if (!enabled) {
			return;
		}
		MusicTrack track = new MusicTrack();
		try {
			track.music = Gdx.audio.newMusic(file);
			if (completionListener != null) {
				track.music.setOnCompletionListener(completionListener);
			}
		} catch (GdxRuntimeException e) {
			return;
		}

		fadeOut(fade);

		boolean success = false;
		while(!success) {
			try {
				if (fade == 0) {
					track.fade = () -> 1;
				} else {
					track.fade = TimeGradient.fadeIn(Engine.time(), fade);
					track.music.setVolume(0f);
				}
				track.music.play();
				success = true;
			} catch (GdxRuntimeException e) {
				System.err.println(e.getMessage());
				track.music.dispose();
			}
		}

		track.music.setLooping(loop);

		currentTracks.push(track);
	}

	public void fadeOut(float fade) {
		if (fade == 0) {
			// If no fade, stop all previous tracks immediately
			stopMusic();
		} else {
			// If there is a fade, all previous tracks will start a fadeout
			currentTracks.forEach(t -> {
				t.fade = TimeGradient.fadeOut(Engine.time(), fade);
				t.ending = true;
			});
		}
	}

	public void stopMusic() {
		while (!currentTracks.isEmpty()) {
			MusicTrack previous = currentTracks.pop();
			previous.music.stop();
			previous.music.dispose();
			System.err.println("Forcefully stopped track: " + previous);
		}
	}

	public void playSound(Sound sound, Vector2 origin, float volume, float pitchVariance) {
		// TODO keep track of all sounds being played so their volume and panning can be updated (and infinite loops can be paused & resumed)
		ViewPort viewPort = Engine.getMainViewport();
		Vector2 offset = origin.cpy().sub(viewPort.cameraX + viewPort.cameraWidth / 2f, viewPort.cameraY + viewPort.cameraHeight / 2f);
		float pan = offset.x / (viewPort.cameraWidth / 2f);
		float vol = volume * (1 - offset.len() / viewPort.cameraWidth);
		sound.play(Util.clamp(vol), Rand.between(1f - pitchVariance / 2f, 1f + pitchVariance * 2f), Util.clamp(pan, -1f, 1f));
	}

	public void update() {
		for (Iterator<MusicTrack> iterator = currentTracks.iterator(); iterator.hasNext();) {
			MusicTrack track = iterator.next();
			track.music.setVolume(track.fade.get() * musicVolume);
			// Any track that has completely faded out will be stopped, disposed and removed
			if (track.music.getVolume() == 0f && track.ending) {
				track.music.stop();
				track.music.dispose();
				iterator.remove();
			}
		}
	}

	public float getMusicVolume() {
		return musicVolume;
	}

	public void setMusicVolume(float musicVolume) {
		this.musicVolume = musicVolume;
	}
}
