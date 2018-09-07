package snorri.world;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import snorri.audio.ClipWrapper;
import snorri.entities.Entity;
import snorri.main.Debug;
import snorri.main.FocusedWindow;
import snorri.main.Main;
import snorri.masking.Mask;
import snorri.semantics.Nominal;
import snorri.world.TileType;

public class Tile implements Comparable<Tile>, Nominal {
	
	private static final long serialVersionUID = 1L;
	
	public static final int	WIDTH = 64;
	public static final BufferedImage DEFAULT_TEXTURE = Main.DEFAULT_TEXTURE;
	public static final BufferedImage DEFAULT_MIDGROUND_TEXTURE = getImage("/textures/tiles/default_midground00.png");
	public static final BufferedImage DEFAULT_FOREGROUND_TEXTURE = getImage("/textures/tiles/default_foreground00.png");
	public static final BufferedImage BLANK_TEXTURE = getImage("/textures/tiles/blank00.png");
									
	protected TileType type;
	private int style;
	private boolean reachable, surroundingsPathable = true;
	private List<Entity> entities;
	
	private List<Mask> masks;
	
	protected static ClipWrapper[] sounds;
	
	public Tile(TileType type, int style) {
		this.type = type;
		this.style = style;
		masks = new ArrayList<>();
		entities = new ArrayList<>();
	}
	
	private Tile() {
		this((TileType) null, 0);
	}
	
	public Tile(TileType type) {
		this(type, 0);
	}
	
	public Tile(Class<? extends TileType> c, int id, int style) {
		this(UnifiedTileType.values()[id], style);
	}
	
	public Tile(Tile tile) {
		this((TileType) tile.getType(), tile.getStyle());
	}
	
	/**
	 * Takes string representations of tiles.
	 * With two arguments, takes formats like "0:0" or "BLACK:0".
	 * With three arguments, takes the format "0:0:0" where the first argument is the layer.
	 * @param substring the substring to parse
	 */
	public Tile(String substring) {
		// TODO(#52): Refactor this constructor as a static factory function.
		this();
		String[] l = substring.split(":");
		if (l.length != 2) {
			throw new IllegalArgumentException("Invalid string for creating tile.");
		}
		try {
			// Interpret the string as a TileType name.
			type = UnifiedTileType.valueOf(l[0]);
		} catch (IllegalArgumentException e) {
			// Interpret the string as a TileType id.
			type = UnifiedTileType.values()[Integer.parseInt(l[0])];
		}
		style = Integer.parseInt(l[1]);
	}
	
	public Tile(int layer, int id, int style) {
		this(UnifiedTileType.values()[id], style);
	}

	public static ArrayList<Tile> getAllTypes() {
		ArrayList<Tile> list = new ArrayList<Tile>();
		for (TileType type : UnifiedTileType.values()) {
			list.add(new Tile(type, 0));
		}
		return list;
	}

	public TileType getType() {
		return type;
	}
	
	public int getStyle() {
		return style;
	}
	
	/**
	 * Draw a tile given an absolute grid position
	 * @param g
	 * @param gr
	 * @param v the absolute grid position of the tile to be drawn
	 */
	public void drawTile(FocusedWindow<?> g, Graphics2D gr, Vector v) {
		Vector relPos = v.getRelPosGrid(g);
		drawTileAbs(gr, relPos, false);
	}
	
	/**
	 * Draw a position at a global position relative to the screen center
	 * @param g 
	 * @param gr
	 * @param relPos
	 */
	public void drawTileAbs(Graphics2D gr, Vector pos, boolean mask) {
		//TODO some sort of quad search for rendering
		if (getBaseTexture() == null) {
			return;
		}
		gr.drawImage(getBaseTexture(), pos.getX(), pos.getY(), null);
	
		if (mask) {
			for (Mask m : masks) {
				m.drawMask(gr, pos);
				// TODO save the geometry for updates with each mask
			}
		}	
	
	}
	
	public BufferedImage getBaseTexture() {
		return type.getTexture(style);
	}
	
	@Override
	public String toString() {
		return type.name() + ":" + style;
	}
	
	public String toStringShort() {
		return type.name();
	}
	
	public boolean equals(Tile t) {
		if (t == null) {
			return false;
		}
		return (getType().equals(t.getType()) && getStyle() == t.getStyle());
	}
	
	public static BufferedImage getImage(String path) {
		if (Main.getImage(path) != null)
			return Main.getImage(path);
		else {
			//Main.error("No texture found, returning default texture");
			return DEFAULT_TEXTURE;
		}
	}

	public String toNumericString() {
		return getType().getId() + ":" + getStyle();
	}

	public boolean isPathable() {
		return type.isNotSurface();
	}
	
	public boolean isContextPathable() {
		return isPathable() && surroundingsPathable;
	}

	public void setReachable(boolean b) {
		reachable = b;
	}
	
	public boolean isReachable() {
		return reachable;
	}

	public boolean canShootOver() {
		return type.canShootOver();
	}

	/**
	 * Use this for deciding which tile to override while bitmasking
	 * TODO Confirm that a greater value means draw on top
	 */
	@Override
	public int compareTo(Tile t) {
		
		if (!type.getClass().equals(t.type.getClass())) {
			Debug.logger.warning("Comparing TileTypes from different layers.");
			return 0;
		}
		
		if (type == null || t.type == null) {
			Debug.logger.warning("One or more tiles are null.");
			if (type == null && t.type == null) {
				return 0;
			}
			else if (type == null) {
				return -1;
			}
			else {
				return 1;
			}
		}
		
		//int n = TileType.getValues(type.getClass()).length;
		//return Integer.compare(style * n + type.getId(),  t.style * n + t.type.getId()); //FIXME: the small number multiplication might cause a few bugs
		/*if (Double.compare(type.getBlendOrder() + 0.0001 * type.getId() + 0.0000001 * style, t.type.getBlendOrder() + 0.0001 * t.type.getId() + 0.0000001 * t.style) != 0) {
			Debug.log("" + Double.compare(type.getBlendOrder() + 0.0001 * type.getId() + 0.0000001 * style, t.type.getBlendOrder() + 0.0001 * t.type.getId() + 0.0000001 * t.style));
		}*/
		return Double.compare(getOrderValue(), t.getOrderValue());
		
//		if (type.equals(t.type)) {
//			return Integer.compare(style, t.style);
//		}
//		else
//			return 0;
	}
	
	/**
	 * @return where this tile should be drawn in the hierarchy of layers
	 */
	private double getOrderValue() {
		return type.getBlendOrder() + 0.0001 * type.getId() + 0.0000001 * style;
	}
	
	public void addMask(Mask mask) {
		masks.add(mask);
	}
	
	public void addEntity(Entity e) {
		entities.add(e);
	}
	
	public void removeEntity(Entity e) {
		entities.remove(e);
	}
	
	public static Rectangle getRectangle(int i, int j) {
		return new Rectangle(i * Tile.WIDTH, j * Tile.WIDTH, Tile.WIDTH, Tile.WIDTH);
	}

	public Tile getReplacementTile() {
		
		if (type.getReplacement() == null) {
			return null;
		}
		
		return new Tile(type.getReplacement(), style);
		
	}
	
	public List<Mask> getMasks() {
		return masks;
	}
	
	public static List<Tile> getBlendOrdering() {
		List<Tile> tiles = new ArrayList<>();
		for (TileType type : UnifiedTileType.values()) {
			for (int i = 0; i < type.getNumberStyles(); i++) {
				tiles.add(new Tile(type, i));
			}
		}
		Collections.sort(tiles);
		Collections.reverse(tiles);
		return tiles;
	}

	public void clearMasks() {
		masks = new ArrayList<>();
	}

	public void removeMask(Mask subbed) {
		masks.remove(subbed);
	}

	
}
