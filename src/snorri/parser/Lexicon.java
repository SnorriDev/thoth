package snorri.parser;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import snorri.entities.Mummy;
import snorri.entities.Entity;
import snorri.entities.Flower;
import snorri.entities.Fountain;
import snorri.entities.Glyph;
import snorri.entities.Spike;
import snorri.entities.Urn;
import snorri.inventory.DropContainer;
import snorri.inventory.Droppable;
import snorri.inventory.RandomDrop.Tier;
import snorri.inventory.VocabDrop;
import snorri.nonterminals.AbstractNoun;
import snorri.nonterminals.Name;
import snorri.nonterminals.Noun;
import snorri.semantics.Above;
import snorri.semantics.And;
import snorri.semantics.At;
import snorri.semantics.Be;
import snorri.semantics.Boom;
import snorri.semantics.Break;
import snorri.semantics.Burn;
import snorri.semantics.ClassWrapper;
import snorri.semantics.If;
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
import snorri.semantics.Nominal.NameConstant;
import snorri.semantics.Not;
import snorri.semantics.Open;
import snorri.semantics.Order;
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
import snorri.world.BackgroundElement;

public class Lexicon extends HashSet<String> implements DropContainer<Droppable>, Serializable {
	
	/**
	 * Maps strings -> meaning. By design, each word can only have one meaning.
	 */
	private static final long serialVersionUID = 1L;
	
	private static transient Map<String, Definition<?>> lexicon;
	
	public static void load() {
		
		lexicon = new HashMap<>();
		
		//Prepositions
		lexicon.put("r", new To()); //to
		lexicon.put("n", new At()); //in
		lexicon.put("Xr", new Under()); //under
		lexicon.put("tp", new Above()); //above
		lexicon.put("iAb", new LeftOf()); //behind
		lexicon.put("imnt", new RightOf()); //in front of
		lexicon.put("Hna", new With()); //with
		//lexicon.put("mhAw", null); //around
		
		//Adverbs
		lexicon.put("nn", new Not());
		lexicon.put("aAw", new Greatly());
		lexicon.put("wrt", new DegreeModifierDef(1)); //"very"
		
		//Nouns
		//lexicon.put("nht", new StaticDef(Noun.class, BackgroundElement.TREE)); //tree
		lexicon.put("Hrrt", new StaticDef(Noun.class, new ClassWrapper(Flower.class))); //flower, gotta make this an entity
		lexicon.put("mw", new StaticDef(Noun.class, BackgroundElement.WATER));
		lexicon.put("Say", new StaticDef(Noun.class, BackgroundElement.SAND));
		lexicon.put("nsr", new StaticDef(Noun.class, BackgroundElement.LAVA)); //technically this is fire
		lexicon.put("snbt", new StaticDef(Noun.class, new ClassWrapper(Urn.class)));
//		lexicon.put("bit", new StaticDef(Noun.class, null)); //bee
		lexicon.put("txn", new StaticDef(Noun.class, new ClassWrapper(Spike.class)));
		lexicon.put("anx", new StaticDef(Noun.class, new ClassWrapper(Glyph.class)));
		lexicon.put("z", new StaticDef(Noun.class, new ClassWrapper(Entity.class)));
		lexicon.put("saH", new StaticDef(Noun.class, new ClassWrapper(Mummy.class)));
		lexicon.put("rwDt", new StaticDef(Noun.class, BackgroundElement.SANDSTONE));
		lexicon.put("mAAt", new StaticDef(Noun.class, new Order()));
		lexicon.put("FOUNTAIN", new StaticDef(Noun.class, new ClassWrapper(Fountain.class)));
		
		lexicon.put("bw", new StaticDef(AbstractNoun.class, AbstractSemantics.POSITION));
		lexicon.put("aHAw", new StaticDef(AbstractNoun.class, AbstractSemantics.WEAPON));
		lexicon.put("sAt", new StaticDef(AbstractNoun.class, AbstractSemantics.TILE));
		lexicon.put("rn", new StaticDef(AbstractNoun.class, AbstractSemantics.NAME));
		lexicon.put("nb", new StaticDef(AbstractNoun.class, AbstractSemantics.SOURCE)); //means lord literally
		lexicon.put("nbt", new StaticDef(AbstractNoun.class, AbstractSemantics.SOURCE)); //means lord literally
		lexicon.put("Hap", new StaticDef(AbstractNoun.class, AbstractSemantics.FLOOD));
		lexicon.put("nSni", new StaticDef(AbstractNoun.class, AbstractSemantics.STORM));
				
		//Names
		lexicon.put("DHwty", new StaticDef(Name.class, new NameConstant("Thoth")));
				
		//Suffix Pronouns
		lexicon.put("i", new FirstSuffixPronoun());
		lexicon.put("k", new SecondSuffixPronoun());
		lexicon.put("t", new SecondSuffixPronoun());
		lexicon.put("s", new ThirdSuffixPronoun());
		lexicon.put("f", new ThirdSuffixPronoun());
		
		//Object Pronouns
		lexicon.put("wi", new FirstObjectPronoun());
		lexicon.put("tn", new SecondObjectPronoun());
		lexicon.put("st", new ThirdObjectPronoun()); //this one should be default/preferred
		lexicon.put("sw", new ThirdObjectPronoun());
		lexicon.put("sy", new ThirdObjectPronoun());
		
		//Verbs
		lexicon.put("ms", new Move());
		lexicon.put("Sm", new Go());
		lexicon.put("wn", new Be());
		lexicon.put("bm", new Boom());
		lexicon.put("Htm", new Boom());
		lexicon.put("mAX", new Burn());
		lexicon.put("ssnb", new Heal()); //literally means "protect"
		lexicon.put("pA", new Fly());	
		lexicon.put("mAA", new See());
		lexicon.put("Hwi", new Damage());
		lexicon.put("qmA", new CreateObject());
		lexicon.put("rd", new Grow());
		lexicon.put("wpi", new Open()); Tier.COMMON.add("wpi");
		lexicon.put("sS", new Write());
		lexicon.put("smAa", new Pray());
		lexicon.put("sdfA", new Slow());
		lexicon.put("sD", new Break());
		lexicon.put("dmi", new Cross());
		//lexicon.put("sxpr", new CreateUnit()); //conjure
		
		//Connectives
		lexicon.put("Dr", new If());
		lexicon.put("AND", new And());
				
	}
	
	public static Definition<?> lookup(String form) {
		if (lexicon.containsKey(form))
			return lexicon.get(form);
		return null;
	}
		
	public static Set<Entry<String, Definition<?>>> getAllTerminals() {
		return lexicon.entrySet();
	}
	
	public static Set<String> getELang() {
		return lexicon.keySet();
	}
	
	public static Collection<Definition<?>> getILang() {
		return lexicon.values();
	}

	public static Collection<Droppable> getDropsInLang() {
		List<Droppable> out = new ArrayList<>();
		for (String raw : getELang()) {
			out.add(new VocabDrop(raw));
		}
		return out;
	}

	@Override
	public boolean add(Droppable d) {
		if (d instanceof VocabDrop) {
			return add(((VocabDrop) d).getOrthography());
		}
		return false;
	}
	
	@Override
	public boolean remove(Droppable d, boolean specific) {
		if (d instanceof VocabDrop) {
			return remove(((VocabDrop) d).getOrthography(), specific);
		}
		return false;
	}
	
	public boolean remove(String word, boolean specific) {
		return super.remove(word);
	}
		
	public boolean contains(Collection<String> words) {
		for (String word : words) {
			if (!contains(word)) {
				return false;
			}
		}
		return true;
	}
	
}
