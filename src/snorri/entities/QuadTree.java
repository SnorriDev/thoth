package snorri.entities;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.PriorityQueue;
import java.util.concurrent.CopyOnWriteArrayList;

import snorri.collisions.RectCollider;
import snorri.main.Debug;
import snorri.main.FocusedWindow;
import snorri.world.EntityGroup;
import snorri.world.Level;
import snorri.world.Tile;
import snorri.world.Vector;
import snorri.world.World;

public class QuadTree extends Entity implements EntityGroup {

	private static final long serialVersionUID = 1L;

	private static HashMap<Entity, QuadTree> nodeMap;
	
	static {
		nodeMap = new HashMap<>();
	}
	
	/** the entities in this level of the tree */
	private CopyOnWriteArrayList<Entity> entities; // the entities in this level
	private QuadTree parent;
	private QuadTree[] nodes; // if this is a leaf, then nodes == null
	private boolean isEmpty;
	
	public QuadTree(Vector pos, RectCollider collider, QuadTree parent) {
		super(pos, collider);
		isEmpty = true;
		entities = new CopyOnWriteArrayList<Entity>();
		this.parent = parent;
		if (getRectCollider().getRadiusX() / 2 >= Tile.WIDTH) {
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
	 * 	the dimensions to cover, in grid coordinates
	 */
	public static QuadTree coverLevel(Level l) {
		Vector pos = l.getDimensions().copy().toGlobalPos().divide(2);
		return new QuadTree(pos, new RectCollider(l.getDimensions().copy().toGlobalPos()), null);
	}

	private QuadTree getSubQuad(int x, int y) {
		Vector newDim = getRectCollider().getDimensions().copy().divide(2);
		Vector newPos = pos.copy().add(x * newDim.getX() / 2, y * newDim.getY() / 2);
		RectCollider newCol = new RectCollider(newDim);
		return new QuadTree(newPos, newCol, this);
	}

	private RectCollider getRectCollider() {
		return (RectCollider) collider;
	}

	public void add(Entity e) {
		entities.add(e);
		nodeMap.put(e, this);
	}
	
	/**
	 * Attempt to insert an entity into the <code>QuadTree</code>
	 * @return <code>true</code> iff insertion is successful in this node or in
	 *         a child node
	 */
	@Override
	public boolean insert(Entity e) {

		if (!contains(e)) {
			return false;
		}
		
		EntityGroup.super.insert(e); //update tags

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
			add(e);
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
		
		QuadTree local = nodeMap.get(e);
		if (local == null) {
			return false;
		}
		boolean out = local.entities.remove(e);
		local.calculateEmptyRec();
		return out;

	}
	
	public void move(Entity e, Vector newPos) {
		QuadTree node = nodeMap.get(e);
		e.setPos(newPos.copy());
		if (!node.contains(e)) {
			delete(e);
			insert(e);
		}
	}
	
	@Deprecated @SuppressWarnings("unused")
	private void backInsert(Entity e) {
		if (!insert(e) && getParent() != null) {
			getParent().backInsert(e);
		}
	}

	@Override
	public List<Entity> getAllCollisions(Entity e, boolean hitAll) {
		List<Entity> out = new ArrayList<>();
		for (Entity each : entities) {
			if ((hitAll || !each.shouldIgnoreCollisions()) && each.intersects(e) && !each.equals(e)) {
				out.add(each);
			}
		}
		if (nodes != null) {
			for (QuadTree node : nodes) {
				if (!node.isEmpty() && node.intersects(e)) {
					out.addAll(node.getAllCollisions(e, hitAll));
				}
			}
		}
		return out;
	}
	
	public PriorityQueue<Entity> getRenderQueue(Rectangle r) {
		
		PriorityQueue<Entity> out = new PriorityQueue<>();
		for (Entity each : entities) {
			if (each.intersects(r)) {
				out.add(each);
			}
		}
		if (nodes != null) {
			for (QuadTree node : nodes) {
				if (!node.isEmpty() && node.intersects(r)) {
					out.addAll(node.getRenderQueue(r));
				}
			}
		}
		return out;
	}

	public Entity getFirstCollision(Entity e, boolean hitAll) {
		if (nodes != null) {
			for (QuadTree node : nodes) {
				if (!node.isEmpty() && node.intersects(e)) {
					Entity col = node.getFirstCollision(e, hitAll);
					if (col != null) {
						return col;
					}
				}
			}
		}
		for (Entity each : entities) {
			if ((hitAll || !each.shouldIgnoreCollisions()) && each.intersects(e) && !each.equals(e)) {
				return each;
			}
		}
		return null;
	}
	
	/**
	 * used to check for entities in tiles
	 */
	public Entity getFirstCollision(Rectangle rect, boolean hitAll) {
		if (nodes != null) {
			for (QuadTree node : nodes) {
				if (!node.isEmpty() && node.intersects(rect)) {
					Entity col = node.getFirstCollision(rect, hitAll);
					if (col != null) {
						return col;
					}
				}
			}
		}
		for (Entity each : entities) {
			if ((hitAll || !each.shouldIgnoreCollisions()) && each.intersects(rect)) {
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
	
	private void calculateEmptyRec() {
		calculateEmpty();
		if (isEmpty() && getParent() != null) {
			getParent().calculateEmpty();
		}
	}

	public QuadTree getParent() {
		return parent;
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

	/**
	 * Render all entities in the tree, and add in the passed list
	 */
	@Override
	public void renderAround(FocusedWindow<?> window, Graphics gr, double deltaTime) {
		
		Vector playerPos = window.getFocus().getPos();
		Vector dim = window.getDimensions();
		Rectangle view = new Rectangle(playerPos.getX() - dim.getX() / 2, playerPos.getY() - dim.getY() / 2, dim.getX(),
				dim.getY());
		
		PriorityQueue<Entity> renderQueue = getRenderQueue(view);
		
		if (Debug.LOG_RENDER_QUEUE) {
			Debug.log("render queue: " + renderQueue);
		}
		
		while (!renderQueue.isEmpty()) {
			renderQueue.poll().renderAround(window, gr, deltaTime);
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
		Debug.log("traverse not yet implemented for QuadTree");
	}

	@Override
	public Entity getFirstCollisionOtherThan(Entity e, Entity other) {
		if (nodes != null) {
			for (QuadTree node : nodes) {
				if (!node.isEmpty() && node.intersects(e)) {
					Entity col = node.getFirstCollisionOtherThan(e, other);
					if (col != null) {
						return col;
					}
				}
			}
		}
		for (Entity each : entities) {
			if (!each.shouldIgnoreCollisions() && each.intersects(e) && !each.equals(other)) {
				return each;
			}
		}
		return null;
	}

	@Override @SuppressWarnings("unchecked")
	public <P> P getFirstCollision(Entity checker, Class<P> class1) {
		if (nodes != null) {
			for (QuadTree node : nodes) {
				if (!node.isEmpty() && node.intersects(checker)) {
					P col = node.getFirstCollision(checker, class1);
					if (col != null) {
						return col;
					}
				}
			}
		}
		for (Entity each : entities) {
			if (class1.isInstance(each) && each.intersects(checker)) {
				return (P) each;
			}
		}
		return null;
	}

}
