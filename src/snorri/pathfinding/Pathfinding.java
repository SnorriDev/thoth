package snorri.pathfinding;

import java.util.ArrayDeque;
import java.util.List;

import snorri.entities.Entity;
import snorri.main.Debug;
import snorri.world.Level;
import snorri.world.Tile;
import snorri.world.Vector;

public class Pathfinding {
	
	private static final int MAX_WIDTH = 3, MAX_HEIGHT = 3;
	/** An array of bounding-box sizes for which pathfinding grids should be calculated */
	private static final Vector[] SIZES = new Vector[] {
		new Vector(1, 1), //cobra
		new Vector(2, 1), //crocodile
	};
	
	private PathGraph[][] graphs = new PathGraph[MAX_WIDTH][MAX_HEIGHT];
	
	public Pathfinding(List<Level> pathfindingLevels) {
		for (Vector size : SIZES) {
			graphs[size.getX()][size.getY()] = new PathGraph(pathfindingLevels, size.getX(), size.getY());
		}
	}
	
	public PathGraph getGraph(int x, int y) {
		return graphs[x][y];
	}
	
	public PathGraph getGraph(Vector v) {
		return getGraph(v.getX(), v.getY());
	}
	
	public PathGraph getGraph(Pathfinder p) {
		if (getGraph(p.getGridBounds()) == null) {
			Debug.warning("missing pathfinding graph for " + p);
			return getDefaultGraph();
		}
		return getGraph(p.getGridBounds());
	}
	
	public PathGraph getDefaultGraph() {
		return getGraph(1, 1);
	}
	
	public void setPathAsync(Vector start, Vector goal, Pathfinder p) {	
		new Thread(new Runnable() {
			@Override
			public void run() {
				Vector bounds = p.getGridBounds();
				p.setPath(graphs[bounds.getX()][bounds.getY()].findPath(start, goal));
			}
		}).start();
	}
	
	public static ArrayDeque<PathNode> reconstructPath(PathNode current) {
		
		ArrayDeque<PathNode> stack = new ArrayDeque<PathNode>();
		while (current != null) {
			stack.push(current);
			current = current.getOrigin();
		}
		return stack;
		
	}

	//TODO store pathability/occupied information in one graph, and contextPathability in another
	
	public boolean isOccupied(Vector posGrid) {
		return getDefaultGraph().isOccupied(posGrid);
	}
	
	public boolean isPathable(int x, int y) {
		return getDefaultGraph().isPathable(x, y);
	}

	public void wrapPathingUpdate(Vector posGrid, Tile oldTile, Tile tile) {
		for (Vector size : SIZES) {
			getGraph(size).wrapPathingUpdate(posGrid, oldTile, tile);
		}
	}

	public void addEntity(Entity e) {
		for (Vector size : SIZES) {
			getGraph(size).addEntity(e);
		}
	}

	public void compute() {
		for (Vector size : SIZES) {
			getGraph(size).computePathfinding();
		}
	}
	
}
