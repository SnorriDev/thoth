package snorri.world;

import java.awt.FileDialog;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import snorri.entities.Entity;
import snorri.entities.Unit;
import snorri.main.FocusedWindow;
import snorri.main.Main;
import snorri.pathfinding.PathNode;
import snorri.terrain.DungeonGen;
import snorri.world.Tile.TileType;

public class Level implements Editable {

	public static final int MAX_SIZE = 1024;
	
	private Tile[][] map;
	private Vector dim;
	
	private ArrayList<ArrayList<Vector>> connectedSubGraphs;
	private HashMap<Vector, List<Vector>> graphHash;

	// not that indexing conventions are Cartesian, not matrix-based

	public Level(int width, int height, TileType bg) {
		map = new Tile[width][height];
		dim = new Vector(width, height);

		for (int i = 0; i < dim.getX(); i++) {
			for (int j = 0; j < dim.getY(); j++) {
				map[i][j] = new Tile(bg);
			}
		}
		
	}
	
	public Level(Vector v, TileType bg) {
		this(v.getX(), v.getY(), bg);
	}
	
	public Level(int width, int height) {
		this(width, height, TileType.SAND);
	}
	
	public Level(Vector v) {
		this(v, TileType.SAND);
	}

	public Level(File file) throws FileNotFoundException, IOException {
		load(file);
	}
	
	/**
	 * Constructor used for resizing
	 */
	private Level(Level l, int newWidth, int newHeight) {
		
		map = new Tile[newWidth][newHeight];
		dim = new Vector(newWidth, newHeight);
		
		for (int i = 0; i < dim.getX(); i++) {
			for (int j = 0; j < dim.getY(); j++) {
				map[i][j] = new Tile(TileType.SAND);
			}
		}
		for (int i = 0; i < dim.getX() && i < l.dim.getX(); i++) {
			for (int j = 0; j < dim.getY() && j < l.dim.getY(); j++) {
				map[i][j] = l.map[i][j];
			}
		}
		
	}
	
	public Level getTransposed() {
		Level t = new Level(dim.getInverted());
		for (int x = 0; x < dim.getX(); x++) {
			for (int y = 0; y < dim.getY(); y++) {
				t.setTileGrid(y, x, getNewTileGrid(x, y));
			}
		}
		return t;
	}
	
	/**
	 * Flip the level on the x axis.
	 * Using this method and <code>getTransposed()</code>, one can produce levels
	 * with a door facing out from all four sides if a door exists.
	 */
	public Level getXReflected() {
		Level f = new Level(dim);
		for (int x = 0; x < dim.getX(); x++) {
			for (int y = 0; y < dim.getY(); y++) {
				f.setTileGrid(dim.getX() - 1 - x, y, getNewTileGrid(x, y));
			}
		}
		return f;
	}
	
	@Override
	public void resize(int newWidth, int newHeight) {
		
		Tile[][] newMap = new Tile[newWidth][newHeight];
		Vector newDim = new Vector(newWidth, newHeight);
		
		for (int i = 0; i < newDim.getX(); i++) {
			for (int j = 0; j < newDim.getY(); j++) {
				newMap[i][j] = new Tile(TileType.SAND);
			}
		}
		for (int i = 0; i < newDim.getX() && i < dim.getX(); i++) {
			for (int j = 0; j < newDim.getY() && j < dim.getY(); j++) {
				newMap[i][j] = map[i][j];
			}
		}
		
		map = newMap;
		dim = newDim;
		
	}
	
	public Level getResized(int newWidth, int newHeight) {
		return new Level(this, newWidth, newHeight);
	}

	public void setTile(int x, int y, Tile t) {
		setTileGrid(x / Tile.WIDTH,y / Tile.WIDTH, t);
	}

	public void setTileGrid(int x, int y, Tile t) {
		if (x < 0 || x >= map.length || y < 0 || y >= map[x].length) {
			return;
		}
		map[x][y] = t;
	}

	public Tile getTile(int x, int y) {
		return getTileGrid(x / Tile.WIDTH, y / Tile.WIDTH);
	}

	public Tile getTile(Vector v) {
		return getTile(v.getX(), v.getY());
	}
	
	public Tile getNewTileGrid(int x, int y) {
		if (x < 0 || x >= map.length || y < 0 || y >= map[x].length) {
			return null;
		}
		if (getTileGrid(x,y) == null) {
			return null;
		}
		return new Tile(getTileGrid(x,y));
	}

	public Tile getTileGrid(int x, int y) {
		if (x < 0 || x >= map.length || y < 0 || y >= map[x].length) {
			return null;
		}
		return map[x][y];
	}
	
	public Tile getTileGrid(Vector v) {
		return getTileGrid(v.getX(), v.getY());
	}

	public Vector getDimensions() {
		return dim;
	}
	
	@Override
	public void render(FocusedWindow g, Graphics gr, boolean renderOutside) {
				
		int cushion = 4;
		int scaleFactor = 2;
		int minX = g.getFocus().getPos().getX() / Tile.WIDTH - g.getDimensions().getX() / Tile.WIDTH / scaleFactor - cushion;
		int maxX = g.getFocus().getPos().getX() / Tile.WIDTH + g.getDimensions().getX() / Tile.WIDTH / scaleFactor + cushion;
		int minY = g.getFocus().getPos().getY() / Tile.WIDTH - g.getDimensions().getY() / Tile.WIDTH / scaleFactor - cushion;
		int maxY = g.getFocus().getPos().getY() / Tile.WIDTH + g.getDimensions().getX() / Tile.WIDTH / scaleFactor + cushion;
		
		for (int i = minX; i < maxX; i++) {
			for (int j = minY; j < maxY; j++) {
				if (i >= 0 && i < map.length) {
					if (j >= 0 && j < map[i].length) {
						map[i][j].drawTile(g, gr, new Vector(i,j));
					}
					else if (renderOutside) {
						map[0][0].drawTile(g, gr, new Vector(i,j));
					}
				}
				else if (renderOutside) {
					map[0][0].drawTile(g, gr, new Vector(i,j));
				}
			}
		}
		
	}
	
	private static String getNameWithoutExtension(File f) {
		return f.getName().substring(0, f.getName().lastIndexOf('.'));
	}

	public void load(File file) throws FileNotFoundException, IOException {

		Main.log("loading " + file + "...");

		byte[] b = new byte[4];

		FileInputStream is = new FileInputStream(file);

		is.read(b);
		int width = ByteBuffer.wrap(b).getInt();
		is.read(b);
		int height = ByteBuffer.wrap(b).getInt();

		dim = new Vector(width, height);
		map = new Tile[width][height];

		byte[] b2 = new byte[2];
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				is.read(b2);
				map[i][j] = new Tile(((Byte) b2[0]).intValue(), ((Byte) b2[1]).intValue());
			}
		}

		is.close();
		
		loadSubGraphs(new File(file.getParentFile(), getNameWithoutExtension(file) + "-graphs.dat"));

		Main.log("load complete!");
		return;
	}

	public void save(File file) throws IOException {
		save(file, true);
	}
	
	public void save(File file, boolean saveGraphs) throws IOException {

		Main.log("saving " + file.getName() + "...");

		FileOutputStream os = new FileOutputStream(file);
		ByteBuffer b1 = ByteBuffer.allocate(4);
		ByteBuffer b2 = ByteBuffer.allocate(4);

		byte[] buffer = b1.putInt(dim.getX()).array();
		os.write(buffer);
		buffer = b2.putInt(dim.getY()).array();
		os.write(buffer);

		for (int i = 0; i < dim.getX(); i++) {
			for (int j = 0; j < dim.getY(); j++) {
				os.write(((byte) map[i][j].getType().getId()) & 0xFF);
				os.write(((byte) map[i][j].getStyle()) & 0xFF);
			}
		}

		os.close();
		
		if (saveGraphs) {
			saveSubGraphs(new File(file.getParentFile(), getNameWithoutExtension(file) + "-graphs.dat"));
		}

		Main.log("save complete!");
		return;
	}
	
	public static Rectangle getRectange(int i, int j) {
		return new Rectangle(i * Tile.WIDTH, j * Tile.WIDTH, Tile.WIDTH, Tile.WIDTH);
	}
	
	/**
	 * computes whether tiles in the map are context-pathable
	 * this is FAR less computationally intensive than computing all sub-graphs
	 */
	public void computePathability() {
		Main.log("computing pathfinding grid...");
		for (int i = 0; i < dim.getX(); i++) {
			for (int j = 0; j < dim.getY(); j++) {
				map[i][j].computeSurroundingsPathable(i, j, this);
			}
		}
		Main.log("pathfinding grid computed!");
	}
	
	public boolean isPathable(Vector pos) {
		Tile t = getTileGrid(pos);
		return t != null && t.isPathable();
	}
	
	/**
	 * @param pos
	 * the tile position in grid coordinates
	 * @return whether this tile is context pathable
	 */
	public boolean isContextPathable(Vector pos) {
		Tile t = getTileGrid(pos);
		return t != null && t.isContextPathable();
	}
	
	public boolean isContextPathable(int x, int y) {
		Tile t = getTileGrid(x, y);
		return t != null && t.isContextPathable();
	}
	
	public boolean canShootOver(Vector pos) {
		Tile t = getTileGrid(pos);
		return t != null && t.canShootOver();
	}
	
	public List<Vector> getGraph(Entity e) {
		return getGraph(e.getPos().copy().toGridPos());
	}
	
	public List<Vector> getGraph(Vector pos) {
		return graphHash.get(pos);
	}
	
	public boolean arePathConnected(Vector p1, Vector p2) {
		
		if (!isContextPathable(p1) || !isContextPathable(p2)) {
			return false;
		}
		
		ArrayList<Vector> v = new ArrayList<Vector>();
		v.add(p1);
		v.add(p2);
		
		for (ArrayList<Vector> graph : connectedSubGraphs) {
			
			//we found them in the same connected graph
			if (graph.containsAll(v)) {
				return true;
			}
			
			//we found one in the graph, but not the other
			if (graph.contains(p1) || graph.contains(p2)) {
				return false;
			}
			
		}
		
		return false;
		
	}
	
	@SuppressWarnings("unchecked")
	private void loadSubGraphs(File f) throws FileNotFoundException, IOException {
		
		if (! f.exists()) {
			Main.log("graph data not found in world; computing it from scratch");
			computeConnectedSubGraphs();
			return;
		}
		
		ObjectInputStream in = new ObjectInputStream(new FileInputStream(f));
		try {
			connectedSubGraphs = (ArrayList<ArrayList<Vector>>) in.readObject();
			computeGraphHash();
		} catch (ClassNotFoundException e) {
			Main.error("recalculating corrupted pathfinding data");
			computeConnectedSubGraphs();
		}
		in.close();
	}
	
	private void saveSubGraphs(File f) throws FileNotFoundException, IOException {
		
		//TODO: track when we do and don't need to do this
		Main.log("recomputing sub-graphs before save...");
		computePathfinding();
		
		ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(f));
		out.writeObject(connectedSubGraphs);
		out.close();
	}

	private void computeConnectedSubGraphs() {
		
		connectedSubGraphs = new ArrayList<ArrayList<Vector>>();
		
		Main.log("computing connected sub-graphs...");
		
		for (int x = 0; x < dim.getX(); x++) {
			tile: for (int y = 0; y < dim.getY(); y++) {
				
				double percent = 100 * (1.0 * x * dim.getY() + y) / (dim.getX() * dim.getY());
				if (percent % 20 == 0) {
					Main.log("\t" + (int) percent + "% of tiles checked");
				}
				
				if (! isContextPathable(x, y)) {
					continue;
				}
				
				Vector pos = new Vector(x, y);
				for (ArrayList<Vector> graph : connectedSubGraphs) {
					if (graph.contains(pos)) {
						continue tile;
					}
				}
								
				connectedSubGraphs.add(computeConnectedSubGraph(pos));
				Main.log("\tfound new sub-graph");
				
			}
		}
		
		Main.log(connectedSubGraphs.size() + " sub-graph(s) computed!");
		
		computeGraphHash();
		
	}
	
	//TODO update the hash when we do dynamic level changes
	private void computeGraphHash() {
		graphHash = new HashMap<Vector, List<Vector>>();
		for (List<Vector> graph : connectedSubGraphs) {
			for (Vector v : graph) {
				graphHash.put(v, graph);
			}
		}
		
		Main.log("position -> graph hash table set");
	}
	
	private ArrayList<Vector> computeConnectedSubGraph(Vector start) {
		
		ArrayList<Vector> graph = new ArrayList<Vector>();
		Queue<Vector> searchQ = new LinkedList<Vector>();
		searchQ.add(start);	
		Vector pos;
		boolean[][] visited = new boolean[dim.getX()][dim.getY()];
		
		while (!searchQ.isEmpty()) {
			
			pos = searchQ.poll();
			if (!isContextPathable(pos) || visited[pos.getX()][pos.getY()]) {
				continue;
			}
						
			visited[pos.getX()][pos.getY()] = true;
			graph.add(pos);
			
			for (Vector v : PathNode.NEIGHBORS) {
				searchQ.add(pos.copy().add(v));
			}
			
		}
		
		return graph;
				
	}

	public void computePathfinding() {
		computePathability();
		computeConnectedSubGraphs();
	}
	
	public void setTileGrid(Vector v, Tile newTile) {
		setTileGrid(v.getX(), v.getY(), newTile);
	}
	
	/**
	 * only use this method to update to pathable tiles
	 * @param v
	 * 	position, in grid coordinates
	 * @param newTile
	 * 	the new tile to place at this position
	 */
	public void setPathableGrid(Vector v, Tile newTile) {		
		
		//change the tile
		Tile oldTile = getTileGrid(v);
		setTileGrid(v, newTile);
		
		if (oldTile.isPathable() == newTile.isPathable()) {
			return;
		}
					
		//recalculate context pathability on "nearby" tiles
		for (int x = (v.getX() * Tile.WIDTH - Unit.RADIUS) / Tile.WIDTH; x <= (v.getX() * Tile.WIDTH + Unit.RADIUS) / Tile.WIDTH; x++) {
			for (int y = (v.getY() * Tile.WIDTH - Unit.RADIUS) / Tile.WIDTH; y <= (v.getY() * Tile.WIDTH + Unit.RADIUS) / Tile.WIDTH; y++) {
				map[x][y].computeSurroundingsPathable(x, y, this);			
			}
		}
		
		//union-ize all the graphs to which a neighbor belongs
		ArrayList<Vector> union = new ArrayList<Vector>();
		for (Vector trans : PathNode.NEIGHBORS) {
			Vector pos = v.copy().add(trans);
			if (isContextPathable(pos)) {
				for (ArrayList<Vector> graph : connectedSubGraphs) {
					if (graph.contains(pos)) {
						union.addAll(graph);
						connectedSubGraphs.remove(graph);
					}
				}
			}
		}
		connectedSubGraphs.add(union);
			
	}
	
	/**
	 * equivalent to setPathableGrid in global coordinates
	 * only use this method to update to pathable tiles
	 * @param v
	 * 	position, in global coordinates
	 * @param t
	 * 	the new tile to place at this position
	 */
	public void setPathable(Vector v, Tile t) {
		setPathableGrid(v.copy().toGridPos(), t);
	}

	public Vector getGoodSpawn(int startX, int startY) {
		for (int x = startX; x < dim.getX(); x++) {
			changeStart: for (int y = startY; y < dim.getY(); y++) {
				
				for (int x1 = (x * Tile.WIDTH - 2 * Unit.RADIUS) / Tile.WIDTH; x1 <= (x * Tile.WIDTH + 2 * Unit.RADIUS) / Tile.WIDTH; x1++) {
					for (int y1 = (y * Tile.WIDTH - 2 * Unit.RADIUS) / Tile.WIDTH; y1 <= (y * Tile.WIDTH + 2 * Unit.RADIUS) / Tile.WIDTH; y1++) {
						if (!isContextPathable(x1, y1)) {
							continue changeStart;
						}
					}
				}
				
				return new Vector(x, y).toGlobalPos();
				
			}
		}
		return null;
	}
	
	public Vector getGoodSpawn(Vector v) {
		return getGoodSpawn(v.getX(), v.getY());
	}

	@Override
	public Level getLevel() {
		return this;
	}

	public static Level wrapLoad() {
		
		File file = Main.getFileDialog("Select file to load", FileDialog.LOAD, true);

		if (file == null) {
			return null;
		}

		try {
			return new Level(file);
		} catch (IOException er) {
			Main.error("error opening world " + file.getName());
			return null;
		}
		
	}

	/**
	 * Helper function for fill door which might have broader utility.
	 */
	public boolean isBg(int x, int y, TileType wall, TileType floor) {
		return getTileGrid(x, y) == null ||
				(getTileGrid(x, y).getType() != floor && getTileGrid(x, y).getType() != wall);
	}
	
	/**
	 * For use in world generation. Fill in a door in a dungeon.
	 * @param pos
	 * The position of the door
	 * @param floor
	 * The background tile type (used for orientation)
	 */
	public void fillDoor(Vector pos, Tile fill, TileType floor) {
		
		Vector dir;
		
		//TODO change this to detect NOT FLOOR, not BACKGROUND
		
		if (isBg(pos.getX() + 1, pos.getY(), fill.getType(), floor) || isBg(pos.getX() - 1, pos.getY(), fill.getType(), floor)) {
			dir = new Vector(0, 1);
		} else if (isBg(pos.getX(), pos.getY() + 1, fill.getType(), floor) || isBg(pos.getX(), pos.getY() - 1, fill.getType(), floor)) {
			dir = new Vector(1, 0);
		} else {
			return;
		}
		
		for (int i = -DungeonGen.DOOR_WIDTH / 2; i <= DungeonGen.DOOR_WIDTH / 2; i++) {
			setTileGrid(dir.copy().multiply(i).add(pos), new Tile(fill));
		}
		
//		for (dir.multiply(-DungeonGen.DOOR_WIDTH / 2); dir.magnitude() <= DungeonGen.DOOR_WIDTH / 2; dir.incr()) {
//			setTileGrid(pos.copy().add(dir), new Tile(fill));
//		}
		
	}
	
}
