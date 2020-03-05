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

public class LeftOf implements Definition<Lambda<Noun, Lambda<Command, Command>>> {

	// This word changes both the location and destination positions.
	
	private static Vector DELTA = new Vector(-100, 0);

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
						Vector newPos = ((Vector) pos).add(DELTA);
						e.setLocative(newPos);
						e.setDestination(newPos);
						return cmd.apply(e);
					}
					
					return CommandStatus.FAILED;
				};
			};
		};
	}

	@Override
	public String getEnglish() {
		return "left of";
	}

	@Override
	public String getDocumentation() {
		return "Set the target location left of some object.";
	}

}
