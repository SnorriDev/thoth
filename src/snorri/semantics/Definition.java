package snorri.semantics;

public class Definition {

	@SuppressWarnings("rawtypes")
	private Class partOfSpeech;
	private Object meaning;
	
	@SuppressWarnings("rawtypes")
	public Definition(Class partOfSpeech, Object meaning) {
		this.partOfSpeech = partOfSpeech;
		this.meaning = meaning;
	}
	
	@SuppressWarnings("rawtypes")
	public Class getPOS() {
		return partOfSpeech;
	}
	
	public Object getMeaning() {
		return meaning;
	}
	
}
