package snorri.semantics;

import snorri.events.SpellEvent;
import snorri.nonterminals.NonTerminal;

public class StaticDef extends Definition {

	private Object meaning;
	
	public StaticDef(Class<? extends NonTerminal> partOfSpeech, Object meaning) {
		super(partOfSpeech);
		this.meaning = meaning;
	}
	
	@Override
	public Object getMeaning(SpellEvent e) {
		return meaning;
	}

}