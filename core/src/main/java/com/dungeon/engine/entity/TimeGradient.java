package com.dungeon.engine.entity;

import com.dungeon.engine.Engine;
import com.dungeon.engine.util.Util;

public interface TimeGradient {
	float get();

	static TimeGradient fadeIn(float startTime, float duration) {
		return () -> Util.clamp((Engine.time() - startTime) / duration);
	}

	static TimeGradient fadeOut(float startTime, float duration) {
		return () -> 1 - Util.clamp((Engine.time() - startTime) / duration);
	}

	static TimeGradient crossFade(float start, float end, float startTime, float duration) {
		return () -> {
			float fade = (Engine.time() - startTime) / duration;
			return start * fade + end * (1 - fade);
		};
	}
}
