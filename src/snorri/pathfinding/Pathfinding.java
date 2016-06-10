package snorri.pathfinding;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.PriorityQueue;

import snorri.world.Level;
import snorri.world.Vector;

public class Pathfinding {
	
	//save array of costs from start
	//everything is null, or etc.
		
	private Level level;
	private Vector dim;
	
	//reset with every call to findPath
	private PathNode[][] map;
	private PriorityQueue<PathNode> openSet;
	private ArrayList<PathNode> closedSet;
	
	public Pathfinding(Level level) {
		this.level = level;
		dim = level.getDimensions();
	}
	
	public void setPathAsync(Vector start, Vector goal, Pathfinder p) {	
		new Thread(new Runnable() {
			@Override
			public void run() {
				//TODO: still test somehow for reachability?
				p.setPath(findPath(start, goal));
			}
		}).start();
	}
	
	//TODO: optimize this using the map double array instead of a random ass set
	//TODO: can still tweak a bit to introduce more optimal routes, random variation, diagonal movement, etc.
	public Deque<PathNode> findPath(Vector start, Vector goal) {
		
		map = new PathNode[dim.getX()][dim.getY()];
		openSet = new PriorityQueue<PathNode>();
		closedSet = new ArrayList<PathNode>();

		map[start.getX()][start.getY()] = new PathNode(start, 0, goal);
		openSet.offer(map[start.getX()][start.getY()]);
		
		while (! openSet.isEmpty()) {
						
			PathNode current = openSet.poll();
			
			if (current.getGridPos().equals(goal)) {
				return reconstructPath(current);
			}
			
			closedSet.add(current);
			
			//getNeighbors has the side effect of creating PathNodes which are null
			for (PathNode neighbor : current.getNeighbors(map, level)) {
								
				if (closedSet.contains(neighbor)) {
					continue;
				}
				
				double tentativeG = current.getG() + current.distance(neighbor);
				if (! openSet.contains(neighbor)) {
					openSet.offer(neighbor);
				}
				else if (tentativeG >= neighbor.getG()) {
					continue;
				}
				
				neighbor.setOrigin(current);
				neighbor.updateHeuristics(tentativeG, goal);
				
			}
			
		}
		
		return null;
		
	}
	
	public Deque<PathNode> reconstructPath(PathNode current) {
				
		Deque<PathNode> stack = new ArrayDeque<PathNode>();
		while (current != null) {
			stack.push(current);
			current = current.getOrigin();
		}
		return stack;
	}
	
	public PathNode getNode(Vector pos) {
		return map[pos.getX()][pos.getY()];
	}
	
}
