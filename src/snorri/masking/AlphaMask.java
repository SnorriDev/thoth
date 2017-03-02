package snorri.masking;

import java.awt.image.BufferedImage;

import snorri.main.Main;
import snorri.main.Util;
import snorri.world.Tile;

public class AlphaMask {
	
	private static final BufferedImage FULL_IMAGE = Main.getImage("/textures/alphaMasks.png");
	private static final AlphaMask[] MASKS = new AlphaMask[32];
	
	static {
		
		for (int i = 0; i < 32; i++) {
			MASKS[i] = new AlphaMask(i % 16, i / 16);
		}		
	}

	private final BufferedImage mask;

	public AlphaMask(int x, int y) {
		//TODO: maybe this is off by a pixel?
		mask = FULL_IMAGE.getSubimage(x * Tile.WIDTH, y * Tile.WIDTH, Tile.WIDTH, Tile.WIDTH);
	}

	public BufferedImage getMasked(BufferedImage i) {
		
		BufferedImage image = Util.deepCopy(i);
		
		final int width = image.getWidth();
		int[] imgData = new int[width];
		int[] maskData = new int[width];

		for (int y = 0; y < image.getHeight(); y++) {
		    // fetch a line of data from each image
		    image.getRGB(0, y, width, 1, imgData, 0, 1);
		    mask.getRGB(0, y, width, 1, maskData, 0, 1);
		    // apply the mask
		    for (int x = 0; x < width; x++) {
		        int color = imgData[x] & 0x00FFFFFF; // mask away any alpha present
		        int maskColor = (0x00FF0000 - (maskData[x] & 0x00FF0000)) << 8; // shift red into alpha bits
		        color |= maskColor;
		        imgData[x] = color;
		    }
		    // replace the data
		    image.setRGB(0, y, width, 1, imgData, 0, 1);
		}
		
		return image;
		
	}

	public static AlphaMask getMask(int i) {
		return MASKS[i];
	}

}
