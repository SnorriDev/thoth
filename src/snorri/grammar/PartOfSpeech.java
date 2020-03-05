package snorri.grammar;

import snorri.grammar.categories.AtomicCategory;
import snorri.grammar.categories.Category;
import snorri.grammar.categories.DerivedCategory;

public enum PartOfSpeech {

	/**
	 * Each part of speech defines a distinct frame that a word can occur in. Words
	 * can be registered with multiple parts of speech.
	 */

	NOUN(new AtomicCategory("Noun")),

	INTRANS_CMD(new AtomicCategory("Command")),

	TRANS_CMD(new DerivedCategory(INTRANS_CMD.cat, '/', NOUN.cat)),

	ADV(new DerivedCategory(INTRANS_CMD.cat, '\\', INTRANS_CMD.cat)),

	PREP(new DerivedCategory(ADV.cat, '/', NOUN.cat)),

	INTRANS_PRED(new DerivedCategory(new AtomicCategory("Predicate"), '/', NOUN.cat)),

	TRANS_PRED(new DerivedCategory(INTRANS_PRED.cat, '/', NOUN.cat)),

	CONDITIONAL(new DerivedCategory(ADV.cat, '/', new AtomicCategory("Predicate"))),

	ELSE(new DerivedCategory(new DerivedCategory(ADV.cat, '\\', ADV.cat), '/', INTRANS_CMD.cat)),

	CONJUNCTION(new DerivedCategory(new DerivedCategory(INTRANS_CMD.cat, '\\', INTRANS_CMD.cat), '/', INTRANS_CMD.cat));

	private Category cat;

	PartOfSpeech(Category cat) {
		this.cat = cat;
	}

	public Category getCategory() {
		return cat;
	}

}
