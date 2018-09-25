package com.dungeon.engine.entity.repository;

import com.badlogic.gdx.math.Rectangle;
import com.dungeon.engine.entity.Entity;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

public class QuadTree {

	private final int nodeCapacity;
	private final int maxDepth;

	private final int depth;
	private final Rectangle bounds;
	private final List<Entity> objects;
	private final QuadTree[] nodes;
	private final float midWidth;
	private final float midHeight;

	/** Create a QuadTree with the specified bounds */
	public QuadTree(Rectangle bounds) {
		this(0, bounds, 200, 10);
	}

	private QuadTree(int depth, Rectangle bounds, int nodeCapacity, int maxDepth) {
		this.depth = depth;
		this.bounds = bounds;
		this.nodeCapacity = nodeCapacity;
		this.maxDepth = maxDepth;
		this.objects = new ArrayList<>();
		this.nodes = new QuadTree[4];
		this.midWidth = bounds.getWidth() / 2f;
		this.midHeight = bounds.getHeight() / 2f;
	}

	public void clear() {
		objects.clear();
		for (int i = 0; i < nodes.length; ++i) {
			nodes[i].clear();
			nodes[i] = null;
		}
	}

	public void insert(Entity entity) {
		// There are subnodes already
		if (nodes[0] != null) {
			int index = getIndex(entity);
			// And the entity fits into one
			if (index != -1) {
				nodes[index].insert(entity);
				return;
			}
		}
		// Otherwise, store the node here
		objects.add(entity);

		// If node capacity is reached (and not yet on max depth) split the node and move the objects
		if (objects.size() >= nodeCapacity && depth < maxDepth - 1) {
			if (nodes[0] == null) {
				split();
			}
		}
	}

	public void remove(Entity entity) {
		if (nodes[0] != null) {
			int index = getIndex(entity);
			if (index != -1) {
				nodes[index].remove(entity);
				return;
			}
		}
		// Hmm this is linear ... not great at all
		objects.remove(entity);
	}

	private void split() {
		nodes[0] = new QuadTree(depth + 1, new Rectangle(bounds.x, bounds.y, midWidth, midHeight), nodeCapacity, maxDepth);
		nodes[1] = new QuadTree(depth + 1, new Rectangle(bounds.x + midWidth, bounds.y, midWidth, midHeight), nodeCapacity, maxDepth);
		nodes[2] = new QuadTree(depth + 1, new Rectangle(bounds.x, bounds.y + midHeight, midWidth, midHeight), nodeCapacity, maxDepth);
		nodes[3] = new QuadTree(depth + 1, new Rectangle(bounds.x + midWidth, bounds.y + midHeight, midWidth, midHeight), nodeCapacity, maxDepth);
		// Relocate all entities that fit in a single child node
		for (Iterator<Entity> e = objects.iterator(); e.hasNext();) {
			Entity entity = e.next();
			int index = getIndex(entity);
			if (index != -1) {
				nodes[index].insert(entity);
				e.remove();
			}
		}
	}

	private int getIndex(Entity entity) {
		return getIndex(
				entity.getBody().getBottomLeft().x,
				entity.getBody().getTopRight().x,
				entity.getBody().getBottomLeft().y,
				entity.getBody().getTopRight().y);
	}

	private int getIndex(float left, float right, float bottom, float top) {
		int index = -1;
		float xSplit = bounds.x + midWidth;
		float ySplit = bounds.y + midHeight;
		boolean fitsTop = bottom >= ySplit;
		boolean fitsBottom = top < ySplit;
		// fits left
		if (right < xSplit) {
			if (fitsTop) {
				index = 2;
			} else if (fitsBottom) {
				index = 0;
			}
		} else if (left >= xSplit) {
			if (fitsTop) {
				index = 3;
			} else if (fitsBottom) {
				index = 1;
			}
		}
		return index;
	}

	private void retrieve(Stream.Builder<Entity> stream, float left, float right, float bottom, float top) {
		if (nodes[0] != null) {
			int index = getIndex(left, right, bottom, top);
			if (index != -1) {
				nodes[index].retrieve(stream, left, right, bottom, top);
			} else {
				nodes[0].retrieve(stream, left, right, bottom, top);
				nodes[1].retrieve(stream, left, right, bottom, top);
				nodes[2].retrieve(stream, left, right, bottom, top);
				nodes[3].retrieve(stream, left, right, bottom, top);
			}
		}
		objects.forEach(stream);
	}

	/** Return a list of elements that might collide with the supplied rectangle */
	public Stream<Entity> retrieve(float left, float right, float bottom, float top) {
		Stream.Builder<Entity> stream = Stream.builder();
		retrieve(stream, left, right, bottom, top);
		return stream.build();
	}

	public static class Stats {
		public int min = Integer.MAX_VALUE;
		public int max = Integer.MIN_VALUE;
		public int avg;
		public int count;

		@Override
		public String toString() {
			return "Stats{" +
					"min=" + min +
					", max=" + max +
					", avg=" + avg +
					", count=" + count +
					'}';
		}
	}

	public List<Stats> getStats() {
		List<Stats> stats = new ArrayList<>(maxDepth);
		for (int i = 0; i < maxDepth; ++i) {
			stats.add(new Stats());
		}
		getStats(stats);
		// Calculate averages
		for (int i = 0; i < maxDepth; ++i) {
			if (stats.get(i).count > 0) {
				stats.get(i).avg /= stats.get(i).count;
			}
		}
		return stats;
	}

	private void getStats(List<Stats> stats) {
		if (nodes[0] != null) {
			nodes[0].getStats(stats);
			nodes[1].getStats(stats);
			nodes[2].getStats(stats);
			nodes[3].getStats(stats);
		}
		stats.get(depth).max = Math.max(stats.get(depth).max, objects.size());
		stats.get(depth).min = Math.min(stats.get(depth).min, objects.size());
		stats.get(depth).avg += objects.size();
		stats.get(depth).count++;
	}
}
