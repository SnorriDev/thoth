package snorri.semantics.commands.intrans;

import snorri.entities.Unit;
import snorri.events.CastEvent.Caster;
import snorri.grammar.PartOfSpeech;
import snorri.modifiers.FlyModifier;
import snorri.semantics.CommandStatus;
import snorri.semantics.Definition;
import snorri.semantics.commands.Command;

public class Fly implements Definition<Command> {
	
	// TODO: This needs to have a high mana cost to avoid abuse. Also consider giving each Modifier type a unique hash to avoid duplication.
	
	@Override
	public PartOfSpeech getPartOfSpeech() {
		return PartOfSpeech.INTRANS_CMD;
	}

	@Override
	public Command getMeaning() {
		return event -> {
			Caster firstPerson = event.getFirstPerson();
			if (!(firstPerson instanceof Unit)) {
				return CommandStatus.FAILED;
			}
			((Unit) firstPerson).addModifier(new FlyModifier());
			return CommandStatus.DONE;
		};
	}

	@Override
	public String getEnglish() {
		return "fly";
	}

	@Override
	public String getDocumentation() {
		return "Allows the player to fly for 10 seconds.";
	}
	
}
