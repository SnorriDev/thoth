package snorri.semantics;

import snorri.events.CastEvent;
import snorri.nonterminals.Prep;
import snorri.world.Tile;

public abstract class PrepDef extends Definition<Lambda<Nominal, CastEvent>> {
	
	protected static final int DISPLACE_DISTANCE = Tile.WIDTH * 3;
	private static final Category CATEGORY = new Category(Nominal.class, CastEvent.class);
	
	protected CastEvent e;
	
	public PrepDef() {
		super(Prep.class, CATEGORY);
	}

	//unify these arguments with verb objects
	
	@Override
	public Lambda<Nominal, CastEvent> getMeaning(final CastEvent e) {
		return new Lambda<Nominal, CastEvent>(CATEGORY) {	
			@Override
			public CastEvent eval(Nominal obj) {
				return PrepDef.this.eval(obj, new CastEvent(e));
			}
		};
	}
		
	/**
	 * Returns modified context given the prepositional object
	 * @param obj the object of the preposition
	 * @return the new SpellEvent
	 */
	public abstract CastEvent eval(Nominal obj, CastEvent e);

}