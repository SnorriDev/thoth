package snorri.parser;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import snorri.nonterminals.AbstractNoun;
import snorri.nonterminals.Noun;
import snorri.nonterminals.Prep;
import snorri.semantics.Be;
import snorri.semantics.Definition;
import snorri.semantics.FirstObjectPronoun;
import snorri.semantics.FirstSuffixPronoun;
import snorri.semantics.Move;
import snorri.semantics.Nominal.AbstractSemantics;
import snorri.semantics.SecondObjectPronoun;
import snorri.semantics.SecondSuffixPronoun;
import snorri.semantics.StaticDef;
import snorri.semantics.ThirdObjectPronoun;
import snorri.semantics.ThirdSuffixPronoun;
import snorri.semantics.Walk;
import snorri.world.Tile;

public class Lexicon {
	
	private static Map<String, Definition> lexicon;
	
	//TODO: allow duplicate definitions? hash by String, POS
	//TODO: definitionSet
	
	public static void init() {
		
		lexicon = new HashMap<String, Definition>();
		
		//Prepositions
		lexicon.put("r", new StaticDef(Prep.class, null)); //to
		lexicon.put("m", new StaticDef(Prep.class, null)); //in
		lexicon.put("Xr", new StaticDef(Prep.class, null)); //under
		lexicon.put("tp", new StaticDef(Prep.class, null)); //above
		lexicon.put("HA", new StaticDef(Prep.class, null)); //behind
		lexicon.put("xft", new StaticDef(Prep.class, null)); //in front of
		//lexicon.put("in", new StaticDef(Prep.class, null)); //by (is this the locative "near" sense)?
		
		//TODO: add semantics for these prepositions
		
		//Nouns
		lexicon.put("jAm", new StaticDef(Noun.class, Tile.TREE)); //tree
		lexicon.put("ssn", new StaticDef(Noun.class, Tile.LILY)); //flower
		
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
		
		//Abstract Nouns
		lexicon.put("st", new StaticDef(AbstractNoun.class, AbstractSemantics.POSITION));
		lexicon.put("iry", new StaticDef(AbstractNoun.class, AbstractSemantics.WEAPON));
		lexicon.put("pr", new StaticDef(AbstractNoun.class, AbstractSemantics.TILE));
		lexicon.put("rn", new StaticDef(AbstractNoun.class, AbstractSemantics.NAME));
		
		//Verbs
		lexicon.put("in", new Move());
		lexicon.put("ini", new Move());
		lexicon.put("ms", new Move());
		lexicon.put("xpi", new Walk());
		lexicon.put("iw", new Be());
//		lexicon.put("mAA", new StaticDef(TransVerb.class, null)); //see
//		lexicon.put("sDm", new StaticDef(TransVerb.class, null)); //hear
		
		Grammar.loadLexicon();
		
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
