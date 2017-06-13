package snorri.semantics;

import snorri.entities.Entity;
import snorri.entities.Spike;
import snorri.events.SpellEvent;
import snorri.parser.Node;
import snorri.world.Level;
import snorri.world.MidgroundElement;
import snorri.world.Tile;
import snorri.world.TileType;
import snorri.world.Vector;

public class CreateObject extends TransVerbDef {
	
	public CreateObject() {
		super();
	}

	@Override @SuppressWarnings("unchecked")
	public boolean exec(Node<Object> object, SpellEvent e) {
		
		Object obj = object.getMeaning(e);
				
		if (obj instanceof Tile) {
			
			Class<? extends TileType> layer = ((Tile) obj).getType().getClass();			
			Tile tile = e.getWorld().getLevel(layer).getTile(e.getLocative());
			if (tile == null || !tile.getType().isChangable()) {
				return false;
			}
			
			Level midground = e.getWorld().getLevel(MidgroundElement.class);
			if (midground.getTile(e.getLocative()).getType() == MidgroundElement.BROKEN_DEBRIS) {
				e.getWorld().wrapUpdate(e.getLocative(), new Tile(MidgroundElement.NONE));
			}
			
			//check if there is an entity in the way
			if (!((Tile) obj).isPathable() && tile.isPathable()) {
				Vector pos = e.getLocative().copy().toGridPos();
				if (e.getWorld().tileHasEntity(pos)) { //could move this out, but want to allow pathable tiles to be placed
					return false;
				}
			}
			
			e.getWorld().wrapUpdate(e.getLocative(), (Tile) obj);
			return true;
		}
				
		if (obj instanceof ClassWrapper) {
			Class<? extends Entity> c = (Class<? extends Entity>) ((ClassWrapper) obj).getValue();
			if (!Entity.canSpawn(c)) {
				return false;
			}
			boolean checkCollisions = c != Spike.class;
			Entity spawned = Entity.spawnNew(e.getWorld(), e.getLocative(), c, checkCollisions);
			return spawned != null;
		}
		
		return false;
		
	}

	@Override
	public boolean eval(Object subj, Object obj, SpellEvent e) {
		return false;
	}

	@Override
	public String toString() {
		return "create (object)";
	}

}
