package snorri.semantics;

import java.awt.Rectangle;

import snorri.entities.Entity;
import snorri.events.SpellEvent;
import snorri.main.Main;
import snorri.parser.Node;
import snorri.world.Vector;

public class See extends TransVerbDef {

	public See() {
		super();
	}

	@Override
	public boolean exec(Node<Object> object, SpellEvent e) {
		return false;
	}

	@Override
	public boolean eval(Object subj, Object obj, SpellEvent e) {
		if (subj instanceof Entity && obj instanceof Entity) {
			Vector center = ((Entity) subj).getPos();
			Vector dim = Main.getWindow().getDimensions();
			Rectangle visionRect = new Rectangle(center.getX() - dim.getX() / 2, center.getY() - dim.getY() / 2, dim.getX(), dim.getY()); 
			return ((Entity) obj).intersects(visionRect);
		}
		return false;
	}

	@Override
	public String toString() {
		return "see";
	}

}
