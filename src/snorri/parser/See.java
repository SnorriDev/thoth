package snorri.parser;

import java.awt.Rectangle;

import snorri.entities.Entity;
import snorri.main.Main;
import snorri.semantics.VerbDef;
import snorri.world.Vector;

public class See extends VerbDef {

	public See() {
		super(true);
	}

	@Override
	public boolean exec(Object obj) {
		return false;
	}

	@Override
	public boolean eval(Object subj, Object obj) {
		if (subj instanceof Entity && obj instanceof Entity) {
			Vector center = ((Entity) subj).getPos();
			Vector dim = Main.getWindow().getDimensions();
			Rectangle visionRect = new Rectangle(center.getX() - dim.getX() / 2, center.getY() - dim.getY() / 2, dim.getX(), dim.getY()); 
			return ((Entity) obj).intersects(visionRect);
		}
		return false;
	}

}
