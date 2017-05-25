package snorri.semantics;

import snorri.events.SpellEvent;
import snorri.nonterminals.IntransVerb;
import snorri.parser.Node;

public abstract class IntransVerbDef extends VerbDef<Lambda<Object, Boolean>> {

	private static final Category CATEGORY = new Category(Object.class, Boolean.class);
	
	protected IntransVerbDef() {
		super(IntransVerb.class, CATEGORY);
	}
	
	@Override
	public Lambda<Object, Boolean> getMeaning(SpellEvent e) {
		
		return new Lambda<Object, Boolean>(CATEGORY) {
			
			@Override
			public Boolean eval(Object arg1) {
				return IntransVerbDef.this.eval(arg1, e);
			};
			
			@Override
			public Boolean exec(Node<Object> arg1) {
				return IntransVerbDef.this.exec(e);
			}
			
		};
	}
	
	public abstract boolean eval(Object subject, SpellEvent e);
	
	public abstract boolean exec(SpellEvent e);

}
