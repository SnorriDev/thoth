package snorri.semantics;

import snorri.entities.Mummy;
import snorri.entities.Entity;
import snorri.entities.Sarcophagus;
import snorri.masking.Mask;
import snorri.triggers.Trigger.TriggerType;
import snorri.world.Vector;
import snorri.world.BackgroundElement;
import snorri.world.Level;
import snorri.world.Tile;
import snorri.world.TileType;

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
		
		if (obj instanceof Sarcophagus) {
			e.getWorld().delete((Entity) obj);
			Vector spawnPos = e.getWorld().getLevel().getGoodSpawn(((Sarcophagus) obj).getPos().copy().toGridPos());
			Entity.spawnNew(e.getWorld(), spawnPos, Mummy.class);
		}
		
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
		
	public static boolean openDoor(Level l, Vector pos) {
		
		if (l.getTileGrid(pos) == null) {
			return false;
		}
		
		if (l.getTileGrid(pos).getType() == BackgroundElement.DOOR) {
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
