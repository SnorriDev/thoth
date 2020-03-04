package snorri.grammar;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import snorri.grammar.categories.Category;
import snorri.main.Debug;
import snorri.semantics.Definition;
import snorri.semantics.Lambda;
import snorri.semantics.commands.Command;
import snorri.util.Pair;

public class ChartParser {

	private static final Category COMMAND = PartOfSpeech.INTRANS_CMD.getCategory();
	
	private HashMap<Pair<Integer, Integer>, List<Pair<Category, Object>>> chart;
	private List<String> tokens;

	public ChartParser(List<String> tokens) {
		this.tokens = tokens;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void fillChart() {
		chart = new HashMap<>();
		Pair<Integer, Integer> key;
		for (int idx = 0; idx < tokens.size(); idx++) {
			key = new Pair<>(idx, idx);
			List<Definition<?>> definitions = DefaultLexicon.lookup(tokens.get(idx));
			chart.put(key, new LinkedList<>());
			List<Pair<Category, Object>> values = chart.get(key);
			definitions.forEach(def -> {
				values.add(new Pair<>(def.getCategory(), def.getMeaning()));
			});
			chart.put(key, values);
		}
		
		Category newCategory;
		Object newMeaning;
		for (int len = 1; len < tokens.size(); len++) {
			for (int start = 0; start < tokens.size() - len; start++) {
				int end = start + len;
				key = new Pair<>(start, end);
				chart.put(key, new LinkedList<>());
				List<Pair<Category, Object>> values = chart.get(key);
				for (int split = start; split < end; split++) {
					for (Pair<Category, Object> startPair : chart.get(new Pair<>(start, split))) {
						for (Pair<Category, Object> endPair : chart.get(new Pair<>(split + 1, end))) {
							if ((newCategory = startPair.getFirst().apply(endPair.getFirst())) != null) {
								newMeaning = ((Lambda) startPair.getSecond()).apply(endPair.getSecond());
								values.add(new Pair<>(newCategory, newMeaning));
							}
							else if ((newCategory = endPair.getFirst().apply(startPair.getFirst())) != null) {
								newMeaning = ((Lambda) endPair.getSecond()).apply(startPair.getSecond());
								values.add(new Pair<>(newCategory, newMeaning));
							}
						}
					}
				}
			}
		}
		
	}
	
	public Command getCommand() {
		List<Pair<Category, Object>> results = chart.get(new Pair<>(0, tokens.size() - 1));
		if (results.size() == 0) {
			return null;
		}
		if (results.size() > 1) {
			Debug.logger.severe("Got ambiguous parses: " + results);
			return null;
		}
		
		Pair<Category, Object> result = results.get(0);
		if (result.getFirst().equals(COMMAND)) {
			return (Command) result.getSecond();
		}
		return null;
	}

	public static Command parseText(String text) {
		List<String> tokens = tokenize(text);
		ChartParser parser = new ChartParser(tokens);
		parser.fillChart();
		return parser.getCommand();
	}

	public static List<String> tokenize(String text) {
		return Arrays.asList(text.replaceAll("\\.", "").split("\\s+|="));
	}
	
	public HashMap<Pair<Integer, Integer>, List<Pair<Category, Object>>> getChart() {
		return chart;
	}

}
