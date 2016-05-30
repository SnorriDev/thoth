package snorri.semantics;

import snorri.events.SpellEvent;
import snorri.nonterminals.Prep;
import snorri.world.Tile;

public abstract class PrepDef extends Definition {
	
	protected static final int DISPLACE_DISTANCE = Tile.WIDTH * 3;
	
	protected SpellEvent e;
	
	public PrepDef() {
		super(Prep.class);
	}

	@Override
	public Object getMeaning(SpellEvent e) {		
		this.e = new SpellEvent(e);
		return this;
	}
		
	/**
	 * do the action associated with the imperative mood of this verb
	 * @param obj the direct object associated with a verb (null for intransitive usage)
	 * @return a location
	 */
	public abstract SpellEvent getModified(Nominal obj);

}