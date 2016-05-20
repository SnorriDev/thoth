package snorri.entities;

import snorri.main.Main;
import snorri.world.Vector;

public class Projectile extends Collider {

	private static final int PROJECTILE_SPEED = 5;
	
	public static final int LIFE_SPAN = 4000; //number of milliseconds towards despawn
	
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
	public void update() {
		
		Main.log(velocity);
		if (velocity.equals(Vector.ZERO)) {
			Main.log(pos);
		}
		
		pos.add(velocity);
	}

	@Override
	public void onCollision(CollisionEvent e) {
	}

}
