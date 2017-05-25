package snorri.entities;

import snorri.animations.Animation;
import snorri.inventory.Item;
import snorri.inventory.Item.ItemType;
import snorri.nonterminals.Sentence;
import snorri.parser.Grammar;
import snorri.world.Vector;

public class Thoth extends BossAIUnit {

	private static final long serialVersionUID = 1L;
	private static final Animation THOTH = new Animation("/textures/animations/thoth");
	private static final Sentence SPELL = Grammar.parseSentence("qmA txn n sy");
	
	public Thoth(Vector pos, Entity target) {
		super(pos, target, new Animation(THOTH), new Animation(THOTH));
	}
	
	public Thoth(Vector pos) {
		this(pos, null);
	}
	
	@Override
	public void updateEntityStats() {
		super.updateEntityStats();
		Item papyrus = Item.newItem(ItemType.PAPYRUS);
		papyrus.setSpell(SPELL);
		inventory.add(papyrus);
	}
	
}
