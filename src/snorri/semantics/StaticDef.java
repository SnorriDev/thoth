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
		if (meaning == null) {
			return "unknown";
		}
		if (meaning instanceof Class<?>) {
			return ((Class<?>) meaning).getSimpleName().toLowerCase();
		}
		return meaning.toString();
	}

}