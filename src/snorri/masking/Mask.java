package snorri.masking;

import java.awt.image.BufferedImage;

import snorri.world.Tile;
import snorri.world.Vector;

public class Mask {
	
	public static final Vector[] NEIGHBORS = new Vector[] {
			new Vector(-1, 0),
			new Vector(0, -1),
			new Vector(1, 0),
			new Vector(0, 1)
	};
	
	private final Tile tile;
	private BufferedImage texture;
	private int bitmask;
	
	public Mask(Tile tile) {
		this.tile = tile;
		bitmask = 0;
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
			return hasTile(((Mask) o).tile);
		}
		return false;
	}
	
	//is this the issue?
	public boolean hasTile(Tile t) {
		return tile.equals(t);
	}
	
	public void add(int value) {
		bitmask += value;
	}

}
