package snorri.pathfinding;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.PriorityQueue;

import snorri.main.Debug;
import snorri.world.Vector;
import snorri.world.World;

public class Pathfinding {
	
	//save array of costs from start
	//everything is null, or etc.
		
	private static World world;
	private static Vector dim;
	
	public static void setWorld(World world) {
		Pathfinding.world = world;
		dim = world.getLevel().getDimensions();
	}
	
	public static void setPathAsync(Vector start, Vector goal, Pathfinder p) {	
		new Thread(new Runnable() {
			@Override
			public void run() {
				ArrayDeque<PathNode> stack = findPath(start, goal);
				p.setPath(stack);
			}
		}).start();
	}
	
	//TODO: can still tweak a bit to introduce more optimal routes, random variation, diagonal movement, etc.
	public static ArrayDeque<PathNode> findPath(Vector start, Vector goal) {
		
		if (Debug.DISABLE_PATHFINDING) {
			return null;
		}
		
		//do this to avoid lots of unnecessary computation
		if (!world.getLevel().arePathConnected(start, goal)) {
			return null;
		}
		
		PathNode[][] map = new PathNode[dim.getX()][dim.getY()];
		PriorityQueue<PathNode> openSet = new PriorityQueue<PathNode>();
		ArrayList<PathNode> closedSet = new ArrayList<PathNode>();
				
		map[start.getX()][start.getY()] = new PathNode(start, 0, goal);
		openSet.offer(map[start.getX()][start.getY()]);
		
		PathNode current;
		while (! openSet.isEmpty()) {
			
			current = openSet.poll();
						
			if (current.getGridPos().equals(goal)) {
				return reconstructPath(current);
			}
			
			closedSet.add(current);
			
			//getNeighbors has the side effect of creating PathNodes which are null
			for (PathNode neighbor : current.getNeighbors(map, world.getLevel())) {
				
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
	
	public static ArrayDeque<PathNode> reconstructPath(PathNode current) {
		
		ArrayDeque<PathNode> stack = new ArrayDeque<PathNode>();
		while (current != null) {
			stack.push(current);
			current = current.getOrigin();
		}
		return stack;
		
	}
	
}
