package snorri.pathfinding;

import java.util.ArrayDeque;

import snorri.world.Vector;

public class Pathfinding {
			
	private static PathGraph graph;
	
	public static void setGraph(PathGraph graph) {
		Pathfinding.graph = graph;
	}
	
	public static void setPathAsync(Vector start, Vector goal, Pathfinder p) {	
		new Thread(new Runnable() {
			@Override
			public void run() {
				p.setPath(graph.findPath(start, goal));
			}
		}).start();
	}
	
	public static PathGraph getGraph() {
		return graph;
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
