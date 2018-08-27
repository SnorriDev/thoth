package snorri.world;

import java.awt.image.BufferedImage;
import java.util.ArrayList;

import snorri.main.Util;
import snorri.world.TileType;

public enum BackgroundElement implements TileType {
	
	SAND(true, new BufferedImage[] {
		getImage("sand00.png"),
		getImage("sand01.png"),
		getImage("sand02.png"),
		getImage("sand03.png")}, Param.changable(true), Param.blendOrder(1.5)),
	BLACK(new BufferedImage[] {
			getImage("black00.png")}, Param.pathable(false), Param.atTop(true)),
	STAIRS(TileType.addAll(TileType.getRotations(getImage("stairs00.png")),TileType.getRotations(getImage("stairs01.png"))), Param.pathable(true), Param.atTop(true)),
	HUT(Param.pathable(false), Param.atTop(true)),
	WATER(false, getImage("water00.png"), Param.changable(true), Param.swimmable(true), Param.blendOrder(101.0)),
	LAVA(false, new BufferedImage[] {
		getImage("lava00.png")}, Param.swimmable(true), Param.blendOrder(99.0)),
	GRASS(true, new BufferedImage[] {
		getImage("grass00.png"),
		getImage("grass01.png")}, Param.changable(true), Param.blendOrder(1.8)),
	VOID(false, getImage("void00.png"), Param.atTop(true), Param.canShootOver(false), Param.blendOrder(-1.0)),
	COLUMN(Param.pathable(false), Param.atTop(true)),
	DOOR(Param.pathable(false), Param.atTop(true), Param.canShootOver(false)),
	SANDSTONE(false, new BufferedImage[] {
		getImage("sandstone00.png"),
		getImage("sandstone01.png"),
		getImage("sandstone02.png"),
		getImage("sandstone03.png"),
		getImage("sandstone04.png"),
		getImage("drymud00.png")}, Param.canShootOver(false), Param.blendOrder(1.0)), //will change to look like bricks
	FLOOR(true, new BufferedImage[] {
		getImage("floor00-floor00.png"),
		getImage("floor01-floor01.png"),
		getImage("floor02-floor02.png"),
		getImage("floor03-floor03.png"),
		getImage("floor04-floor04.png"),
		getImage("floor05-floor05.png"),
		getImage("floor06-floor06.png"),
		getImage("floor07-floor07.png"),
		getImage("floor08-floor08.png"),
		getImage("floor09-floor09.png"),
		getImage("floor10-floor10.png"),
		getImage("floor11-floor11.png"),
		getImage("floor12-floor12.png")}, Param.atTop(true)),
	GRAVEL(true, getImage("gravel00.png"), Param.changable(true), Param.blendOrder(3.0)),
	STONE(false, new BufferedImage[] {
		getImage("stone00.png"),
		getImage("stone01.png"),
		getImage("stone02.png"),
		getImage("stone03.png"),
		getImage("stone04.png")}, Param.canShootOver(false), Param.blendOrder(0.1)),
	DEEP_WATER(false, getImage("water01.png"), Param.swimmable(true), Param.canShootOver(true), Param.blendOrder(100.0)),
	EARTH(true, new BufferedImage[] {
		getImage("earth00.png"),
		getImage("earth01.png"),
		getImage("earth02.png"),
		getImage("earth03.png")}, Param.changable(true), Param.blendOrder(5.0)),
	WOOD(true, new BufferedImage[] {
		getImage("wood00.png"),
		getImage("wood01.png"),
		getImage("wood02.png"),
		getImage("wood03.png"),
		getImage("wood04.png")}, Param.atTop(true)),
	BRICK(false, new BufferedImage[] {
		getImage("wall00.png"),
		getImage("wall01.png"),
		getImage("wall02.png"),
		getImage("wall03.png"),
		getImage("wall04.png"),
		getImage("wall05.png"),
		getImage("wall06.png"),
		getImage("wall07.png"),
		getImage("wall08.png"),
		getImage("wall09.png")}, Param.atTop(true), Param.canShootOver(false), Param.blendOrder(0.0)),
	THRESHOLD(true, new BufferedImage[] {
		getImage("floor00-floor00.png"),
		getImage("floor01-floor01.png"),
		getImage("floor02-floor02.png"),
		getImage("floor03-floor03.png"),
		getImage("floor04-floor04.png"),
		getImage("floor05-floor05.png"),
		getImage("floor06-floor06.png"),
		getImage("floor07-floor07.png"),
		getImage("floor08-floor08.png"),
		getImage("floor09-floor09.png"),
		getImage("floor10-floor10.png"),
		getImage("floor11-floor11.png"),
		getImage("floor12-floor12.png")});
	
	protected boolean pathable, canShootOver, atTop, changable, swimmable;
	protected double blendOrder;
	protected BufferedImage[] textures;
	
	BackgroundElement() {
		this(Tile.DEFAULT_BACKGROUND_TEXTURE);
	}
	
	BackgroundElement(BufferedImage texture) {
		this(new BufferedImage[] {texture});
	}
	
	BackgroundElement(BufferedImage[] textures) {
		this.textures = textures;
		pathable = true;
		swimmable = false;
		changable = false;
		atTop = false;
		canShootOver = true;
		blendOrder = 2.0;
	}
	
	BackgroundElement(Param<?>...params) {
		this(new BufferedImage[] {Tile.DEFAULT_BACKGROUND_TEXTURE}, params);
	}
	
	BackgroundElement(BufferedImage texture, Param<?>...params) {
		this(new BufferedImage[] {texture}, params);
	}
	
	BackgroundElement(BufferedImage[] textures, Param<?>...params) {
		this(textures);
		for (Param<?> p : params) {
			setParam(p);
		}
	}
	
	@Deprecated
	BackgroundElement(boolean pathable) {
		this(pathable, new BufferedImage[] {Tile.DEFAULT_BACKGROUND_TEXTURE});
	}
	
	@Deprecated
	BackgroundElement(boolean pathable, BufferedImage texture) {
		this(pathable, new BufferedImage[] {texture});
	}
	
	@Deprecated
	BackgroundElement(boolean pathable, BufferedImage[] textures) {
		this(textures);
		this.pathable = pathable;
	}
	
	@Deprecated
	BackgroundElement(boolean pathable, BufferedImage[] textures, Param<?>...params) {
		this(pathable, textures);
		for (Param<?> p : params) {
			setParam(p);
		}
	}
	
	@Deprecated
	BackgroundElement(boolean pathable, BufferedImage texture, Param<?>...params) {
		this(pathable, new BufferedImage[] {texture}, params);
	}
	
	
	private static BufferedImage getImage(String string) {
		return Tile.getImage("/textures/tiles/background/" + string, 0);
	}


	public boolean isLiquid() {
		return !pathable && canShootOver;
	}
	
	@Override
	public boolean isChangable() {
		return changable;
	}
	
	@Override
	public BackgroundElement byId(int id) {
		return values()[id];
	}
	
	public static BackgroundElement byIdStatic(int id) {
		return values()[id];
	}
	
	@Override
	public int getId() {
		return ordinal();
	}
	
	public boolean isPathable() {
		return pathable;
	}
	
	public boolean canShootOver() {
		return canShootOver;
	}
	
	@Override
	public BufferedImage[] getTextures() {
		if (textures != null)
				return textures;
		else
			//Main.error("no textures found, returning default texture");
			return new BufferedImage[] {Tile.DEFAULT_BACKGROUND_TEXTURE};
	}
	
	@Override
	public BufferedImage getTexture(int index) {
		if (index >= textures.length) {
			//Main.error("texture not found, index out of bounds, returning default texture");
			return Tile.DEFAULT_BACKGROUND_TEXTURE;
		}
		return textures[index];
	}
	
	@Override
	public int getNumberStyles() {
		return textures.length;
	}
	
	@Override
	public String toString() {
		return Util.clean(name());
	}
	
	public boolean isAtTop() {
		return atTop;
	}
	
	@Override
	public ArrayList<Tile> getSubTypes() {
		int i = getId();
		ArrayList<Tile> list = new ArrayList<Tile>();
		for(int j = 0; j < byId(i).getNumberStyles(); j++) {
			list.add(new Tile(BackgroundElement.class, i,j));
		}
		return list;
	}
	
	@Override 
	public int getLayer() {
		return TileLayer.BACKGROUND;
	}

	@Override
	public TileType getReplacement() {
		return null;
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
			case SWIMMABLE:
				swimmable = (Boolean) param.getValue();
				break;
			case CHANGABLE:
				changable = (Boolean) param.getValue();
				break;
			case AT_TOP:
				atTop = (Boolean) param.getValue();
				break;
			case CAN_SHOOT_OVER:
				canShootOver = (Boolean) param.getValue();
				break;
			case BLEND_ORDER:
				blendOrder = (Double) param.getValue();
				break;
			default:
				break;
		}
	}
}
