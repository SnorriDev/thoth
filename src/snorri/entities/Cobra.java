package snorri.entities;

import snorri.animations.Animation;
import snorri.inventory.Droppable;
import snorri.inventory.Inventory;
import snorri.inventory.Item;
import snorri.inventory.Item.ItemType;
import snorri.world.Vector;

public class Cobra extends CloseRangeAIUnit {

	private static final long serialVersionUID = 1L;
	
	public static final Animation IDLE = new Animation("/textures/animations/cobra");
	
	public Cobra(Vector pos, Entity target) {
		super(pos, target, new Animation(IDLE));
	}
	
	public Cobra(Vector pos) {
		this(pos, null);
	}
	
	@Override
	public void updateEntityStats() {
		super.updateEntityStats();
		speed = 195;
		getInventory().add(Item.newItem(ItemType.VENOM));
		getInventory().add(Item.newItem(ItemType.SNAKE_BITE));
	}
	
}
