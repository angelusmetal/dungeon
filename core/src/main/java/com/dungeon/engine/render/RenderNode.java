package com.dungeon.engine.render;

import com.dungeon.engine.util.CyclicProfiler;

/**
 * A single node of rendering pipeline.
 */
public class RenderNode {

	private boolean enabled = true;
	private final Renderer renderer;
	private final String label;
	private final CyclicProfiler profiler;

	public RenderNode(Renderer renderer, String label, CyclicProfiler profiler) {
		this.renderer = renderer;
		this.label = label;
		this.profiler = profiler;
	}

	/**
	 * Toggle whether this node is enabled.
	 */
	public final void toggle() {
		enabled = !enabled;
	}

	/**
	 * Render the current node, if enabled
	 */
	public final void render() {
		if (enabled) {
			renderer.render();
		}
	}

	public final void dispose() {
		renderer.dispose();
	}

	public final String getLabel() {
		return label;
	}

	public final CyclicProfiler getProfiler() {
		return profiler;
	}

}
