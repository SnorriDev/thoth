package snorri.world;

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
import java.util.LinkedList;
import java.util.Queue;

import snorri.main.FocusedWindow;
import snorri.main.Main;
import snorri.pathfinding.PathNode;
import snorri.world.Tile.TileType;

public class Level {

	public static final int MAX_SIZE = 1024;
	
	private Tile[][] map;
	private Vector dim;
	
	private ArrayList<ArrayList<Vector>> connectedSubGraphs;

	// not that indexing conventions are Cartesian, not matrix-based

	public Level(int width, int height) {
		map = new Tile[width][height];
		dim = new Vector(width, height);

		for (int i = 0; i < dim.getX(); i++) {
			for (int j = 0; j < dim.getY(); j++) {
				map[i][j] = new Tile(TileType.SAND);
			}
		}
		
	}
	
	public Level(Vector v) {
		this(v.getX(), v.getY());
	}

	public Level(File file) throws FileNotFoundException, IOException {
		load(file);
	}
	
	//for resizing
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
	
	public Level resize(int newWidth, int newHeight) {
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
	
	//TODO: we probably don't need this
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
	
	public void renderMap(FocusedWindow g, Graphics gr, boolean renderOutside) {
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
		return;
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
		
		saveSubGraphs(new File(file.getParentFile(), getNameWithoutExtension(file) + "-graphs.dat"));

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
	
	public boolean arePathConnected(Vector p1, Vector p2) {
		
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
		} catch (ClassNotFoundException e) {
			Main.error("recalculating corrupted pathfinding data");
			computeConnectedSubGraphs();
		}
		in.close();
	}
	
	private void saveSubGraphs(File f) throws FileNotFoundException, IOException {
		
		//TODO: track when we do and don't need to save
		Main.log("recomputing graphs before save");
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

	public Vector getGoodSpawn(int startX, int startY) {
		for (int x = startX; x < dim.getX(); x++) {
			for (int y = startY; y < dim.getY(); y++) {
				if (isContextPathable(x, y) && isContextPathable(x - 2, y - 2) && isContextPathable(x + 2, y - 2)
						&& isContextPathable(x - 2, y + 2) && isContextPathable(x + 2, y + 2)) {
					return new Vector(x, y).toGlobalPos();
				}
			}
		}
		return null;
	}
	
	public Vector getGoodSpawn(Vector v) {
		return getGoodSpawn(v.getX(), v.getY());
	}
	
}
