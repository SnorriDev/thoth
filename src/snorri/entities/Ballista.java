package snorri.entities;

import snorri.animations.Animation;
import snorri.main.FocusedWindow;
import snorri.main.Main;
import snorri.world.Vector;
import snorri.world.World;

public class Ballista extends Entity {

	private static final long serialVersionUID = 1L;
	
	private static final Animation IDLE = new Animation("/textures/animations/ballista/idle");
	private static final Animation SHOOT = new Animation("/textures/animations/ballista/shoot");
	
	private boolean shooting = false;
	private Entity target;
	
	public Ballista(Vector pos, Vector dir) {
		super(pos, 35);
		setAnimation(IDLE);	
		setDirection(dir);
		shoot();
	}
	
	@Override
	public void update(World world, double d) {
		super.update(world, d);
		if (target == null) {
			target = ((FocusedWindow<?>) Main.getWindow()).getFocus();
		}
	}
	
	//TODO pass spell event, use shooter interface
	public void shoot() {
		setAnimation(SHOOT);
		shooting = true;
	}
	
	@Override
	public void onCycleComplete(World world) {
		if (shooting) {
			setAnimation(IDLE);
			shooting = false;
		}
	}

}
