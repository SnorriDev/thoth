package snorri.entities;

import snorri.world.Vector;
import snorri.world.World;

public class Projectile extends Collider {

	private static final int PROJECTILE_SPEED = 100;
	
	public static final int LIFE_SPAN = 4000; //number of milliseconds towards despawn
	private long age;
	
	private Vector velocity;
	
	private Entity root;
	
	public Projectile(Entity root, Vector rootVelocity, Vector path) {
		super(root.getPos().copy(), 1); //radius of a projectile is 1
		velocity = rootVelocity.copy().add(path.copy().multiply(PROJECTILE_SPEED));
		this.root = root;
		age = 0;
	}

	public Entity getRoot() {
		return root;
	}
	
	@Override
	public void update(World world, float deltaTime) {
		
		pos.add(velocity.copy().multiply(deltaTime));
				
		age += deltaTime;
		if (age > LIFE_SPAN) {
			world.deleteSoft(this);
		}
		
	}

	@Override
	public void onCollision(CollisionEvent e) {
		
		if (e.getTarget().equals(root)) {
			return;
		}
		
		//TODO: "damage" root
		
		e.getWorld().deleteHard(this); //could use removeFrom, but this is a little better
				
	}

}
