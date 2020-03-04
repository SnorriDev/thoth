package snorri.semantics.nouns;

import snorri.grammar.PartOfSpeech;
import snorri.semantics.Definition;

public class ThirdPerson implements Definition<Noun> {

	@Override
	public PartOfSpeech getPartOfSpeech() {
		return PartOfSpeech.NOUN;
	}

	@Override
	public Noun getMeaning() {
		return event -> (Nominal) event.getThirdPerson();
	}

	@Override
	public String getEnglish() {
		return "it";
	}

	@Override
	public String getDocumentation() {
		return "The position of the mouse.";
	}

}
