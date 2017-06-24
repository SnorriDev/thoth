package snorri.entities;

import snorri.animations.Animation;
import snorri.collisions.RectCollider;
import snorri.inventory.RandomDrop;
import snorri.semantics.Break.Smashable;
import snorri.world.Vector;
import snorri.world.World;

public class Urn extends Despawner implements Smashable {

	private static final long serialVersionUID = 1L;
	private static final Animation ANIMATION = new Animation("/textures/objects/urn.png");
	private static final double DROP_CHANCE = 0.1;
	
	public Urn(Vector pos) {
		super(pos, new RectCollider(new Vector(10, 26)));
		animation = new Animation(ANIMATION);
	}
	
	@Override
	public boolean onDelete(World world) {
		if (!super.onDelete(world)) {
			return false;
		}
		if (Math.random() < DROP_CHANCE) {
			world.add(new Drop(pos.copy(), new RandomDrop(RandomDrop.Tier.COMMON)));
		}
		return true;
	}

}
