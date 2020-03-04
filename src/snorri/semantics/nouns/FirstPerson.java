package snorri.semantics.nouns;

import snorri.grammar.PartOfSpeech;
import snorri.semantics.Definition;

public class FirstPerson implements Definition<Noun> {

	@Override
	public PartOfSpeech getPartOfSpeech() {
		return PartOfSpeech.NOUN;
	}

	@Override
	public Noun getMeaning() {
		return event -> (Nominal) event.getFirstPerson();
	}

	@Override
	public String getEnglish() {
		return "me";
	}

	@Override
	public String getDocumentation() {
		return "The player themselves.";
	}

}
