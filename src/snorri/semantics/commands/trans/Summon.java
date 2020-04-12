package snorri.semantics.commands.trans;

import snorri.entities.Entity;
import snorri.grammar.PartOfSpeech;
import snorri.semantics.ClassWrapper;
import snorri.semantics.CommandStatus;
import snorri.semantics.Definition;
import snorri.semantics.Lambda;
import snorri.semantics.commands.Command;
import snorri.semantics.nouns.Noun;

public class Summon implements Definition<Lambda<Noun, Command>> {
	
	public interface Summonable {
		/** `Summonable` entities should have a constructor that takes a position. */
	}

	@Override
	public PartOfSpeech getPartOfSpeech() {
		return PartOfSpeech.TRANS_CMD;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Lambda<Noun, Command> getMeaning() {
		return noun -> {
			return e -> {				
				if (!e.getFirstPerson().spendMana(100.)) {
					return CommandStatus.RAN_OUT_OF_MANA;
				}
				
				Object obj = noun.apply(e);
				if (!(obj instanceof ClassWrapper)) {
					return CommandStatus.FAILED;
				}
				Class<? extends Entity> c = (Class<? extends Entity>) ((ClassWrapper) obj).getValue();
				if (!Summonable.class.isAssignableFrom(c)) {
					return CommandStatus.FAILED;
				}
				
				Entity spawned = Entity.spawnNew(e.getWorld(), e.getLocative(), c, true);
				if (spawned == null) {
					return CommandStatus.FAILED;
				}
				return CommandStatus.DONE;
			};
		};
	}

	@Override
	public String getEnglish() {
		return "summon";
	}

	@Override
	public String getDocumentation() {
		return "Summon an entity (for example, a mummy).";
	}

}
