package snorri.semantics;

import snorri.events.SpellEvent;
import snorri.nonterminals.Connective;
import snorri.parser.Node;

@SuppressWarnings("rawtypes")
public abstract class ConnectiveDef extends Definition<Lambda<Boolean, Lambda>> {
	
	private static final Category INNER_CAT = new Category(Boolean.class, Boolean.class);
	private static final Category OUTER_CAT = new Category(Boolean.class, INNER_CAT);
	
	public ConnectiveDef(Class<? extends Connective> c) {
		super(c, OUTER_CAT);
	}
	
	@Override
	public Lambda<Boolean, Lambda> getMeaning(SpellEvent e) {
				
		return new Lambda<Boolean, Lambda>(OUTER_CAT) {
			
			@Override
			public Lambda eval(Boolean arg1) {
				return new Lambda<Boolean, Boolean>(INNER_CAT) {
					@Override
					public Boolean eval(Boolean arg2) {
						return ConnectiveDef.this.eval(arg1, arg2, e);
					}
				};
			}
			
			@Override
			public Lambda exec(Node<Boolean> arg1) {
				return new Lambda<Boolean, Boolean>(INNER_CAT) {
					@Override
					public Boolean exec(Node<Boolean> arg2) {
						return ConnectiveDef.this.exec(arg1, arg2, e);
					}
				};
			}
			
		};
	}
		
	/**
	 * Execute two connected commands
	 * @param arg1 The first argument to the connective
	 * @param arg2 The second argument to the connective
	 * @return Whether or not anything executed correctly
	 */
	public abstract boolean exec(Node<Boolean> arg1, Node<Boolean> arg2, SpellEvent e);

	/**
	 * Evaluate the truth value with two truth valued arguments
	 * @param arg1 the first argument to the connective
	 * @param arg2 the second argument to the connective
	 * @return A truth value
	 */
	public abstract boolean eval(boolean arg1, boolean arg2, SpellEvent e);

}