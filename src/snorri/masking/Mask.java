package snorri.masking;

import java.awt.image.BufferedImage;

import snorri.main.Util;
import snorri.world.Tile;
import snorri.world.Vector;

public class Mask {
	
	public static final Vector[] NEIGHBORS;
	public static final Vector[] CORNERS;
	public static final Vector[] NEIGHBORS_AND_CORNERS;
	
	static {
		
		NEIGHBORS = new Vector[] {
				new Vector(-1, 0),
				new Vector(0, -1),
				new Vector(1, 0),
				new Vector(0, 1)
		};
		
		CORNERS = new Vector[] {
				new Vector(-1, -1),
				new Vector(1, -1),
				new Vector(1, 1),
				new Vector(-1, 1)
		};
		
		NEIGHBORS_AND_CORNERS = Util.concatenate(NEIGHBORS, CORNERS);
		
	}
	
	private final Tile tile;
	private boolean corner;
	private BufferedImage texture;
	private short bitmask;
	
	public Mask(Tile tile, boolean corner) {
		this.tile = tile;
		this.bitmask = (short) (corner ? 16 : 0);
		this.corner = corner;
	}
	
	private void setTexture() {
		texture = AlphaMask.getMask(bitmask).getMasked(tile.getTexture());
	}
	
	public BufferedImage getTexture() {
		if (texture == null) {
			setTexture();
		}
		return texture;
	}
	
	@Override
	public boolean equals(Object o) {
		if (o instanceof Mask) {
			return hasTile(((Mask) o).tile) && isCorner() == ((Mask) o).isCorner();
		}
		return false;
	}
	
	public boolean hasTile(Tile t) {
		return tile.equals(t);
	}
	
	public boolean isCorner() {
		return corner;
	}
	
	public void add(int value) {
		bitmask += value;
	}

}
