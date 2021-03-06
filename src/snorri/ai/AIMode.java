package snorri.ai;

import snorri.entities.Entity;
import snorri.world.Vector;
import snorri.world.World;

public enum AIMode {
	
	IDLE((agent, world, deltaTime) -> {
		// Do nothing.
	}),
	
	TURRET((agent, world, deltaTime) -> {
		// If the target is in range, attack them.
		Entity target = agent.getTarget();
		if (target != null && agent.canAttack(target, world)) {
			agent.attack(target, world);
		}
	}),
	
	CHARGE((agent, world, deltaTime) -> {
		// If the target is in range, attack them. Otherwise, charge them!
		Entity target = agent.getTarget();
		if (target == null) {
			return;
		}
		if (agent.canAttack(target, world)) {
			agent.attack(target, world);
		}
		agent.walkTowards(target, world, deltaTime);
	}),
	
	BUDDY((agent, world, deltaTime) -> {
		// Walk towards the target if we are out of "attack" range.
		Entity target = agent.getTarget();
		if (target != null && !agent.canAttack(target, world)) {
			agent.walkTowards(target, world, deltaTime);
		}
	}),
	
	FLY((agent, world, deltaTime) -> {
		// Hover above the enemy and attack from there. Should only use with flying units.
		Entity target = agent.getTarget();
		if (target == null) {
			return;
		}
		if (agent.canAttack(target, world)) {
			agent.attack(target, world);
			return;
		}
		
		// Fly to the point above the target entity.
		Vector flyPos = new Vector(target.getPos().getX(), agent.getPos().getY());
		agent.walkTowards(new Entity(flyPos), world, deltaTime);
	});
	
	private final AILogic logic;
	
	AIMode(AILogic logic) {
		this.logic = logic;
	}
	
	/** Public method for AI updates. The logic instance itself is never exposed. */
	public void update(AIAgent agent, World world, double deltaTime) {
		logic.update(agent, world, deltaTime);
	}

	/**
	 * Interface for AI behavior.
	 * @author lambdaviking
	 * 
	 * Classes implementing this interface should have no state. Instead, they should read state from the provided AIAgent.
	 * 
	 * Similarly, AILogic instances should not be serialized. Instead, an enum string should be saved.
	 * 
	 * Can treat this as a functional interface, but it's probably better practice to create subclasses.
	 */
	interface AILogic {
		
		void update(AIAgent agent, World world, double deltaTime);
		
	}

}
