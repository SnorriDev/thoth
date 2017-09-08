package snorri.masking;

import java.awt.Color;
import java.awt.Rectangle;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;

import snorri.main.Debug;
import snorri.main.Main;
import snorri.world.Tile;

public class AlphaMask {

	private static final int STRIP_WIDTH = 16;
	private static final int CIRCLE_DIAMETER = 2 * (Tile.WIDTH - STRIP_WIDTH);
	private static final int MINOR_DIAMETER = Tile.WIDTH - 2 * STRIP_WIDTH;
	private static final int MAJOR_DIAMETER = Tile.WIDTH;

	private static final BufferedImage FULL_IMAGE = Main.getImage("/textures/alphaMasks.png");
	private static final AlphaMask[] MASKS;
	private static final AlphaMask[] CORNER_MASKS;

	static {
		// for (int i = 0; i < 32; i++) {
		// MASKS[i] = new AlphaMask(i % 16, i / 16);
		// }
		
		MASKS = new AlphaMask[] {
			new AlphaMask(new Area()), new AlphaMask(getRectArea(0, 0, false)),
			new AlphaMask(getRectArea(0, 0, true)),
			new AlphaMask(diff(getTileArea(), getCircleArea(Tile.WIDTH, Tile.WIDTH))),
			new AlphaMask(new Area(new Rectangle(Tile.WIDTH - STRIP_WIDTH, 0, STRIP_WIDTH, Tile.WIDTH))),
			new AlphaMask(sum(getRectArea(0, 0, false), getRectArea(Tile.WIDTH - STRIP_WIDTH, 0, false))),
			new AlphaMask(diff(getTileArea(), getCircleArea(0, Tile.WIDTH))),
			new AlphaMask(diff(getTileArea(), getEllipseArea(Tile.WIDTH / 2, Tile.WIDTH, false))),
			new AlphaMask(getRectArea(0, Tile.WIDTH - STRIP_WIDTH, true)),
			new AlphaMask(diff(getTileArea(), getCircleArea(Tile.WIDTH, 0))),
			new AlphaMask(sum(getRectArea(0, 0, true), getRectArea(0, Tile.WIDTH - STRIP_WIDTH, true))),
			new AlphaMask(diff(getTileArea(), getEllipseArea(Tile.WIDTH, Tile.WIDTH / 2, true))),
			new AlphaMask(diff(getTileArea(), getCircleArea(0, 0))),
			new AlphaMask(diff(getTileArea(), getEllipseArea(Tile.WIDTH / 2, 0, false))),
			new AlphaMask(diff(getTileArea(), getEllipseArea(0, Tile.WIDTH / 2, true))),
			new AlphaMask(diff(getTileArea(), getCenterArea()))
		};
		
		CORNER_MASKS = new AlphaMask[16];
		for (int i = 0; i < 16; i++) {
			int b1 = i >> 3 & 0x01, b2 = i >> 2 & 0x01, b3 = i >> 1 & 0x01, b4 = i & 0x01;
			CORNER_MASKS[i] = new AlphaMask(getCorners(b1, b2, b3, b4));
		}
		
		assert MASKS.length == 16;
		assert CORNER_MASKS.length == 16;
		Debug.log("alpha masks initialized");
	}

	private final Area mask;

	@Deprecated
	public AlphaMask(int x, int y) {
		// TODO: maybe this is off by a pixel?

		mask = new Area();
		BufferedImage subimage = FULL_IMAGE.getSubimage(x * Tile.WIDTH, y * Tile.WIDTH, Tile.WIDTH, Tile.WIDTH);
		for (int xi = 0; xi < subimage.getWidth(); xi++) {
			for (int yi = 0; yi < subimage.getHeight(); yi++) {
				Color color = new Color(subimage.getRGB(xi, yi));
				if (isBlack(color)) {
					mask.add(new Area(new Rectangle(xi, yi, 1, 1)));
				}
			}
		}

	}

	public AlphaMask(Area area) {
		mask = area;
	}

	// public BufferedImage getMasked(BufferedImage i) {
	//
	// if (i == null) {
	// Debug.raw("got null");
	// }
	//
	// BufferedImage image = Util.deepCopy(i);
	//
	// final int width = image.getWidth();
	// int[] imgData = new int[width];
	// int[] maskData = new int[width];
	//
	// for (int y = 0; y < image.getHeight(); y++) {
	// // fetch a line of data from each image
	// image.getRGB(0, y, width, 1, imgData, 0, 1);
	// image.getR
	// mask.contains(p))
	// mask.getRGB(0, y, width, 1, maskData, 0, 1);
	// // apply the mask
	// for (int x = 0; x < width; x++) {
	// int color = imgData[x] & 0x00FFFFFF; // mask away any alpha present
	// int maskColor = (0x00FF0000 - (maskData[x] & 0x00FF0000)) << 8; // shift
	// red into alpha bits
	// color |= maskColor;
	// imgData[x] = color;
	// }
	// // replace the data
	// image.setRGB(0, y, width, 1, imgData, 0, 1);
	// }
	//
	// return image;
	//
	// }

	public static AlphaMask getMask(int i) {
		if (i < 16) {
			return MASKS[i];
		}
		return CORNER_MASKS[i - 16];
	}

	/**
	 * This method uses the red value for the color because it is assumed that
	 * everything is greyscale.
	 * 
	 * @param color
	 *            a color.
	 * @return true iff the color is dark.
	 */
	private static boolean isBlack(Color color) {
		return color.getRed() < 128;
	}

	public Area getArea() {
		return mask;
	}

	private static Area getTileArea() {
		return new Area(new Rectangle(0, 0, Tile.WIDTH, Tile.WIDTH));
	}

	private static Area getCircleArea(int cx, int cy) {
		return new Area(new Ellipse2D.Double(cx - CIRCLE_DIAMETER / 2, cy - CIRCLE_DIAMETER / 2, CIRCLE_DIAMETER, CIRCLE_DIAMETER));
	}

	private static Area getEllipseArea(int cx, int cy, boolean xMajor) {
		int width = xMajor ? MAJOR_DIAMETER : MINOR_DIAMETER;
		int height = xMajor ? MINOR_DIAMETER : MAJOR_DIAMETER;
		return new Area(new Ellipse2D.Double(cx - width / 2, cy - height / 2, width, height));
	}

	private static Area getRectArea(int x, int y, boolean xMajor) {
		return new Area(new Rectangle(x, y, xMajor ? Tile.WIDTH : STRIP_WIDTH, xMajor ? STRIP_WIDTH : Tile.WIDTH));
	}

	private static Area diff(Area a1, Area a2) {
		a1.subtract(a2);
		return a1;
	}

	private static Area sum(Area a1, Area a2) {
		a1.add(a2);
		return a1;
	}
	
	private static Area getCenterArea() {	
		return new Area(new Ellipse2D.Double(Tile.WIDTH / 2 - MINOR_DIAMETER / 2, Tile.WIDTH / 2 - MINOR_DIAMETER / 2, MINOR_DIAMETER, MINOR_DIAMETER));
	}
	
	private static Area getCornerArea(int x, int y) {
		return new Area(new Ellipse2D.Double(x - STRIP_WIDTH, y - STRIP_WIDTH, 2 * STRIP_WIDTH, 2 * STRIP_WIDTH));
	}
	
	private static Area getCorners(int b1, int b2, int b3, int b4) {
		Area area = new Area();
		if (b1 == 1) {
			area.add(getCornerArea(0, Tile.WIDTH));
		}
		if (b2 == 1) {
			area.add(getCornerArea(Tile.WIDTH, Tile.WIDTH));
		}
		if (b3 == 1) {
			area.add(getCornerArea(Tile.WIDTH, 0));
		}
		if (b4 == 1) {
			area.add(getCornerArea(0, 0));
		}
		return area;
	}

}
