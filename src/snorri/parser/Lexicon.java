package snorri.parser;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import snorri.nonterminals.Noun;
import snorri.nonterminals.Prep;
import snorri.nonterminals.TransVerb;
import snorri.semantics.StaticDef;

public class Lexicon {
	
	private static Map<String, Definition> lexicon;
	
	static {
		
		lexicon = new HashMap<String, Definition>();
		
		//Prepositions
		lexicon.put("r", new StaticDef(Prep.class, null)); //to (pos)
		lexicon.put("n", new StaticDef(Prep.class, null)); //to (ent)
		lexicon.put("m", new StaticDef(Prep.class, null)); //in/at
		
		//Nouns
		lexicon.put("jAm", new StaticDef(Noun.class, null)); //tree
		
		//Verbs
		lexicon.put("mAA", new StaticDef(TransVerb.class, null)); //see
		lexicon.put("sDm", new StaticDef(TransVerb.class, null)); //hear
				
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
