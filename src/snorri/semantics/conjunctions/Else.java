package snorri.semantics.conjunctions;

import snorri.grammar.PartOfSpeech;
import snorri.semantics.CommandStatus;
import snorri.semantics.Definition;
import snorri.semantics.Lambda;
import snorri.semantics.commands.Command;

public class Else implements Definition<Lambda<Command, Lambda<Lambda<Command, Command>, Lambda<Command, Command>>>> {
	
	// FIXME: Debug this.
	
	@Override
	public PartOfSpeech getPartOfSpeech() {
		return PartOfSpeech.ELSE;
	}

	@Override
	public Lambda<Command, Lambda<Lambda<Command, Command>, Lambda<Command, Command>>> getMeaning() {
		return elseCommand -> {
			return conditional -> {
				return ifCommand -> {
					return event -> {
						Command wrapped = conditional.apply(ifCommand);
						CommandStatus ifStatus = wrapped.apply(event);
						if (ifStatus == CommandStatus.FALSE) {
							return elseCommand.apply(event);
						}
						return ifStatus;
					};
				};
			};
		};
	}

	@Override
	public String getEnglish() {
		return "else";
	}

	@Override
	public String getDocumentation() {
		return "Provide an alternative to an if clause.";
	}

}
