package com.dungeon.engine.util.automation;
import java.util.ArrayList;
import java.util.List;

/**
 * Automates a value through time.
 */
public interface Automation {
	float get(float time);
	float duration();
	static AutomationBuilder of() {
		return new AutomationBuilder();
	}

	class LinearAutomation implements Automation {
		private final float startValue;
		private final float duration;
		private final float endValue;
		public LinearAutomation(float startValue, float endValue, float duration) {
			this.startValue = startValue;
			this.endValue = endValue;
			this.duration = duration;
		}
		@Override public float get(float time) {
			return startValue + (endValue-startValue) * (time/duration);
		}

		@Override public float duration() {
			return duration;
		}
	}

	class CompositeAutomation implements Automation {
		private final List<Automation> segments;
		private final float duration;
		private CompositeAutomation(List<Automation> segments, float duration) {
			this.segments = segments;
			this.duration = duration;
		}
		@Override public float get(float time) {
			float start = 0f;
			// Find segment corresponding to time
			for (Automation segment : segments) {
				if (time < (start + segment.duration())) {
					return segment.get(time - start);
				} else {
					start += segment.duration();
				}
			}
			// Otherwise, over-sample the last segment
			return segments.get(segments.size() - 1).get(time);
		}

		@Override public float duration() {
			return duration;
		}
	}

	class AutomationBuilder {
		private final List<Automation> segments = new ArrayList<>();
		private float duration;
		private AutomationBuilder() {}
		public AutomationBuilder linear(float startValue, float endValue, float duration) {
			segments.add(new LinearAutomation(startValue, endValue, duration));
			this.duration += duration;
			return this;
		}
		public CompositeAutomation build() {
			return new CompositeAutomation(segments, duration);
		}
	}
}
