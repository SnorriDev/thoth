package snorri.semantics.commands.intrans;

import snorri.entities.Entity;
import snorri.grammar.PartOfSpeech;
import snorri.semantics.CommandStatus;
import snorri.semantics.Definition;
import snorri.semantics.commands.Command;

public class Boom implements Definition<Command> {

	// If we expand second person to target other entity types besides projectiles, we should restrict Boom to projectiles.
	
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
		return "For projectile spells, cause the projectile to explode.";
	}
	
}
