package snorri.semantics.commands.intrans;

import snorri.entities.Entity;
import snorri.grammar.PartOfSpeech;
import snorri.semantics.CommandStatus;
import snorri.semantics.Definition;
import snorri.semantics.commands.Command;

public class Boom implements Definition<Command> {

	private static final double DAMAGE = 100;

	@Override
	public PartOfSpeech getPartOfSpeech() {
		return PartOfSpeech.INTRANS_CMD;
	}

	@Override
	public Command getMeaning() {
		return event -> {
			Entity secondPerson = event.getSecondPerson();
			if (secondPerson == null) {
				return CommandStatus.FAILED;
			}
			secondPerson.explode(event.getWorld(), DAMAGE);
			return CommandStatus.DONE;
		};
	}

	@Override
	public String getEnglish() {
		return "explode";
	}

	@Override
	public String getDocumentation() {
		return "Cause an explosion at the 2nd person object.";
	}
	
}
