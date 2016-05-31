package snorri.entities;

import snorri.events.CollisionEvent;
import snorri.world.Vector;
import snorri.world.World;

public class Projectile extends Collider {

	private static final int PROJECTILE_SPEED = 100;
		
	private Vector velocity;
	private Entity root;
	
	public Projectile(Entity root, Vector rootVelocity, Vector path) {
		super(root.getPos().copy(), 1); //radius of a projectile is 1
		velocity = rootVelocity.copy().add(path.copy().multiply(PROJECTILE_SPEED));
		this.root = root;
	}

	public Entity getRoot() {
		return root;
	}
	
	@Override
	public void update(World world, float deltaTime) {
		
		pos.add(velocity.copy().multiply(deltaTime));
		super.update(world, deltaTime);
		
	}

	@Override
	public void onCollision(CollisionEvent e) {
		
		if (e.getTarget().equals(root)) {
			return;
		}
		
		//TODO: "damage" root
		
		e.getWorld().deleteSoft(this); //could use removeFrom, but this is a little better
				
	}

}
