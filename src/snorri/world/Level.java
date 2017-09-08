package snorri.world;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.TexturePaint;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Path2D;
import java.awt.geom.PathIterator;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;

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

	public static final int CUSHION = 4;
	public static final int SCALE_FACTOR = 2;

	/**
	 * An array of tiles. Note that coordinates are Cartesian, not matrix-based
	 **/
	private Tile[][] map;
	private Map<BufferedImage, Area> textureMap;
	private BufferedImage layer;
	
	private int level;

	public Level(int width, int height, TileType bg) {
		map = new Tile[width][height];
		level = bg.getLayer();
		
		// TODO add an int layer here?
		// change other thing to renderedLayer?

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
		this(width, height, ((layer == 0) ? BackgroundElement.SAND
				: ((layer == 1) ? MidgroundElement.NONE : ForegroundElement.NONE)));
	}

	public Level(Vector v) {
		this(v, 0);
	}

	public Level(Vector v, int layer) {
		this(v, ((layer == 0) ? BackgroundElement.SAND
				: ((layer == 1) ? MidgroundElement.NONE : ForegroundElement.NONE)));
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
		} else if (layer == 1) {
			for (int i = 0; i < getWidth(); i++) {
				for (int j = 0; j < getHeight(); j++) {
					map[i][j] = new Tile(MidgroundElement.NONE);
				}
			}
		} else {
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
	 * Flip the level on the x axis. Using this method and
	 * <code>getTransposed()</code>, one can produce levels with a door facing
	 * out from all four sides if a door exists.
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
		setTileGrid(x / Tile.WIDTH, y / Tile.WIDTH, t);
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
		if (getTileGrid(x, y) == null) {
			return null;
		}
		return new Tile(getTileGrid(x, y));
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

		// TODO add some shit that checks if layer is empty etc.

		// Debug.log("RENDERING...");
		// Debug.log("" + g.getCenterObject());
		// Debug.log("" + g.getCenterObject().getPos());

		int minX;
		int maxX;
		int minY;
		int maxY;

		minX = g.getCenterObject().getPos().getX() / Tile.WIDTH - g.getDimensions().getX() / Tile.WIDTH / SCALE_FACTOR
				- CUSHION;
		maxX = g.getCenterObject().getPos().getX() / Tile.WIDTH + g.getDimensions().getX() / Tile.WIDTH / SCALE_FACTOR
				+ CUSHION;
		minY = g.getCenterObject().getPos().getY() / Tile.WIDTH - g.getDimensions().getY() / Tile.WIDTH / SCALE_FACTOR
				- CUSHION;
		maxY = g.getCenterObject().getPos().getY() / Tile.WIDTH + g.getDimensions().getX() / Tile.WIDTH / SCALE_FACTOR
				+ CUSHION;

		for (int i = minX; i < maxX; i++) {
			for (int j = minY; j < maxY; j++) {
				if (i >= 0 && i < map.length) {
					if (j >= 0 && j < map[i].length) {
						map[i][j].drawTile(g, gr, new Vector(i, j));
					} else if (renderOutside) {
						map[0][0].drawTile(g, gr, new Vector(i, j));
					}
				} else if (renderOutside) {
					map[0][0].drawTile(g, gr, new Vector(i, j));
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
		level = map[0][0].getType().getLayer();

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
	 * @param x
	 *            coordinate
	 * @param y
	 *            coordinate
	 * @return whether the tile at <code>(x, y)</code> is pathable and
	 *         unoccupied
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
		return getTileGrid(x, y) == null
				|| (getTileGrid(x, y).getType() != floor && getTileGrid(x, y).getType() != wall);
	}

	/**
	 * For use in world generation. Fill in a door in a dungeon.
	 * 
	 * @param pos
	 *            The position of the door
	 * @param floor
	 *            The background tile type (used for orientation)
	 */
	public void fillDoor(Vector pos, Tile fill, TileType floor) {

		Vector dir;

		if (isBg(pos.getX() + 1, pos.getY(), fill.getType(), floor)
				|| isBg(pos.getX() - 1, pos.getY(), fill.getType(), floor)) {
			dir = new Vector(0, 1);
		} else if (isBg(pos.getX(), pos.getY() + 1, fill.getType(), floor)
				|| isBg(pos.getX(), pos.getY() - 1, fill.getType(), floor)) {
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
		// int maxK = 0;
		for (Vector v : Mask.CORNERS) {
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
		
		for (Mask mask : masks) {
			if (mask == null) {
				break;
			}
			tile.addMask(mask);
		}

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
		Debug.log("computing texture map");
		computeTextureMap();
		Debug.log("rendering layer");
		renderLayer();
		try {
			File fh = new File("/Users/snorri/Desktop/thothLevels/" + (int) (Math.random() * 10000) + ".png");
			Debug.log(fh.getName());
			fh.createNewFile();
			ImageIO.write(layer, "png", fh);
		} catch (IOException e) {
			Debug.error(e);
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

	/**
	 * Builds a mapping from each texture in the level to the area where that texture should be drawn.
	 * @return the computed mapping
	 */
	public void computeTextureMap() {
		textureMap = new HashMap<>();
		for (int x = 0; x < getWidth(); x++) {
			for (int y = 0; y < getHeight(); y++) {
				
				if (map[x][y].getTexture() != null) {
					if (!textureMap.containsKey(map[x][y].getTexture())) {
						textureMap.put(map[x][y].getTexture(), new Area());
					}
					textureMap.get(map[x][y].getTexture()).add(new Area(new Rectangle(x * Tile.WIDTH, y * Tile.WIDTH, Tile.WIDTH, Tile.WIDTH)));
				}
				
				AffineTransform transform = AffineTransform.getTranslateInstance(x * Tile.WIDTH, y * Tile.WIDTH);
				for (Mask m : map[x][y].getMasks()) {
					BufferedImage texture = m.getBaseTexture();
					if (!textureMap.containsKey(texture)) {
						textureMap.put(texture, new Area());
					}
					Area area = m.getArea().createTransformedArea(transform);
//					Area area = new Area(new Ellipse2D.Double(x * Tile.WIDTH, y * Tile.WIDTH, Tile.WIDTH, Tile.WIDTH));
					textureMap.get(texture).add(area);
//					Debug.log("added area: " + area.getBounds());
				}
				
			}
		}
	}
	
	//TODO try using GeneralPath to build areas
	
	//see https://gamedev.stackexchange.com/questions/72924/how-do-i-add-an-image-inside-a-rectangle-or-a-circle-in-javafx
	//see https://www.cse.iitb.ac.in/~paragc/teaching/2012/cs775/assignments/A3/groups/jai+ahana/Depixelizing%20pixelArt.pdf
	// TODO want to implement this second paper for blending nearby stuff
	
	//TODO general path: https://docs.oracle.com/javase/tutorial/2d/geometry/arbitrary.html
	
	//TODO maybe cut the area, draw images directly
	
	//confirmed: can draw non-Rectangles onto the other shape
	
	//solution: make a path2d through all the border points
	
	public void renderLayer() {
		layer = new BufferedImage(getWidth() * Tile.WIDTH, getHeight() * Tile.WIDTH, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = layer.createGraphics();
		
		//TODO this should be sorted, and can be made a lot more efficient
		for (Tile tile : Tile.getBlendOrdering(getLayer())) {
			
			BufferedImage texture = tile.getBaseTexture();
			if (!textureMap.containsKey(texture)) {
				continue;
			}
			
			Rectangle r = new Rectangle(0, 0, texture.getWidth(), texture.getHeight());
			g.setPaint(new TexturePaint(texture, r));
			g.fill(textureMap.get(texture));
			
//			if (! tile.getType().isAtTop()) {
			g.setColor(Color.BLACK);
			Path2D path = new Path2D.Double(textureMap.get(texture));
			g.draw(path);
//			}
			
		}
		
//		Main.log("blurring layer");
//		SmartBlurFilter filter = new SmartBlurFilter();
//		layer = filter.filter(layer);
		
		g.dispose();
		
	}
	
	public int getLayer() {
		return level;
	}

}
