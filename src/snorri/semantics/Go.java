package snorri.semantics;

import snorri.entities.Projectile;
import snorri.events.SpellEvent;
import snorri.world.Vector;
import snorri.world.World;

public class Go extends IntransVerbDef {
	
	private static final double SPEED = 350;
	private static final double DELETE_MARGIN = 5;
	
	public interface Walker {
		
		public void walk(World world, Vector delta);
			
		default void walk(World world, Vector dir, double deltaTime) {
			walk(world, dir.copy().multiply(deltaTime));
		}
		
		default void walkNormalized(World world, Vector dir, double deltaTime) {
			walk(world, dir.copy().normalize(), deltaTime);
		}
		
	}
	
	public Go() {
		super();
	}

	@Override
	public boolean exec(SpellEvent e) {
		if (e.getSecondPerson() instanceof Walker) {
			Vector trans = e.getDestination().copy().sub(e.getSecondPerson().getPos());
			if (trans.magnitude() < DELETE_MARGIN) {
				e.getWorld().delete(e.getSecondPerson());
				return false;
			}
			((Walker) e.getSecondPerson()).walkNormalized(e.getWorld(), trans, SPEED * e.getDeltaTime());
			return true;
		}
		return false;
	}
	
	//TODO: track movement better
	/**
	 * @return whether or not something is a projectile
	 */
	@Override
	public boolean eval(Object subj, SpellEvent e) {
		return subj instanceof Projectile;
	}
	
	@Override
	public boolean altersMovement() {
		return true;
	}

	@Override
	public String toString() {
		return "go";
	}

}
