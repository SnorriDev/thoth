package snorri.semantics;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import snorri.parser.Terminal;

public class Lexicon {
	
	private static Map<String, Definition> lexicon;
	
	static {
		
		lexicon = new HashMap<String, Definition>();
		
	}
	
	public static Definition lookup(String form) {
		if (lexicon.containsKey(form))
			return lexicon.get(form);
		return null;
	}
	
	//could make an iterator maybe
	@SuppressWarnings("rawtypes")
	public static List<Terminal> getAllTerminals(Class c) {
		List<Terminal> result = new ArrayList<Terminal>();
		for (Entry<String, Definition> entry : lexicon.entrySet()) {
			if (entry.getValue().getClass().equals(c)) {
				result.add(new Terminal(entry.getKey()));
			}
		}
		return result;
	}
	
}
