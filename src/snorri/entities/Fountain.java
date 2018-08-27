package snorri.entities;

import snorri.animations.Animation;
import snorri.events.CollisionEvent;
import snorri.world.BackgroundElement;
import snorri.world.Tile;
import snorri.world.Vector;

public class Fountain extends Entity {

	private static final long serialVersionUID = 1L;
	private static final Animation SPRINKLER = new Animation("/textures/animations/fountain");
	private static final int RANGE = 2;
	
	public Fountain(Vector pos) {
		super(pos, 30);
	}
	
	@Override
	public void updateEntityStats() {
		super.updateEntityStats();
		this.animation = new Animation(SPRINKLER);
	}
	
	@Override
	public void onExplosion(CollisionEvent e) {
		
		Vector gridPos = pos.copy().gridPos_();
		for (int x = gridPos.getX() - RANGE; x <= gridPos.getX() + RANGE; x++) {
			for (int y = gridPos.getY() - RANGE; y <= gridPos.getY() + RANGE; y++) {
				if (e.getWorld().getTileLayer().getTileGrid(x, y).getType() == BackgroundElement.WATER) {
					e.getWorld().wrapGridUpdate(x, y, new Tile(BackgroundElement.SAND));
				}
			}
		}
		
		e.getWorld().delete(this);
		
	}
	
}
