package snorri.parser;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import snorri.entities.Flower;
import snorri.nonterminals.AbstractNoun;
import snorri.nonterminals.Noun;
import snorri.nonterminals.Prep;
import snorri.semantics.Above;
import snorri.semantics.At;
import snorri.semantics.Be;
import snorri.semantics.Boom;
import snorri.semantics.Burn;
import snorri.semantics.Definition;
import snorri.semantics.DegreeModifierDef;
import snorri.semantics.FirstObjectPronoun;
import snorri.semantics.FirstSuffixPronoun;
import snorri.semantics.Flood;
import snorri.semantics.Fly;
import snorri.semantics.Greatly;
import snorri.semantics.Heal;
import snorri.semantics.LeftOf;
import snorri.semantics.Move;
import snorri.semantics.Nominal.AbstractSemantics;
import snorri.semantics.Not;
import snorri.semantics.RightOf;
import snorri.semantics.SecondObjectPronoun;
import snorri.semantics.SecondSuffixPronoun;
import snorri.semantics.StaticDef;
import snorri.semantics.ThirdObjectPronoun;
import snorri.semantics.ThirdSuffixPronoun;
import snorri.semantics.To;
import snorri.semantics.Under;
import snorri.semantics.Walk;
import snorri.semantics.With;
import snorri.world.Tile.TileType;

public class Lexicon {
	
	/**
	 * maps strings -> meaning
	 * currently, each word can only have one meaning,
	 * although this may change
	 */
	private static Map<String, Definition> lexicon;
	
	//TODO: allow duplicate definitions? hash by String, POS
	//TODO: definitionSet
	
	public static void init() {
		
		lexicon = new HashMap<String, Definition>();
		
		//Prepositions
		lexicon.put("n", new StaticDef(Prep.class, new To())); //to
		lexicon.put("m", new StaticDef(Prep.class, new At())); //in
		lexicon.put("Xr", new StaticDef(Prep.class, new Under())); //under
		lexicon.put("tp", new StaticDef(Prep.class, new Above())); //above
		lexicon.put("HA", new StaticDef(Prep.class, new LeftOf())); //behind
		lexicon.put("xft", new StaticDef(Prep.class, new RightOf())); //in front of
		lexicon.put("Hna", new StaticDef(Prep.class, new With())); //with
		lexicon.put("mhAw", new StaticDef(Prep.class, null)); //around
		
		//Adverbs
		lexicon.put("nn", new Not());
		lexicon.put("aAw", new Greatly());
		lexicon.put("wrt", new DegreeModifierDef(1));
		lexicon.put("er", new DegreeModifierDef(2)); //TODO is this correct enough usage?
		
		//Nouns
		lexicon.put("jAm", new StaticDef(Noun.class, TileType.TREE)); //tree
		lexicon.put("ssn", new StaticDef(Noun.class, Flower.class)); //flower, gotta make this an entity
		lexicon.put("mw", new StaticDef(Noun.class, TileType.WATER));
		lexicon.put("xt", new StaticDef(Noun.class, TileType.LAVA)); //technically this is fire
		lexicon.put("Axt", new Flood()); //TODO: flood as noun
		lexicon.put("bit", new StaticDef(Noun.class, null)); //TODO: bee
		
		lexicon.put("st", new StaticDef(AbstractNoun.class, AbstractSemantics.POSITION));
		lexicon.put("iry", new StaticDef(AbstractNoun.class, AbstractSemantics.WEAPON));
		lexicon.put("pr", new StaticDef(AbstractNoun.class, AbstractSemantics.TILE));
		lexicon.put("rn", new StaticDef(AbstractNoun.class, AbstractSemantics.NAME));
		lexicon.put("nb", new StaticDef(AbstractNoun.class, AbstractSemantics.SOURCE)); //means lord literally
		lexicon.put("nbt", new StaticDef(AbstractNoun.class, AbstractSemantics.SOURCE)); //means lord literally
		
		//cactus, reeds
		//words for "create" sxpr "build/create"; qmA "create"; two levels of building
						
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
		
		//Verbs
		lexicon.put("in", new Move());
		lexicon.put("ini", new Move());
		lexicon.put("ms", new Move());
		lexicon.put("xpi", new Walk());
		lexicon.put("iw", new Be());
		lexicon.put("bm", new Boom());
		lexicon.put("mAX", new Burn());
		lexicon.put("nD", new Heal()); //literally means "protect"
		lexicon.put("pA", new Fly());	
		lexicon.put("mAA", new See()); //see
		
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
	
	public static Set<String> getELang() {
		return lexicon.keySet();
	}
	
	public static Collection<Definition> getILang() {
		return lexicon.values();
	}
	
}
