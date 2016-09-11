package snorri.semantics;

import snorri.entities.Entity;
import snorri.entities.Spike;
import snorri.world.Tile;
import snorri.world.Vector;

public class CreateObject extends VerbDef {
	
	public CreateObject() {
		super(true);
	}

	@Override @SuppressWarnings("unchecked")
	public boolean exec(Object obj) {
				
		if (obj instanceof Tile) {
			
			Tile tile = e.getWorld().getLevel().getTile(e.getLocative());
			if (tile == null || !tile.getType().isChangable()) {
				return false;
			}
			
			//check if there is an entity in the way
			if (!((Tile) obj).isPathable() && tile.isPathable()) {
				Vector pos = e.getLocative().copy().toGridPos();
				if (e.getWorld().tileHasEntity(pos)) { //could move this out, but want to allow pathable tiles to be placed
					return false;
				}
			}
			
			e.getWorld().getLevel().wrapUpdate(e.getLocative(), (Tile) obj);
			return true;
		}
				
		if (obj instanceof Class<?> && Entity.canSpawn((Class<?>) obj)) {
			if (e.getWorld().tileHasEntity(e.getLocative().copy().toGridPos()) && obj != Spike.class) {
				return false;
			}
			return Entity.spawnNew(e.getWorld(), e.getLocative(), (Class<? extends Entity>) obj) != null;
		}
		
		return false;
		
	}

	@Override
	public boolean eval(Object subj, Object obj) {
		return false;
	}

	@Override
	public String toString() {
		return "create object";
	}

}
