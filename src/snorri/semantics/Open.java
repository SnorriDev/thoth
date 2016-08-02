package snorri.semantics;

import snorri.entities.Entity;
import snorri.world.Vector;
import snorri.world.Tile;
import snorri.world.Tile.TileType;

public class Open extends VerbDef {

	private static final Tile REPLACEMENT_TILE = new Tile(0);
	
	public Open() {
		super(true);
	}

	/**
	 * Opens the tile under the object.
	 */
	@Override
	public boolean exec(Object obj) {
		if (obj instanceof Entity) {
			Vector tilePos = ((Entity) obj).getPos().copy().toGridPos();
			if (e.getWorld().getLevel().getTileGrid(tilePos).getType() == TileType.DOOR) {
				e.getWorld().getLevel().wrapTileUpdate(tilePos, new Tile(REPLACEMENT_TILE));
				return true;
			}
		}
		return false;
	}

	/**
	 * @return whether the terrain under the object is pathable
	 */
	@Override
	public boolean eval(Object subj, Object obj) {
		if (obj instanceof Entity) {
			return e.getWorld().getLevel().isPathable(((Entity) obj).getPos().copy().toGridPos());
		}
		return false;
	}

	@Override
	public String getShortDesc() {
		// TODO Auto-generated method stub
		return null;
	}

}
