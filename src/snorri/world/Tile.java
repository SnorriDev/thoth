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
import snorri.main.Util;
import snorri.masking.Mask;
import snorri.semantics.Nominal;

public class Tile implements Comparable<Tile>, Nominal {
	
	public static final int	WIDTH	= 16;
	public static final BufferedImage DEFAULT_TEXTURE = Main.DEFAULT_TEXTURE;
									
	private TileType type;
	private int style;
	private boolean reachable, surroundingsPathable = true;
	private List<Entity> entities;
	
	private Mask[] bitMasks;
	
	protected static ClipWrapper[] sounds;
	

	private Tile() {
		entities = new ArrayList<>();
	}
	
	public Tile(TileType type) {
		this();
		this.type = type;
		style = 0;
	}
	
	public Tile(TileType type, int style) {
		this(type);
		this.style = style;
	}
	
	public Tile(int id) {
		this(TileType.byId(id));
		style = 0;
	}
	
	public Tile(int id, int style) {
		this(id);
		this.style = style;
	}
	
	public Tile(Tile tile) {
		this(tile.getType(), tile.getStyle());
	}
	
	public Tile(String substring) {
		this();
		String[] l = substring.split(":");
		type = TileType.byId(Integer.parseInt(l[0]));
		style = Integer.parseInt(l[1]);
	}

	public static ArrayList<Tile> getAllTypes() {
		ArrayList<Tile> list = new ArrayList<Tile>();
		for(int i = 0; i < TileType.values().length; i++) {
			list.add(new Tile(i,0));
		}
		return list;
	}
	
	public static ArrayList<Tile> getSubTypes(int i) {
		ArrayList<Tile> list = new ArrayList<Tile>();
		for(int j = 0; j < TileType.byId(i).getNumberStyles(); j++) {
			list.add(new Tile(i,j));
		}
		return list;
	}
	
	public static ArrayList<Tile> getAll() {
		ArrayList<Tile> list = new ArrayList<Tile>();
		for(int i = 0; i < TileType.values().length; i++) {
			for(int j = 0; j < TileType.byId(i).getNumberStyles(); j++) {
				list.add(new Tile(i,j));
			}
		}
		return list;
	}
	
	public TileType getType() {
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
		else
			return DEFAULT_TEXTURE;
	}
	
	public enum TileType implements Nominal {
												
		SAND(true, false, new BufferedImage[] {
			getImage("/textures/tiles/sand00.png"),
			getImage("/textures/tiles/sand01.png"),
			getImage("/textures/tiles/sand02.png"),
			getImage("/textures/tiles/sand03.png")}, false, true),
		WALL(false, new BufferedImage[] {
			getImage("/textures/tiles/wall00.png"),
			getImage("/textures/tiles/wall01.png"),
			getImage("/textures/tiles/wall02.png"),
			getImage("/textures/tiles/wall03.png"),
			getImage("/textures/tiles/wall04.png"),
			getImage("/textures/tiles/wall05.png"),
			getImage("/textures/tiles/wall06.png"),
			getImage("/textures/tiles/wall07.png"),
			getImage("/textures/tiles/wall08.png"),
			getImage("/textures/tiles/wall09.png"),
			getImage("/textures/tiles/wall10.png")}, true),
		TREE(false, Main.getImage("/textures/tiles/tree00.png")),
		FOUNDATION(false, DEFAULT_TEXTURE),
		HUT(false, DEFAULT_TEXTURE),
		WATER(false, true, getImage("/textures/tiles/water00.png")),
		LAVA(false, true, new BufferedImage[] {
			getImage("/textures/tiles/lava00.png"),
			getImage("/textures/tiles/lava01.png"),
			getImage("/textures/tiles/lava02.png")}),
		GRASS(true, false, new BufferedImage[] {
			getImage("/textures/tiles/grass00.png"),
			getImage("/textures/tiles/grass01.png")}, false, true),
		VOID(false, false, Main.getImage("/textures/tiles/void00.png")),
		COLUMN(false, new BufferedImage[] {
			getImage("/textures/tiles/column00.png"),
			getImage("/textures/tiles/column01.png")}, true),
		DOOR(false, getImage("/textures/tiles/door00.png"), true),
		SANDSTONE(false, false, new BufferedImage[] {
				getImage("/textures/tiles/sandstone00.png"),
				getImage("/textures/tiles/sandstone01.png"),
				getImage("/textures/tiles/sandstone02.png"),
				getImage("/textures/tiles/default00.png")}, false, true), //will change to look like bricks
		FLOOR(true, new BufferedImage[] {getImage("/textures/tiles/floor00.png"),
			getImage("/textures/tiles/floor01.png"),
			getImage("/textures/tiles/floor02.png"),
			getImage("/textures/tiles/floor03.png"),
			getImage("/textures/tiles/floor04.png"),
			getImage("/textures/tiles/floor05.png"),
			getImage("/textures/tiles/floor06.png"),
			getImage("/textures/tiles/floor07.png"),
			getImage("/textures/tiles/floor08.png"),
			getImage("/textures/tiles/floor09.png"),
			getImage("/textures/tiles/floor10.png"),
			getImage("/textures/tiles/floor11.png"),
			getImage("/textures/tiles/floor12.png")}, true),
		GRAVEL(true, getImage("/textures/tiles/floor11.png")),
		STONE(false, new BufferedImage[] {
			getImage("/textures/tiles/stone00.png"),
			getImage("/textures/tiles/stone01.png")}),
		DEEP_WATER(false, false, getImage("/textures/tiles/water01.png")),
		CLIFF(false, false, new BufferedImage[] {
			getImage("/textures/tiles/cliff00.png"),
			getImage("/textures/tiles/cliff01.png"),
			getImage("/textures/tiles/cliff02.png"),
			getImage("/textures/tiles/cliff03.png"),
			getImage("/textures/tiles/cliff04.png"),
			getImage("/textures/tiles/cliff05.png"),
			getImage("/textures/tiles/cliff06.png"),
			getImage("/textures/tiles/cliff07.png"),
			getImage("/textures/tiles/cliff08.png"),
			getImage("/textures/tiles/cliff09.png"),
			getImage("/textures/tiles/cliff10.png"),
			getImage("/textures/tiles/cliff11.png")}, true),
		WOOD(true, new BufferedImage[] {
			getImage("/textures/tiles/wood00.png"),
			getImage("/textures/tiles/wood01.png"),
			getImage("/textures/tiles/wood02.png")}, true),
		BRICK(true, new BufferedImage[] {
			getImage("/textures/tiles/brick00.png"),
			getImage("/textures/tiles/brick01.png"),
			getImage("/textures/tiles/brick02.png"),
			getImage("/textures/tiles/brick03.png"),
			getImage("/textures/tiles/brick04.png"),
			getImage("/textures/tiles/brick05.png"),
			getImage("/textures/tiles/brick06.png")}, true);
		
		private boolean	pathable, canShootOver, atTop, changable;
		private BufferedImage[]	textures;
									
		TileType() {
			this(true);
		}
		TileType(boolean pathable) {
			this(pathable, new BufferedImage[] {DEFAULT_TEXTURE});
		}
		
		TileType(boolean pathable, BufferedImage texture) {
			this(pathable, new BufferedImage[] {texture});
		}
		
		TileType(boolean pathable, BufferedImage[] textures) {
			this.pathable = pathable;
			this.textures = textures;
			canShootOver = pathable;
			changable = false;
			atTop = false;
		}
		
		/**
		 * Create a a liquid tile
		 * @param pathable
		 * 	Whether this tile can be walked over
		 * @param liquidEditable
		 * 	Whether the player can modify this tile with spells
		 * @param textures
		 * 	A list of textures
		 */
		TileType(boolean pathable, boolean liquidEditable, BufferedImage[] textures) {
			this(pathable, textures);
			canShootOver = true;
			changable = liquidEditable;
		}
		
		TileType(boolean pathable, boolean liquidEditable, BufferedImage texture) {
			this(pathable, texture);
			canShootOver = true;
			changable = liquidEditable;
		}
		
		TileType(boolean pathable, boolean liquidEditable, BufferedImage[] textures, boolean atTop) {
			this(pathable, textures);
			canShootOver = true;
			changable = liquidEditable;
			this.atTop = atTop;
		}
		
		TileType(boolean pathable, boolean liquidEditable, BufferedImage texture, boolean atTop) {
			this(pathable, texture);
			canShootOver = true;
			changable = liquidEditable;
			this.atTop = atTop;
		}
		
		TileType(boolean pathable, BufferedImage[] textures, boolean atTop) {
			this(pathable , textures);
			this.atTop = atTop;
		}
		
		TileType(boolean pathable, BufferedImage texture, boolean atTop) {
			this(pathable , texture);
			this.atTop = atTop;
		}
		
		/**
		 * Use to make tile types which can be changed with sandstorms, etc.
		 * @param pathable
		 * 	Whether it can be walked on
		 * @param swimmable
		 * 	Whether it is a liquid
		 * @param texture
		 * 	The texture to display
		 * @param atTop
		 * 	Whether it should be bitmapped over
		 * @param changable
		 * 	Whether it can be changed
		 */
		TileType(boolean pathable, boolean swimmable, BufferedImage texture, boolean atTop, boolean changable) {
			this(pathable , texture, atTop);
			this.changable = changable;
		}
		

		/**
		 * Use to make tile types which can be changed with sandstorms, etc.
		 * @param pathable
		 * 	Whether it can be walked on
		 * @param swimmable
		 * 	Whether it is a liquid
		 * @param texture
		 * 	The texture to display
		 * @param atTop
		 * 	Whether it should be bitmapped over
		 * @param changable
		 * 	Whether it can be changed
		 */

		TileType(boolean pathable, boolean swimmable, BufferedImage[] textures, boolean atTop, boolean changable) {
			this(pathable , textures, atTop);
			this.changable = changable;
		}
		


		public boolean isLiquid() {
			return !pathable && canShootOver;
		}
		
		public boolean isChangable() {
			return changable;
		}
		
		public static TileType byId(int id) {
			return values()[id];
		}
		
		public int getId() {
			return ordinal();
		}
		
		public boolean isPathable() {
			return pathable;
		}
		
		public boolean canShootOver() {
			return canShootOver;
		}
		
		public BufferedImage[] getTextures() {
			if (textures != null)
					return textures;
			else
				Main.error("no textures found, returning default texture");
				return new BufferedImage[] {DEFAULT_TEXTURE};
		}
		
		public BufferedImage getTexture(int index) {
			if (index >= textures.length) {
				Main.error("texture not found, index out of bounds, returning default texture");
				return DEFAULT_TEXTURE;
			}
			return textures[index];
		}
		
		public int getNumberStyles() {
			return textures.length;
		}
		
		@Override
		public String toString() {
			return Util.clean(name());
		}
		
		@Override
		public Object get(World world, AbstractSemantics attr) {
			
			if (attr == AbstractSemantics.FLOOD && isLiquid()) {
				return new Tile(this);
			}
			
			if (attr == AbstractSemantics.STORM && this == SAND) {
				return new Tile(this, 0);
			}
			
			return Nominal.super.get(world, attr);
			
		}
		
		public boolean isAtTop() {
			return atTop;
		}
		
		public ArrayList<Tile> getSubTypes() {
			int i = getId();
			ArrayList<Tile> list = new ArrayList<Tile>();
			for(int j = 0; j < TileType.byId(i).getNumberStyles(); j++) {
				list.add(new Tile(i,j));
			}
			return list;
		}
		
		public boolean hasSounds() {
			//return (sounds != null && sounds.length >= 1);
			return sounds.length >= 1;
		}
		
		public int getNumSounds() {
			return sounds.length;
		}
		
		public ClipWrapper[] getSounds() {
			return sounds;
		}
		
		public ClipWrapper getSound(int x) { //FIXME: what happens when a null sound gets played?  Should there be a default sound?
			if (x < sounds.length)
				return sounds[x];
			else {
				Main.error("index out of bounds");
				return null;
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
	public int compareTo(Tile o) {
		if (type.equals(o.type)) {
			return Integer.compare(style, o.style);
		}
		return type.compareTo(o.type);
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
