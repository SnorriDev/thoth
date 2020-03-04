package snorri.semantics.conjunctions;

import snorri.grammar.PartOfSpeech;
import snorri.semantics.CommandStatus;
import snorri.semantics.Definition;
import snorri.semantics.Lambda;
import snorri.semantics.commands.Command;

public class If implements Definition<Lambda<CommandStatus, Lambda<Command, Command>>> {
	
	@Override
	public PartOfSpeech getPartOfSpeech() {
		return PartOfSpeech.CONDITIONAL;
	}

	@Override
	public Lambda<CommandStatus, Lambda<Command, Command>> getMeaning() {
		return predStatus -> {
			return cmd -> {
				if (predStatus == CommandStatus.TRUE) {
					return cmd;
				}
				return event -> CommandStatus.IF_FAILED;
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
