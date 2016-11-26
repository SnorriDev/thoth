package snorri.semantics;

import snorri.entities.Projectile;
import snorri.world.Vector;
import snorri.world.World;

public class Walk extends VerbDef {
	
	private static final double SPEED = 175;
	
	public interface Walker {
		
		/**
		 * This method is public, but don't call it
		 * @param world
		 * 	The world we are walking in
		 * @param delta
		 * 	The change vector
		 */
		public void walk(World world, Vector delta);
		
		default void walk(World world, Vector dir, double deltaTime) {
			walk(world, dir.copy().scale(deltaTime));
		}
		
	}
	
	public Walk() {
		super(false);
	}

	@Override
	public boolean exec(Object obj) {
		if (e.getSecondPerson() instanceof Walker) {
			Vector trans = e.getDestination().copy().sub(e.getSecondPerson().getPos()).normalize();	
			((Walker) e.getSecondPerson()).walk(e.getWorld(), trans, SPEED * e.getDeltaTime());
			return true;
		}
		return false;
	}
	
	//TODO: track if something is moving better
	
	/**
	 * @return whether or not an entity is moving
	 */
	@Override
	public boolean eval(Object subj, Object obj) {
		return subj instanceof Projectile;
	}
	
	@Override
	public boolean altersMovement() {
		return true;
	}

	@Override
	public String toString() {
		return "walk";
	}

}
