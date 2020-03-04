package snorri.semantics.commands.trans;

import snorri.entities.Unit;
import snorri.grammar.PartOfSpeech;
import snorri.semantics.CommandStatus;
import snorri.semantics.Definition;
import snorri.semantics.Lambda;
import snorri.semantics.commands.Command;
import snorri.semantics.nouns.Nominal;
import snorri.semantics.nouns.Noun;

public class Damage implements Definition<Lambda<Noun, Command>> {

	private static final int AMOUNT = 30;

	@Override
	public PartOfSpeech getPartOfSpeech() {
		return PartOfSpeech.TRANS_CMD;
	}

	@Override
	public Lambda<Noun, Command> getMeaning() {
		return noun -> {
			return e -> {
				Nominal obj = noun.apply(e);
				if (obj instanceof Unit) {
					((Unit) obj).damage(AMOUNT, e);
					return CommandStatus.DONE;
				}
				return CommandStatus.FAILED;
			};
		};
	}

	@Override
	public String getEnglish() {
		return "damage";
	}

	@Override
	public String getDocumentation() {
		return "Damage a target.";
	}

}
