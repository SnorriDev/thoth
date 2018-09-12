package snorri.semantics;

import snorri.entities.Projectile;
import snorri.events.SpellEvent;
import snorri.world.Vector;
import snorri.world.World;

public class Go extends IntransVerbDef {
	
	private static final double SPEED = 350;
	private static final double DELETE_MARGIN = 5;
	
	public interface Movable {
		
		/** default gravity vector **/
		final static Vector GRAVITY = new Vector(0.0, 512.0);
		
		/**
		 * translates (moves) the movable object through the world along the delta vector
		 * @param world word to move object through
		 * @param delta change in position (velocity*time)
		 */
		public void translate(World world, Vector delta);
		
		/**
		 * @return whether the object should be falling
		 */
		public boolean isFalling();
		
		/**
		 * translates (moves) the movable object through the world along the dir vector
		 * @param world word to move object through
		 * @param dir change in position (velocity)
		 * @param deltaTime change in time since last frame
		 */
		default void translate(World world, Vector dir, double deltaTime) {
			translate(world, dir.copy().multiply_(deltaTime));
		}
		
		/**
		 * translates (moves) the movable object through the world along the dir vector at a normalized speed
		 * @param world word to move object through
		 * @param dir change in position (velocity vector)
		 * @param deltaTime change in time since last frame
		 */
		default void translateNormalized(World world, Vector dir, double deltaTime) {
			translate(world, dir.copy().normalize_(), deltaTime);
		}
		
	}
	
	public Go() {
		super();
	}
	
	@Override
	public boolean exec(SpellEvent e) {
		if (e.getSecondPerson() instanceof Movable && e.getDestination() != null) {
			Vector trans = e.getDestination().copy().sub_(e.getSecondPerson().getPos());
			if (trans.magnitude() < DELETE_MARGIN) {
				e.getWorld().delete(e.getSecondPerson());
				return false;
			}
			((Movable) e.getSecondPerson()).translateNormalized(e.getWorld(), trans, SPEED * e.getDeltaTime());
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
