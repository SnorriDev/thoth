package snorri.entities;

import snorri.animations.Animation;
import snorri.events.SpellEvent.Caster;
import snorri.nonterminals.Sentence;
import snorri.parser.Grammar;
import snorri.parser.Lexicon;
import snorri.world.Vector;
import snorri.world.World;

public abstract class BossAIUnit extends AIUnit implements Caster {

	private static final long serialVersionUID = 1L;
	
	protected Lexicon lexicon;

	public BossAIUnit(Vector pos) {
		this(pos, null, null, null);
	}
	
	protected BossAIUnit(Vector pos, Entity target, Animation idle, Animation walking) {
		super(pos, target, idle, walking);
	}
	
	protected void setSpell(String spell) {
		Sentence sentence = Grammar.parseSentence(spell);
		getInventory().getPapyrus().setSpell(sentence);
		for (String word: Grammar.getWords(spell)) {
			getLexicon().add(word);
		}
	}
	
	// should override these methods for more complex behavior
	
	@Override
	public boolean canAttack(Entity target, World world) {
		if (inventory == null || inventory.getPapyrus() == null) {
			return false;
		}
		return target.pos.distanceSquared(pos) < attackRange * attackRange && inventory.getPapyrus().canUse();
	}
	
	@Override
	public void attack(World world, Entity e) {
		// knows to aim for target in getAimPosition
		inventory.getPapyrus().castPrewritten(world, this);
	}
	
	@Override
	public void updateEntityStats() {
		super.updateEntityStats();
		lexicon = new Lexicon();
		attackRange = 900;
	}
	
	@Override
	public Vector getAimPosition() {
		if (target == null) {
			return pos.copy();
		}
		return target.pos.copy();
	}
	
	@Override
	public Lexicon getLexicon() {
		return lexicon;
	}
	
}
