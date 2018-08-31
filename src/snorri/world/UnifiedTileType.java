package snorri.world;

import java.awt.image.BufferedImage;
import java.util.ArrayList;

import snorri.main.Debug;
import snorri.main.Main;

public enum UnifiedTileType implements TileType {
	
	EMPTY((BufferedImage) null, Param.pathable(true)),
	SAND(new BufferedImage[] {
			Main.getImage("/textures/tiles/background/sand00.png"),
			Main.getImage("/textures/tiles/background/sand01.png"),
			Main.getImage("/textures/tiles/background/sand02.png"),
			Main.getImage("/textures/tiles/background/sand03.png"),
	},
    Param.changable(true), Param.blendOrder(1.5)),
	DEBRIS(Main.getImage("/textures/tiles/midground/debris00.png")),
	BROKEN_DEBRIS(Main.getImage("/textures/tiles/midground/brokendebris00.png"), Param.replacementType(EMPTY)),
	WATER(new BufferedImage[] {
			Main.getImage("/textures/tiles/background/water00.png"),
			Main.getImage("/textures/tiles/background/water01.png"),
	}, Param.swimmable(true)),
	TRIPWIRE(TileType.getTwoRotations(Main.getImage("/textures/tiles/foreground/tripwire00.png")), Param.pathable(true)),
	TRIPWIRE_END(TileType.getReflections(Main.getImage("/textures/tiles/foreground/tripwireend00.png")), Param.pathable(true)),
	DOOR(new BufferedImage[] {
			Main.getImage("/textures/tiles/door00.png"),
			Main.getImage("/textures/tiles/door01.png"),
	}, Param.replacementType(EMPTY));
	
	private final BufferedImage[] textures;
	
	// Default values for these fields can be used by passing Param.
	private boolean pathable = false;
	private boolean swimmable = false;
	private boolean changable = false;
	private boolean atTop = true;
	private double blendOrder = 2.0;
	private UnifiedTileType replacementType;
	
	UnifiedTileType(BufferedImage[] textures, Param<?>...params) {
		this.textures = textures;
		for (Param<?> param : params) {
			setParam(param);
		}
	}
	
	UnifiedTileType(BufferedImage texture, Param<?>...params){
		this(new BufferedImage[] {texture}, params);
	}
	
	@Override
	public void setParam(Param<?> param) {
		switch (param.getKey()) {
		case AT_TOP:
			atTop = (boolean) param.getValue();
			break;
		case BLEND_ORDER:
			blendOrder = (double) param.getValue();
			break;
		case CHANGABLE:
			changable = (boolean) param.getValue();
			break;
		case OPEN_TYPE:
			break;
		case PATHABLE:
			pathable = (boolean) param.getValue();
			break;
		case REPLACEMENT_TYPE:
			replacementType = (UnifiedTileType) param.getValue();
			break;
		case SWIMMABLE:
			swimmable = (boolean) param.getValue();
			break;
		default:
			Debug.logger.warning("Param argument " + param.getKey().name() + " had no affect on TileType " + name() + ".");
			break;
		}
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
		return textures[index];
	}

	@Override
	public int getNumberStyles() {
		return textures.length;
	}

	@Override
	public ArrayList<Tile> getAllStyles() {
		int i = getId();
		ArrayList<Tile> list = new ArrayList<Tile>();
		for(int j = 0; j < values()[i].getNumberStyles(); j++) {
			TileType type = UnifiedTileType.values()[i];
			list.add(new Tile(type, j));
		}
		return list;
	}

	@Override
	public boolean isPathable() {
		return pathable;
	}

	@Override
	public boolean canShootOver() {
		return pathable || swimmable;
	}

	@Override
	public boolean isAtTop() {
		return atTop;
	}

	@Override
	public boolean isChangable() {
		return changable;
	}

	@Override
	public boolean isSwimmable() {
		return swimmable;
	}

	@Override
	public UnifiedTileType getReplacement() {
		return replacementType;
	}

	@Override
	public double getBlendOrder() {
		return blendOrder;
	}

}
