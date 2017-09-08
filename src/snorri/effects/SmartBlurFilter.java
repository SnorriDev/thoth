package snorri.effects;

import java.awt.image.Kernel;
import java.awt.image.BufferedImage;
import java.awt.image.ConvolveOp;
import java.awt.Graphics;

/**
 * Blur filter code taken from the internet
 * @author Kas Thomas
 * @see http://asserttrue.blogspot.com/2010/08/implementing-smart-blur-in-java.html
 */

public class SmartBlurFilter {

	private static final double SENSITIVITY = 10;
	private static final int REGION_SIZE = 5;

	float[] kernelArray = {
		1, 1, 1, 1, 1, 1, 1, 1, 1, 
		1, 1, 1, 1, 1, 1, 1, 1, 1, 
		1, 1, 1, 1, 1, 1, 1, 1, 1, 
		1, 1, 1, 1, 1, 1, 1, 1, 1, 
		1, 1, 1, 1, 1, 1, 1, 1, 1, 
		1, 1, 1, 1, 1, 1, 1, 1, 1, 
		1, 1, 1, 1, 1, 1, 1, 1, 1, 
		1, 1, 1, 1, 1, 1, 1, 1, 1, 
		1, 1, 1, 1, 1, 1, 1, 1, 1
	};

	Kernel kernel = new Kernel(9, 9, normalizeKernel(kernelArray));

	float[] normalizeKernel(float[] ar) {
		int n = 0;
		for (int i = 0; i < ar.length; i++)
			n += ar[i];
		for (int i = 0; i < ar.length; i++)
			ar[i] /= n;

		return ar;
	}

	public double lerp(double a, double b, double amt) {
		return a + amt * (b - a);
	}

	public double getLerpAmount(double a, double cutoff) {

		if (a > cutoff)
			return 1.0;

		return a / cutoff;
	}

	public double rmsError(int[] pixels) {

		double ave = 0;

		for (int i = 0; i < pixels.length; i++)
			ave += (pixels[i] >> 8) & 255;

		ave /= pixels.length;

		double diff = 0;
		double accumulator = 0;

		for (int i = 0; i < pixels.length; i++) {
			diff = ((pixels[i] >> 8) & 255) - ave;
			diff *= diff;
			accumulator += diff;
		}

		double rms = accumulator / pixels.length;

		rms = Math.sqrt(rms);

		return rms;
	}

	int[] getSample(BufferedImage image, int x, int y, int size) {

		int[] pixels = {};

		try {
			BufferedImage subimage = image.getSubimage(x, y, size, size);
			pixels = subimage.getRGB(0, 0, size, size, null, 0, size);
		} catch (Exception e) {
			// will arrive here if we requested
			// pixels outside the image bounds
		}
		return pixels;
	}

	int lerpPixel(int oldpixel, int newpixel, double amt) {

		int oldRed = (oldpixel >> 16) & 255;
		int newRed = (newpixel >> 16) & 255;
		int red = (int) lerp((double) oldRed, (double) newRed, amt) & 255;

		int oldGreen = (oldpixel >> 8) & 255;
		int newGreen = (newpixel >> 8) & 255;
		int green = (int) lerp((double) oldGreen, (double) newGreen, amt) & 255;

		int oldBlue = oldpixel & 255;
		int newBlue = newpixel & 255;
		int blue = (int) lerp((double) oldBlue, (double) newBlue, amt) & 255;

		return (red << 16) | (green << 8) | blue;
	}

	int[] blurImage(BufferedImage image, int[] orig, int[] blur, double sensitivity) {

		int newPixel = 0;
		double amt = 0;
		int size = REGION_SIZE;

		for (int i = 0; i < orig.length; i++) {
			int w = image.getWidth();
			int[] pix = getSample(image, i % w, i / w, size);
			if (pix.length == 0)
				continue;

			amt = getLerpAmount(rmsError(pix), sensitivity);
			newPixel = lerpPixel(blur[i], orig[i], amt);
			orig[i] = newPixel;
		}

		return orig;
	}

	public BufferedImage filter(BufferedImage image) {

		ConvolveOp convolver = new ConvolveOp(kernel, ConvolveOp.EDGE_NO_OP, null);

		// clone image into target
		BufferedImage target = new BufferedImage(image.getWidth(), image.getHeight(), image.getType());
		Graphics g = target.createGraphics();
		g.drawImage(image, 0, 0, null);
		g.dispose();

		int w = target.getWidth();
		int h = target.getHeight();

		// get source pixels
		int[] pixels = image.getRGB(0, 0, w, h, null, 0, w);

		// blur the cloned image
		target = convolver.filter(target, image);

		// get the blurred pixels
		int[] blurryPixels = target.getRGB(0, 0, w, h, null, 0, w);

		// go thru the image and interpolate values
		pixels = blurImage(image, pixels, blurryPixels, SENSITIVITY);

		// replace original pixels with new ones
		image.setRGB(0, 0, w, h, pixels, 0, w);
		return image;
	}
}