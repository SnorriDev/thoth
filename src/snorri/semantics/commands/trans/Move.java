package snorri.semantics.commands.trans;

import snorri.entities.Entity;
import snorri.grammar.PartOfSpeech;
import snorri.semantics.CommandStatus;
import snorri.semantics.Definition;
import snorri.semantics.Lambda;
import snorri.semantics.commands.Command;
import snorri.semantics.nouns.Noun;
import snorri.world.Vector;

public class Move implements Definition<Lambda<Noun, Command>> {

	@Override
	public PartOfSpeech getPartOfSpeech() {
		return PartOfSpeech.TRANS_CMD;
	}

	@Override
	public Lambda<Noun, Command> getMeaning() {
		return object -> {
			return e -> {
				Object obj = object.apply(e);
				Vector destination = e.getDestination();
				if (destination == null || e.getWorld().isOccupied(destination.gridPos())) {
					return CommandStatus.FAILED;
				}
				
				if (obj instanceof Entity) {
					e.getWorld().getEntityTree().move((Entity) obj, e.getDestination());
					return CommandStatus.DONE;
				}
				return CommandStatus.FAILED;
			};
		};
	}

	@Override
	public String getEnglish() {
		return "teleport";
	}

	@Override
	public String getDocumentation() {
		return "Teleport an object to a target location.";
	}

}
