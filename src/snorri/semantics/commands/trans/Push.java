package snorri.semantics.commands.trans;

import snorri.entities.Entity;
import snorri.grammar.PartOfSpeech;
import snorri.main.Debug;
import snorri.semantics.CommandStatus;
import snorri.semantics.Definition;
import snorri.semantics.Lambda;
import snorri.semantics.commands.Command;
import snorri.semantics.nouns.Nominal;
import snorri.semantics.nouns.Noun;
import snorri.world.Vector;

public class Push implements Definition<Lambda<Noun, Command>> {
	
	private static Vector DELTA = new Vector(0, -10);
	private static double FORCE = 450d;

	@Override
	public PartOfSpeech getPartOfSpeech() {
		return PartOfSpeech.TRANS_CMD;
	}

	@Override
	public Lambda<Noun, Command> getMeaning() {
		return noun -> {
			return e -> {
				Nominal object = noun.apply(e);
				if (!(object instanceof Entity)) {
					return CommandStatus.FAILED;
				}
				Entity entity = (Entity) object;

				if (entity.isStaticObject()) {
					Debug.logger.info("Can't push static object.");
					return CommandStatus.FAILED;
				}
				
				Vector target = e.getDestination();
				Vector velocity = target.sub(entity.getPos()).scale(FORCE);
				entity.setPos(entity.getPos().add(DELTA));
				entity.setVelocity(velocity);
				return CommandStatus.DONE;
			};
		};
	}

	@Override
	public String getEnglish() {
		return "push";
	}

	@Override
	public String getDocumentation() {
		return "Push an object towards the target location.";
	}

}
