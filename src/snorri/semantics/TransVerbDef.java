package snorri.semantics;

import snorri.events.SpellEvent;
import snorri.nonterminals.TransVerb;
import snorri.parser.Node;

@SuppressWarnings("rawtypes")
public abstract class TransVerbDef extends VerbDef<Lambda<Object, Lambda>> {
	
	protected TransVerbDef() {
		super(TransVerb.class);
	}
	
	@Override
	public Lambda<Object, Lambda> getMeaning(SpellEvent e) {
		
		return new Lambda<Object, Lambda>(Object.class) {
			
			@Override
			public Lambda eval(Object arg1) {
				return new Lambda<Object, Boolean>(Object.class) {
					@Override
					public Boolean eval(Object arg2) {
						return TransVerbDef.this.eval(arg2, arg1, e);
					}
				};
			}
			
			@Override
			public Lambda exec(Node<Object> arg1) {
				return new Lambda<Object, Boolean>(Object.class) {
					@Override
					public Boolean exec(Node<Object> arg2) {
						return TransVerbDef.this.exec(arg1, e);
					}
				};
			}
			
		};

	}
	
	public abstract boolean eval(Object subject, Object object, SpellEvent e);
	
	public abstract boolean exec(Node<Object> object, SpellEvent e);

}
