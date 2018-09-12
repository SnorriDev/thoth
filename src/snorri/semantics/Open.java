package snorri.semantics;

import java.util.HashSet;
import java.util.Set;

import snorri.entities.Entity;
import snorri.entities.Sarcophagus;
import snorri.events.CastEvent;
import snorri.triggers.Trigger.TriggerType;
import snorri.world.Vector;
import snorri.world.World;
import snorri.world.Tile;
import snorri.world.TileType;
import snorri.world.UnifiedTileType;

public class Open extends IntransVerbDef {
	
	private static final Set<TileType> DOOR_TYPES = new HashSet<>();
	
	static {
		DOOR_TYPES.add(UnifiedTileType.DOOR);
	}
	
	public Open() {
		super();
	}

	/** Opens the tile under the object. */
	@Override
	public boolean exec(CastEvent e) {		
		Entity checker = e.getSecondPerson();
		if (checker instanceof Sarcophagus) {
			e.getWorld().delete(checker);
			return true;
		}
		Vector tilePos = checker.getPos().gridPos();
		return openDoor(e.getWorld(), tilePos);
	}

	/** Returns whether the tile is pathable. */
	@Override
	public boolean eval(Object subj, CastEvent e) {
		Entity checker = e.getSecondPerson();
		return e.getWorld().getTileLayer().isPathable(checker.getPos().gridPos());
	}
		
	public static boolean openDoor(World w, Vector pos) {
		Tile tile = w.getTileLayer().getTileGrid(pos);
		if (!isDoor(tile)) {
			return false;
		}
		
		// Do the door replacement at this tile.
		Tile replacementTile = tile.getType().newReplacementTile(tile);
		if (replacementTile == null) {
			throw new IllegalArgumentException("The replacement type for a door must be defined.");
		}
		w.wrapGridUpdate(pos, new Tile(replacementTile));
		TriggerType.DOOR_OPEN.activate(pos);
		
		// Recurse on neighbors of this tile.
		w.getTileLayer().forEachNeighborOf(pos, neighborPos -> {
			openDoor(w, neighborPos);
		});
		return true;
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
