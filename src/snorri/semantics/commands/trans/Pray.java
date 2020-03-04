package snorri.semantics.commands.trans;

import snorri.grammar.PartOfSpeech;
import snorri.semantics.CommandStatus;
import snorri.semantics.Definition;
import snorri.semantics.Lambda;
import snorri.semantics.commands.Command;
import snorri.semantics.nouns.Noun;
import snorri.triggers.TriggerType;

public class Pray implements Definition<Lambda<Noun, Command>> {

	@Override
	public PartOfSpeech getPartOfSpeech() {
		return PartOfSpeech.TRANS_CMD;
	}

	@Override
	public Lambda<Noun, Command> getMeaning() {
		return noun -> {
			return e -> {
				Object obj = noun.apply(e);
				if (obj == null) {
					return CommandStatus.FAILED;
				}
				TriggerType.PRAY.activate(obj.toString());
				return CommandStatus.DONE;
			};
		};
	}

	@Override
	public String getEnglish() {
		return "pray";
	}

	@Override
	public String getDocumentation() {
		return "Pray to a god's name.";
	}

}
