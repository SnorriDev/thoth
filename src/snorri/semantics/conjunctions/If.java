package snorri.semantics.conjunctions;

import snorri.grammar.PartOfSpeech;
import snorri.semantics.CommandStatus;
import snorri.semantics.Definition;
import snorri.semantics.Lambda;
import snorri.semantics.commands.Command;
import snorri.semantics.predicates.Predicate;

public class If implements Definition<Lambda<Predicate, Lambda<Command, Command>>> {
	
	@Override
	public PartOfSpeech getPartOfSpeech() {
		return PartOfSpeech.CONDITIONAL;
	}

	@Override
	public Lambda<Predicate, Lambda<Command, Command>> getMeaning() {
		return pred -> {
			return cmd -> {
				return event -> {
					CommandStatus status = pred.apply(event);
					if (status == CommandStatus.TRUE) {
						return cmd.apply(event);
					}
					return status;
				};
			};
		};
	}

	@Override
	public String getEnglish() {
		return "if";
	}

	@Override
	public String getDocumentation() {
		return "Execute the previous command if the following statement is true.";
	}

}
