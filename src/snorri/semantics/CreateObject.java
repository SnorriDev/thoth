package snorri.semantics;

import snorri.entities.Entity;
import snorri.entities.Spike;
import snorri.events.SpellEvent;
import snorri.parser.Node;
import snorri.world.Level;
import snorri.world.MidgroundElement;
import snorri.world.Tile;
import snorri.world.Vector;

public class CreateObject extends TransVerbDef {
	
	public CreateObject() {
		super();
	}

	@Override @SuppressWarnings("unchecked")
	public boolean exec(Node<Object> object, SpellEvent e) {
		
		Object obj = object.getMeaning(e);
				
		if (obj instanceof Tile) {
			
			int layer = ((Tile) obj).getType().getLayer();
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
				
		if (obj instanceof Class<?> && Entity.canSpawn((Class<?>) obj)) {
			//TODO check if the entity can fit at the position (probably do this in spawnNew)
//			if (e.getWorld().tileHasEntity(e.getLocative().copy().toGridPos()) && obj != Spike.class) {
//				return false;
//			}
			boolean checkCollisions = obj != Spike.class;
			return Entity.spawnNew(e.getWorld(), e.getLocative(), (Class<? extends Entity>) obj, checkCollisions) != null;
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
