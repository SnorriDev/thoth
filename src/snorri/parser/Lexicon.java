	package snorri.parser;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import snorri.entities.Mummy;
import snorri.entities.Entity;
import snorri.entities.Flower;
import snorri.entities.Glyph;
import snorri.entities.Spike;
import snorri.entities.Urn;
import snorri.inventory.Droppable;
import snorri.inventory.VocabDrop;
import snorri.nonterminals.AbstractNoun;
import snorri.nonterminals.Name;
import snorri.nonterminals.Noun;
import snorri.semantics.Above;
import snorri.semantics.At;
import snorri.semantics.Be;
import snorri.semantics.Boom;
import snorri.semantics.Break;
import snorri.semantics.Burn;
import snorri.semantics.ConditionalDef;
import snorri.semantics.CreateObject;
import snorri.semantics.Cross;
import snorri.semantics.Damage;
import snorri.semantics.Definition;
import snorri.semantics.DegreeModifierDef;
import snorri.semantics.FirstObjectPronoun;
import snorri.semantics.FirstSuffixPronoun;
import snorri.semantics.Fly;
import snorri.semantics.Greatly;
import snorri.semantics.Grow;
import snorri.semantics.Heal;
import snorri.semantics.LeftOf;
import snorri.semantics.Move;
import snorri.semantics.Nominal.AbstractSemantics;
import snorri.semantics.Not;
import snorri.semantics.Open;
import snorri.semantics.Pray;
import snorri.semantics.RightOf;
import snorri.semantics.SecondObjectPronoun;
import snorri.semantics.SecondSuffixPronoun;
import snorri.semantics.See;
import snorri.semantics.Slow;
import snorri.semantics.StaticDef;
import snorri.semantics.ThirdObjectPronoun;
import snorri.semantics.ThirdSuffixPronoun;
import snorri.semantics.To;
import snorri.semantics.Under;
import snorri.semantics.Go;
import snorri.semantics.With;
import snorri.semantics.Write;
import snorri.world.Tile.TileType;

public class Lexicon {
	
	/**
	 * maps strings -> meaning
	 * currently, each word can only have one meaning,
	 * although this may change
	 */
	private static Map<String, Definition> lexicon;
		
	public static void load() {
		
		lexicon = new HashMap<String, Definition>();
		
		//Prepositions
		lexicon.put("n", new To()); //to
		lexicon.put("m", new At()); //in
		lexicon.put("Xr", new Under()); //under
		lexicon.put("tp", new Above()); //above
		lexicon.put("HA", new LeftOf()); //behind
		lexicon.put("xft", new RightOf()); //in front of
		lexicon.put("Hna", new With()); //with
		//lexicon.put("mhAw", null); //around
		
		//Adverbs
		lexicon.put("nn", new Not());
		lexicon.put("aAw", new Greatly());
		lexicon.put("wrt", new DegreeModifierDef(1));
		lexicon.put("er", new DegreeModifierDef(2)); //TODO is this correct enough usage?
		
		//Nouns
		lexicon.put("jAm", new StaticDef(Noun.class, TileType.TREE)); //tree
		lexicon.put("ssn", new StaticDef(Noun.class, Flower.class)); //flower, gotta make this an entity
		lexicon.put("mw", new StaticDef(Noun.class, TileType.WATER));
		lexicon.put("Say", new StaticDef(Noun.class, TileType.SAND));
		lexicon.put("xt1", new StaticDef(Noun.class, TileType.LAVA)); //technically this is fire
		lexicon.put("snbt", new StaticDef(Noun.class, Urn.class));
		lexicon.put("bit", new StaticDef(Noun.class, null)); //TODO: bee
		lexicon.put("Hnyt", new StaticDef(Noun.class, Spike.class));
		lexicon.put("anx", new StaticDef(Noun.class, Glyph.class));
		lexicon.put("ankh", new StaticDef(Noun.class, Glyph.class));
		lexicon.put("xt", new StaticDef(Noun.class, Entity.class));
		lexicon.put("wi1", new StaticDef(Noun.class, Mummy.class)); //TODO kAr.t is better
		lexicon.put("rwdt", new StaticDef(Noun.class, TileType.SANDSTONE));
		
		lexicon.put("st", new StaticDef(AbstractNoun.class, AbstractSemantics.POSITION));
		lexicon.put("iry", new StaticDef(AbstractNoun.class, AbstractSemantics.WEAPON));
		lexicon.put("pr", new StaticDef(AbstractNoun.class, AbstractSemantics.TILE));
		lexicon.put("rn", new StaticDef(AbstractNoun.class, AbstractSemantics.NAME));
		lexicon.put("nb", new StaticDef(AbstractNoun.class, AbstractSemantics.SOURCE)); //means lord literally
		lexicon.put("nbt", new StaticDef(AbstractNoun.class, AbstractSemantics.SOURCE)); //means lord literally
		lexicon.put("Axt", new StaticDef(AbstractNoun.class, AbstractSemantics.FLOOD));
		lexicon.put("Da", new StaticDef(AbstractNoun.class, AbstractSemantics.STORM));
				
		//Names
		lexicon.put("DHwty", new StaticDef(Name.class, "DHwty"));
				
		//Suffix Pronouns
		lexicon.put("i", new FirstSuffixPronoun());
		lexicon.put("k", new SecondSuffixPronoun());
		lexicon.put("t", new SecondSuffixPronoun());
		lexicon.put("s", new ThirdSuffixPronoun());
		lexicon.put("f", new ThirdSuffixPronoun());
		
		//Object Pronouns
		lexicon.put("wi", new FirstObjectPronoun());
		lexicon.put("tn", new SecondObjectPronoun());
		lexicon.put("sw", new ThirdObjectPronoun());
		lexicon.put("sy", new ThirdObjectPronoun());
		
		//Verbs
		lexicon.put("in", new Move());
		lexicon.put("ini", new Move());
		lexicon.put("ms", new Move());
		lexicon.put("xpi", new Go());
		lexicon.put("iw", new Be());
		lexicon.put("bm", new Boom());
		lexicon.put("mAX", new Burn());
		lexicon.put("nD", new Heal()); //literally means "protect"
		lexicon.put("pA", new Fly());	
		lexicon.put("mAA", new See());
		lexicon.put("pH", new Damage());
		lexicon.put("qmA", new CreateObject());
		lexicon.put("rd", new Grow());
		lexicon.put("wn", new Open());
		lexicon.put("sS", new Write());
		lexicon.put("dbH", new Pray());
		lexicon.put("sqbH", new Slow());
		lexicon.put("sDi", new Break());
		lexicon.put("DAi", new Cross());
		//lexicon.put("sxpr", new CreateUnit()); //conjure
		
		//Conditionals
		lexicon.put("Dr", new ConditionalDef());
				
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

	public static Collection<Droppable> getDropsInLang() {
		List<Droppable> out = new ArrayList<>();
		for (String raw : getELang()) {
			out.add(new VocabDrop(raw));
		}
		return out;
	}
	
}
