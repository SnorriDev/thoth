package snorri.world;

import java.awt.Graphics;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import snorri.entities.Entity;
import snorri.main.Debug;
import snorri.main.FocusedWindow;
import snorri.masking.Mask;
import snorri.terrain.DungeonGen;
import snorri.world.TileType;

public class Level implements Editable {

	public static final int MAX_SIZE = 1024;
	
	public static final int BACKGROUND = 0;
	public static final int MIDGROUND = 1;
	public static final int FOREGROUND = 2;
	
	/**An array of tiles. Note that coordinates are Cartesian, not matrix-based**/
	private Tile[][] map;

	public Level(int width, int height, TileType bg) {
		map = new Tile[width][height];
		
		for (int i = 0; i < getWidth(); i++) {
			for (int j = 0; j < getHeight(); j++) {
				map[i][j] = new Tile(bg);
			}
		}
				
	}
	
	public Level(Vector v, TileType bg) {
		this(v.getX(), v.getY(), bg);
	}
	
	public Level(int width, int height) {
		this(width, height, 0);
	}
	
	public Level(int width, int height, int layer) {
		this(width, height, ((layer == 0) ? BackgroundElement.SAND : ((layer == 1) ? MidgroundElement.NONE : ForegroundElement.NONE)));
	}
	
	public Level(Vector v) {
		this(v, 0);
	}
	
	public Level(Vector v, int layer) {
		this(v, ((layer == 0) ? BackgroundElement.SAND : ((layer == 1) ? MidgroundElement.NONE : ForegroundElement.NONE)));
	}

	public Level(File file, Class<? extends TileType> c) throws FileNotFoundException, IOException {
		load(file, c);
		enqueueAllBitMasks();
	}
	
	public Level(File file) throws FileNotFoundException, IOException {
		load(file);
		enqueueAllBitMasks();
	}
	
	/**
	 * Constructor used for resizing
	 */
	private Level(Level l, int newWidth, int newHeight) {
		this(l, newWidth, newHeight, 0);
	}
	
	private Level(Level l, int newWidth, int newHeight, int layer) {
		
		map = new Tile[newWidth][newHeight];
		
		if (layer == 0) {
			for (int i = 0; i < getWidth(); i++) {
				for (int j = 0; j < getHeight(); j++) {
					map[i][j] = new Tile(BackgroundElement.SAND);
				}
			}
		}
		else if (layer == 1) {
			for (int i = 0; i < getWidth(); i++) {
				for (int j = 0; j < getHeight(); j++) {
					map[i][j] = new Tile(MidgroundElement.NONE);
				}
			}
		}
		else {
			for (int i = 0; i < getWidth(); i++) {
				for (int j = 0; j < getHeight(); j++) {
					map[i][j] = new Tile(ForegroundElement.NONE);
				}
			}
		}
		
		for (int i = 0; i < getWidth() && i < l.getWidth(); i++) {
			for (int j = 0; j < getHeight() && j < l.getHeight(); j++) {
				map[i][j] = l.map[i][j];
			}
		}
	}
	
	public Level getTransposed() {
		Level t = new Level(getDimensions().getInverted());
		for (int x = 0; x < getWidth(); x++) {
			for (int y = 0; y < getHeight(); y++) {
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
		Level f = new Level(getDimensions());
		for (int x = 0; x < getWidth(); x++) {
			for (int y = 0; y < getHeight(); y++) {
				f.setTileGrid(getWidth() - 1 - x, y, getNewTileGrid(x, y));
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
				newMap[i][j] = new Tile(BackgroundElement.SAND);
			}
		}
		for (int i = 0; i < newDim.getX() && i < getWidth(); i++) {
			for (int j = 0; j < newDim.getY() && j < getHeight(); j++) {
				newMap[i][j] = map[i][j];
			}
		}
		
		map = newMap;
		
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
		updateMasksGrid(new Vector(x, y));
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
		return new Vector(getWidth(), getHeight());
	}
	
	@Override
	public void render(FocusedWindow<?> g, Graphics gr, double deltaTime, boolean renderOutside) {
		
		//TODO add some shit that checks if layer is empty etc.
		
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

	public void load(File file, Class<? extends TileType> c) throws FileNotFoundException, IOException {

		Debug.log("loading " + file + "...");

		byte[] b = new byte[4];

		FileInputStream is = new FileInputStream(file);

		is.read(b);
		int width = ByteBuffer.wrap(b).getInt();
		is.read(b);
		int height = ByteBuffer.wrap(b).getInt();

		map = new Tile[width][height];

		byte[] b2 = new byte[2];
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				is.read(b2);
				map[i][j] = new Tile(c, ((Byte) b2[0]).intValue(), ((Byte) b2[1]).intValue());
			}
		}

		is.close();
		
	}

	public void save(File file) throws IOException {
		save(file, true);
	}
	
	public void save(File file, boolean saveGraphs) throws IOException {

		Debug.log("saving " + file.getName() + "...");

		FileOutputStream os = new FileOutputStream(file);
		ByteBuffer b1 = ByteBuffer.allocate(4);
		ByteBuffer b2 = ByteBuffer.allocate(4);

		byte[] buffer = b1.putInt(getWidth()).array();
		os.write(buffer);
		buffer = b2.putInt(getHeight()).array();
		os.write(buffer);

		for (int i = 0; i < getWidth(); i++) {
			for (int j = 0; j < getHeight(); j++) {
				os.write(((byte) map[i][j].getType().getId()) & 0xFF);
				os.write(((byte) map[i][j].getStyle()) & 0xFF);
			}
		}

		os.close();
	}
	
	/**
	 * @param x coordinate
	 * @param y coordinate
	 * @return whether the tile at <code>(x, y)</code> is pathable and unoccupied
	 */
	public boolean isPathable(int x, int y) {
		return getTileGrid(x, y) != null && getTileGrid(x, y).isPathable();
	}
	
	public boolean isPathable(Vector pos) {
		return isPathable(pos.getX(), pos.getY());
	}
	
	public void setTileGrid(Vector v, Tile newTile) {
		setTileGrid(v.getX(), v.getY(), newTile);
	}

	@Override
	public Level getLevel() {
		return this;
	}
	
	@Override
	public Level getLevel(int layer) {
		return this;
	}
	
	@Override
	public Level getLevel(Class<? extends TileType> c) {
		return this;
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
		
	}

	public void setTile(Vector pos, Tile tile) {
		setTile(pos.getX(), pos.getY(), tile);
	}
	
	private void updateMasksGrid(Vector pos) {
		enqueueBitMasks(pos);
		for (Vector trans : Mask.NEIGHBORS_AND_CORNERS) {
			Vector p = pos.copy().add(trans);
			if (getTileGrid(p) != null) {
				enqueueBitMasks(p);
			}
		}
	}

	@Override
	public List<Entity> getEntities() {
		return new ArrayList<Entity>();
	}
	
	/**
	 * Enqueues all bitmasks onto a tile
	 */
	public void enqueueBitMasks(int x, int y) {
		
		Tile tile = getTileGrid(x, y);
		if (tile == null || tile.getType().isAtTop()) {
			return;
		}
		
		Mask[] masks = new Mask[8];
		
		short bitVal = 1;
		int j = 0;
		for (Vector v : Mask.NEIGHBORS) {
			Tile t = getTileGrid(v.copy().add(x, y));
			if (t != null && !t.getType().isAtTop() && tile.compareTo(t) > 0) {
				for (j = 0; j < masks.length; j++) {
					if (masks[j] == null) {
						masks[j] = new Mask(t, false);
					}
					if (masks[j].hasTile(t)) {
						masks[j].add(bitVal);
						break;
				
					}
				}
			}
			bitVal *= 2;
		}
		
		bitVal = 1;
		int k = 0;
		//int maxK = 0;
		for (Vector v: Mask.CORNERS) {
			Tile t = getTileGrid(v.copy().add(x, y));
			if (t != null && !t.getType().isAtTop() && tile.compareTo(t) > 0) {
				for (k = 0; k < masks.length; k++) {
					if (masks[k] == null) {
						masks[k] = new Mask(t, true);
					}
					if (masks[k].hasTile(t) && masks[k].isCorner()) {
						masks[k].add(bitVal);
						break;
					}
				}
			}
			bitVal *= 2;
		}

//		int masksSize = 0;
//		for (Mask m : masks) {
//			if (m == null) {
//				break;
//			}
//			else {
//				++masksSize;
//			}
//		}

		//FIXME: dis shit ain't working
		tile.enqueueBitMasks(masks);
		
	}
	
	public void enqueueBitMasks(Vector v) {
		enqueueBitMasks(v.getX(), v.getY());
	}
	
	public void enqueueAllBitMasks() {
		for (int x = 0; x < getWidth(); x++) {
			for (int y = 0; y < getHeight(); y++) {
				enqueueBitMasks(x, y);
			}
		}
	}
	
	public int getWidth() {
		return map.length;
	}
	
	public int getHeight() {
		return map[0].length;
	}

	@Override
	public void load(File folder) throws FileNotFoundException, IOException {
		load(folder, BackgroundElement.class);
	}
	
	public boolean canShootOver(Vector pos) {
		Tile t = getTileGrid(pos);
		return t != null && t.canShootOver();
	}
	
}
