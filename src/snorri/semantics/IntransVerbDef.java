package snorri.semantics;

import snorri.events.SpellEvent;
import snorri.nonterminals.IntransVerb;
import snorri.parser.Node;

public abstract class IntransVerbDef extends VerbDef<Lambda<Object, Boolean>> {

	protected IntransVerbDef() {
		super(IntransVerb.class);
	}
	
	@Override
	public Lambda<Object, Boolean> getMeaning(SpellEvent e) {
		return new Lambda<Object, Boolean>(Object.class) {
			
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
