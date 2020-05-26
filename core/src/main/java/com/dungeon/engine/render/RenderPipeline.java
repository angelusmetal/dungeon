package com.dungeon.engine.render;
import com.dungeon.engine.util.CyclicProfiler;
import com.dungeon.engine.util.StopWatch;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

public class RenderPipeline {

	private final List<RenderNode> nodes = new ArrayList<>();
	private final StopWatch stopWatch = new StopWatch();
	private int profileSize = 200;
	private boolean profile = false;

	/**
	 * Add a node to the rendering pipeline
	 * @param renderer Render code for the node
	 * @param label Label to identify node; preferably unique (but not required)
	 */
	public void addNode(Renderer renderer, String label) {
		nodes.add(new RenderNode(renderer, label, new CyclicProfiler(profileSize)));
	}

	/**
	 * Render the pipeline
	 */
	public void render() {
		if (!profile) {
			nodes.forEach(RenderNode::render);
		} else {
			stopWatch.start();
			nodes.forEach(node -> {
				node.render();
				node.getProfiler().sample((int) stopWatch.getAndReset());
			});
		}
	}

	/**
	 * Get the current profile size
	 */
	public int getProfileSize() {
		return profileSize;
	}

	/**
	 * Set the current profile size (will only affect nodes added afterwards)
	 * @param profileSize Amount of samples per node profile
	 */
	public void setProfileSize(int profileSize) {
		this.profileSize = profileSize;
	}

	/**
	 * Toggle whether profiling is enabled
	 */
	public void toggleProfile() {
		profile = !profile;
	}

	/**
	 * Find the first node that matches the specified label (exact match) or Optional.empty();
	 */
	public Optional<RenderNode> findNode(String nodeName) {
		return nodes.stream().filter(node -> Objects.equals(node.getLabel(), nodeName)).findFirst();
	}

	/**
	 * Stream all nodes in the pipeline
	 */
	public Stream<RenderNode> streamAll() {
		return nodes.stream();
	}
}
