package snorri.world;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import snorri.entities.Unit;
import snorri.main.Debug;
import snorri.main.FocusedWindow;
import snorri.main.Main;
import snorri.masking.Mask;
import snorri.semantics.Nominal;

public class Tile implements Comparable<Tile> {
	
	public static final int	WIDTH	= 16;
									
	private TileType type;
	private int style;
	private boolean surroundingsPathable = true;
	private boolean reachable;
	
	private Mask[] bitMasks;

	public Tile(TileType type) {
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
		if (tile == null)
			return;
		type = tile.getType();
		style = tile.getStyle();
	}
	
	public Tile(String substring) {
		String[] l = substring.split(":");
		type = TileType.byId(Integer.parseInt(l[0]));
		style = Integer.parseInt(l[1]);
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
		Vector relPos = v.getRelPos(g);
		gr.drawImage(type.getTexture(style), relPos.getX(), relPos.getY(), g);
		
		if (Debug.HIDE_MASKS || bitMasks == null) {
			return;
		}
		
		//TODO g vs. null as ImageObserver
		for (Mask m : bitMasks) {
			if (m == null) {
				break;
			}
			Main.log("yo");
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
	
	public boolean equals(Tile t) {
		if (t == null || this == null) {
			return false;
		}
		return (type.getId() == t.getType().getId() && style == t.getStyle());
	}
	
	public enum TileType implements Nominal {
												
		SAND(true, new BufferedImage[] {
			Main.getImageResource("/textures/tiles/sand00.png"),
			Main.getImageResource("/textures/tiles/sand01.png"),
			Main.getImageResource("/textures/tiles/sand02.png"),
			Main.getImageResource("/textures/tiles/sand03.png")}),
		WALL(false, new BufferedImage[] {
			Main.getImageResource("/textures/tiles/wall00.png"),
			Main.getImageResource("/textures/tiles/wall01.png"),
			Main.getImageResource("/textures/tiles/wall02.png"),
			Main.getImageResource("/textures/tiles/wall03.png"),
			Main.getImageResource("/textures/tiles/wall04.png"),
			Main.getImageResource("/textures/tiles/wall05.png"),
			Main.getImageResource("/textures/tiles/wall06.png"),
			Main.getImageResource("/textures/tiles/wall07.png")}),
		TREE(false, Main.getImageResource("/textures/tiles/tree00.png")),
		FOUNDATION(false, Main.getImageResource("/textures/tiles/default00.png")),
		HUT(false, Main.getImageResource("/textures/tiles/default00.png")),
		WATER(false, true, new BufferedImage[] {
			Main.getImageResource("/textures/tiles/water00.png"),
			Main.getImageResource("/textures/tiles/water01.png")}),
		LAVA(false, true, new BufferedImage[] {
			Main.getImageResource("/textures/tiles/lava00.png"),
			Main.getImageResource("/textures/tiles/lava01.png"),
			Main.getImageResource("/textures/tiles/lava02.png")}),
		GRASS(true, new BufferedImage[] {
			Main.getImageResource("/textures/tiles/grass00.png"),
			Main.getImageResource("/textures/tiles/grass01.png")}),
		VOID(false, Main.getImageResource("/textures/tiles/void00.png")),
		COLUMN(false, Main.getImageResource("/textures/tiles/column00.png"));
		
		private boolean	pathable, canShootOver;
		private BufferedImage[]	textures;
									
		TileType() {
			this(true);
		}
		TileType(boolean pathable) {
			this(pathable, new BufferedImage[] {Main.getImageResource("/textures/tiles/default00.png")});
		}
		
		TileType(boolean pathable, BufferedImage texture) {
			this(pathable, new BufferedImage[] {texture});
		}
		
		TileType(boolean pathable, BufferedImage[] textures) {
			this.pathable = pathable;
			this.textures = textures;
			canShootOver = pathable;
		}
		
		TileType(boolean pathable, boolean swimmable, BufferedImage[] textures) {
			this(pathable, textures);
			canShootOver = swimmable;
		}
		
		public boolean isLiquid() {
			return !pathable && canShootOver;
		}
		
		public boolean isChangable() {
			return pathable || canShootOver;
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
			return canShootOver; //TODO: maybe change this to store more info
		}
		
		public BufferedImage[] getTextures() {
			return textures;
		}
		
		public BufferedImage getTexture(int index) {
			if (index >= textures.length) {
				Main.error("texture not found, index out of bounds, returning default texture");
				return Main.getImageResource("/textures/tiles/default00.png");
			}
			return textures[index];
		}
		
		public int getNumberStyles() {
			return textures.length;
		}
		
		//TODO move this to an interface Named
		@Override
		public String toString() {
			return name().toLowerCase();
		}
		
		@Override
		public Object get(World world, AbstractSemantics attr) {
			
			if (attr == AbstractSemantics.FLOOD && isLiquid()) {
				return new Tile(this);
			}
			
			if (attr == AbstractSemantics.STORM && this == SAND) {
				return new Tile(this, 3);
			}
			
			return Nominal.super.get(world, attr);
			
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
	
	//figure out if we can stand on this block at the very beginning
	public void computeSurroundingsPathable(int x, int y, Level level) {
		
		surroundingsPathable = true;
			
		for (int i = (x * Tile.WIDTH - Unit.RADIUS_X) / Tile.WIDTH; i <= (x * Tile.WIDTH + Unit.RADIUS_X) / Tile.WIDTH; i++) {
			for (int j = (y * Tile.WIDTH - Unit.RADIUS_Y) / Tile.WIDTH; j <= (y * Tile.WIDTH + Unit.RADIUS_Y) / Tile.WIDTH; j++) {
				
				Tile t = level.getTileGrid(i, j);
				if (t == null || !t.isPathable()) {
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
	 */
	@Override
	public int compareTo(Tile o) {
		if (type == o.type) {
			return Integer.compare(style, o.style);
		}
		return type.compareTo(o.type);
	}
	
	public void setBitMasks(Mask[] bitMasks) {
		this.bitMasks = bitMasks;
	}
	
}
