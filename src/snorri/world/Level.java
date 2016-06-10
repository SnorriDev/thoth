package snorri.world;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

import snorri.main.FocusedWindow;
import snorri.main.Main;
import snorri.world.Tile.TileType;

public class Level {

	private Tile[][] map;
	private Vector dim;

	// TODO: load from file, not empty grid with parameters
	// not that indexing conventions are Cartesian, not matrix-based

	public Level(int width, int height) {
		map = new Tile[width][height];
		dim = new Vector(width, height);

		for (int i = 0; i < dim.getX(); i++) {
			for (int j = 0; j < dim.getY(); j++) {
				map[i][j] = new Tile(TileType.SAND);
			}
		}
		
		computePathfinding();
		
	}

	public Level(File file) throws FileNotFoundException, IOException {
		load(file);
		computePathfinding();
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
	
	public void renderMap(FocusedWindow g, Graphics gr, boolean renderOutside) {
		int cushion = 4;
		int scaleFactor = 2;
		int minX = g.getFocus().getPos().getX() / Tile.WIDTH - g.getDimensions().getX() / Tile.WIDTH / scaleFactor - cushion;
		int maxX = g.getFocus().getPos().getX() / Tile.WIDTH + g.getDimensions().getX() / Tile.WIDTH / scaleFactor + cushion;
		int minY = g.getFocus().getPos().getY() / Tile.WIDTH - g.getDimensions().getY() / Tile.WIDTH / scaleFactor - cushion;
		int maxY = g.getFocus().getPos().getY() / Tile.WIDTH + g.getDimensions().getX() / Tile.WIDTH / scaleFactor + cushion;
		//Main.log(g.getFocus().getPos().getX() / Tile.WIDTH + " " + g.getFocus().getPos().getY() / Tile.WIDTH + "\t\t" + g.getFocus().getPos().getX() + " " + g.getFocus().getPos().getY() + "\t\t" + g.getDimensions().getX() + " " + g.getDimensions().getY() + "\t\t" + minX + " " + minY + "\t\t" + maxX + " " + maxY);
		
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

		Main.log("save complete!");
		return;
	}
	
	public static Rectangle getRectange(int i, int j) {
		return new Rectangle(i * Tile.WIDTH, j * Tile.WIDTH, Tile.WIDTH, Tile.WIDTH);
	}
	
	public void computePathfinding() {
		Main.log("computing pathfinding grid...");
		for (int i = 0; i < dim.getX(); i++) {
			for (int j = 0; j < dim.getY(); j++) {
				map[i][j].computeSurroundingsPathable(i, j, this);
			}
		}
		Main.log("pathfinding grid computed!");
	}
	
	public boolean isContextPathable(Vector pos) {
		Tile t = getTileGrid(pos);
		return t != null && t.isContextPathable();
	}
	
}
