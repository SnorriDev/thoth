package snorri.semantics.commands.trans;

import snorri.entities.Drop;
import snorri.grammar.PartOfSpeech;
import snorri.inventory.Droppable;
import snorri.semantics.CommandStatus;
import snorri.semantics.Definition;
import snorri.semantics.Lambda;
import snorri.semantics.commands.Command;
import snorri.semantics.nouns.Noun;

public class Acquire implements Definition<Lambda<Noun, Command>> {

	@Override
	public PartOfSpeech getPartOfSpeech() {
		return PartOfSpeech.TRANS_CMD;
	}

	@Override
	public Lambda<Noun, Command> getMeaning() {
		return obj -> {
			return e -> {
				Object object = obj.apply(e);
				if (object instanceof Droppable) {
					e.getFirstPerson().getInventory().add((Droppable) object);
					return CommandStatus.DONE;
				}
				
				//maybe we shouldn't do this; force players to say "prize of it"
				if (object instanceof Drop) {
					e.getFirstPerson().getInventory().add(((Drop) object).getPrize());
					return CommandStatus.DONE;
				}
				
				return CommandStatus.FAILED;
			};
		};
	}

	@Override
	public String getEnglish() {
		return "acquire";
	}

	@Override
	public String getDocumentation() {
		return "Pick up a targeted item drop";
	}

}
