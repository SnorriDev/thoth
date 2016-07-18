package snorri.entities;

import java.util.concurrent.CopyOnWriteArrayList;

import snorri.collisions.RectCollider;
import snorri.world.Tile;
import snorri.world.Vector;

public class QuadEntityGroup extends Entity {

	private static final long serialVersionUID = 1L;
	
	private CopyOnWriteArrayList<Entity> entities; //the entities in this level
	private QuadEntityGroup[] nodes; //if this is a leaf, then nodes == null
	
	public QuadEntityGroup(Vector pos, RectCollider collider) {
		super(pos, collider);
		entities = new CopyOnWriteArrayList<Entity>();
		if (getRectCollider().getWidth() / 2 >= Tile.WIDTH) {
			nodes = new QuadEntityGroup[4];
			nodes[0] = getSubQuad(-1, -1);
			nodes[1] = getSubQuad(1, -1);
			nodes[2] = getSubQuad(-1, 1);
			nodes[3] = getSubQuad(1, 1);
		}
	}
	
	private QuadEntityGroup getSubQuad(int x, int y) {
		Vector newDim = getRectCollider().getDimensions().copy().divide(2);
		Vector newPos = pos.copy().add(x * newDim.getX() / 2, y * newDim.getY() / 2);
		RectCollider newCol = new RectCollider(newPos, newDim);
		return new QuadEntityGroup(newPos, newCol);
	}
	
	private RectCollider getRectCollider() {
		return (RectCollider) collider;
	}

}
