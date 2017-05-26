package snorri.world;

import java.awt.image.BufferedImage;
import java.util.ArrayList;

import snorri.audio.ClipWrapper;
import snorri.main.Main;
import snorri.main.Util;
import snorri.semantics.Nominal;

public enum ForegroundElement implements Nominal, TileType {
	
	NONE,
	COL_MID(getImage("colmid00.png")),
	COL_TOP(getImage("coltop00.png")),
	GATE(TileType.getRotations(getImage("gate00.png"))),
	GATE_LEFT_OPEN(TileType.getRotations(getImage("gateleftopen00.png"))),
	GATE_RIGHT_OPEN(TileType.getRotations(getImage("gaterightopen00.png"))),
	GATE_LEFT(TileType.getRotations(getImage("gateleft00.png")), GATE_LEFT_OPEN),
	GATE_RIGHT(TileType.getRotations(getImage("gateright00.png")), GATE_RIGHT_OPEN);
	
	protected BufferedImage[] textures;
	protected TileType replacementType;
	protected double blendOrder;
	
	ForegroundElement() {
		this((BufferedImage) null);
	}
	
	ForegroundElement(BufferedImage[] textures) {
		this.textures = textures;
	}
	
	ForegroundElement(BufferedImage texture) {
		this(new BufferedImage[] {texture});
	}
	
	ForegroundElement(BufferedImage[] textures, ForegroundElement replacementType) {
		this(textures);
		this.replacementType = replacementType;
	}
	
	public static ForegroundElement byIdStatic(int id) {
		return values()[id];
	}
	
	private static BufferedImage getImage(String string) {
		return Tile.getImage("/textures/tiles/foreground/" + string, 2);
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
			return new BufferedImage[] {Tile.DEFAULT_FOREGROUND_TEXTURE};
		}
	}

	@Override
	public BufferedImage getTexture(int index) {
		if (index >= textures.length) {
			//Main.error("texture not found, index out of bounds, returning default texture");
			return Tile.DEFAULT_FOREGROUND_TEXTURE;
		}
		return textures[index];
	}

	@Override
	public int getNumberStyles() {
		return textures.length;
	}

	//TODO: can implement all these in terms of a supermethod that takes a class parameter
	@Override
	public ArrayList<Tile> getSubTypes() {
		int i = getId();
		ArrayList<Tile> list = new ArrayList<Tile>();
		for(int j = 0; j < byId(i).getNumberStyles(); j++) {
			list.add(new Tile(ForegroundElement.class, i,j));
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
		return true; //everything in this layer is pathable
	}

	@Override
	public boolean canShootOver() {
		return true; //everythign in this layer should let things shoot through it
	}
	
	@Override
	public String toString() {
		return Util.clean(name());
	}
	
	@Override
	public boolean isAtTop() {
		return true; //nothign should be blended in this layer
	}

	@Override
	public boolean isChangable() { //FIXME
		return true;
	}
	
	@Override 
	public int getLayer() {
		return 2;
	}

	@Override
	public boolean isLiquid() {
		return false;
	}

	@Override
	public TileType getReplacement() {
		return replacementType;
	}
	
	@Override
	public double getBlendOrder() {
		return blendOrder;
	}
	
}
