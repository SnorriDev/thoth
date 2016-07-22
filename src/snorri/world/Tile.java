package snorri.world;

import java.awt.Graphics;
import java.awt.Image;
import java.util.ArrayList;

import snorri.entities.Unit;
import snorri.main.FocusedWindow;
import snorri.main.Main;
import snorri.semantics.Nominal;

public class Tile {
	
	public static final int	WIDTH	= 16;
									
	private TileType		type;
	private int				style;
	private boolean surroundingsPathable = true;
	private boolean reachable;
	
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
		gr.drawImage(type.getTexture(style), relPos.getX(), (int)relPos.getY(), g);
		return;
	}
	
	public Image getTexture() {
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
												
		SAND(true, new Image[] {
			Main.getImageResource("/textures/tiles/sand00.png"),
			Main.getImageResource("/textures/tiles/sand01.png"),
			Main.getImageResource("/textures/tiles/sand02.png"),
			Main.getImageResource("/textures/tiles/sand03.png")}),
		WALL(false, new Image[] {
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
		WATER(false, true, new Image[] {
			Main.getImageResource("/textures/tiles/water00.png"),
			Main.getImageResource("/textures/tiles/water01.png")}),
		LAVA(false, true, new Image[] {
			Main.getImageResource("/textures/tiles/lava00.png"),
			Main.getImageResource("/textures/tiles/lava01.png"),
			Main.getImageResource("/textures/tiles/lava02.png")}),
		GRASS(true, new Image[] {
			Main.getImageResource("/textures/tiles/grass00.png"),
			Main.getImageResource("/textures/tiles/grass01.png")}),
		VOID(false, Main.getImageResource("/textures/tiles/void00.png")),
		COLUMN(false, Main.getImageResource("/textures/tiles/column00.png"));
		
		private boolean	pathable, canShootOver;
		private Image[]	textures;
									
		TileType() {
			this(true);
		}
		TileType(boolean pathable) {
			this(pathable, new Image[] {Main.getImageResource("/textures/tiles/default00.png")});
		}
		
		TileType(boolean pathable, Image texture) {
			this(pathable, new Image[] {texture});
		}
		
		TileType(boolean pathable, Image[] textures) {
			this.pathable = pathable;
			this.textures = textures;
			canShootOver = pathable;
		}
		
		TileType(boolean pathable, boolean swimmable, Image[] textures) {
			this(pathable, textures);
			canShootOver = swimmable;
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
		
		public Image[] getTextures() {
			return textures;
		}
		
		public Image getTexture(int index) {
			if (index >= textures.length) {
				Main.error("texture not found, index out of bounds, returning default texture");
				return Main.getImageResource("/textures/tiles/default00.png");
			}
			return textures[index];
		}
		
		public int getNumberStyles() {
			return textures.length;
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
		
		for (int i = (x * Tile.WIDTH - Unit.RADIUS) / Tile.WIDTH; i <= (x * Tile.WIDTH + Unit.RADIUS) / Tile.WIDTH; i++) {
			for (int j = (y * Tile.WIDTH - Unit.RADIUS) / Tile.WIDTH; j <= (y * Tile.WIDTH + Unit.RADIUS) / Tile.WIDTH; j++) {
				
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
	
}
