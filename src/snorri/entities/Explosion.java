package snorri.entities;

import snorri.animations.Animation;
import snorri.events.CollisionEvent;
import snorri.world.Vector;
import snorri.world.World;

public class Explosion extends Detector {

	private static final long serialVersionUID = 1L;
	private static final Animation ANIMATION = new Animation("/textures/animations/explosion");
	
	private double damage = 100;
	
	public Explosion(Vector pos, int r) {
		super(pos, r);
		animation = new Animation(ANIMATION);
	}
	
	public Explosion(Vector pos) {
		this(pos, 10);
	}
	
	public Explosion(Vector pos, int r, double damage) {
		this(pos, r);
		this.damage = damage;
	}
	
	public Explosion(Vector pos, double damage) {
		this(pos);
		this.damage = damage;
	}
	
	@Override
	public void update(World world, double deltaTime) {
		super.update(world, deltaTime);
	}

	@Override
	public void onCollision(CollisionEvent e) {
		if (e.getTarget() instanceof Unit) {
			((Unit) e.getTarget()).damage(damage);
		}
	}
	
	@Override
	protected boolean shouldDespawn() {
		return animation == null || animation.hasCycled();
	}

}
