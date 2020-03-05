package snorri.semantics.predicates.trans;

import java.awt.Rectangle;

import snorri.entities.Entity;
import snorri.grammar.PartOfSpeech;
import snorri.main.Main;
import snorri.semantics.CommandStatus;
import snorri.semantics.Definition;
import snorri.semantics.Lambda;
import snorri.semantics.commands.Command;
import snorri.semantics.nouns.Nominal;
import snorri.semantics.nouns.Noun;
import snorri.world.Vector;

public class See implements Definition<Lambda<Noun, Lambda<Noun, Command>>> {

	@Override
	public PartOfSpeech getPartOfSpeech() {
		return PartOfSpeech.TRANS_PRED;
	}

	@Override
	public Lambda<Noun, Lambda<Noun, Command>> getMeaning() {
		return subjNoun -> {
			return objNoun -> {
				return e -> {
					Nominal subj = subjNoun.apply(e);
					Nominal obj = objNoun.apply(e);
					if (subj instanceof Entity && obj instanceof Entity) {
						Vector center = ((Entity) subj).getPos();
						Vector dim = Main.getWindow().getDimensions();
						Rectangle visionRect = new Rectangle(center.getX() - dim.getX() / 2, center.getY() - dim.getY() / 2, dim.getX(), dim.getY()); 
						if (((Entity) obj).intersects(visionRect)) {
							return CommandStatus.TRUE;
						}
					}
					return CommandStatus.FALSE;
				};
			};
		};
	}

	@Override
	public String getEnglish() {
		return "sees";
	}

	@Override
	public String getDocumentation() {
		return "Check whether the object is in the subject's field of vision.";
	}

}
