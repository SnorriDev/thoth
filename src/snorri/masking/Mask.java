package snorri.masking;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import snorri.main.Util;
import snorri.world.Tile;
import snorri.world.Vector;

public class Mask implements Comparable<Mask>, Comparator<Mask> {
	
	//TODO can rework this so each tile stores one BufferedImage, which
	//gets changed with setImage
	
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
	//private double order; //low order masks go on top of high ones
	
	public Mask(Tile tile, boolean corner) {
		this.tile = tile;
		this.bitmask = (short) (corner ? 16 : 0);
		this.corner = corner;
	}
	
	private void calculateTexture() {
		texture = AlphaMask.getMask(bitmask).getMasked(tile.getBaseTexture());
	}
	
	public BufferedImage getTexture() {
		if (texture == null) {
			calculateTexture();
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

	public static List<Vector> getNeighbors(Vector pos) {
		List<Vector> out = new ArrayList<>();
		for (Vector v : NEIGHBORS) {
			out.add(pos.copy().add(v));
		}
		return out;
	}

	@Override
	public int compareTo(Mask m) {
		return -1 * tile.compareTo(m.tile);
	}

	@Override
	public int compare(Mask m1, Mask m2) {
		return m1.compareTo(m2);
	}
	
	public final Tile getTile()  {
		return tile;
	}

//	/**
//	 * @return the order
//	 */
//	public double getOrder() {
//		return order;
//	}
//
//	@Deprecated
//	/**
//	 * @param order the order to set
//	 */
//	public void setOrder(double order) {
//		this.order = order;
//	}

}
