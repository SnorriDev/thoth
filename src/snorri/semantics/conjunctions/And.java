package snorri.semantics.conjunctions;

import snorri.grammar.PartOfSpeech;
import snorri.semantics.CommandStatus;
import snorri.semantics.Definition;
import snorri.semantics.Lambda;
import snorri.semantics.commands.Command;

public class And implements Definition<Lambda<Command, Lambda<Command, Command>>> {

	@Override
	public PartOfSpeech getPartOfSpeech() {
		return PartOfSpeech.CONJUNCTION;
	}

	@Override
	public Lambda<Command, Lambda<Command, Command>> getMeaning() {
		return rightCmd -> {
			return leftCmd -> {
				return event -> {
					CommandStatus status = rightCmd.apply(event);
					if (status == CommandStatus.DONE) {
						return leftCmd.apply(event);
					}
					return status;
				};
			};
		};
	}

	@Override
	public String getEnglish() {
		return "and";
	}

	@Override
	public String getDocumentation() {
		return "Execute the first command, then execute the second command if the first one does't fail.";
	}

}
