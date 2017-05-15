package snorri.world;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import snorri.audio.ClipWrapper;
import snorri.main.Util;
import snorri.semantics.Nominal;

public interface TileType extends Nominal {
	
	//BufferedImage getImage(String string);
	
	TileType byId(int id);
	
	int getId();
	
	BufferedImage[] getTextures();
	
	BufferedImage getTexture(int index);
	
	int getNumberStyles();
	
	@Override
	String toString();
		
	ArrayList<Tile> getSubTypes();
	
	boolean hasSounds();
	
	int getNumSounds();
	
	ClipWrapper[] getSounds();
	
	public ClipWrapper getSound(int x);
	
	String name();

	boolean isPathable();

	boolean canShootOver();

	boolean isAtTop();
	
	boolean isChangable();
	
	int getLayer();

	public static TileType lookup(Class<? extends TileType> c, int id) {
		
		if (c.equals(BackgroundElement.class)) {
			return BackgroundElement.byIdStatic(id);
		}
		
		if (c.equals(MidgroundElement.class)) {
			return MidgroundElement.byIdStatic(id);
		}
		
		if (c.equals(ForegroundElement.class)) {
			return ForegroundElement.byIdStatic(id);
		}
		
		return null;
		
	}
	
	public boolean isLiquid();
	
	public int ordinal();
	
	@Deprecated
	default int getOrdinal() {
		return ordinal();
	}
	
	@Override
	public default Object get(World world, AbstractSemantics attr) {
		if (attr == AbstractSemantics.FLOOD && isLiquid()) {
			return new Tile(this);
		}
		
		if (attr == AbstractSemantics.STORM && this == BackgroundElement.SAND) {
			return new Tile(this, 1);
		}
		
		return Nominal.super.get(world, attr);
	}
	
	public static TileType[] getValues(Class<? extends TileType> c) {
		
		if (c.equals(BackgroundElement.class)) {
			return BackgroundElement.values();
		}
		
		if (c.equals(MidgroundElement.class)) {
			return MidgroundElement.values();
		}
		
		if (c.equals(ForegroundElement.class)) {
			return ForegroundElement.values();
		}
		
		return null;
		
	}
	
	static BufferedImage[] getReflections(BufferedImage image) {
		
		List<BufferedImage> out = new ArrayList<>();
		
		final boolean[] FLIP_OPTIONS = new boolean[] {false, true};
		for (boolean flipY : FLIP_OPTIONS) {
			for (boolean flipX : FLIP_OPTIONS) {
				out.add(Util.getFlipped(image, flipX, flipY));
			}
		}
		
		return out.toArray(new BufferedImage[0]);
		
	}
	
	static BufferedImage[] getRotations(BufferedImage image) {
		
		List<BufferedImage> out = new ArrayList<>();
		
		for (double theta = 0; theta < Math.PI * 2; theta += Math.PI / 2) {
			out.add(Util.getRotated(image, theta));
		}
		
		return out.toArray(new BufferedImage[0]);
		
	}
	
}
