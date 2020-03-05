package snorri.grammar.categories;

import snorri.grammar.ComposeRule;

public interface Category {

	abstract public Category apply(Category other);
	
	abstract public String toString();
	
	abstract public ComposeRule getRule();
	
	abstract public char getDirection();
		
}
