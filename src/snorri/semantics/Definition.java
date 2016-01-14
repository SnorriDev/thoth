package snorri.semantics;

public class Definition {

	@SuppressWarnings("rawtypes")
	private Class partOfSpeach;
	private Object meaning;
	
	@SuppressWarnings("rawtypes")
	public Definition(Class partOfSpeach, Object meaning) {
		this.partOfSpeach = partOfSpeach;
		this.meaning = meaning;
	}
	
	@SuppressWarnings("rawtypes")
	public Class getPOS() {
		return partOfSpeach;
	}
	
	public Object getMeaning() {
		return meaning;
	}
	
}
