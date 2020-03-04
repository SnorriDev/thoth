package snorri.semantics.prepositions;

import snorri.events.CastEvent;
import snorri.grammar.PartOfSpeech;
import snorri.semantics.CommandStatus;
import snorri.semantics.Definition;
import snorri.semantics.Lambda;
import snorri.semantics.commands.Command;
import snorri.semantics.nouns.Nominal;
import snorri.semantics.nouns.Noun;
import snorri.semantics.nouns.Nominal.AbstractSemantics;
import snorri.world.Vector;

public class At implements Definition<Lambda<Noun, Lambda<Command, Command>>> {

	// Should refer to "target" and "destination" locations.
	
	// TODO: Add back other locative prepositions, potentially?
	
	@Override
	public PartOfSpeech getPartOfSpeech() {
		return PartOfSpeech.PREP;
	}

	@Override
	public Lambda<Noun, Lambda<Command, Command>> getMeaning() {
		return noun -> {
			return cmd -> {
				return e -> {
					Nominal obj = noun.apply(e);
					Object pos = obj.get(AbstractSemantics.POSITION, e);
					
					if (pos instanceof Vector) {
						e = new CastEvent(e);
						e.setLocative(((Vector) pos).copy());
						return cmd.apply(e);
					}
					
					return CommandStatus.FAILED;
				};
			};
		};
	}

	@Override
	public String getEnglish() {
		return "at";
	}

	@Override
	public String getDocumentation() {
		return "Set the target location of a command.";
	}

}
