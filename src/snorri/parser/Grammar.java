package snorri.parser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map.Entry;

import snorri.main.Main;
import snorri.nonterminals.AbstractNoun;
import snorri.nonterminals.Command;
import snorri.nonterminals.IntransVerb;
import snorri.nonterminals.Sentence;
import snorri.nonterminals.NonTerminal;
import snorri.nonterminals.Noun;
import snorri.nonterminals.NounPhrase;
import snorri.nonterminals.Prep;
import snorri.nonterminals.PrepPhrase;
import snorri.nonterminals.Statement;
import snorri.nonterminals.SuffixPronoun;
import snorri.nonterminals.TransVerb;
import snorri.semantics.Definition;

public class Grammar {

	private static ArrayList<Rule> rules;
	
	static {
		
		rules = new ArrayList<Rule>();
		
		rules.add(new Rule(new Object[] {TransVerb.class, SuffixPronoun.class, NounPhrase.class}, Statement.class));
		rules.add(new Rule(new Object[] {IntransVerb.class, SuffixPronoun.class}, Statement.class));
		
		rules.add(new Rule(new Object[] {Command.class, "jr", Statement.class}, Command.class));
		rules.add(new Rule(new Object[] {TransVerb.class, NounPhrase.class}, Command.class));
		rules.add(new Rule(new Object[] {IntransVerb.class}, Command.class));
		
		rules.add(new Rule(new Object[] {AbstractNoun.class}, NounPhrase.class));
		rules.add(new Rule(new Object[] {Noun.class}, NounPhrase.class));
		rules.add(new Rule(new Object[] {AbstractNoun.class, NounPhrase.class}, NounPhrase.class));
		rules.add(new Rule(new Object[] {AbstractNoun.class, SuffixPronoun.class}, NounPhrase.class));
		
		rules.add(new Rule(new Object[] {Prep.class, NounPhrase.class}, PrepPhrase.class));
		rules.add(new Rule(new Object[] {Prep.class, SuffixPronoun.class}, PrepPhrase.class));
		
		rules.add(new Rule(new Object[] {Statement.class}, Sentence.class));
		rules.add(new Rule(new Object[] {Command.class}, Sentence.class));
		rules.add(new Rule(new Object[] {Sentence.class, PrepPhrase.class}, Sentence.class));
		
		Main.log("CFG with " + rules.size() + " high-level rules loaded");
		
	}
	
	public static void loadLexicon() {
		
		for (Entry<String, Definition> e : Lexicon.getAllTerminals()) {
			rules.add(new Rule(new Object[] {e.getKey()}, e.getValue().getPOS()));
		}
		
		Main.log("Lexicon with " + Lexicon.getAllTerminals().size() + " definitions loaded");
		
	}
	
	public static Node parseString(String input) {
		List<String> raw = Arrays.asList(input.replaceAll("\\.",  "").split(" +|="));
		List<Node> result = new ArrayList<Node>();
		for (int i = 0; i < raw.size(); i++) {
			if (! raw.get(i).equals("")) {
				result.add(new Terminal(raw.get(i)));
			}
		}
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
	public static Node parseRec(List<Node> nodes) throws InstantiationException, IllegalAccessException {
	
		if (nodes.size() == 1 && nodes.get(0) instanceof Sentence) {
			return nodes.get(0);
		}
		
		for (int i = 0; i < nodes.size(); i++) {
			for (int j = nodes.size(); j >= i + 1; j--) {
				for (Rule rule : rules) { //can store rules by length to be more efficient and add argument
					Class<? extends NonTerminal> fit = rule.fits(nodes.subList(i, j));
					if (fit != null) {
																		
						int size = nodes.size();
						NonTerminal nonTerm = fit.newInstance();
						nonTerm.setChildren(nodes.subList(i, j));
						List<Node> result = new ArrayList<Node>(nodes.subList(0, i));
						result.add(nonTerm);
						result.addAll(new ArrayList<Node>(nodes.subList(j, size)));
						Node parse = parseRec(result);
						
						if (parse == null) {
							continue;
						}
						
						return parse;
					}
				}
			}
		}
		
		if (nodes.size() == 1) { //we have a sentence fragment
			return nodes.get(0);
		}
		
		return null;
	}
	
}
