package snorri.semantics.nouns;

import snorri.grammar.PartOfSpeech;
import snorri.semantics.Definition;

public class ConstantNounDef implements Definition<Noun> {

	private Nominal value;

	public ConstantNounDef(Nominal value) {
		this.value = value;
	}
	
	@Override
	public PartOfSpeech getPartOfSpeech() {
		return PartOfSpeech.NOUN;
	}

	@Override
	public Noun getMeaning() {
		return event -> {
			return value;
		};
	}

	@Override
	public String getEnglish() {
		return value.toString();
	}

	@Override
	public String getDocumentation() {
		return value.toString();
	}


}
