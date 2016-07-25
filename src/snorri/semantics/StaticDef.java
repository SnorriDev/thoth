package snorri.semantics;

import snorri.events.SpellEvent;
import snorri.nonterminals.SemiTerminal;

public class StaticDef extends Definition {

	private Object meaning;
	
	public StaticDef(Class<? extends SemiTerminal> partOfSpeech, Object meaning) {
		super(partOfSpeech);
		this.meaning = meaning;
	}
	
	@Override
	public Object getMeaning(SpellEvent e) {
		return meaning;
	}

	@Override
	public String getShortDesc() {
		return meaning.toString();
	}

}