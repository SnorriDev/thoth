package snorri.entities;

import snorri.animations.Animation;
import snorri.events.CastEvent.Caster;
import snorri.grammar.ChartParser;
import snorri.grammar.Lexicon;
import snorri.inventory.Spell;
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
	
	protected void setSpell(String text) {
		Spell spell = Spell.fromString(text);
		getInventory().getPapyrus().setSpell(spell);
		for (String word: ChartParser.tokenize(text)) {
			getLexicon().add(word);
		}
	}
	
	@Override
	public boolean canAttack(Entity target, World world) {
		if (getInventory() == null || getInventory().getPapyrus() == null) {
			return false;
		}
		return getTarget().getPos().distanceSquared(pos) < getAttackRange() * getAttackRange() && getInventory().getPapyrus().canUse();
	}
	
	@Override
	public void attack(Entity target, World world) {
		getInventory().getPapyrus().castPrewritten(world, this);
	}
		
	@Override
	public void refreshStats() {
		super.refreshStats();
		lexicon = new Lexicon();
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
		return 100; // No actual mana currently.
	}
	
}
