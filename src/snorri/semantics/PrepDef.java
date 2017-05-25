package snorri.semantics;

import snorri.events.SpellEvent;
import snorri.nonterminals.Prep;
import snorri.semantics.Lambda.Category;
import snorri.world.Tile;

public abstract class PrepDef extends Definition<Lambda<Nominal, SpellEvent>> {
	
	protected static final int DISPLACE_DISTANCE = Tile.WIDTH * 3;
	
	protected SpellEvent e;
	
	public PrepDef() {
		super(Prep.class);
	}

	//unify these arguments with verb objects
	
	@Override
	public Lambda<Nominal, SpellEvent> getMeaning(final SpellEvent e) {
		Category category = new Category(Nominal.class, SpellEvent.class);
		return new Lambda<Nominal, SpellEvent>(category) {	
			@Override
			public SpellEvent eval(Nominal obj) {
				return PrepDef.this.eval(obj, new SpellEvent(e));
			}
		};
	}
		
	/**
	 * Returns modified context given the prepositional object
	 * @param obj the object of the preposition
	 * @return the new SpellEvent
	 */
	public abstract SpellEvent eval(Nominal obj, SpellEvent e);

}