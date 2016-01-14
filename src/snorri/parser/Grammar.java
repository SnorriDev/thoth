package snorri.parser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import snorri.nonterminals.Sentence;

public class Grammar {

	private static ArrayList<Rule> rules;
	
	static {
		rules = new ArrayList<Rule>();
		
		rules.add(new Rule(new Object[] {"hello"}, Sentence.class));
		rules.add(new Rule(new Object[] {Sentence.class, "world"}, Sentence.class));
		
		//Can actually add all terminals programmatically based on PoS
		
		System.out.println("CFG with " + rules.size() + " rules loaded");
		
	}
	
	public static NonTerminal parseString(String input) {
		List<String> raw = Arrays.asList(input.split(" "));
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
	@SuppressWarnings("unchecked")
	public static NonTerminal parseRec(List<Node> nodes) throws InstantiationException, IllegalAccessException {
		if (nodes.size() == 1 && nodes.get(0) instanceof NonTerminal)
			return (NonTerminal) nodes.get(0);
		for (int i = 0; i < nodes.size(); i++) {
			for (int j = i + 1; j <= nodes.size(); j++) {
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
