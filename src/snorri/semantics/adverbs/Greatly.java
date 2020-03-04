package snorri.semantics.adverbs;

import snorri.events.CastEvent;
import snorri.grammar.PartOfSpeech;
import snorri.semantics.Definition;
import snorri.semantics.Lambda;
import snorri.semantics.commands.Command;

public class Greatly implements Definition<Lambda<Command, Command>> {
	
	@Override
	public Lambda<Command, Command> getMeaning() {
		return cmd -> {
			return e -> {
				e = new CastEvent(e).scaleHealthInteractionModifier(1.3 + 0.3 * e.pollDegree());
				return cmd.apply(e);
			};
		};
	}

	@Override
	public PartOfSpeech getPartOfSpeech() {
		return PartOfSpeech.ADV;
	}

	@Override
	public String getEnglish() {
		return "greatly";
	}

	@Override
	public String getDocumentation() {
		return "Magnifies the effect of a command.";
	}
	
}
