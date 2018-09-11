package snorri.entities;

import snorri.animations.Animation;
import snorri.events.CollisionEvent;
import snorri.world.Vector;

public class Bomb extends Detector {

	private static final long serialVersionUID = 1L;
	private static final double DAMAGE = 100;
	
	private static final Animation UNEXPLODED = new Animation("/textures/objects/bomb.png");
	
	public Bomb(Vector pos) {
		super(pos, 10);
		animation = UNEXPLODED;
		age = -1;
	}
	
	@Override
	public void refreshStats() {
		ignoreCollisions = true;
	}

	@Override
	public void onCollision(CollisionEvent e) {
		explode(e.getWorld(), DAMAGE);
	}

}
