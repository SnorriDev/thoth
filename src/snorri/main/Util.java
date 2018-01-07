package snorri.main;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.lang.reflect.Array;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.swing.ImageIcon;

import snorri.pathfinding.PathNode;
import snorri.world.Tile;
import snorri.world.Vector;

public class Util {

	public static final double GOLDEN_RATIO = 1.61803398875;

	public static Integer getInteger(String input) {
		try {
			return Integer.parseInt(input);
		} catch (Exception e) {
			return null;
		}
	}

	public static Double getDouble(String input) {
		try {
			return Double.parseDouble(input);
		} catch (Exception e) {
			return null;
		}
	}

	public static String removeExtension(String fileName) {
		return fileName.replaceFirst("[.][^.]+$", "");
	}

	public static BufferedImage deepCopy(BufferedImage bi) {
		ColorModel cm = bi.getColorModel();
		boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
		WritableRaster raster = bi.copyData(null);
		return new BufferedImage(cm, raster, isAlphaPremultiplied, null);
	}

	public static String clean(String constant) {
		return constant.toLowerCase().replace('_', ' ');
	}

	public static String unclean(String raw) {
		return raw.toUpperCase().replace(' ', '_');
	}

	public static Collection<Object> safe(Collection<Object> c) {
		return c == null ? Collections.emptyList() : c;
	}

	/**
	 * Resize a buffered image. Can leave one dimension equal to 0 to autoscale
	 * 
	 * @param image
	 *            the original image
	 * @param newWidth
	 *            the desired height
	 * @param newHeight
	 *            the desired width
	 * @return the resized image
	 */
	public static BufferedImage resize(Image image, int newWidth, int newHeight) {
		
		if (image == null) {
			return null;
		}
		
		if (newWidth <= 0 && newHeight != 0) {
			newWidth = image.getWidth(null) * newHeight / image.getHeight(null);
		}
		if (newHeight <= 0 && newWidth != 0) {
			newHeight = image.getHeight(null) * newWidth / image.getWidth(null);
		}

		Image scaled = image.getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH);
		BufferedImage img = new BufferedImage(scaled.getWidth(null), scaled.getHeight(null),
				BufferedImage.TYPE_INT_ARGB);
		Graphics g = img.getGraphics();
		g.drawImage(scaled, 0, 0, null);
		g.dispose();
		return img;
	}
	
	/**
	 * Resize an ImageIcon. Can leave one dimension equal to 0 to autoscale
	 * 
	 * @paramicon
	 *            the original imageIcon
	 * @param newWidth
	 *            the desired height
	 * @param newHeight
	 *            the desired width
	 * @return the resized imageIcon
	 */
	public static ImageIcon resize(ImageIcon icon, int newWidth, int newHeight) {
		return new ImageIcon(resize(toBufferedImage(icon), newWidth, newHeight));
	}
	
	public static BufferedImage toBufferedImage(ImageIcon icon) {
		return (BufferedImage) (icon.getImage());
	}

	public static <T> T[] concatenate(T[] a, T[] b) {
		int aLen = a.length;
		int bLen = b.length;

		@SuppressWarnings("unchecked")
		T[] c = (T[]) Array.newInstance(a.getClass().getComponentType(), aLen + bLen);
		System.arraycopy(a, 0, c, 0, aLen);
		System.arraycopy(b, 0, c, aLen, bLen);

		return c;
	}

	public static <T> T random(Collection<T> coll) {
		int num = (int) (Math.random() * coll.size());
		for (T t : coll) {
			if (--num < 0) {
				return t;
			}
		}
		throw new AssertionError();
	}

	public static int niceMod(int n, int m) {
		return (((n % m) + m) % m);
	}

	public static <T> List<List<T>> computeCombinations(List<List<T>> lists) {
		List<List<T>> combinations = Arrays.asList(Arrays.asList());
		for (List<T> list : lists) {
			List<List<T>> extraColumnCombinations = new ArrayList<>();
			for (List<T> combination : combinations) {
				for (T element : list) {
					List<T> newCombination = new ArrayList<>(combination);
					newCombination.add(element);
					extraColumnCombinations.add(newCombination);
				}
			}
			combinations = extraColumnCombinations;
		}
		return combinations;
	}
	
	/**
	 * Get a copy of the input <code>BufferedImage</code> flipped about either (or both) axes
	 * @param image
	 * 	The image to flip
	 * @param xFlip
	 * 	Whether the image should be flipped about the x axis
	 * @param yFlip
	 * 	Whether the image should be flipped about the y axis
	 * @return
	 * 	The flipped image
	 */
	public static BufferedImage getFlipped(BufferedImage image, boolean flipX, boolean flipY) {
		AffineTransform tx = AffineTransform.getScaleInstance(flipX ? -1 : 1, flipY ? -1 : 1);
		if (flipX) {
			tx.translate(-image.getWidth(null), 0);
		}
		if (flipY) {
			tx.translate(0, -image.getHeight(null));
		}
		AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
		return op.filter(image, null);
	}
	
	/**
	 * Rotate an image to point in <code>dir</code>
	 * @param image
	 * 	The image to rotate
	 * @param dir
	 * 	The direction to point in
	 * @return
	 * 	The rotated image
	 */
	public static BufferedImage getRotated(BufferedImage image, Vector dir) {
		double theta = dir.getStandardAngle();
		return getRotated(image, theta);
	}
	
	/**
	 * Rotate an image by an angle
	 * @param image
	 * 	The image to rotate
	 * @param angle
	 * 	The angle by which to rotate
	 * @return
	 * 	The rotated image
	 */
	public static BufferedImage getRotated(BufferedImage image, double theta) {
		
		double midX = image.getWidth() / 2, midY = image.getHeight() / 2;
		BufferedImage copy = new BufferedImage(image.getWidth(), image.getHeight(), image.getType());
				
		Graphics2D g = copy.createGraphics();
		g.rotate(theta, midX, midY);
		g.drawRenderedImage(image, null);
		g.dispose();
		
		return copy;
		
	}

	public static BufferedImage getBufferedImage(Image img) {
		if (img instanceof BufferedImage) {
			return (BufferedImage) img;
		}

		BufferedImage bimage = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_ARGB);

		Graphics2D bGr = bimage.createGraphics();
		bGr.drawImage(img, 0, 0, null);
		bGr.dispose();

		return bimage;
	}
	
	/** Render a path through the pathfinding graph in the game window. Does not modify <code>path</code>
	 * @param g game window
	 * @param gr graphics object
	 * @param path the stack of path nodes to draw
	 */
	public static void drawPath(GameWindow g, Graphics gr, ArrayDeque<PathNode> path) {
		Vector p1 = path.getFirst().getGlobalPos();
		for (PathNode n : path) {
			Vector p2 = n.getGlobalPos();
			Vector player = g.getFocus().getPos().copy().sub(g.getCenter().copy().add(Tile.WIDTH / 2, Tile.WIDTH / 2));
			gr.drawLine(p1.getX() - player.getX(), p1.getY() - player.getY(), p2.getX() - player.getX(), p2.getY() - player.getY());
			p1 = p2;
		}
	}

	public static String toTitleCase(String input) {
		StringBuilder titleCase = new StringBuilder();
		boolean nextTitleCase = true;

		for (char c : input.toCharArray()) {
			if (Character.isSpaceChar(c)) {
				nextTitleCase = true;
			} else if (nextTitleCase) {
				c = Character.toTitleCase(c);
				nextTitleCase = false;
			} else {
				c = Character.toLowerCase(c);
			}

			titleCase.append(c);
		}

		return titleCase.toString();
	}
	
	/**
	 * @param p the probability of success
	 * @return true with probability p
	 */
	public static boolean flip(double p) {
		return Math.random() <= p;
	}

}
