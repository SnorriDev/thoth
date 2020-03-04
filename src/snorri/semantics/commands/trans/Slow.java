package snorri.semantics.commands.trans;

import snorri.entities.Unit;
import snorri.grammar.PartOfSpeech;
import snorri.modifiers.SlowModifier;
import snorri.semantics.CommandStatus;
import snorri.semantics.Definition;
import snorri.semantics.Lambda;
import snorri.semantics.commands.Command;
import snorri.semantics.nouns.Noun;

public class Slow implements Definition<Lambda<Noun, Command>> {

	@Override
	public PartOfSpeech getPartOfSpeech() {
		return PartOfSpeech.TRANS_CMD;
	}

	@Override
	public Lambda<Noun, Command> getMeaning() {
		return noun -> {
			return e -> {
				Object obj = noun.apply(e);
				if (obj instanceof Unit) {
					((Unit) obj).addModifier(new SlowModifier());
					return CommandStatus.DONE;
				}
				return CommandStatus.FAILED;
			};
		};
	}

	@Override
	public String getEnglish() {
		return "slow";
	}

	@Override
	public String getDocumentation() {
		return "Slow the target entity.";
	}

}
