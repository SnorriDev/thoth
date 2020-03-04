package snorri.semantics.commands.trans;

import snorri.entities.Unit;
import snorri.grammar.PartOfSpeech;
import snorri.modifiers.BurnModifier;
import snorri.semantics.CommandStatus;
import snorri.semantics.Definition;
import snorri.semantics.Lambda;
import snorri.semantics.commands.Command;
import snorri.semantics.nouns.Noun;

public class Burn implements Definition<Lambda<Noun, Command>> {

	@Override
	public PartOfSpeech getPartOfSpeech() {
		return PartOfSpeech.TRANS_CMD;
	}

	@Override
	public Lambda<Noun, Command> getMeaning() {
		return object -> {
			return event -> {
				Object obj = object.apply(event);
				if (obj instanceof Unit) {
					((Unit) obj).addModifier(new BurnModifier());
					return CommandStatus.DONE;
				}
				return CommandStatus.FAILED;
			};
		};
	}

	@Override
	public String getEnglish() {
		return "burn";
	}

	@Override
	public String getDocumentation() {
		return "Burn a targetted object.";
	}

}
