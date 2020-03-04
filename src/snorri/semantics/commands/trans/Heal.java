package snorri.semantics.commands.trans;

import snorri.entities.Unit;
import snorri.grammar.PartOfSpeech;
import snorri.semantics.CommandStatus;
import snorri.semantics.Definition;
import snorri.semantics.Lambda;
import snorri.semantics.commands.Command;
import snorri.semantics.nouns.Noun;
import snorri.triggers.TriggerType;

public class Heal implements Definition<Lambda<Noun, Command>> {

	private static final double AMOUNT = 10;

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
					((Unit) obj).heal(AMOUNT, e);
					TriggerType.HEAL.activate(((Unit) obj).getTag());
					return CommandStatus.DONE;
				}
				return CommandStatus.FAILED;
			};
		};
	}

	@Override
	public String getEnglish() {
		return "heal";
	}

	@Override
	public String getDocumentation() {
		return "Heal a target entity.";
	}

}
