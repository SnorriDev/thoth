package snorri.world;

import java.awt.image.BufferedImage;
import java.util.ArrayList;

import snorri.main.Debug;
import snorri.main.Main;

public enum UnifiedTileType implements TileType {
	
	EMPTY((BufferedImage) null, Param.isNotSurface(true)),
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
	TRIPWIRE(TileType.getTwoRotations(Main.getImage("/textures/tiles/foreground/tripwire00.png")), Param.isNotSurface(true)),
	TRIPWIRE_END(TileType.getReflections(Main.getImage("/textures/tiles/foreground/tripwireend00.png")), Param.isNotSurface(true)),
	DOOR(new BufferedImage[] {
			Main.getImage("/textures/tiles/door00.png"),
			Main.getImage("/textures/tiles/door01.png"),
	}, Param.replacementTile(new Tile(EMPTY)));
	
	private final BufferedImage[] textures;
	
	// Default values for these fields can be used by passing Param.
	private boolean isNotSurface = false;
	private boolean swimmable = false;
	private boolean changable = false;
	private boolean atTop = true;
	private double blendOrder = 2.0;
	private Tile replacementTile = null;
	private UnifiedTileType replacementType = null;
	
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
		case IS_NOT_SURFACE:
			isNotSurface = (boolean) param.getValue();
			break;
		case REPLACEMENT_TYPE:
			replacementType = (UnifiedTileType) param.getValue();
			break;
		case REPLACEMENT_TILE:
			replacementTile = (Tile) param.getValue();
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
	public boolean isNotSurface() {
		return isNotSurface;
	}

	@Override
	public boolean canShootOver() {
		return isNotSurface || swimmable;
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

	/** Get a new replacement tile for this type.
	 * 
	 * The exact method for creating this tile differs based on what parameters have been set:
	 * 1. If a replacementTile is set, a copy of that is returned.
	 * 2. If a replacementType is set, a tile of that type with the same style is returned.
	 * 3. Otherwise, the null Tile is returned.
	 */
	@Override
	public Tile newReplacementTile(Tile oldTile) {
		if (replacementTile != null) {
			return new Tile(replacementTile);
		} else if (replacementType != null) {
			return new Tile(replacementType, oldTile.getStyle());
		}
		return null;
	}

	@Override
	public double getBlendOrder() {
		return blendOrder;
	}

}
