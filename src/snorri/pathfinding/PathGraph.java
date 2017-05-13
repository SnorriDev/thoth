package snorri.pathfinding;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import snorri.entities.Entity;
import snorri.entities.Unit;
import snorri.main.Main;
import snorri.collisions.Collider;
import snorri.world.Level;
import snorri.world.Tile;
import snorri.world.Vector;

public class PathGraph {

	private boolean[][] contextPathable;
	private List<Entity>[][] entities;
	private final List<Level> levels;

	/** A list of the graph's components */
	private List<List<Vector>> components;
	/** Indexes component graphs by coordinate */
	private List<Vector>[][] componentLookup;

	@SuppressWarnings("unchecked")
	public PathGraph(int width, int height, List<Level> levels) {

		contextPathable = new boolean[width][height];
		this.levels = levels;
		entities = new ArrayList[width][height];

		//initialize the entities arrays at each index
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				entities[x][y] = new ArrayList<>();
			}
		}
		
		computePathfinding();

	}

	public PathGraph(Vector v, List<Level> levels) {
		this(v.getX(), v.getY(), levels);
	}

	public int getWidth() {
		return contextPathable.length;
	}

	public int getHeight() {
		return contextPathable[0].length;
	}

	public List<Vector> getComponent(Entity e) {
		return getComponent(e.getPos().copy().toGridPos());
	}

	public List<Vector> getComponent(Vector pos) {
		return getComponent(pos.getX(), pos.getY());
	}

	public List<Vector> getComponent(int x, int y) {
		if (x < 0 || componentLookup.length <= x || y < 0 || componentLookup[0].length <= y) {
			return null;
		}
		return componentLookup[x][y];
	}

	public void setGraph(int x, int y, List<Vector> graph) {
		componentLookup[x][y] = graph;
	}

	public void setGraph(Vector pos, List<Vector> graph) {
		setGraph(pos.getX(), pos.getY(), graph);
	}

	private boolean isInMap(int x, int y) {
		return x >= 0 && getWidth() > x && y >= 0 && getHeight() > y;
	}
	
	public boolean isPathable(int x, int y) {
		
		if (isOccupied(x, y)) {
			return false;
		}
		
		for (Level l : levels) {
			if (!l.isPathable(x, y)) {
				return false;
			}
		}		
		return true;
		
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
		for (int i = 0; i < getWidth(); i++) {
			for (int j = 0; j < getHeight(); j++) {
				computeContextPathable(i, j);
			}
		}
	}
	
	private void computeComponents() {

		components = new ArrayList<List<Vector>>();
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

	@SuppressWarnings("unchecked")
	private void computeGraphHash() {
		componentLookup = (List<Vector>[][]) new ArrayList[getWidth()][getHeight()];
		for (List<Vector> graph : components) {
			for (Vector v : graph) {
				setGraph(v, graph);
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
			components = (List<List<Vector>>) in.readObject();
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
	private ArrayList<Vector> computeComponent(Vector start, boolean[][] visited) {

		ArrayList<Vector> graph = new ArrayList<Vector>();
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

		// update graphs to reflect the changes
		for (int x = (v.getX() * Tile.WIDTH - Unit.RADIUS_X) / Tile.WIDTH
				- 2; x <= (v.getX() * Tile.WIDTH + Unit.RADIUS_X) / Tile.WIDTH + 2; x++) {
			for (int y = (v.getY() * Tile.WIDTH - Unit.RADIUS_Y) / Tile.WIDTH
					- 2; y <= (v.getY() * Tile.WIDTH + Unit.RADIUS_Y) / Tile.WIDTH + 2; y++) {

				Vector pos = new Vector(x, y);
				if (isContextPathable(pos) && getComponent(pos) == null) {
					List<List<Vector>> graphs = new ArrayList<>();
					for (Vector p : PathNode.getNeighbors(pos)) {
						if (getComponent(p) != null && !graphs.contains(getComponent(p))) { // merge
																					// here
																					// if
																					// graph
																					// exists
							graphs.add(getComponent(p));
						}
					}
					List<Vector> newGraph = mergeGraphs(graphs);
					newGraph.add(pos);
					setGraph(pos, newGraph);
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

		List<List<Vector>> graphs = new ArrayList<>();

		// update graphs to reflect the changes
		for (int x = (v.getX() * Tile.WIDTH - Unit.RADIUS_X) / Tile.WIDTH
				- 2; x <= (v.getX() * Tile.WIDTH + Unit.RADIUS_X) / Tile.WIDTH + 2; x++) {
			for (int y = (v.getY() * Tile.WIDTH - Unit.RADIUS_Y) / Tile.WIDTH
					- 2; y <= (v.getY() * Tile.WIDTH + Unit.RADIUS_Y) / Tile.WIDTH + 2; y++) {

				Vector pos = new Vector(x, y);
				if (getComponent(pos) == null) {
					continue;
				}

				if (!isContextPathable(pos)) {
					List<Vector> graph = getComponent(pos);
					graph.remove(pos);
					setGraph(pos, null);
					computeContextPathable(pos.getX(), pos.getY());
				} else if (!graphs.contains(getComponent(pos))) {
					graphs.add(getComponent(pos));
				}
			}
		}

		for (List<Vector> graph : graphs) {
			splitGraph(graph);
		}

	}

	/**
	 * Update the context pathability of surrounding tiles.
	 * 
	 * @param v position around which to update, in grid coordinates
	 */
	public void updateSurroundingContext(Vector v) {
		for (int x = (v.getX() * Tile.WIDTH - Unit.RADIUS_X) / Tile.WIDTH
				- 1; x <= (v.getX() * Tile.WIDTH + Unit.RADIUS_X) / Tile.WIDTH + 1; x++) {
			for (int y = (v.getY() * Tile.WIDTH - Unit.RADIUS_Y) / Tile.WIDTH
					- 1; y <= (v.getY() * Tile.WIDTH + Unit.RADIUS_Y) / Tile.WIDTH + 1; y++) {
				
				computeContextPathable(x, y);
			}
		}
	}

	private void computeContextPathable(int x, int y) {
		if (!isInMap(x, y)) {
			return;
		}
		contextPathable[x][y] = searchContextPathable(x, y);
	}
	
	private boolean searchContextPathable(int x, int y) {

		for (int i = (x * Tile.WIDTH - Unit.RADIUS_X) / Tile.WIDTH; i <= (x * Tile.WIDTH + Unit.RADIUS_X)
				/ Tile.WIDTH; i++) {
			for (int j = (y * Tile.WIDTH - Unit.RADIUS_Y) / Tile.WIDTH; j <= (y * Tile.WIDTH + Unit.RADIUS_Y)
					/ Tile.WIDTH; j++) {

				if (!isPathable(x, y)) {
					return false;
				}

			}
		}

		return true;

	}

	/**
	 * Merges disjoint graphs into one big graph. Ignores duplicate graphs and
	 * graphs which are not in components. Adds result to components, even if it empty
	 * 
	 * @return the merged graph
	 */
	private List<Vector> mergeGraphs(List<List<Vector>> graphs) {

		// TODO: can make this more efficient by merging all other components
		// into the largest one

		List<Vector> union = new ArrayList<>();
		for (List<Vector> graph : graphs) {
			if (components.remove(graph)) {
				union.addAll(graph);
			}
		}
		if (!union.isEmpty()) {
			for (Vector v : union) {
				setGraph(v, union);
			}
		}
		components.add(union);
		return union;

	}

	private void splitGraph(List<Vector> graph) {

		List<List<Vector>> graphs = new ArrayList<>();
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
		for (List<Vector> g : graphs) {
			components.add(g);
			for (Vector v : g) {
				setGraph(v, g);
			}
		}

	}
	
	public void wrapPathingUpdate(Vector pos, Tile oldTile, Tile newTile) {

		updateSurroundingContext(pos); // update context pathability on nearby
										// tiles

		if (oldTile.isPathable() == newTile.isPathable()) {
			return;
		}

		if (newTile.isPathable()) {
			setPathableGrid(pos);
		} else {
			setUnpathableGrid(pos);
		}

	}
	
	public void addEntity(Entity e) {

		int x = e.getPos().getX();
		int y = e.getPos().getY();
		Collider c = e.getCollider();

		// mark all tiles which are occupied
		for (int x1 = (x - c.getRadiusX()) / Tile.WIDTH; x1 <= (x + c.getRadiusX()) / Tile.WIDTH; x1++) {
			for (int y1 = (y - c.getRadiusY()) / Tile.WIDTH; y1 <= (y + c.getRadiusY()) / Tile.WIDTH; y1++) {

				if (isInMap(x1, y1) && c.intersects(Tile.getRectangle(x1, y1))) {
					entities[x1][y1].add(e);
				}

			}
		}

		Queue<Vector> unpathableQ = new LinkedList<>();

		// update all tiles in range of occupied tiles
		for (int x1 = (x - c.getRadiusX() - 2 * Unit.RADIUS_X)
				/ Tile.WIDTH; x1 <= (x + c.getRadiusX() + 2 * Unit.RADIUS_X) / Tile.WIDTH; x1++) {
			for (int y1 = (y - c.getRadiusY() - 2 * Unit.RADIUS_Y)
					/ Tile.WIDTH; y1 <= (y + c.getRadiusY() + 2 * Unit.RADIUS_Y) / Tile.WIDTH; y1++) {

				boolean wasContextPathable = isContextPathable(x1, y1);
				computeContextPathable(x1, y1);
				if (wasContextPathable && !isContextPathable(x1, y1)) {
					unpathableQ.add(new Vector(x1, y1));
				}

			}
		}

		// recalculate graphs around unpathable tiles
		while (!unpathableQ.isEmpty()) {
			this.setUnpathableGrid(unpathableQ.poll());
		}

	}

	public void removeEntity(Entity e) {

		int x = e.getPos().getX();
		int y = e.getPos().getY();
		Collider c = e.getCollider();

		// unmark all tiles which are occupied
		for (int x1 = (x - c.getRadiusX()) / Tile.WIDTH; x1 <= (x + c.getRadiusX()) / Tile.WIDTH + 1; x1++) {
			for (int y1 = (y - c.getRadiusY()) / Tile.WIDTH; y1 <= (y + c.getRadiusY()) / Tile.WIDTH + 1; y1++) {

				if (isInMap(x1, y1) && c.intersects(Tile.getRectangle(x1, y1))) {
					entities[x1][y1].remove(e);
				}

			}
		}

		Queue<Vector> unpathableQ = new LinkedList<>();

		// update all tiles in range of occupied tiles
		for (int x1 = (x - c.getRadiusX() - 2 * Unit.RADIUS_X)
				/ Tile.WIDTH; x1 <= (x + c.getRadiusX() + 2 * Unit.RADIUS_X) / Tile.WIDTH; x1++) {
			for (int y1 = (y - c.getRadiusY() - 2 * Unit.RADIUS_Y)
					/ Tile.WIDTH; y1 <= (y + c.getRadiusY() + 2 * Unit.RADIUS_Y) / Tile.WIDTH; y1++) {

				if (!isInMap(x1, y1)) {
					continue;
				}

				boolean wasContextPathable = isContextPathable(x1, y1);
				computeContextPathable(x1, y1);
				if (!wasContextPathable && isContextPathable(x1, y1)) {
					unpathableQ.add(new Vector(x1, y1));
				}

			}
		}

		// recalculate graphs around unpathable tiles
		while (!unpathableQ.isEmpty()) {
			this.setPathableGrid(unpathableQ.poll());
		}

	}

}
