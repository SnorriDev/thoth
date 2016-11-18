package snorri.parser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import snorri.main.Main;
import snorri.main.Util;
import snorri.nonterminals.AbstractNoun;
import snorri.nonterminals.Adverb;
import snorri.nonterminals.DegreeModifier;
import snorri.nonterminals.AdverbPhrase;
import snorri.nonterminals.Command;
import snorri.nonterminals.Conditional;
import snorri.nonterminals.IntransVerb;
import snorri.nonterminals.Name;
import snorri.nonterminals.Sentence;
import snorri.nonterminals.NonTerminal;
import snorri.nonterminals.Noun;
import snorri.nonterminals.NounPhrase;
import snorri.nonterminals.Prep;
import snorri.nonterminals.PrepPhrase;
import snorri.nonterminals.Statement;
import snorri.nonterminals.SuffixPronoun;
import snorri.nonterminals.TransVerb;

/**
 * Use this static class to parse spell sentences
 * For efficient parsing, grammar rules should be represented in Chomsky Normal Form.
 * @author lambdaviking
 */

public class Grammar extends HashMap<Class<? extends NonTerminal>, List<Rule>> {

	private static final long serialVersionUID = 1L;
	
	private static Grammar grammar;
	private static int maxLength;
		
	static {
		
		grammar = new Grammar();
		
		grammar.add(new Rule(new Object[] {TransVerb.class, SuffixPronoun.class, NounPhrase.class}, Statement.class));
		grammar.add(new Rule(new Object[] {IntransVerb.class, SuffixPronoun.class}, Statement.class));
		
		grammar.add(new Rule(new Object[] {Command.class, Conditional.class, Statement.class}, Command.class));
		grammar.add(new Rule(new Object[] {TransVerb.class, NounPhrase.class}, Command.class));
		grammar.add(new Rule(new Object[] {IntransVerb.class}, Command.class));
		
		grammar.add(new Rule(new Object[] {AbstractNoun.class}, NounPhrase.class));
		grammar.add(new Rule(new Object[] {Noun.class}, NounPhrase.class));
		grammar.add(new Rule(new Object[] {Name.class}, Noun.class));
		grammar.add(new Rule(new Object[] {AbstractNoun.class, NounPhrase.class}, NounPhrase.class));
		grammar.add(new Rule(new Object[] {AbstractNoun.class, SuffixPronoun.class}, NounPhrase.class));
		
		grammar.add(new Rule(new Object[] {Prep.class, NounPhrase.class}, PrepPhrase.class));
		grammar.add(new Rule(new Object[] {Prep.class, SuffixPronoun.class}, PrepPhrase.class));
		
		grammar.add(new Rule(new Object[] {Adverb.class}, AdverbPhrase.class));
		grammar.add(new Rule(new Object[] {DegreeModifier.class, Adverb.class}, AdverbPhrase.class));
		grammar.add(new Rule(new Object[] {PrepPhrase.class}, AdverbPhrase.class));
		
		grammar.add(new Rule(new Object[] {Statement.class}, Sentence.class));
		grammar.add(new Rule(new Object[] {Command.class}, Sentence.class));
		grammar.add(new Rule(new Object[] {Sentence.class, AdverbPhrase.class}, Sentence.class));
		
		List<Rule> allRules = grammar.get();
		Main.log("CFG with " + allRules.size() + " high-level rules loaded");
		maxLength = 0;
		for (Rule rule : allRules) {
			maxLength = Integer.max(maxLength, rule.getLength());
		}
		
	}
	
	public void add(Rule rule) {
		List<Rule> rules = get(rule.getRoot());
		if (rules == null) {
			put(rule.getRoot(), new ArrayList<>());
			rules = get(rule.getRoot());
		}
		rules.add(rule);
	}
	
	public List<Rule> get() {
		List<Rule> out = new ArrayList<>();
		for (List<Rule> rules : values()) {
			out.addAll(rules);
		}
		return out;
	}
	
	//TODO get by POS
	
	public static List<String> getWords(String input) {
		return Arrays.asList(input.replaceAll("\\.",  "").split("\\s+|="));
	}
	
	public static List<Node> getNodes(String input) throws InstantiationException, IllegalAccessException {
		List<Node> semiTerminals = new ArrayList<>();
		for (String word : getWords(input)) {
			NonTerminal semi = Lexicon.lookup(word).getPOS().newInstance();
			List<Node> singleton = new ArrayList<>();
			singleton.add(new Terminal(word));
			semi.setChildren(singleton);
			semiTerminals.add(semi);
		}
		return semiTerminals;
	}
	
	public static List<Node> parseAmbigString(String input) {
		try {
			return topDown(getNodes(input), Sentence.class);
		} catch (InstantiationException | IllegalAccessException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static Node parseString(String input) {
		List<Node> parses = parseAmbigString(input);
		return (parses.isEmpty()) ? null : parses.get(0);
	}
	
	public static boolean isValidSentence(Node node) {
		return node instanceof Sentence;
	}
	
	private static List<Node> topDown(List<Node> words, Class<? extends Node> nodeType) throws InstantiationException, IllegalAccessException {
		
		if (words.size() == 1) {
			if (nodeType.isInstance(words.get(0))) {
				return words;
			}
		}
		
		int l = words.size();
		List<Node> out = new ArrayList<>();
		if (grammar.get(nodeType) == null) {
			return out;
		}
		for (Rule rewrite : grammar.get(nodeType)) {
			int n = rewrite.getLength();			
			for (List<Integer> part : getPartitions(l, n)) {
				
				//compute the sets of possible parses for constituents
				List<List<Node>> allPoss = new ArrayList<>();
				Iterator<Integer> iter = part.iterator();
				for (int start = 0, next = iter.next(), i = 0; start < words.size(); start += next, next = iter.next(), i++) {
					allPoss.add(topDown(new ArrayList<>(words.subList(start, start + next)), rewrite.getNode(i)));
				
					if (! iter.hasNext()) {
						break;
					}
				
				}
								
				//loops through the possible valid combinations
				for (List<Node> nodes : Util.computeCombinations(allPoss)) {
					Class<? extends NonTerminal> c = rewrite.fits(nodes);
//					if (c == null) {
//						Main.error("doesn't fit; reevaluate parsing");
//						break;
//					}
					NonTerminal node = c.newInstance();
					node.setChildren(nodes);
					out.add(node);
				}
				
			}
		}
		return out;
	}
	
	private static List<List<Integer>> getPartitions(int l, int n) {
		
		if (n == 1) {
			List<List<Integer>> singleton = new LinkedList<>();
			List<Integer> s1 = new LinkedList<>();
			s1.add(l);
			singleton.add(s1);
			return singleton;
		}
		
		List<List<Integer>> out = new LinkedList<>();
		for (int i = 1; i < l; i++) {
			for (List<Integer> part : getPartitions(l - i, n - 1)) {
				List<Integer> newPart = new LinkedList<>();
				newPart.add(i);
				newPart.addAll(part);
				out.add(newPart);
			}
		}
		
		return out;
		
	}
	
}
