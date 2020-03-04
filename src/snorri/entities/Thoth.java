package snorri.entities;

import snorri.animations.Animation;
import snorri.inventory.Spell;
import snorri.world.Vector;

public class Thoth extends BossAIUnit {

	private static final long serialVersionUID = 1L;
	private static final Animation THOTH = new Animation("/textures/animations/thoth");
	private static final Spell SPELL = Spell.fromString("qmA txn n sy");
	
	public Thoth(Vector pos, Entity target) {
		super(pos, target, new Animation(THOTH), new Animation(THOTH));
		getInventory().getPapyrus().setSpell(SPELL);
	}

	@Override
	protected int getAttackRange() {
		return 900;
	}
	
}
