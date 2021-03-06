package snorri.entities;

import snorri.animations.Animation;
import snorri.events.CollisionEvent;
import snorri.world.Tile;
import snorri.world.UnifiedTileType;
import snorri.world.Vector;

public class Fountain extends Entity {

	private static final long serialVersionUID = 1L;
	private static final Animation SPRINKLER = new Animation("/textures/animations/fountain");
	private static final int RANGE = 2;
	
	public Fountain(Vector pos) {
		super(pos, 30);
	}
	
	@Override
	public void refreshStats() {
		super.refreshStats();
		this.animation = new Animation(SPRINKLER);
	}
	
	@Override
	public void onExplosion(CollisionEvent e) {
		
		Vector gridPos = pos.copy().gridPos_();
		for (int x = gridPos.getX() - RANGE; x <= gridPos.getX() + RANGE; x++) {
			for (int y = gridPos.getY() - RANGE; y <= gridPos.getY() + RANGE; y++) {
				if (e.getWorld().getTileLayer().getTileGrid(x, y).getType() == UnifiedTileType.WATER) {
					e.getWorld().wrapGridUpdate(x, y, new Tile(UnifiedTileType.SAND));
				}
			}
		}
		
		e.getWorld().delete(this);
		
	}
	
}
