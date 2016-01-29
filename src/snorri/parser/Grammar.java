package snorri.parser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map.Entry;

import snorri.nonterminals.Command;
import snorri.nonterminals.InflIntransVerb;
import snorri.nonterminals.InflMorpheme;
import snorri.nonterminals.InflTransVerb;
import snorri.nonterminals.IntransVerb;
import snorri.nonterminals.Noun;
import snorri.nonterminals.NounPhrase;
import snorri.nonterminals.Prep;
import snorri.nonterminals.PrepPhrase;
import snorri.nonterminals.Pronoun;
import snorri.nonterminals.Sentence;
import snorri.nonterminals.Statement;
import snorri.nonterminals.TransVerb;
import snorri.nonterminals.VerbPhrase;
import snorri.semantics.Definition;
import snorri.semantics.Lexicon;

public class Grammar {

	private static ArrayList<Rule> rules;
	
	static {
		rules = new ArrayList<Rule>();
		
		rules.add(new Rule(new Object[] {Command.class}, Sentence.class));
		rules.add(new Rule(new Object[] {Command.class, "jr", Statement.class}, Sentence.class));
		rules.add(new Rule(new Object[] {TransVerb.class, NounPhrase.class}, Command.class));
		rules.add(new Rule(new Object[] {IntransVerb.class}, Command.class));
		rules.add(new Rule(new Object[] {Noun.class}, NounPhrase.class));
		rules.add(new Rule(new Object[] {Pronoun.class}, NounPhrase.class));
		rules.add(new Rule(new Object[] {Noun.class, PrepPhrase.class}, NounPhrase.class));
		rules.add(new Rule(new Object[] {Noun.class, NounPhrase.class}, NounPhrase.class));
		rules.add(new Rule(new Object[] {Prep.class, NounPhrase.class}, PrepPhrase.class));
		rules.add(new Rule(new Object[] {NounPhrase.class, VerbPhrase.class}, Statement.class));
		rules.add(new Rule(new Object[] {VerbPhrase.class}, Statement.class));
		rules.add(new Rule(new Object[] {InflTransVerb.class, NounPhrase.class}, VerbPhrase.class));
		rules.add(new Rule(new Object[] {InflIntransVerb.class}, VerbPhrase.class));
		rules.add(new Rule(new Object[] {TransVerb.class, InflMorpheme.class}, InflTransVerb.class));
		rules.add(new Rule(new Object[] {IntransVerb.class, InflMorpheme.class}, InflIntransVerb.class));
		rules.add(new Rule(new Object[] {PrepPhrase.class, TransVerb.class}, TransVerb.class));
		rules.add(new Rule(new Object[] {PrepPhrase.class, IntransVerb.class}, TransVerb.class));
		rules.add(new Rule(new Object[] {PrepPhrase.class, InflTransVerb.class}, TransVerb.class));
		rules.add(new Rule(new Object[] {PrepPhrase.class, InflIntransVerb.class}, TransVerb.class));
		
		System.out.println("CFG with " + rules.size() + " high-level rules loaded");
		
		for (Entry<String, Definition> e : Lexicon.getAllTerminals()) {
			rules.add(new Rule(new Object[] {e.getKey()}, e.getValue().getPOS()));
		}
		
		System.out.println("Lexicon with " + Lexicon.getAllTerminals().size() + " definitions loaded");
		
	}
	
	public static NonTerminal parseString(String input) {
		List<String> raw = Arrays.asList(input.split(" +|\\.|="));
		List<Node> result = new ArrayList<Node>();
		for (int i = 0; i < raw.size(); i++)
			result.add(new Terminal(raw.get(i)));
		try {
			return parseRec(result);
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	//create tree of nonterminals recursively by matching ltr
	//really not efficiently written
	@SuppressWarnings({ "unchecked" })
	public static NonTerminal parseRec(List<Node> nodes) throws InstantiationException, IllegalAccessException {
		if (nodes.size() == 1 && nodes.get(0) instanceof Sentence) //potentially rewrite to make more robust
			return (NonTerminal) nodes.get(0);
		for (int i = 0; i < nodes.size(); i++) {
			for (int j = nodes.size(); j >= i + 1; j--) {
				for (Rule rule : rules) { //can store rules by length to be more efficient and add argument
					Class<NonTerminal> fit = rule.fits(nodes.subList(i, j));
					if (fit != null) {
												
						//some weird shit going on here with indices
						//should be resolved with copying
						int size = nodes.size();
						NonTerminal nonTerm = fit.newInstance();
						nonTerm.setChildren(nodes.subList(i, j));
						List<Node> result = new ArrayList<Node>(nodes.subList(0, i));
						result.add(nonTerm);
						if (j < size)
							result.addAll(new ArrayList<Node>(nodes.subList(j, size)));
						return parseRec(result);
					}
				}
			}
		}
		return null;
	}
	
}
