package snorri.entities;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import snorri.collisions.RectCollider;
import snorri.main.Debug;
import snorri.main.FocusedWindow;
import snorri.main.Main;
import snorri.world.EntityGroup;
import snorri.world.Level;
import snorri.world.Tile;
import snorri.world.Vector;
import snorri.world.World;

public class QuadTree extends Entity implements EntityGroup {

	private static final long serialVersionUID = 1L;

	private CopyOnWriteArrayList<Entity> entities; // the entities in this level
	private QuadTree[] nodes; // if this is a leaf, then nodes == null
	private boolean isEmpty;

	public QuadTree(Vector pos, RectCollider collider) {
		super(pos, collider);
		isEmpty = true;
		entities = new CopyOnWriteArrayList<Entity>();
		if (getRectCollider().getWidth() / 2 >= Tile.WIDTH) {
			nodes = new QuadTree[4];
			nodes[0] = getSubQuad(-1, -1);
			nodes[1] = getSubQuad(1, -1);
			nodes[2] = getSubQuad(-1, 1);
			nodes[3] = getSubQuad(1, 1);
		}
	}

	/**
	 * Create a quad tree with dimensions <code>dim</code>.
	 * @param dim
	 * the dimensions to cover, in grid coordinates
	 */
	public static QuadTree coverLevel(Level l) {
		Vector pos = l.getDimensions().copy().toGlobalPos().divide(2);
		return new QuadTree(pos, new RectCollider(pos, l.getDimensions().copy().toGlobalPos()));
	}

	private QuadTree getSubQuad(int x, int y) {
		Vector newDim = getRectCollider().getDimensions().copy().divide(2);
		Vector newPos = pos.copy().add(x * newDim.getX() / 2, y * newDim.getY() / 2);
		RectCollider newCol = new RectCollider(newPos, newDim);
		return new QuadTree(newPos, newCol);
	}

	private RectCollider getRectCollider() {
		return (RectCollider) collider;
	}

	/**
	 * Attempt to insert an entity into the QuadTree.
	 * 
	 * @return <code>true</code> iff insertion is successful in this node or in
	 *         a child node
	 */
	public boolean insert(Entity e) {

		if (!contains(e)) {
			return false;
		}

		boolean inChild = false;
		if (nodes != null) {
			for (QuadTree node : nodes) {
				if (node.insert(e)) {
					inChild = true;
					break;
				}
			}
		}
		if (!inChild) {
			entities.add(e);
		}
		isEmpty = false;
		return true;

	}

	/**
	 * Attempt to delete an entity from the tree.
	 * 
	 * @return <code>true</code> iff deletion is successful in this node or in a
	 *         child node
	 */
	public boolean delete(Entity e) {

		// TODO use isEmpty to make even better

		if (isEmpty() || !contains(e)) {
			return false;
		}

		if (nodes != null) {
			for (QuadTree node : nodes) {
				if (!node.isEmpty() && node.contains(e)) {
					boolean out = node.delete(e);
					calculateEmpty();
					return out;
				}
			}
		}
		boolean out = entities.remove(e);
		calculateEmpty();
		return out;

	}

	public List<Entity> getAllCollisions(Entity e) {
		List<Entity> out = new ArrayList<>();
		for (Entity each : entities) {
			if (each.intersects(e)) {
				out.add(each);
			}
		}
		if (nodes != null) {
			for (QuadTree node : nodes) {
				if (!node.isEmpty() && node.intersects(e)) {
					out.addAll(node.getAllCollisions(e));
				}
			}
		}
		return out;
	}

	public Entity getFirstCollision(Entity e) {
		if (nodes != null) {
			for (QuadTree node : nodes) {
				if (!node.isEmpty() && node.intersects(e)) {
					Entity col = node.getFirstCollision(e);
					if (col != null) {
						return col;
					}
				}
			}
		}
		for (Entity each : entities) {
			if (each.intersects(e)) {
				return each;
			}
		}
		return null;
	}

	public void calculateEmpty() {
		isEmpty = entities.isEmpty();
		if (nodes != null) {
			isEmpty &= nodes[0].isEmpty && nodes[1].isEmpty && nodes[2].isEmpty && nodes[3].isEmpty;
		}
	}

	public boolean isEmpty() {
		return isEmpty;
	}

	public int getHeight() {

		if (nodes == null) {
			return 1;
		}

		return 1 + Integer.max(Integer.max(nodes[0].getHeight(), nodes[1].getHeight()),
				Integer.max(nodes[2].getHeight(), nodes[3].getHeight()));

	}

	@Override
	public void updateAround(World world, double deltaTime, Entity focus) {

		if (focus == null) {
			return;
		}

		Entity updateRange = new Entity(focus.pos, World.UPDATE_RADIUS);

		for (Entity e : entities) {
			if (e.intersects(updateRange)) {
				e.update(world, deltaTime);
			}
		}
		if (nodes != null) {
			for (QuadTree node : nodes) {
				if (!node.isEmpty() && node.intersects(updateRange)) {
					node.updateAround(world, deltaTime, focus);
				}
			}
		}

	}

	@Override
	public void renderAround(FocusedWindow window, Graphics gr) {
		
		if (Debug.RENDER_TREE) {
			collider.render(window, gr);
		}
		
		Vector playerPos = window.getFocus().getPos();
		Vector dim = window.getDimensions();
		Rectangle view = new Rectangle(playerPos.getX() - dim.getX() / 2, playerPos.getY() - dim.getY() / 2, dim.getX(),
				dim.getY());
		for (Entity e : entities) {
			if (e.intersects(view)) {
				e.renderAround(window, gr);
			}
		}
		if (nodes != null) {
			for (QuadTree node : nodes) {
				if (!node.isEmpty() && node.intersects(view)) {
					node.renderAround(window, gr);
				}
			}
		}
	}

	@Override
	public List<Entity> getAllEntities() {
		List<Entity> result = new ArrayList<>();
		result.addAll(entities);
		if (nodes != null) {
			for (QuadTree node : nodes) {
				if (!node.isEmpty()) {
					result.addAll(node.getAllEntities());
				}
			}
		}
		return result;
	}

	@Override
	public void traverse() {
		Main.log("traverse not yet implemented for QuadTree");
	}

}
