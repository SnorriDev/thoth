package snorri.world;

import java.awt.Image;

import snorri.main.Main;
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
												
												SAND(
														true,
														new Image[] {	Main.getImageResource("/textures/tiles/sand00.png"),
																		Main.getImageResource("/textures/tiles/sand01.png"),
																		Main.getImageResource("/textures/tiles/sand02.png") }),
												WALL(
														false,
														new Image[] {	Main.getImageResource("/textures/tiles/wall00.png"),
																		Main.getImageResource("/textures/tiles/wall01.png"),
																		Main.getImageResource("/textures/tiles/wall02.png"),
																		Main.getImageResource("/textures/tiles/wall03.png"),
																		Main.getImageResource("/textures/tiles/wall04.png") }),
												TREE(
														false,
														new Image[] {	Main.getImageResource("/textures/tiles/tree00.png"),
																		Main.getImageResource("/textures/tiles/tree01.png") }),
												FOUNDATION(false, new Image[] { Main.getImageResource("/textures/tiles/column00.png") }),
												HUT(false, new Image[] { Main.getImageResource("/textures/tiles/default00.png") }),
												WATER(
														false,
														new Image[] {	Main.getImageResource("/textures/tiles/water00.png"),
																		Main.getImageResource("/textures/tiles/water01.png") }),
												LAVA(
														false,
														new Image[] {	Main.getImageResource("/textures/tiles/lava00.png"),
																		Main.getImageResource("/textures/tiles/lava01.png"),
																		Main.getImageResource("/textures/tiles/lava02.png") });
																		
		//TODO: pass an array of textures for each one
		
		private boolean	pathable;
		private Image[]	textures;
						
		TileType() {
			pathable = true;
			textures = new Image[] { Main.getImageResource("/textures/tiles/default00.png") };
		}
		
		TileType(boolean pathable, Image[] textures) {
			this.pathable = pathable;
			this.textures = textures;
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
		
	}
	
}
