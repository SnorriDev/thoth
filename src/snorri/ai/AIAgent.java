package snorri.ai;

import snorri.entities.Entity;
import snorri.world.Vector;
import snorri.world.World;

/** Interface for things that can have AI added to them.
 * @author lambdaviking
 *
 * Generally, this will apply to Units.
 */
public interface AIAgent {
	
	/** Get the position of the agent in global coordinates. */
	Vector getPos();
	
	/** Set the target of this agent. */
	void setTarget(Entity target);
	
	/** Get the target of this agent. */
	Entity getTarget();
	
	/** Return true if the agent can currently attack target and false otherwise. */
	boolean canAttack(Entity target, World world);
	
	/** Execute an attack on target. */
	void attack(Entity target, World world);
	
	/** Moves the agent towards target. */
	void walkTowards(Entity target, World world, double deltaTime);

}
