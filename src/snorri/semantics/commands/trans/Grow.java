package snorri.semantics.commands.trans;

import java.lang.reflect.InvocationTargetException;
import java.util.logging.Level;

import snorri.entities.Entity;
import snorri.entities.Plant;
import snorri.grammar.PartOfSpeech;
import snorri.main.Debug;
import snorri.semantics.ClassWrapper;
import snorri.semantics.CommandStatus;
import snorri.semantics.Definition;
import snorri.semantics.Lambda;
import snorri.semantics.commands.Command;
import snorri.semantics.nouns.Noun;
import snorri.world.Vector;

public class Grow implements Definition<Lambda<Noun, Command>> {

	@Override
	public PartOfSpeech getPartOfSpeech() {
		return PartOfSpeech.TRANS_CMD;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Lambda<Noun, Command> getMeaning() {
		return noun -> {
			return e -> {
				Object meaning = noun.apply(e);
				if (!(meaning instanceof ClassWrapper)) {
					return CommandStatus.FAILED;
				}
				Class<?> obj = ((ClassWrapper) meaning).getValue();
				
				if (obj instanceof Class<?> && Plant.class.isAssignableFrom((Class<?>) obj)) {
					try {
						Entity ent = (Entity) ((Class<? extends Entity>) obj).getConstructor(Vector.class).newInstance(e.getLocative());
						e.getWorld().add(ent);
						return CommandStatus.DONE;
					} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
							| NoSuchMethodException | SecurityException e2) {
						Debug.logger.log(Level.WARNING, "Unexpected failure in Grow.", e2);
					}
				}
				
				return CommandStatus.FAILED;
			};
		};
	}

	@Override
	public String getEnglish() {
		return "grow";
	}

	@Override
	public String getDocumentation() {
		return "Grow a type of plant at target location.";
	}

}
