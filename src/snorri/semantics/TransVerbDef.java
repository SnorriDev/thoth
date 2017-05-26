package snorri.semantics;

import snorri.events.SpellEvent;
import snorri.nonterminals.TransVerb;
import snorri.parser.Node;

@SuppressWarnings("rawtypes")
public abstract class TransVerbDef extends VerbDef<Lambda<Object, Lambda>> {
	
	private static final Category INNER_CAT = new Category(Object.class, Boolean.class);
	private static final Category OUTER_CAT = new Category(Object.class, INNER_CAT);
	
	protected TransVerbDef() {
		super(TransVerb.class, OUTER_CAT);
	}
	
	@Override
	public Lambda<Object, Lambda> getMeaning(SpellEvent e) {
		
		return new Lambda<Object, Lambda>(OUTER_CAT) {
			
			@Override
			public Lambda eval(Object arg1) {
				return new Lambda<Object, Boolean>(INNER_CAT) {
					@Override
					public Boolean eval(Object arg2) {
						return TransVerbDef.this.eval(arg1, arg2, e);
					}
				};
			}
			
			@Override
			public Lambda exec(Node<Object> arg1) {
				return new Lambda<Object, Boolean>(INNER_CAT) {
					@Override
					public Boolean exec(Node<Object> arg2) {
						return TransVerbDef.this.exec(arg2, e);
					}
				};
			}
			
		};

	}
	
	public abstract boolean eval(Object subject, Object object, SpellEvent e);
	
	public abstract boolean exec(Node<Object> object, SpellEvent e);

}
