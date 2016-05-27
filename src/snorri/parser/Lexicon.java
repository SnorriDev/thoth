package snorri.parser;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import snorri.nonterminals.Noun;
import snorri.nonterminals.Prep;
import snorri.nonterminals.TransVerb;
import snorri.semantics.Definition;
import snorri.semantics.FirstObjectPronoun;
import snorri.semantics.FirstSuffixPronoun;
import snorri.semantics.SecondObjectPronoun;
import snorri.semantics.SecondSuffixPronoun;
import snorri.semantics.StaticDef;
import snorri.semantics.ThirdObjectPronoun;
import snorri.semantics.ThirdSuffixPronoun;

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
		
		//Suffix Pronouns
		lexicon.put("i", new FirstSuffixPronoun());
		lexicon.put("k", new SecondSuffixPronoun());
		lexicon.put("t", new SecondSuffixPronoun()); //maybe add gender agreement later
		lexicon.put("s", new ThirdSuffixPronoun());
		lexicon.put("f", new ThirdSuffixPronoun()); //maybe add gender agreement
		
		//Object Pronouns
		lexicon.put("wi", new FirstObjectPronoun());
		lexicon.put("w", new FirstObjectPronoun());
		lexicon.put("tn", new SecondObjectPronoun());
		lexicon.put("sw", new ThirdObjectPronoun());
		lexicon.put("sy", new ThirdObjectPronoun());
		lexicon.put("s", new ThirdObjectPronoun());
		lexicon.put("st", new ThirdObjectPronoun());
		
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
