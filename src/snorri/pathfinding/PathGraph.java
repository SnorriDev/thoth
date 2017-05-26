package snorri.pathfinding;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;

import snorri.entities.Entity;
import snorri.main.Debug;
import snorri.main.Main;
import snorri.collisions.Collider;
import snorri.world.Level;
import snorri.world.Tile;
import snorri.world.Vector;

public class PathGraph {

	private boolean[][] contextPathable;
	private Set<Entity>[][] entities;
	private final List<Level> levels;

	/** A list of the graph's components */
	private Set<Component> components;
	/** Indices component graphs by coordinate */
	private Component[][] componentLookup;
	
	private final int width, height;

	@SuppressWarnings("unchecked")
	public PathGraph(List<Level> levels, int width, int height) {

		this.width = width;
		this.height = height;

		int mapWidth = levels.get(0).getWidth();
		int mapHeight = levels.get(0).getHeight();
		
		contextPathable = new boolean[mapWidth][mapHeight];
		this.levels = levels;
		entities = (Set<Entity>[][]) new HashSet[mapWidth][mapHeight];

		//initialize the entities arrays at each index
		for (int x = 0; x < mapWidth; x++) {
			for (int y = 0; y < mapHeight; y++) {
				entities[x][y] = new HashSet<>();
			}
		}
		
		computePathfinding();

	}
	
	public PathGraph(List<Level> levels, Vector v) {
		this(levels, v.getX(), v.getY());
	}
	
	
	//TODO: rework this method to be inside PathGraph, and store a list of PathGraphs in
	// the Pathfinding class
	public ArrayDeque<PathNode> findPath(Vector start, Vector goal) {
		
		if (Debug.DISABLE_PATHFINDING) {
			return null;
		}
		
		//do this to avoid lots of unnecessary computation
		if (!arePathConnected(start, goal)) {
			return null;
		}
		
		PathNode[][] map = new PathNode[getWidth()][getHeight()];
		PriorityQueue<PathNode> openSet = new PriorityQueue<PathNode>();
		ArrayList<PathNode> closedSet = new ArrayList<PathNode>();
				
		map[start.getX()][start.getY()] = new PathNode(start, 0, goal);
		openSet.offer(map[start.getX()][start.getY()]);
		
		PathNode current;
		while (! openSet.isEmpty()) {
			
			current = openSet.poll();
						
			if (current.getGridPos().equals(goal)) {
				return Pathfinding.reconstructPath(current);
			}
			
			closedSet.add(current);
			
			//getNeighbors has the side effect of creating PathNodes which are null
			for (PathNode neighbor : current.getNeighbors(map, this)) {
				
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

	public int getWidth() {
		return contextPathable.length;
	}

	public int getHeight() {
		return contextPathable[0].length;
	}

	public Component getComponent(Entity e) {
		return getComponent(e.getPos().copy().toGridPos());
	}

	public Component getComponent(Vector pos) {
		return getComponent(pos.getX(), pos.getY());
	}

	public Component getComponent(int x, int y) {
		
		if (componentLookup == null) {
			return null;
		}
		
		if (x < 0 || componentLookup.length <= x || y < 0 || componentLookup[0].length <= y) {
			return null;
		}
		return componentLookup[x][y];
		
	}

	public boolean setComponent(int x, int y, Component graph) {
		if (componentLookup == null) {
			return false;
		}
		componentLookup[x][y] = graph;
		return true;
	}
	
	public boolean setComponent(Vector pos, Component graph) {
		return setComponent(pos.getX(), pos.getY(), graph);
	}

	private boolean isInMap(int x, int y) {
		return x >= 0 && getWidth() > x && y >= 0 && getHeight() > y;
	}
	
	public boolean isPathable(int x, int y) {
		
		if (!isInMap(x, y) || isOccupied(x, y)) {
			return false;
		}
		
		for (Level l : levels) {
			if (!l.isPathable(x, y)) {
				return false;
			}
		}		
		return true;
		
	}
	
	public boolean isPathable(Vector v) {
		return isPathable(v.getX(), v.getY());
	}

	public boolean isOccupied(Vector v) {
		return isOccupied(v.getX(), v.getY());
	}
	
	public boolean isOccupied(int x, int y) {
		if (!isInMap(x, y)) {
			return false;
		}
		return !entities[x][y].isEmpty();
	}
	
	public boolean isContextPathable(int x, int y) {
		if (!isInMap(x, y)) {
			return false;
		}
		return contextPathable[x][y];
	}

	public boolean isContextPathable(Vector v) {
		return isContextPathable(v.getX(), v.getY());
	}

	public boolean arePathConnected(Vector p1, Vector p2) {
		if (!isContextPathable(p1) || !isContextPathable(p2)) {
			return false;
		}
		return getComponent(p1) == getComponent(p2);
	}
	
	/**
	 * computes whether tiles in the map are context-pathable
	 * this is FAR less computationally intensive than computing all sub-graphs
	 */
	public void computePathability() {
		
		//TODO store components as hash-sets, not lists (makes everything better)
		
		for (int i = width; i < getWidth() - width; i++) {
			for (int j = height; j < getHeight() - height; j++) {
				contextPathable[i][j] = true;
			}
		}
		
		for (int i = 0; i < getWidth(); i++) {
			for (int j = 0; j < getHeight(); j++) {
				if (!isPathable(i, j)) {
					updateSurroundingContext(i, j);
				}
			}
		}
		
	}
	
	private void computeComponents() {

		components = new HashSet<>();
		boolean[][] visited = new boolean[getWidth()][getHeight()];
		
		for (int x = 0; x < getWidth(); x++) {
			for (int y = 0; y < getHeight(); y++) {

				//TODO problem is here; nothing is context pathable for some reason???
				
				if (!isContextPathable(x, y) || visited[x][y]) {
					continue;
				}

				components.add(computeComponent(new Vector(x, y), visited));

			}
		}

		Main.log("found " + components.size() + " pathfinding components");

		computeGraphHash();

	}

	@Deprecated
	private void computeGraphHash() {
		componentLookup = new Component[getWidth()][getHeight()];
		for (Component graph : components) {
			for (Vector v : graph) {
				setComponent(v, graph);
			}
		}
	}

	@SuppressWarnings("unchecked")
	public void loadComponents(File f) throws FileNotFoundException, IOException {

		if (!f.exists()) {
			Main.log("graph data not found in world; computing it from scratch");
			computePathfinding();
			return;
		}

		ObjectInputStream in = new ObjectInputStream(new FileInputStream(f));
		try {
			components = (Set<Component>) in.readObject();
			computeGraphHash();
		} catch (ClassNotFoundException e) {
			Main.error("recalculating corrupted pathfinding data");
			computeComponents();
		}
		in.close();
	}

	public void saveComponents(File f) throws FileNotFoundException, IOException {

		computePathfinding();

		ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(f));
		out.writeObject(components);
		out.close();
	}

	/**
	 * @param start
	 *            the tile around which to compute a sub-graph
	 * @param visited
	 *            a 2D boolean array which will be modified to reflect the tiles
	 *            which have been visited
	 * @return the sub-graph as an ArrayList
	 */
	private Component computeComponent(Vector start, boolean[][] visited) {

		Component graph = new Component();
		Queue<Vector> searchQ = new LinkedList<Vector>();
		searchQ.add(start);
		Vector pos;

		while (!searchQ.isEmpty()) {

			pos = searchQ.poll();
			
			if (!isContextPathable(pos) || visited[pos.getX()][pos.getY()]) {
				continue;
			}

			visited[pos.getX()][pos.getY()] = true;
			graph.add(pos);

			for (Vector v : PathNode.getNeighbors(pos)) {
				searchQ.add(v);
			}

		}

		return graph;

	}

	public void computePathfinding() {
		computePathability();
		computeComponents();
	}

	/**
	 * update graphs when we insert a pathable tile
	 * 
	 * @param v
	 *            position, in grid coordinates
	 */
	public void setPathableGrid(Vector v) {
		
		updateSurroundingContext(v);
		
		for (int x = v.getX() - width; x <= v.getX() + width; x++) {
			for (int y = v.getY() - height; y <= v.getY() + height; y++) {
								
				Vector pos = new Vector(x, y);
				if (isContextPathable(pos) && getComponent(pos) == null) {
					Set<Component> graphs = new HashSet<>();
					for (Vector p : PathNode.getNeighbors(pos)) {
						if (getComponent(p) != null && !graphs.contains(getComponent(p))) {
							graphs.add(getComponent(p));
						}
					}
					Component newGraph = mergeComponents(graphs);
					newGraph.add(pos);
					setComponent(pos, newGraph);
				}
				
			}
									
		}
	
	}

	/**
	 * Update graphs when we insert an unpathable tile
	 * 
	 * @see <code>setPathableGrid</code>
	 */
	public void setUnpathableGrid(Vector v) {
		for (Component component : updateSurroundingContext(v)) {
			splitGraph(component);
		}
	}
	
	/**
	 * Searches through nearby nodes to determine whether the position is context pathable.
	 * @param x
	 * 	x coordinate
	 * @param y
	 * 	y coordinate
	 * @return
	 * 	Whether (x, y) is context pathable
	 */
	private boolean searchContextPathable(int x, int y) {
		
		for (int x1 = x - width; x1 <= x + width; x1++) {
			for (int y1 = y - height; y1 <= y + height; y1++) {					
				if (!isPathable(x1, y1)) {
					return false;
				}		
			}
		}
		
		return true;

	}

	/**
	 * Updates the context pathability of surrounding tiles if this tile is unpathable
	 * 
	 * @param v position around which to update, in grid coordinates
	 * @return The set of modified components
	 */
	public Set<Component> updateSurroundingContext(int xc, int yc) {
		
		Set<Component> components = new HashSet<>();
		
		for (int x = xc - width; x <= xc + width; x++) {
			for (int y = yc - height; y <= yc + height; y++) {
				if (isInMap(x, y)) {
										
					if (!isPathable(xc, yc)) { //in this case we know everything around will be inpathable
						contextPathable[x][y] = false;
						if (getComponent(x, y) != null) {
							components.add(getComponent(x, y));
						}
						setComponent(x, y, null);
						continue;
					}
					
					contextPathable[x][y] = searchContextPathable(x, y);
					
				}
			}
		}
		
		return components;
		
	}
	
	public Set<Component> updateSurroundingContext(Vector v) {
		return updateSurroundingContext(v.getX(), v.getY());
	}

	/**
	 * Merges disjoint graphs into one big graph. Ignores duplicate graphs and
	 * graphs which are not in components.
	 * For performance reasons, merges smaller graphs into the largest one.
	 * @return the merged graph
	 */
	private Component mergeComponents(Set<Component> graphs) {

		Component largest = new Component();
		for (Component graph : graphs) {
			if (graph.size() > largest.size()) {
				largest = graph;
			}
		}
						
		for (Component graph : graphs) {
			if (!graph.equals(largest)) {
				components.remove(graph);
				largest.addAll(graph);
				for (Vector v : graph) {
					setComponent(v, largest);
				}
			}
		}

		return largest;

	}

	private void splitGraph(Component graph) {

		Set<Component> graphs = new HashSet<>();
		boolean[][] visited = new boolean[getWidth()][getHeight()];

		for (Vector pos : graph) {
			if (isContextPathable(pos) && !visited[pos.getX()][pos.getY()]) {
				graphs.add(computeComponent(pos, visited));
			}
		}

		if (graphs.size() == 1) {
			return;
		}

		components.remove(graph);
		for (Component g : graphs) {
			components.add(g);
			for (Vector v : g) {
				setComponent(v, g);
			}
		}

	}
	
	public void wrapPathingUpdate(Vector pos, Tile oldTile, Tile newTile) {

		if (oldTile.isPathable() == newTile.isPathable()) {
			return;
		}

		if (newTile.isPathable()) {
			setPathableGrid(pos);
		} else {
			setUnpathableGrid(pos);
		}

	}
	
	//untested
	public void addEntity(Entity e) {

		int x = e.getPos().getX();
		int y = e.getPos().getY();
		Collider c = e.getCollider();

		// mark all tiles which are occupied
		for (int x1 = (x - c.getRadiusX()) / Tile.WIDTH; x1 <= (x + c.getRadiusX()) / Tile.WIDTH; x1++) {
			for (int y1 = (y - c.getRadiusY()) / Tile.WIDTH; y1 <= (y + c.getRadiusY()) / Tile.WIDTH; y1++) {

				if (isInMap(x1, y1) && c.intersects(Tile.getRectangle(x1, y1))) {
					entities[x1][y1].add(e);
					updateSurroundingContext(x1, y1); //TODO can make this more efficient
				}

			}
		}

	}

	//untested
	public void removeEntity(Entity e) {

		int x = e.getPos().getX();
		int y = e.getPos().getY();
		Collider c = e.getCollider();

		// unmark all tiles which are occupied
		for (int x1 = (x - c.getRadiusX()) / Tile.WIDTH; x1 <= (x + c.getRadiusX()) / Tile.WIDTH + 1; x1++) {
			for (int y1 = (y - c.getRadiusY()) / Tile.WIDTH; y1 <= (y + c.getRadiusY()) / Tile.WIDTH + 1; y1++) {

				if (isInMap(x1, y1) && c.intersects(Tile.getRectangle(x1, y1))) {
					entities[x1][y1].remove(e);
					updateSurroundingContext(x1, y1);
				}

			}
		}

	}

}
