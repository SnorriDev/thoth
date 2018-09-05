package snorri.entities;

import snorri.animations.Animation;
import snorri.inventory.Item;
import snorri.inventory.Item.ItemType;
import snorri.world.Vector;

public class Mummy extends AIUnit {

	private static final long serialVersionUID = 1L;
	
	private static final Animation IDLE = new Animation("/textures/animations/mummy/idle");
	private static final Animation WALKING = new Animation("textures/animations/mummy/walking");
	
	public Mummy(Vector pos) {
		this(pos, null);
	}
	
	public Mummy(Vector pos, Entity target) {
		super(pos, target, IDLE, WALKING);
		add(Item.newItem(ItemType.PELLET));
		add(Item.newItem(ItemType.SLOW_SLING));
	}

	@Override
	protected int getAttackRange() {
		return 900;
	}

}
