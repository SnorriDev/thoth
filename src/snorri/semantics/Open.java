package snorri.semantics;

import snorri.entities.Mummy;
import snorri.entities.Entity;
import snorri.entities.Sarcophagus;
import snorri.masking.Mask;
import snorri.triggers.Trigger.TriggerType;
import snorri.world.Vector;
import snorri.world.World;
import snorri.world.BackgroundElement;
import snorri.world.Tile;

public class Open extends VerbDef {

	private static final Tile REPLACEMENT_TILE = new Tile(BackgroundElement.SAND);
	
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
			Vector spawnPos = e.getWorld().getGoodSpawn(((Sarcophagus) obj).getPos().copy().toGridPos());
			Entity.spawnNew(e.getWorld(), spawnPos, Mummy.class);
		}
		
		if (obj instanceof Entity) {
			Vector tilePos = ((Entity) obj).getPos().copy().toGridPos();
			return openDoor(e.getWorld(), tilePos);
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
		
	public static boolean openDoor(World w, Vector pos) {
		
		if (w.getLevel().getTileGrid(pos) == null) {
			return false;
		}
		
		if (w.getLevel().getTileGrid(pos).getType() == BackgroundElement.DOOR) {
			TriggerType.DOOR_OPEN.activate(pos);
			w.wrapGridUpdate(pos, new Tile(REPLACEMENT_TILE));
			for (Vector trans : Mask.NEIGHBORS) {
				openDoor(w, pos.copy().add(trans));
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
