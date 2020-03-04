package snorri.semantics.nouns;

import snorri.grammar.PartOfSpeech;
import snorri.semantics.Definition;

public class SecondPerson implements Definition<Noun> {

	@Override
	public PartOfSpeech getPartOfSpeech() {
		return PartOfSpeech.NOUN;
	}

	@Override
	public Noun getMeaning() {
		return event -> (Nominal) event.getSecondPerson();
	}

	@Override
	public String getEnglish() {
		return "you";
	}

	@Override
	public String getDocumentation() {
		return "The position or object we are casting onto.";
	}

}
