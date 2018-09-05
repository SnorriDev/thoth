package snorri.entities;

import snorri.ai.AIMode;
import snorri.animations.Animation;
import snorri.inventory.Item;
import snorri.inventory.Item.ItemType;
import snorri.world.Vector;

public class Cobra extends AIUnit {

	private static final long serialVersionUID = 1L;
	
	public static final Animation IDLE = new Animation("/textures/animations/cobra");
	
	public Cobra(Vector pos, Entity target) {
		super(pos, target, new Animation(IDLE), new Animation(IDLE));
		add(Item.newItem(ItemType.VENOM));
		add(Item.newItem(ItemType.SNAKE_BITE));
	}
	
	public Cobra(Vector pos) {
		this(pos, null);
	}
	
	@Override
	public void refreshStats() {
		super.refreshStats();
		speed = 195;
	}

	@Override
	protected int getAttackRange() {
		return 100;
	}
	
	@Override
	public AIMode getDefaultMode() {
		return AIMode.CHARGE;
	}
	
}
