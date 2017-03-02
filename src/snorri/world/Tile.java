package snorri.world;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import snorri.audio.ClipWrapper;
import snorri.entities.Entity;
import snorri.entities.Unit;
import snorri.main.Debug;
import snorri.main.FocusedWindow;
import snorri.main.Main;
import snorri.masking.Mask;
import snorri.semantics.Nominal;
import snorri.world.TileType;

public class Tile implements Comparable<Tile>, Nominal {
	
	public static final int	WIDTH	= 64;
	public static final BufferedImage DEFAULT_TEXTURE = Main.DEFAULT_TEXTURE;
	public static final BufferedImage DEFAULT_BACKGROUND_TEXTURE = getImage("/textures/tiles/default_background00.png");
	public static final BufferedImage DEFAULT_MIDGROUND_TEXTURE = getImage("/textures/tiles/default_midground00.png");
	public static final BufferedImage DEFAULT_FOREGROUND_TEXTURE = getImage("/textures/tiles/default_foreground00.png");
									
	private TileType type;
	private int style;
	private boolean reachable, surroundingsPathable = true;
	private List<Entity> entities;
	
	private Mask[] bitMasks;
	
	protected static ClipWrapper[] sounds;
	

	private Tile() {
		entities = new ArrayList<>();
	}
	
	public Tile(TileType type) { //FIXME
		this();
		this.type = type;
		style = 0;
	}
	
	public Tile(TileType type, int style) {
		this(type);
		this.style = style;
	}
	
	public Tile(Class<? extends TileType> c, int id) {
		this(TileType.lookup(c, id));
		style = 0;
	}
	
	public Tile(Class<? extends TileType> c, int id, int style) {
		this(c, id);
		this.style = style;
	}
	
	public Tile(Tile tile) {
		this((TileType) tile.getType(), tile.getStyle());
	}
	
	public Tile(String substring) {
		this();
		String[] l = substring.split(":");
		type = BackgroundElement.byIdStatic(Integer.parseInt(l[0]));
		style = Integer.parseInt(l[1]);
	}

	public static ArrayList<Tile> getAllTypes(Class<? extends TileType> c) {
		ArrayList<Tile> list = new ArrayList<Tile>();
		if (c == BackgroundElement.class) {
			for(int i = 0; i < BackgroundElement.values().length; i++) {
				list.add(new Tile(c, i, 0));
			}
		}
		else if (c == MidgroundElement.class) {
			for(int i = 0; i < MidgroundElement.values().length; i++) {
				list.add(new Tile(c, i, 0));
			}
		}
		else if (c == ForegroundElement.class) {
			for(int i = 0; i < ForegroundElement.values().length; i++) {
				list.add(new Tile(c, i, 0));
			}
		}
		return list;
	}

	public static ArrayList<Tile> getSubTypes(Class<? extends TileType> c, int i) {
		ArrayList<Tile> list = new ArrayList<Tile>();
		for(int j = 0; j < BackgroundElement.byIdStatic(i).getNumberStyles(); j++) {
			list.add(new Tile(c, i, j));
		}
		return list;
	}
	
	public static ArrayList<Tile> getAll(Class<? extends TileType> c) {
		ArrayList<Tile> list = new ArrayList<Tile>();
		for(int i = 0; i < BackgroundElement.values().length; i++) {
			for(int j = 0; j < BackgroundElement.byIdStatic(i).getNumberStyles(); j++) {
				list.add(new Tile(c, i, j));
			}
		}
		return list;
	}
	public TileType getType() {
		//Main.debug(type.name());
		return type;
	}
	
	public int getStyle() {
		return style;
	}
	
	public void drawTile(FocusedWindow g, Graphics gr, Vector v) {
		
		Vector relPos = v.getRelPosGrid(g);
		
		if (Debug.RENDER_GRAPHS) {
			if (g.getWorld().getLevel().getGraph(v) != null) {
				gr.setColor(Debug.getColor(g.getWorld().getLevel().getGraph(v)));
				gr.drawRect(relPos.getX(), relPos.getY(), Tile.WIDTH, Tile.WIDTH);
				gr.setColor(Color.BLACK);
				return;
			}
		}
		
		gr.drawImage(type.getTexture(style), relPos.getX(), relPos.getY(), g);
		
		if (Debug.HIDE_MASKS || bitMasks == null) {
			return;
		}
				
		//TODO g vs. null as ImageObserver
		//TODO figure out why we are getting 8
		for (Mask m : bitMasks) {
			if (m == null) {
				break;
			}
			gr.drawImage(m.getTexture(), relPos.getX(), relPos.getY(), g);
		}
		
	}
	
	public BufferedImage getTexture() {
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
		return (type.getId() == t.getType().getId() && style == t.getStyle());
	}
	
	public static BufferedImage getImage(String path) {
		if (Main.getImage(path) != null)
			return Main.getImage(path);
		else {
			//Main.error("No texture found, returning default texture");
			return DEFAULT_TEXTURE;
		}
	}
	
	public static BufferedImage getImage(String path, int layer) {
		if (Main.getImage(path) != null)
			return Main.getImage(path);
		else {
			if (layer == 0) {
				//Main.error("No texture found, returning default background texture");
				return DEFAULT_BACKGROUND_TEXTURE;
			}
			else if (layer == 1) {
				//Main.error("No texture found, returning default midground texture");
				return DEFAULT_MIDGROUND_TEXTURE;
			}
			else if (layer == 2) {
				//Main.error("No texture found, returning default foreground texture");
				return DEFAULT_FOREGROUND_TEXTURE;
			}
			else {
				//Main.error("No texture found, returning default texture");
				return DEFAULT_TEXTURE;
			}
		}
	}

	public String toNumericString() {
		return getType().getId() + ":" + getStyle();
	}

	public boolean isPathable() {
		return type.isPathable();
	}
	
	public boolean isContextPathable() {
		return isPathable() && surroundingsPathable;
	}
	
	public boolean isOccupied() {
		return !entities.isEmpty();
	}
	
	//figure out if we can stand on this block at the very beginning
	public void computeSurroundingsPathable(int x, int y, Level level) {
		
		surroundingsPathable = true;
			
		for (int i = (x * Tile.WIDTH - Unit.RADIUS_X) / Tile.WIDTH; i <= (x * Tile.WIDTH + Unit.RADIUS_X) / Tile.WIDTH; i++) {
			for (int j = (y * Tile.WIDTH - Unit.RADIUS_Y) / Tile.WIDTH; j <= (y * Tile.WIDTH + Unit.RADIUS_Y) / Tile.WIDTH; j++) {
				
				Tile t = level.getTileGrid(i, j);
				if (t == null || !t.isPathable() || t.isOccupied()) {
					surroundingsPathable = false;
					return;
				}
								
			}
		}
		
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
	 * TODO This should use a custom ordering for optimal appearance,
	 * not just the default enumeration
	 */
	@Override
	public int compareTo(Tile t) {
		if (type.equals(t.type)) {
			return Integer.compare(style, t.style);
		}
		else
			return 0;
	}
	
	public void setBitMasks(Mask[] b) {
		bitMasks = b;
	}
	
	public void setBitMasks(Level l, int x, int y) {
		setBitMasks(l.getBitMasks(x, y));
	}
	
	public void setBitMasks(Level l, Vector pos) {
		setBitMasks(l, pos.getX(), pos.getY());
	}
	
	public void addEntity(Entity e) {
		entities.add(e);
	}
	
	public void removeEntity(Entity e) {
		entities.remove(e);
	}
	
}
