package snorri.entities;

import snorri.animations.Animation;
import snorri.inventory.Item;
import snorri.inventory.Item.ItemType;
import snorri.world.Vector;

public class Crocodile extends CloseRangeAIUnit{

	private static final long serialVersionUID = 1L;

	public static final Animation IDLE = new Animation("/textures/animations/crocodile/idle");
	public static final Animation WALK = new Animation("/textures/animations/crocodile/walk");

	public Crocodile(Vector pos, Entity target) {
		super(pos, target, new Animation(IDLE), new Animation(WALK));
	}

	public Crocodile(Vector pos) {
		this(pos, null);
	}

	@Override
	public void updateEntityStats() {
		super.updateEntityStats();
		getInventory().add(Item.newItem(ItemType.CROCODILE_BITE));
	}
	
}
