package snorri.grammar.categories;

import snorri.grammar.ComposeRule;

public class AtomicCategory implements Category {

	private String name;

	public AtomicCategory(String name) {
		this.name = name;
	}
	
	@Override
	public Category apply(Category other) {
		return null;
	}

	@Override
	public String toString() {
		return name;
	}

	@Override
	public boolean equals(Object other) {
		return toString().contentEquals(other.toString());
	}

	@Override
	public ComposeRule getRule() {
		return null;
	}

}
