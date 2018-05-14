package snorri.entities;

import java.util.HashMap;
import java.util.Map;

import snorri.animations.Animation;
import snorri.world.Vector;
import snorri.world.World;

public abstract class LongRangeAIUnit extends AIUnit {

	private static final long serialVersionUID = 1L;
	
	public static class ShootAttempt {
		
		private static Map<ShootAttempt, Boolean> canShoot = new HashMap<>();
		
		public final Vector start, goal;
		
		public ShootAttempt(Vector start, Vector goal) {
			this.start = start;
			this.goal = goal;
		}
		
		//TODO should store this in world for multiworld consistency
		
		public static void reset() {
			canShoot = new HashMap<>();
		}
		
		public static void save(ShootAttempt attempt, boolean result) {
			canShoot.put(attempt, result);
			canShoot.put(new ShootAttempt(attempt.goal, attempt.start), result);
		}
		
		public static Boolean check(ShootAttempt attempt) {
			return canShoot.get(attempt);
		}
		
		//FIXME
		@Override
		public int hashCode() {
			return Vector.hashVectorPair(start, goal);
		}
		
		@Override
		public boolean equals(Object other) {
			if (!(other instanceof ShootAttempt)) {
				return false;
			}
			return start.equals(((ShootAttempt) other).start) && goal.equals(((ShootAttempt) other).goal);
		}
		
	}
	
	protected LongRangeAIUnit(Vector pos, Entity target, Animation idle, Animation walking) {
		super(pos, target, idle, walking);
	}

	/**
	 * does a grid raycast to detect unpathable terrain blocking the shot
	 * @param target
	 * 		the entity we're tryna cap
	 * @return
	 * 		whether terrain obstructs the shot
	 */
	@Override
	public boolean canAttack(Entity target, World world) {
		
		// do this cheap check first for efficiency
		if (!super.canAttack(target, world)) {
			return false;
		}
		
		ShootAttempt attempt = new ShootAttempt(pos.copy().gridPos_(), target.pos.copy().gridPos_());
		Boolean b = ShootAttempt.check(attempt);
		if (b != null) {
			return b;
		}
		
		Vector step = target.pos.copy().sub_(pos).normalize_();
		Vector tempPos = pos.copy();
				
		//I'm checking if pos and target.pos are both okay just in case we're in a wall
		while (tempPos.distanceSquared(pos) <= target.pos.distanceSquared(pos)) {	
					
			if (! world.canShootOver(tempPos)) {
				ShootAttempt.save(attempt, false);
				return false;
			}
			
			if (tempPos.distanceSquared(pos) > attackRange * attackRange) {
				ShootAttempt.save(attempt, false);
				return false;
			}
			
			ShootAttempt.save(new ShootAttempt(pos.copy().gridPos_(), tempPos.copy().gridPos_()), true);
			tempPos.add_(step);	
		}

		ShootAttempt.save(attempt, true); //this line might not be necessary
		return true;
		
	}
	
	@Override
	public void updateEntityStats() {
		super.updateEntityStats();
		attackRange = 450;
		stopRange = 300;
	}
	
}
