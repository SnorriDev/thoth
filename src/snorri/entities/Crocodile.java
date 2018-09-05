package snorri.entities;

import snorri.animations.Animation;
import snorri.collisions.RectCollider;
import snorri.inventory.Item;
import snorri.inventory.Item.ItemType;
import snorri.world.Vector;

public class Crocodile extends AIUnit {

	private static final long serialVersionUID = 1L;

	public static final Animation IDLE = new Animation("/textures/animations/crocodile/idle");
	public static final Animation ATTACK = new Animation("/textures/animations/crocodile/attack");

	public Crocodile(Vector pos, Entity target) {
		super(pos, target, new RectCollider(120, 40), new Animation(IDLE), new Animation(ATTACK));
	}

	public Crocodile(Vector pos) {
		this(pos, null);
	}

	@Override
	public void refreshStats() {
		super.refreshStats();
		getInventory().add(Item.newItem(ItemType.CROCODILE_BITE));
	}

	@Override
	protected int getAttackRange() {
		return 100;
	}
	
}
