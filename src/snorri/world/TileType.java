package snorri.world;

import java.awt.image.BufferedImage;
import java.util.ArrayList;

import snorri.audio.ClipWrapper;
import snorri.semantics.Nominal.AbstractSemantics;

public interface TileType {
	
	//BufferedImage getImage(String string);
	
	TileType byId(int id);
	
	int getId();
	
	BufferedImage[] getTextures();
	
	BufferedImage getTexture(int index);
	
	int getNumberStyles();
	
	@Override
	String toString();
	
	Object get(World world, AbstractSemantics attr);
	
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
	
}
