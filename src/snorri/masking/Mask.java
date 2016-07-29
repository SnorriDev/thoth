package snorri.masking;

import java.awt.image.BufferedImage;

import snorri.main.Util;
import snorri.world.Vector;

public class Mask {
	
	public static final Vector[] NEIGHBORS = new Vector[] {
			new Vector(-1, 0),
			new Vector(0, -1),
			new Vector(1, 0),
			new Vector(0, 1)
	};
	
	private final BufferedImage texture;
	private int bitmask;
	
	public Mask(BufferedImage texture, int bitmask) {
		this.texture = Util.deepCopy(texture);
		this.bitmask = bitmask;
	}
	
	public BufferedImage getTexture() {
		return AlphaMask.getMask(bitmask).getMasked(texture);
	}
	
	@Override
	public boolean equals(Object o) {
		if (o instanceof Mask) {
			return hasTexture(((Mask) o).texture);
		}
		return false;
	}
	
	public boolean hasTexture(BufferedImage t) {
		return texture.equals(t);
	}
	
	public void add(int value) {
		bitmask += value;
	}

}
