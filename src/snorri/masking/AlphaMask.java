package snorri.masking;

import java.awt.Rectangle;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;

import snorri.main.Debug;
import snorri.world.Tile;

public class AlphaMask {

	private static final int STRIP_WIDTH = 16;
	private static final int CIRCLE_DIAMETER = 2 * (Tile.WIDTH - STRIP_WIDTH);
	private static final int MINOR_DIAMETER = Tile.WIDTH - 2 * STRIP_WIDTH;
	private static final int MAJOR_DIAMETER = Tile.WIDTH;

	private static final AlphaMask[] MASKS;
	private static final AlphaMask[] CORNER_MASKS;

	static {
		
		MASKS = new AlphaMask[] {
			new AlphaMask(),
			getRectMask(0, 0, false),
			getRectMask(0, 0, true),
			diff(getTileMask(), getCircleMask(Tile.WIDTH, Tile.WIDTH)),
			getRectMask(Tile.WIDTH - STRIP_WIDTH, 0, false),
			union(getRectMask(0, 0, false), getRectMask(Tile.WIDTH - STRIP_WIDTH, 0, false)),
			diff(getTileMask(), getCircleMask(0, Tile.WIDTH)),
			diff(getTileMask(), getEllipseMask(Tile.WIDTH / 2, Tile.WIDTH, false)),
			getRectMask(0, Tile.WIDTH - STRIP_WIDTH, true),
			diff(getTileMask(), getCircleMask(Tile.WIDTH, 0)),
			union(getRectMask(0, 0, true), getRectMask(0, Tile.WIDTH - STRIP_WIDTH, true)),
			diff(getTileMask(), getEllipseMask(Tile.WIDTH, Tile.WIDTH / 2, true)),
			diff(getTileMask(), getCircleMask(0, 0)),
			diff(getTileMask(), getEllipseMask(Tile.WIDTH / 2, 0, false)),
			diff(getTileMask(), getEllipseMask(0, Tile.WIDTH / 2, true)),
			diff(getTileMask(), getCenterMask())
		};
		
		CORNER_MASKS = new AlphaMask[16];
		for (int i = 0; i < 16; i++) {
			int b1 = i >> 3 & 0x01, b2 = i >> 2 & 0x01, b3 = i >> 1 & 0x01, b4 = i & 0x01;
			CORNER_MASKS[i] = getCorners(b1, b2, b3, b4);
		}
		
		assert MASKS.length == 16;
		assert CORNER_MASKS.length == 16;
		Debug.log("alpha masks initialized");
	}

	private final Area mask;

	public AlphaMask() {
		this(new Area());
	}
	
	public AlphaMask(Area area) {
		mask = area;
	}

	public static AlphaMask getMask(int i) {
		if (i < 16) {
			return MASKS[i];
		}
		return CORNER_MASKS[i - 16];
	}

	public Area getArea() {
		return mask;
	}

	private static AlphaMask getTileMask() {
		Area a = new Area(new Rectangle(0, 0, Tile.WIDTH, Tile.WIDTH));
		return new AlphaMask(a);
	}

	private static AlphaMask getCircleMask(int cx, int cy) {
		Area a = new Area(new Ellipse2D.Double(cx - CIRCLE_DIAMETER / 2, cy - CIRCLE_DIAMETER / 2, CIRCLE_DIAMETER, CIRCLE_DIAMETER));
		return new AlphaMask(a);
	}

	private static AlphaMask getEllipseMask(int cx, int cy, boolean xMajor) {
		int width = xMajor ? MAJOR_DIAMETER : MINOR_DIAMETER;
		int height = xMajor ? MINOR_DIAMETER : MAJOR_DIAMETER;
		Area a = new Area(new Ellipse2D.Double(cx - width / 2, cy - height / 2, width, height));
		return new AlphaMask(a);
	}

	private static AlphaMask getRectMask(int x, int y, boolean xMajor) {
		Area a = new Area(new Rectangle(x, y, xMajor ? Tile.WIDTH : STRIP_WIDTH, xMajor ? STRIP_WIDTH : Tile.WIDTH));
		return new AlphaMask(a);
	}
	
	/**
	 * Take the difference between two mask regions.
	 * For calculating new borders, this method assumes that m2 \subseteq m1.
	 * @param m1 The mask from which to subtract
	 * @param m2 A mask that is subset-equal-to m1
	 * @return A new mask
	 */
	private static AlphaMask diff(AlphaMask m1, AlphaMask m2) {
		Area newArea = new Area(m1.getArea());
		newArea.subtract(m2.getArea());
		return new AlphaMask(newArea);
	}
	
	private static AlphaMask union(AlphaMask m1, AlphaMask m2) {
		Area newArea = new Area(m1.getArea());
		newArea.add(m2.getArea());
		return new AlphaMask(newArea);
	}
	
	private static AlphaMask getCenterMask() {	
		Area a = new Area(new Ellipse2D.Double(Tile.WIDTH / 2 - MINOR_DIAMETER / 2, Tile.WIDTH / 2 - MINOR_DIAMETER / 2, MINOR_DIAMETER, MINOR_DIAMETER));
		return new AlphaMask(a);
	}
	
	private static AlphaMask getCornerMask(int x, int y) {
		Area a = new Area(new Ellipse2D.Double(x - STRIP_WIDTH, y - STRIP_WIDTH, 2 * STRIP_WIDTH, 2 * STRIP_WIDTH));
		return new AlphaMask(a);
	}
	
	private static AlphaMask getCorners(int b1, int b2, int b3, int b4) {
		AlphaMask mask = new AlphaMask();
		if (b1 == 1) {
			mask = union(mask, getCornerMask(0, Tile.WIDTH));
		}
		if (b2 == 1) {
			mask = union(mask, getCornerMask(Tile.WIDTH, Tile.WIDTH));
		}
		if (b3 == 1) {
			mask = union(mask, getCornerMask(Tile.WIDTH, 0));
		}
		if (b4 == 1) {
			mask = union(mask, getCornerMask(0, 0));
		}
		return mask;
	}

}
