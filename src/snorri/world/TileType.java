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
}
