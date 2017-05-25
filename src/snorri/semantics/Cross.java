package snorri.semantics;

import snorri.entities.Entity;
import snorri.events.SpellEvent;
import snorri.parser.Node;
import snorri.world.Level;
import snorri.world.TileType;

public class Cross extends TransVerbDef {

	public Cross() {
		super();
	}

	@Override
	public boolean exec(Node<Object> object, SpellEvent e) {
		return false;
	}

	@Override
	public boolean eval(Object subj, Object obj, SpellEvent e) {
		
		//if the subject isn't an entity, then return false
		if (!(subj instanceof Entity)) {
			return false;
		}
		Entity ent = (Entity) subj;
		
		if (obj instanceof Entity) {
			return ent.intersects((Entity) obj);
		}
		
		if (obj instanceof TileType) {
							
			for (Level level : e.getWorld().getLevels()) {
				if (obj == level.getTile(ent.getPos()).getType()) {
					return true;
				}
			}
			return false;
			
		}
			
		if (obj instanceof Class) {
		
			Class<?> c = (Class<?>) obj;
			
			//return true if we are intersecting an entity of the right type
			if (Entity.class.isAssignableFrom(c)) {
				for (Entity e2 : e.getWorld().getEntityTree().getAllCollisions(ent, true)) {
					if (c.isInstance(e2)) {
						return true;
					}
				}
			}
		
		}
		
		return false;
		
	}

	@Override
	public String toString() {
		return "touch";
	}

}
