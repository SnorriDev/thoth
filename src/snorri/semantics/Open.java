package snorri.semantics;

import snorri.entities.Mummy;

import snorri.entities.Entity;
import snorri.entities.Sarcophagus;
import snorri.events.SpellEvent;
import snorri.masking.Mask;
import snorri.parser.Node;
import snorri.triggers.Trigger.TriggerType;
import snorri.world.Vector;
import snorri.world.World;
import snorri.world.ForegroundElement;
import snorri.world.MidgroundElement;
import snorri.world.Tile;
import snorri.world.TileType;

public class Open extends TransVerbDef {
	
	@SuppressWarnings("unchecked")
	private static final Class<? extends TileType>[] CHECK_LEVELS = new Class[] { 
			MidgroundElement.class,
			ForegroundElement.class
	};
	
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
	public boolean eval(Object subj, Object obj, SpellEvent e) {
		if (obj instanceof Entity) {
			return e.getWorld().getLevel(MidgroundElement.class).isPathable(((Entity) obj).getPos().copy().toGridPos());
		}
		return false;
	}
		
	public static boolean openDoor(World w, Vector pos) {
				
		for (Class<? extends TileType> levelType : CHECK_LEVELS) {
			
			Tile tile = w.getLevel(levelType).getTileGrid(pos);
			if (tile == null) {
				continue;
			}
			
			Tile replacementTile = tile.getReplacementTile();
			if (replacementTile != null) {
				TriggerType.DOOR_OPEN.activate(pos);
				w.wrapGridUpdate(pos, new Tile(replacementTile));
				for (Vector trans : Mask.NEIGHBORS) {
					openDoor(w, pos.copy().add(trans));
				}
				return true;
			}
		}
		
		return false;
	}
	
	@Override
	public String toString() {
		return "open";
	}

}
