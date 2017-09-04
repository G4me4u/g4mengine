package com.g4mesoft.search;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.g4mesoft.math.Vec2f;

public class AStarSearch {
	
	/**
	 * A precalculated value, which tells how large of an
	 * area can be searched. The width of the scanarea has
	 * a width of (1 << (SCAN_SIZE_BIT_SHIFT + 1)) including 
	 * negative indices. 
	 * 
	 * NOTE: do not change! For integer indices, this value 
	 * should be 16.
	 */
	private static final long SCAN_SIZE_BIT_SHIFT = 32;
	
	private static final float MOVE_COST = 10.0f;
	private static final float MOVE_COST_DIAG = (float)Math.sqrt(MOVE_COST * MOVE_COST);
	
	private final BinaryTree<Float, Node> openList;
	private final BinaryTree<Long, Node> searchList;
	
	private final Vec2f goal;
	private final Vec2f tmp;

	private final float stepSize;
	private final boolean allowDiagonals;
	
	public AStarSearch(float stepSize, boolean allowDiagonals) {
		this.stepSize = stepSize;
		this.allowDiagonals = allowDiagonals;

		openList = new BinaryTree<Float, Node>();
		searchList = new BinaryTree<Long, Node>();
		
		goal = new Vec2f();
		tmp = new Vec2f();
	}
	
	public float getDistance(Vec2f pos) {
		return goal.distManhattan(pos) / stepSize;
	}

	public void setGoal(Vec2f goal) {
		this.goal.set(goal);
	}
	
	private long offsetIndex(long index, CardinalDirection dir) {
		return index + (long)dir.getOffset().x + ((long)dir.getOffset().y << SCAN_SIZE_BIT_SHIFT);
	}
	
	private void visitNextNode(PositionFilter filter, Node parent, CardinalDirection dir) {
		if (!searchList.containsKey(offsetIndex(parent.index, dir))) {
			Vec2f pos = dir.offset(new Vec2f(parent.pos), stepSize);
			if (filter.isValidPos(pos, parent.step + 1)) {
				Node node = new Node(parent, pos, dir);
				openList.insert(node.movecost + getDistance(pos), node);
				searchList.insert(node.index, node);
			}
		}
	}
	
	public List<Node> findPath(Vec2f start, PositionFilter filter) {
		openList.clear();
		searchList.clear();

		Node current = new Node(null, start, null);
		
		searchList.insert(current.index, current);
		
		while(true) {
			if (isGoalNode(current))
				break;
			
			visitNextNode(filter, current, CardinalDirection.NORTH);
			visitNextNode(filter, current, CardinalDirection.EAST);
			visitNextNode(filter, current, CardinalDirection.SOUTH);
			visitNextNode(filter, current, CardinalDirection.WEST);
			
			if (allowDiagonals) {
				visitNextNode(filter, current, CardinalDirection.NORTH_EAST);
				visitNextNode(filter, current, CardinalDirection.SOUTH_EAST);
				visitNextNode(filter, current, CardinalDirection.SOUTH_WEST);
				visitNextNode(filter, current, CardinalDirection.NORTH_WEST);
			}
			
			BinaryTree.Entry<Float, Node> min = openList.getMin();
			if (min == null)
				return null;
			
			current = openList.removeFirst(min).value;
		}
		
		List<Node> path = new ArrayList<Node>(current.step);
		path.add(current);
		while(current.parent != null)
			path.add(current = current.parent);
		Collections.reverse(path);
		
		return path;
	}
	
	private boolean isGoalNode(Node current) {
		Vec2f dist = tmp.set(goal).sub(current.pos);
		if (dist.x > stepSize / 2.0f || dist.x < -stepSize / 2.0f) return false;
		if (dist.y > stepSize / 2.0f || dist.y < -stepSize / 2.0f) return false;
		return true;
	}

	public interface PositionFilter {
		
		public boolean isValidPos(Vec2f pos, int step);
		
	}
	
	public class Node {

		private final Node parent;
		public final Vec2f pos;
		public final CardinalDirection dir;
		
		private final long index;
		
		private final float movecost;
		private final int step;
		
		public Node(Node parent, Vec2f pos, CardinalDirection dir) {
			this.parent = parent;
			this.pos = pos;
			this.dir = dir;
			
			if (parent != null) {
				index = offsetIndex(parent.index, dir);
				movecost = parent.movecost + (dir.isDiagonal() ? MOVE_COST_DIAG : MOVE_COST);
				step = parent.step + 1;
			} else {
				index = 0;
				movecost = 0.0f;
				step = 0;
			}
		}
	}
}
