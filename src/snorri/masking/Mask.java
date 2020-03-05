package snorri.masking;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.TexturePaint;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Path2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import snorri.util.Util;
import snorri.world.Tile;
import snorri.world.TileType;
import snorri.world.Vector;

public class Mask implements Comparable<Mask>, Comparator<Mask> {
	
	// TODO(#49): Remove all of this masking logic.
	// This class is not fully deprecated. Weirdly, it has some good stuff, like connectivity function.

	public static final Vector[] NEIGHBORS;
	public static final Vector[] CORNERS;
	public static final Vector[] NEIGHBORS_AND_CORNERS;

	public static class NegativeBitmaskException extends RuntimeException {

		private static final long serialVersionUID = 1L;

		public NegativeBitmaskException(String msg) {
			super(msg);
		}

	}

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
	private byte bitmask;

	public Mask(Tile tile, boolean corner) {
		this.tile = tile;
		this.bitmask = (byte) (corner ? 16 : 0);
		this.corner = corner;
		if (bitmask < 0) {
			throw new NegativeBitmaskException("cannot initialize negative bitmask");
		}
	}
	
	public AlphaMask getAlphaMask() {
		return AlphaMask.getMask(bitmask);
	}

	public Area getArea() {
		return getAlphaMask().getArea();
	}
	
	public Path2D getBorder() {
		return getAlphaMask().getBorder();
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof Mask) {
			return hasTile(((Mask) o).tile) && isCorner() == ((Mask) o).isCorner()
					&& this.getBitVal() == ((Mask) o).getBitVal();
		}
		return false;
	}

	public boolean hasTile(Tile t) {
		return tile.equals(t);
	}

	public boolean isCorner() {
		return corner;
	}

	public void add(byte value) {
		bitmask += value;
	}

	public void sub(byte value) {
		bitmask -= value;
		if (bitmask < 0) {
			throw new NegativeBitmaskException("invalid subtraction: " + (bitmask + value) + " - " + value + " < 0");
		}
	}

	public static List<Vector> getNeighbors(Vector pos) {
		// TODO(lambdaviking): This method is actually useful.
		List<Vector> out = new ArrayList<>();
		for (Vector v : NEIGHBORS) {
			out.add(pos.copy().add_(v));
		}
		return out;
	}

	@Override
	public int compareTo(Mask m) {
		return m.tile.compareTo(tile);
	}

	@Override
	public int compare(Mask m1, Mask m2) {
		return m1.compareTo(m2);
	}

	public final Tile getTile() {
		return tile;
	}

	public final TileType getType() {
		return tile.getType();
	}

	/**
	 * @return the texture that should be drawn in this masked region
	 */
	public BufferedImage getBaseTexture() {
		return getTile().getBaseTexture();
	}

	public byte getBitVal() {
		return bitmask;
	}

	public void drawMask(Graphics2D gr, Vector pos) {
		Area area = getArea().createTransformedArea(AffineTransform.getTranslateInstance(pos.getX(), pos.getY()));
		BufferedImage tex = getBaseTexture();
		gr.setPaint(new TexturePaint(tex, new Rectangle(0, 0, tex.getWidth(), tex.getHeight())));
		gr.fill(area);
		gr.draw(getBorder());
		gr.setPaint(null);
	}

	/**
	 * Use this method to get the neighbor directly across from a corner or
	 * adjacent
	 * 
	 * @param i
	 *            an corner or adjacent index in the set {0, 1, 2, 3}
	 * @return the complement of i as an index in the set {0, 1, 2, 3}
	 */
	public static int getComplement(int i) {
		return (i + 2) % 4;
	}

	/**
	 * The value of some neighbor
	 * 
	 * @param i
	 *            the corner or adjacent index in the set {0, 1, 2, 3}
	 * @return a byte representing the presence of neighbor in a mask
	 */
	public static byte getNeighborValue(int i) {
		return (byte) (1 << i);
	}

}
