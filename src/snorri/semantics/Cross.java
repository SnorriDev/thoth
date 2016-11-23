package snorri.semantics;

import snorri.entities.Entity;
import snorri.main.Main;
import snorri.world.Tile.TileType;

public class Cross extends VerbDef {

	public Cross() {
		super(true);
	}

	@Override
	public boolean exec(Object obj) {
		return false;
	}

	@Override
	public boolean eval(Object subj, Object obj) {
		
		Main.debug(subj);
		Main.debug(obj);
		
		if (!(subj instanceof Entity) || !(obj instanceof Class<?>)) {
			return false;
		}
		
		Entity ent = (Entity) subj;
		Class<?> c = (Class<?>) obj;
		
		//if the argument is a tile type, check what type the entity is standing on
		//TODO loop through all tiles around the player, not just the one they are on
		if (TileType.class.isAssignableFrom(c)) {
			return c.equals(e.getWorld().getLevel().getTile(ent.getPos()).getClass());
		}
		
		//return true if we are intersecting an entity of the right type
		if (Entity.class.isAssignableFrom(c)) {
			for (Entity e2 : e.getWorld().getEntityTree().getAllCollisions(ent)) {
				if (c.isInstance(e2)) {
					Main.log("bam");
					return true;
				}
			}
		}
		
		return false;
		
	}

	@Override
	public String toString() {
		return "collide";
	}

}
