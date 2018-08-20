package snorri.entities;

import snorri.animations.Animation;
import snorri.collisions.RectCollider;
import snorri.entities.Player.Interactor;
import snorri.events.InteractEvent;
import snorri.main.GameWindow;
import snorri.main.Main;
import snorri.world.Vector;

public class Desk extends Entity implements Interactor {

	/**
	 * Desks are the stations where you can edit inventory and spells
	 * Press space to interact with them
	 */
	private static final long serialVersionUID = 1L;
	private static final Animation ANIMATION = new Animation("/textures/objects/inkwell.png");
	
	public Desk(Vector pos) {
		super(pos, new RectCollider(12, 27));
		staticObject = true;
		animation = new Animation(ANIMATION);
	}
	
	@Override
	public void onInteract(InteractEvent e) {
		if (Main.getWindow() instanceof GameWindow) {
			((GameWindow) Main.getWindow()).openInventory(0);
		}
	}

}
