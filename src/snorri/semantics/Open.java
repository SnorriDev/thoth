package snorri.semantics;

import snorri.entities.Entity;
import snorri.masking.Mask;
import snorri.triggers.Trigger.TriggerType;
import snorri.world.Vector;
import snorri.world.Level;
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
			return openDoor(e.getWorld().getLevel(), tilePos);
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
		
	private static boolean openDoor(Level l, Vector pos) {
		if (l.getTileGrid(pos).getType() == TileType.DOOR) {
			TriggerType.DOOR_OPEN.activate(pos);
			l.wrapGridUpdate(pos, new Tile(REPLACEMENT_TILE));
			for (Vector trans : Mask.NEIGHBORS) {
				openDoor(l, pos.copy().add(trans));
			}
			return true;
		}
		return false;
	}
	
	@Override
	public String toString() {
		return "open";
	}

}
