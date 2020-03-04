package snorri.semantics;

import snorri.grammar.categories.Category;
import snorri.grammar.PartOfSpeech;

public interface Definition<S> {
	
	public PartOfSpeech getPartOfSpeech();
	
	default Category getCategory() {
		return getPartOfSpeech().getCategory();
	}
	
	public abstract S getMeaning();

	/**
	 * @return a short one or two-word description of this word
	 */
	public abstract String getEnglish();
	
	/**
	 * @return a longer description of what this word does
	 */
	public abstract String getDocumentation();
	
	default boolean altersMovement() {
		return false;
	}
	
}
