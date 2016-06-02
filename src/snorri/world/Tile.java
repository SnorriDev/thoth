package snorri.world;

import snorri.semantics.Nominal;



public class Tile {
	
	public static final int	WIDTH	= 16;
									
	private TileType		type;
	private int				style	= 0;
									
	public Tile(TileType type) {
		this.type = type;
	}
	
	public Tile(TileType type, int style) {
		this(type);
		this.style = style;
	}
	
	public Tile(int id) {
		this(TileType.byId(id));
	}
	
	public Tile(int id, int style) {
		this(id);
		this.style = style;
	}
	
	public TileType getType() {
		return type;
	}
	
	public int getStyle() {
		return style;
	}
	
	public enum TileType implements Nominal {
		
		SAND(true), WALL(	false,
							50), TREE(	false,
										30), FOUNDATION(false,
														Integer.MAX_VALUE), HUT(false,
																				30), WATER(	false,
																							Integer.MAX_VALUE), LAVA(	false,
																														Integer.MAX_VALUE);
																														
		//TODO: pass an array of textures for each one
		
		private boolean	pathable;
		private int		strength	= 5;
									
		TileType(boolean pathable) {
			this.pathable = pathable;
		}
		
		TileType(boolean pathable, int strength) {
			this.pathable = pathable;
			this.strength = strength;
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
		
		public int getStrength() {
			return strength;
		}
		
	}
	
}
