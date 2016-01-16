package snorri.semantics;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import snorri.nonterminals.Sentence;

public class Lexicon {
	
	private static Map<String, Definition> lexicon;
	
	static {
		
		lexicon = new HashMap<String, Definition>();
		
		//not legit
		lexicon.put("ka", new Definition(Sentence.class, null));
		
	}
	
	public static Definition lookup(String form) {
		if (lexicon.containsKey(form))
			return lexicon.get(form);
		return null;
	}
	
	public static Set<Entry<String, Definition>> getAllTerminals() {
		return lexicon.entrySet();
	}
	
}
