package snorri.semantics.prepositions;

import snorri.events.CastEvent;
import snorri.grammar.PartOfSpeech;
import snorri.semantics.Definition;
import snorri.semantics.Lambda;
import snorri.semantics.commands.Command;
import snorri.semantics.nouns.Nominal;
import snorri.semantics.nouns.Noun;

public class With implements Definition<Lambda<Noun, Lambda<Command, Command>>> {

	@Override
	public PartOfSpeech getPartOfSpeech() {
		return PartOfSpeech.PREP;
	}

	@Override
	public Lambda<Noun, Lambda<Command, Command>> getMeaning() {
		return noun -> {
			return cmd -> {
				return event -> {
					Nominal obj = noun.apply(event);
					event = new CastEvent(event);
					event.setInstrument(obj);
					return cmd.apply(event);
				};
			};
		};
	}

	@Override
	public String getEnglish() {
		return "with";
	}

	@Override
	public String getDocumentation() {
		return "Set the instrument of a command.";
	}

}
