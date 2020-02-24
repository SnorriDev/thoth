package snorri.physics;

import snorri.world.Vector;

public enum SurfaceCollisionMode {
	
	/** Mode for colliding with a wall. */
	
	// Always lose all velocity.
	STOP((oldVelocity, above) -> Vector.ZERO),
	// Always reverse velocity on y axis.
	BOUNCE((oldVelocity, above) -> new Vector(oldVelocity.x, -oldVelocity.y)),
	// Stop on top of a tile and bounce off the bottom.
	STOP_ABOVE_BOUNCE_BELOW((oldVelocity, above) -> {
		if (above) {
			return STOP.getNewVelocity(oldVelocity, above);
		} else {
			return BOUNCE.getNewVelocity(oldVelocity, above);
		}
	});
	
	private SurfaceCollisionLogic logic;
	
	SurfaceCollisionMode(SurfaceCollisionLogic logic) {
		this.logic = logic;
	}
	
	public Vector getNewVelocity(Vector oldVelocity, boolean above) {
		return logic.getNewVelocity(oldVelocity, above);
	}
	
	private interface SurfaceCollisionLogic {
		Vector getNewVelocity(Vector oldVelocity, boolean above);
	}
	
}