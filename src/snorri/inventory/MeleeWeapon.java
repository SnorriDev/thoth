package snorri.inventory;

import snorri.collisions.Collider;
import snorri.entities.Entity;
import snorri.entities.Unit;
import snorri.world.Vector;
import snorri.world.World;

public class MeleeWeapon extends Weapon {

	private static final long serialVersionUID = 1L;
	
	public MeleeWeapon(ItemType t) {
		super(t);
	}
	
	public int getOffsetMagnitude() {
		return (int) type.getProperty(3);
	}
	
	public Collider getCollider() {
		return (Collider) type.getProperty(4);
	}
	
	@Override
	public boolean attack(World world, Entity focus, Vector movement, Vector dir, Orb orb) {
		if (timer.activate()) {
			Vector pos = focus.getPos().copy().add(dir.copy().scale(getOffsetMagnitude()));
			Entity checker = new Entity(pos, getCollider());
			world.getEntityTree().mapOverCollisions(checker, e -> {
				if (e != focus && e instanceof Unit) {
					((Unit) e).damage(getSharpness());
				}
			});
			return true;
		}
		return false;
	}
	//TODO override attack behavior here

}
