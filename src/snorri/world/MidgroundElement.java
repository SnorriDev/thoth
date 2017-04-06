package snorri.world;

import java.awt.image.BufferedImage;
import java.util.ArrayList;

import snorri.audio.ClipWrapper;
import snorri.main.Main;
import snorri.main.Util;
import snorri.semantics.Nominal;

public enum MidgroundElement implements Nominal, TileType {
	NONE,
	COL_BOT(false, getImage("colbot00.png"));
	
	private BufferedImage[]	textures;
	private boolean pathable; //some things (like paths and tiles) will be unpathable
	
	MidgroundElement() {
		this(true, Tile.DEFAULT_MIDGROUND_TEXTURE);
	}
	
	MidgroundElement(BufferedImage[] textures) {
		this.textures = textures;
	}
	
	MidgroundElement(BufferedImage texture) {
		this(new BufferedImage[] {texture});
	}
	
	MidgroundElement(boolean pathable, BufferedImage[] textures) {
		this(textures);
		this.pathable = pathable;
	}
	
	MidgroundElement(boolean pathable, BufferedImage texture) {
		this(texture);
		this.pathable = pathable;
	}
	
	public static MidgroundElement byIdStatic(int id) {
		return values()[id];
	}
	
	private static BufferedImage getImage(String string) {
		return Tile.getImage("/textures/tiles/midground/" + string, 1);
	}

	@Override
	public TileType byId(int id) {
		return values()[id];
	}

	@Override
	public int getId() {
		return ordinal();
	}

	@Override
	public BufferedImage[] getTextures() {
		if (textures != null)
			return textures;
		else {
			//Main.error("no textures found, returning default texture");
			return new BufferedImage[] {Tile.DEFAULT_MIDGROUND_TEXTURE};
		}
	}

	@Override
	public BufferedImage getTexture(int index) {
		if (index >= textures.length) {
			//Main.error("texture not found, index out of bounds, returning default texture");
			return Tile.DEFAULT_MIDGROUND_TEXTURE;
		}
		return textures[index];
	}

	@Override
	public int getNumberStyles() {
		return textures.length;
	}

	@Override
	public Object get(World world, AbstractSemantics attr) {
		return Nominal.super.get(world, attr);
	}

	@Override
	public ArrayList<Tile> getSubTypes() {
		int i = getId();
		ArrayList<Tile> list = new ArrayList<Tile>();
		for(int j = 0; j < byId(i).getNumberStyles(); j++) {
			list.add(new Tile(MidgroundElement.class, i,j));
		}
		return list;
	}

	@Override
	public boolean hasSounds() {
		return Tile.sounds.length >= 1;
	}

	@Override
	public int getNumSounds() {
		return Tile.sounds.length;
	}

	@Override
	public ClipWrapper[] getSounds() {
		return Tile.sounds;
	}

	@Override
	public ClipWrapper getSound(int x) {
		if (x < Tile.sounds.length)
			return Tile.sounds[x];
		else {
			Main.error("index out of bounds");
			return null;
		}
	}

	@Override
	public boolean isPathable() {
		return pathable;
	}

	@Override
	public boolean canShootOver() {
		return pathable; //I this always equivalent to pathability in this layer
	}
	
	@Override
	public String toString() {
		return Util.clean(name());
	}

	@Override
	public boolean isAtTop() {
		return true; //we want all tiles in this layer to be unblended
	}

	@Override
	public boolean isChangable() { //FIXME:
		return true;
	}
	
	@Override 
	public int getLayer() {
		return 1;
	}
}