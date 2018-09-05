package snorri.entities;

import snorri.ai.AIAgent;
import snorri.animations.Animation;
import snorri.events.SpellEvent.Caster;
import snorri.inventory.Carrier;
import snorri.nonterminals.Sentence;
import snorri.parser.Grammar;
import snorri.parser.Lexicon;
import snorri.world.Vector;
import snorri.world.World;

public abstract class BossAIUnit extends AIUnit implements Caster {

	private static final long serialVersionUID = 1L;
	
	protected Lexicon lexicon;
	
	private int attackRange;

	public BossAIUnit(Vector pos) {
		this(pos, null, null, null);
	}
	
	protected BossAIUnit(Vector pos, Entity target, Animation idle, Animation walking) {
		super(pos, idle, walking);
		setTarget(target);
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
		if (getInventory() == null || getInventory().getPapyrus() == null) {
			return false;
		}
		return getTarget().getPos().distanceSquared(pos) < attackRange * attackRange && getInventory().getPapyrus().canUse();
	}
	
	@Override
	public void attack(Entity target, World world) {
		getInventory().getPapyrus().castPrewritten(world, this);
	}
	
	@Override
	public void updateEntityStats() {
		super.updateEntityStats();
		lexicon = new Lexicon();
		setAttackRange(900);
	}
	
	@Override
	public Vector getAimPosition() {
		if (getTarget() == null) {
			return pos.copy();
		}
		return getTarget().getPos().copy();
	}
	
	@Override
	public Lexicon getLexicon() {
		return lexicon;
	}
	
	@Override
	public double getMana() {
		return 100; // no actual mana currently
	}
	
	public void setAttackRange(int attackRange) {
		this.attackRange = attackRange;
	}
	
}
