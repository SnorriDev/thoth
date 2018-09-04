package snorri.semantics;

import java.util.HashSet;
import java.util.Set;

import snorri.entities.Entity;
import snorri.entities.Sarcophagus;
import snorri.events.SpellEvent;
import snorri.masking.Mask;
import snorri.parser.Node;
import snorri.triggers.Trigger.TriggerType;
import snorri.world.Vector;
import snorri.world.World;
import snorri.world.Tile;
import snorri.world.TileType;
import snorri.world.UnifiedTileType;

public class Open extends TransVerbDef {
	
	private static final Set<TileType> DOOR_TYPES = new HashSet<>();
	
	static {
		DOOR_TYPES.add(UnifiedTileType.DOOR);
	}
	
	public Open() {
		super();
	}

	/**
	 * Opens the tile under the object.
	 */
	@Override
	public boolean exec(Node<Object> object, SpellEvent e) {
		
		Object obj = object.getMeaning(e);
		
		if (obj instanceof Sarcophagus) {
			e.getWorld().delete((Entity) obj);
			return true;
		}
		
		if (obj instanceof Entity) {
			Vector tilePos = ((Entity) obj).getPos().copy().gridPos_();
			return openDoor(e.getWorld(), tilePos);
		}
		return false;
	}

	/**
	 * @return whether the terrain under the object is pathable
	 */
	@Override
	public boolean eval(Object subj, Object obj, SpellEvent e) {
		if (obj instanceof Entity) {
			return e.getWorld().getTileLayer().isPathable(((Entity) obj).getPos().copy().gridPos_());
		}
		return false;
	}
		
	public static boolean openDoor(World w, Vector pos) {
		Tile tile = w.getTileLayer().getTileGrid(pos);
		Tile replacementTile = tile.getReplacementTile();
		if (replacementTile != null) {
			TriggerType.DOOR_OPEN.activate(pos);
			w.wrapGridUpdate(pos, new Tile(replacementTile));
			for (Vector trans : Mask.NEIGHBORS) {
				openDoor(w, pos.copy().add_(trans));
			}
			return true;
		}
		return false;
	}
	
	public static boolean isDoor(Tile tile) {
		if (tile == null) {
			return false;
		}
		return DOOR_TYPES.contains(tile.getType());
	}
	
	@Override
	public String toString() {
		return "open";
	}

}
