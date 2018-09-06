package snorri.world;

import java.awt.image.BufferedImage;
import java.util.ArrayList;

import snorri.main.Util;


public enum MidgroundElement implements TileType {
	
	NONE,
	COL_BOT(getImage("colbot00.png")),
	DOOR(TileType.getRotations(getImage("door00.png")), Param.replacementType(NONE)),
	DEBRIS(getImage("debris00.png")),
	BROKEN_DEBRIS(getImage("brokendebris00.png"), Param.replacementType(NONE)),
	WALL(TileType.getRotations(getImage("wall00.png"))),
	WALL_CONCAVE(TileType.getRotations(getImage("wallconcave00.png"))),
	WALL_CONVEX(TileType.getRotations(getImage("wallconvex00.png"))),
	WALL_END_LEFT(TileType.addAll(TileType.getRotations(getImage("wallendleft00.png")),TileType.getRotations(getImage("wallendleft03.png")))),
	WALL_END_RIGHT(TileType.addAll(TileType.getRotations(getImage("wallendright00.png")),TileType.getRotations(getImage("wallendright03.png")))),
	WALL_DEFAULT(Tile.DEFAULT_TEXTURE),
	WALL_STUB(TileType.getRotations(getImage("wallstub00.png"))),
	BROKEN_WALL(TileType.getRotations(getImage("brokenwall00.png"))),
	BROKEN_WALL_END_LEFT(TileType.getRotations(getImage("brokenwallendleft00.png"))),
	BROKEN_WALL_END_RIGHT(TileType.getRotations(getImage("brokenwallendright00.png"))),
	WALL_CROSS_LEFT(TileType.getRotations(getImage("wallcrossleft00.png"))),
	WALL_CROSS_RIGHT(TileType.getRotations(getImage("wallcrossright00.png")));
	
	protected BufferedImage[] textures;
	protected boolean pathable; //some things (like paths and tiles) will be unpathable
	protected TileType replacementType;
	protected double blendOrder;
	
	MidgroundElement() {
		this(new BufferedImage[] {null}, Param.isNotSurface(true));
	}
	
	MidgroundElement(BufferedImage texture) {
		this(new BufferedImage[] {texture});
	}
	
	MidgroundElement(BufferedImage[] textures) {
		this.textures = textures;
		pathable = false;
		replacementType = null;
		blendOrder = 0.0;
	}
	
	MidgroundElement(Param<?>...params) {
		this(new BufferedImage[] {}, params);
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
		return textures;
	}

	@Override
	public BufferedImage getTexture(int index) {
		if (index >= textures.length) {
			return null;
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
	public boolean isNotSurface() {
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
		return replacementType != null;
	}
	
	@Override 
	public int getLayer() {
		return Level.MIDGROUND;
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
			case IS_NOT_SURFACE:
				pathable = (Boolean) param.getValue();
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
