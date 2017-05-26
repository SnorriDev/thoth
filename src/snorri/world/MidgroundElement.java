package snorri.world;

import java.awt.image.BufferedImage;
import java.util.ArrayList;

import snorri.audio.ClipWrapper;
import snorri.main.Main;
import snorri.main.Util;


public enum MidgroundElement implements TileType {
	
	NONE,
	COL_BOT(getImage("colbot00.png")),
	DOOR(false, TileType.getRotations(getImage("door00.png")), NONE),
	DEBRIS(getImage("debris00.png")),
	BROKEN_DEBRIS(false, getImage("brokendebris00.png"), true),
	WALL(TileType.getRotations(getImage("wall00.png"))),
	WALL_CONCAVE(TileType.getRotations(getImage("wallconcave00.png"))),
	WALL_CONVEX(TileType.getRotations(getImage("wallconvex00.png"))),
	WALL_END_LEFT(TileType.getRotations(getImage("wallendleft00.png"))),
	WALL_END_RIGHT(TileType.getRotations(getImage("wallendright00.png"))),
	WALL_DEFAULT(Tile.DEFAULT_TEXTURE),
	WALL_STUB(TileType.getRotations(getImage("wallstub00.png"))),
	BROKEN_WALL(false, TileType.getRotations(getImage("brokenwall00.png"))),
	BROKEN_WALL_END_LEFT(false, TileType.getRotations(getImage("brokenwallendleft00.png"))),
	BROKEN_WALL_END_RIGHT(false, TileType.getRotations(getImage("brokenwallendright00.png")));
	
	protected BufferedImage[]	textures;
	protected boolean pathable, changable = true; //some things (like paths and tiles) will be unpathable
	protected TileType replacementType;
	protected double blendOrder;
	
	MidgroundElement() {
		this((BufferedImage) null, Param.pathable(true));
	}
	
	MidgroundElement(BufferedImage texture) {
		this(new BufferedImage[] {texture});
	}
	
	MidgroundElement(BufferedImage[] textures) {
		this.textures = textures;
		pathable = false;
		changable = false;
		replacementType = null;
		blendOrder = 0.0;
	}
	
	MidgroundElement(Param<?>...params) {
		this(Tile.DEFAULT_FOREGROUND_TEXTURE, params);
	}
	
	MidgroundElement(BufferedImage texture, Param<?>...params) {
		this(new BufferedImage[] {texture}, params);
	}
	
	MidgroundElement(BufferedImage[] textures, Param<?>...params) {
		this(textures);
		for (Param<?> p : params) {
			setParam(p);
		}
	}
	
	@Deprecated
	MidgroundElement(boolean pathable, BufferedImage[] textures) {
		this(textures);
		this.pathable = pathable;
	}
	
	@Deprecated
	MidgroundElement(boolean pathable, BufferedImage texture) {
		this(texture);
		this.pathable = pathable;
	}
	
	@Deprecated
	MidgroundElement(boolean pathable, BufferedImage texture, boolean changable) {
		this(pathable, texture);
		this.changable = changable;
	}
	
	@Deprecated
	MidgroundElement(boolean pathable, BufferedImage[] textures, MidgroundElement replacementType) {
		this(pathable, textures);
		this.replacementType = replacementType;
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
	public boolean isChangable() {
		return changable;
	}
	
	@Override 
	public int getLayer() {
		return 1;
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
	
	@Override
	public void setParam(Param<?> param) {
		switch (param.getKey()) {
			case PATHABLE:
				pathable = (Boolean) param.getValue();
				break;
			case CHANGABLE:
				changable = (Boolean) param.getValue();
				break;
			case REPLACEMENT_TYPE:
				replacementType = (TileType) param.getValue();
				break;
			case BLEND_ORDER:
				blendOrder = (Double) param.getValue();
				break;
			default:
				break;
		}
	}
}
