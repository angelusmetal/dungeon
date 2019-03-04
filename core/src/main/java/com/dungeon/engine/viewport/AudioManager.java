package com.dungeon.engine.viewport;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.dungeon.engine.Engine;
import com.dungeon.engine.util.TimeGradient;

import java.util.Iterator;
import java.util.Stack;

public class AudioManager {

	boolean enabled = false;

	private static class MusicTrack {
		Music music;
		TimeGradient fade;
		boolean ending;
	}

	private Stack<MusicTrack> currentTracks = new Stack<>();

	public void playMusic(FileHandle file) {
		playMusic(file, true, 4f);
	}

	public void playMusic(FileHandle file, float fade) {
		playMusic(file, true, fade);
	}

	public void playMusic(FileHandle file, boolean loop, float fade) {
		if (!enabled) {
			return;
		}
		MusicTrack track = new MusicTrack();
		try {
			track.music = Gdx.audio.newMusic(file);
		} catch (GdxRuntimeException e) {
			System.err.println("Couldn't find audio file: " + file.name());
			return;
		}

		if (fade == 0) {
			// If no fade, stop all previous tracks immediately
			track.fade = () -> 1;
			while (!currentTracks.empty()) {
				MusicTrack previous = currentTracks.pop();
				previous.music.stop();
				previous.music.dispose();
			}
		} else {
			// If there is a fade, all previous tracks will start a fadeout
			track.fade = TimeGradient.fadeIn(Engine.time(), fade);
			track.music.setVolume(0f);
			currentTracks.forEach(t -> {
				t.fade = TimeGradient.fadeOut(Engine.time(), fade);//TimeGradient.crossFade(t.music.getVolume(), 0, Engine.time(), fade);
				t.ending = true;
			});
		}
		track.music.play();
		track.music.setLooping(loop);
		currentTracks.push(track);
	}

	public void update() {
		for (Iterator<MusicTrack> iterator = currentTracks.iterator(); iterator.hasNext();) {
			MusicTrack track = iterator.next();
			track.music.setVolume(track.fade.get());
			// Any track that has completely faded out will be stopped, disposed and removed
			if (track.music.getVolume() == 0f && track.ending) {
				track.music.stop();
				track.music.dispose();
				iterator.remove();
			}
		}
	}

}
