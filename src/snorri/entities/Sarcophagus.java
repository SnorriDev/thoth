package snorri.entities;

import snorri.animations.Animation;
import snorri.collisions.RectCollider;
import snorri.main.Main;
import snorri.world.Vector;

public class Sarcophagus extends Entity {

	public Sarcophagus(Vector pos) {
		super(pos, new RectCollider(new Vector(30, 140)));
		Main.log("new sarcophagus");
		animation = new Animation("/textures/objects/sarcophagus.png");
	}

	private static final long serialVersionUID = 1L;

}
