package snorri.grammar;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import snorri.entities.Entity;
import snorri.entities.Flower;
import snorri.entities.Fountain;
import snorri.entities.Glyph;
import snorri.entities.Mummy;
import snorri.entities.Spike;
import snorri.entities.Statue;
import snorri.entities.Urn;
import snorri.inventory.RandomDrop.Tier;
import snorri.semantics.ClassWrapper;
import snorri.semantics.Definition;
import snorri.semantics.adverbs.Greatly;
import snorri.semantics.commands.intrans.Boom;
import snorri.semantics.commands.intrans.Fly;
import snorri.semantics.commands.intrans.Go;
import snorri.semantics.commands.intrans.Open;
import snorri.semantics.commands.trans.Acquire;
import snorri.semantics.commands.trans.Break;
import snorri.semantics.commands.trans.Burn;
import snorri.semantics.commands.trans.CreateObject;
import snorri.semantics.commands.trans.Damage;
import snorri.semantics.commands.trans.Grow;
import snorri.semantics.commands.trans.Heal;
import snorri.semantics.commands.trans.Move;
import snorri.semantics.commands.trans.Pray;
import snorri.semantics.commands.trans.Push;
import snorri.semantics.commands.trans.Slow;
import snorri.semantics.commands.trans.Write;
import snorri.semantics.conjunctions.And;
import snorri.semantics.conjunctions.Else;
import snorri.semantics.conjunctions.If;
import snorri.semantics.conjunctions.Or;
import snorri.semantics.nouns.ConstantNounDef;
import snorri.semantics.nouns.FirstPerson;
import snorri.semantics.nouns.Nominal.NameConstant;
import snorri.semantics.nouns.SecondPerson;
import snorri.semantics.nouns.ThirdPerson;
import snorri.semantics.predicates.trans.Cross;
import snorri.semantics.predicates.trans.See;
import snorri.semantics.prepositions.At;
import snorri.semantics.prepositions.LeftOf;
import snorri.semantics.prepositions.To;
import snorri.semantics.prepositions.With;
import snorri.world.UnifiedTileType;

public class DefaultLexicon {
	
	static Map<String, List<Definition<?>>> lexicon;
	private static String[] orthographicForms;
	
	public static void load() {
		// Don't use a static initializer.
		lexicon = new HashMap<>();
		addPrepositions();
		addAdverbs();
		addNouns();
		addPronouns();
		addAdverbs();
		addVerbs();
		addConnectives();
		addNames();
		orthographicForms = lexicon.keySet().toArray(new String[] {});
		Arrays.sort(orthographicForms);
		initializeTiers();
	}
	
	private static void register(String name, Definition<?> definition) {
		if (lexicon.get(name) == null) {
			lexicon.put(name, new LinkedList<>());
		}
		lexicon.get(name).add(definition);
	}

	private static void addNames() {
		register("DHwty", new ConstantNounDef(new NameConstant("Thoth")));
	}

	private static void addVerbs() {
		register("ms", new Move());
		register("Sm", new Go());
		register("bm", new Boom());
		register("Htm", new Boom());
		register("mAX", new Burn());
		register("ssnb", new Heal()); //literally means "protect"
		register("mAA", new See());
		register("Hwi", new Damage());
		register("qmA", new CreateObject());
		register("rd", new Grow());
		register("wpi", new Open());
		register("sS", new Write());
		register("smAa", new Pray());
		register("sdfA", new Slow());
		register("sD", new Break());
		register("dmi", new Cross());
		register("PUSH", new Push());
		register("ACQUIRE", new Acquire());
		register("FLY", new Fly());
		//register("sxpr", new CreateUnit()); //spawn
	}

	private static void addConnectives() {
		register("Dr", new If());
		register("ELSE", new Else());
		register("AND", new And());
		register("OR", new Or());
	}

	private static void addAdverbs() {
		register("aAw", new Greatly());
	}

	private static void addPronouns() {
		// We merged the suffix and object pronouns together. But the first set are suffix, second set are object.
		
		//Suffix Pronouns
//		register("i", new FirstPerson());
//		register("k", new SecondPerson());
//		register("t", new SecondPerson());
//		register("s", new ThirdSuffixPronoun());
//		register("f", new ThirdSuffixPronoun());
		
		//Object Pronouns
		register("wi", new FirstPerson());
		register("tn", new SecondPerson());
		register("st", new ThirdPerson()); //this one should be default/preferred
//		register("sw", new ThirdObjectPronoun());
//		register("sy", new ThirdObjectPronoun());
	}

	private static void addNouns() {
		//register("nht", new ConstantNounDef(BackgroundElement.TREE)); //tree
		register("Hrrt", new ConstantNounDef(new ClassWrapper(Flower.class))); //flower, gotta make this an entity
		register("mw", new ConstantNounDef(UnifiedTileType.WATER));
		register("Say", new ConstantNounDef(UnifiedTileType.SAND));
//		register("nsr", new ConstantNounDef(BackgroundElement.LAVA)); //technically this is fire
		register("snbt", new ConstantNounDef(new ClassWrapper(Urn.class)));
//		register("bit", new ConstantNounDef(null)); //bee
		register("txn", new ConstantNounDef(new ClassWrapper(Spike.class)));
		register("anx", new ConstantNounDef(new ClassWrapper(Glyph.class)));
		register("z", new ConstantNounDef(new ClassWrapper(Entity.class)));
		register("saH", new ConstantNounDef(new ClassWrapper(Mummy.class)));
		register("STATUE", new ConstantNounDef(new ClassWrapper(Statue.class)));
//		register("rwDt", new ConstantNounDef(BackgroundElement.SANDSTONE));
		register("FOUNTAIN", new ConstantNounDef(new ClassWrapper(Fountain.class)));
		
//		TODO: Should we add these back??
//		register("bw", new StaticDef(AbstractNoun.class, AbstractSemantics.POSITION));
//		register("aHAw", new StaticDef(AbstractNoun.class, AbstractSemantics.WEAPON));
//		register("sAt", new StaticDef(AbstractNoun.class, AbstractSemantics.TILE));
//		register("rn", new StaticDef(AbstractNoun.class, AbstractSemantics.NAME));
//		register("nb", new StaticDef(AbstractNoun.class, AbstractSemantics.SOURCE)); //means lord literally
//		register("nbt", new StaticDef(AbstractNoun.class, AbstractSemantics.SOURCE)); //means lord literally
//		register("Hap", new StaticDef(AbstractNoun.class, AbstractSemantics.FLOOD));
//		register("nSni", new StaticDef(AbstractNoun.class, AbstractSemantics.STORM));
	}

	private static void addPrepositions() {
		register("r", new To()); //to
		register("n", new At()); //in
		register("Hna", new With()); //with

//		register("Xr", new Under()); //under
//		register("tp", new Above()); //above
		register("iAb", new LeftOf()); //behind
//		register("imnt", new RightOf()); //in front of
		
		//lexicon.put("mhAw", null); //around
	}
	
	private static void initializeTiers() {
		Tier.COMMON.add("wpi");
	}

	public static String[] getOrthographicForms() {
		return orthographicForms;
	}

	public static List<Definition<?>> lookup(String form) {
		if (lexicon.containsKey(form)) {
			return lexicon.get(form);
		} else {
			return null;
		}
	}
	
}
