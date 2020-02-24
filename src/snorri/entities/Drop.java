package snorri.entities;

import snorri.events.CollisionEvent;
import snorri.inventory.Droppable;
import snorri.inventory.Item;
import snorri.inventory.RandomDrop;
import snorri.windows.DialogMap;
import snorri.world.Vector;
import snorri.world.World;

public class Drop extends Detector {
	
	private static final long serialVersionUID = 1L;
	
	private final Droppable prize;

	
	public Drop(Vector pos, Droppable prize) {
		super(pos, 15);
		this.prize = prize;
		age = -1;
		ignoreCollisions = true;
		animation = prize.getAnimation();
	}
	
	public Drop(Vector pos, String prize, String spell) {
		this(pos, Droppable.fromStringWithSpell(prize, spell));
	}
	
	public Droppable getPrize() {
		if (prize instanceof RandomDrop) {
			return ((RandomDrop) prize).getDroppable();
		}
		return prize;
	}
	
	@Override
	public void onCollision(CollisionEvent e) {
		if (e.getTarget() instanceof Player) {
			((Player) e.getTarget()).add(getPrize());
			e.getWorld().delete(this);
		}
	}

	public String getPrizeString() {
		if (prize == null) {
			return "[null]";
		}
		return prize.toString();
	}

	@Override
	public DialogMap prepareDialogMap() {
		DialogMap inputs = super.prepareDialogMap();
		inputs.put("Prize", getPrizeString());
		Droppable prize = getPrize();
		inputs.put("Spell", (prize instanceof Item && ((Item) prize).getSpell() != null) ? ((Item) prize).getSpell().getOrthography() : "");
		return inputs;
	}
	
	@Override
	public void processDialogMap(DialogMap inputs, World world) {
		super.processDialogMap(inputs, world);
		world.delete(this);
		world.add(new Drop(getPos().copy(), inputs.getText("Prize"), inputs.getText("Spell")));
	}

}
