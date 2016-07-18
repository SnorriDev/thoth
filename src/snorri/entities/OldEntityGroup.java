package snorri.entities;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ConcurrentModificationException;
import java.util.concurrent.CopyOnWriteArrayList;

import snorri.collisions.CircleCollider;
import snorri.main.FocusedWindow;
import snorri.main.Main;
import snorri.world.EntityGroup;
import snorri.world.Vector;
import snorri.world.World;


public class OldEntityGroup extends Entity implements EntityGroup {

	private static final long serialVersionUID = 1L;

	// TODO: within each EntityGroup, store entities in a PriorityQueue
	// nice PriorityQueue from
	// https://docs.oracle.com/javase/7/docs/api/java/util/concurrent/package-summary.html
	
	private CopyOnWriteArrayList<Entity> entities;

	// can make this stuff more elegant
	public OldEntityGroup(Entity root) {
		super(root);
		collider = new CircleCollider(pos, root.collider.getMaxRadius());
		entities = new CopyOnWriteArrayList<Entity>();
		entities.add(root);
	}

	public OldEntityGroup(Entity r1, Entity r2) {

		this(r1);
		
		if (r1.intersects(r2)) {
			if (r1 instanceof OldEntityGroup && r2 instanceof OldEntityGroup) {
				((OldEntityGroup) r1).merge((OldEntityGroup) r2);
				set(r1);
				return;
			} else if (r1 instanceof OldEntityGroup) {
				((OldEntityGroup) r1).insert(r2);
				set(r1);
				return;
			} else if (r2 instanceof OldEntityGroup) {
				((OldEntityGroup) r2).insert(r1);// this fix?
				set(r2);
				return;
			}
		}

		//TODO: figure out what to do here
		
		entities.add(r2);

		// compute axis vector between the two centers
		Vector axis = r2.pos.copy();
		axis.sub(r1.pos);

		// compute the new radius
		((CircleCollider) collider).setRadius((r1.pos.distance(r2.pos) + r1.collider.getMaxRadius() + r2.collider.getMaxRadius()) / 2);

		// scale the axis to the vector from r1 center to the new center
		axis.scale(collider.getMaxRadius() - r1.collider.getMaxRadius());
		pos.add(axis);

	}

	public OldEntityGroup() {
		super(null, 0);
		entities = new CopyOnWriteArrayList<Entity>();
	}

	public OldEntityGroup(File file) throws FileNotFoundException, IOException {
		this();
		loadEntities(file);
	}

	public OldEntityGroup(Vector center, int rad) {
		super(center, rad);
		entities = new CopyOnWriteArrayList<Entity>();
	}

	public OldEntityGroup(Entity e1, Entity e2, Entity e3) {

		// make slightly better by not treating EntityGroups as points? nah
		// need to check if the things intersect each other in the constructor?

		this();

		entities.add(e1);
		entities.add(e2);
		entities.add(e3);

		Vector p1 = e1.pos;
		Vector p2 = e2.pos;
		Vector p3 = e3.pos;

		double a = p2.x - p1.x;
		double b = p2.y - p1.y;
		double c = p3.y - p1.y;
		double d = p3.y - p1.y;
		double e = a * (p2.x + p1.x) * 0.5 + b * (p2.y + p1.y) * 0.5;
		double f = c * (p3.x + p1.x) * 0.5 + d * (p3.y + p1.y) * 0.5;
		double det = a * d - b * c;

		pos = new Vector((int) ((d * e - b * f) / det), (int) ((-c * e + a * f) / det));
		((CircleCollider) collider).setRadius(p1.distance(pos) + Math.max(e3.collider.getMaxRadius(), Math.max(e1.collider.getMaxRadius(), e2.collider.getMaxRadius())));

	}

	/**
	 * @return the collection of entities in the world
	 */
	public CopyOnWriteArrayList<Entity> getAllEntities() {
		CopyOnWriteArrayList<Entity> res = new CopyOnWriteArrayList<Entity>();
		for (Entity e : entities) {
			if (e instanceof OldEntityGroup) {
				res.addAll(((OldEntityGroup) e).getAllEntities());
				continue;
			}
			res.add(e);
		}
		return res;
	}

	public boolean hasChild(Entity e) {

		for (Entity e2 : entities) {
			if (e2 instanceof OldEntityGroup) {
				if (e2.intersects(e)) {
					return true;
				}
			}
			if (e2 == e) {
				return true;
			}
		}

		return false;

	}

	/**
	 * blind-add an entity to the group
	 * @param n
	 * 	the entity to add
	 */
	private void add(Entity n) {
		entities.add(n);
		setEnclosing();
	}

	/**
	 * blind remove an entity from the group
	 * @param n
	 * 	the entity to remove
	 * @return
	 * 	whether or not something was removed
	 */
	private boolean remove(Entity n) {

		if (entities.remove(n)) {
			setEnclosing();
			return true;
		}

		return false;

	}
	
	/**
	 * @return an array containing the entities in this group
	 */
	public Entity[] getSafeArray() {
		return entities.toArray(new Entity[0]);
	}

	public void setEnclosing() {

		Entity[] points = getSafeArray();
		Entity[] boundary = new Entity[3];

		OldEntityGroup enclosing = getEnclosing(points, points.length, boundary, 0);

		pos = (enclosing.pos == null) ? null : enclosing.pos.copy();
		((CircleCollider) collider).setRadius(enclosing.collider.getMaxRadius());
		((CircleCollider) collider).increaseRadius(getMaxRadius(points));

		//TODO: equals vs spatialEquals here
		if (entities.size() == 1 && equals(entities.get(0))) {
			set(entities.get(0));
		}

	}

	private int getMaxRadius(Entity[] boundary) {
		int max = 0;
		for (int i = 0; i < boundary.length; i++) {
			if (boundary[i] != null && !contains(boundary[i]) && Double.isFinite(boundary[i].collider.getMaxRadius())) {
				max = boundary[i].collider.getMaxRadius() > max ? boundary[i].collider.getMaxRadius() : max;
			}
		}
		return max;
	}

	/**
	 * linear time algorithm for getting enclosing entity
	 * 
	 * @return an EntityGroup which encloses all the other entities
	 */
	private static OldEntityGroup getEnclosing(Entity[] points, int n, Entity[] boundary, int b) {

		if (points.length == 0) {
			return new OldEntityGroup();
		}

		if (n == 0 && b == 2) {
			return new OldEntityGroup(boundary[0], boundary[1]);
		}
		if (n == 1 && b == 0) {
			return new OldEntityGroup(points[0]);
		}
		if (n == 1 && b == 1) {
			return new OldEntityGroup(boundary[0], points[0]);
		}
		if (b == 3) {
			return new OldEntityGroup(boundary[0], boundary[1], boundary[2]);
		}

		OldEntityGroup circle = getEnclosing(points, n - 1, boundary, b);

		// TODO: figure out what to do. contains?
		// this is it
		if (!circle.intersects(points[n - 1].pos)) {

			boundary[b++] = points[n - 1];
			circle = getEnclosing(points, n - 1, boundary, b);

		}

		return circle;

	}

	public boolean isEmpty() {
		return entities.isEmpty();
	}

	public boolean insert(Entity e) {

		// TODO: can we make this even better?

		if (pos == null) {
			set(new OldEntityGroup(e));
			return true;
		}

		if (entities.size() == 1) {
			set(new OldEntityGroup(entities.get(0), e));
			return true;
		}

		if (!intersects(e)) {
			OldEntityGroup group = new OldEntityGroup();
			group.set(this);
			set(new OldEntityGroup(group, e));
			return true;
		}

//		if (e instanceof EntityGroup) {
//			for (Entity child : ((EntityGroup) e).entities) {
//				insert(child);
//			}
//			return;
//		}

		for (Entity child : getSafeArray()) {
			
			if (child instanceof OldEntityGroup && child.intersects(e)) {
				remove(child);
				((OldEntityGroup) child).insert(e);
				insert(child);
				return true;
			}
			
			//this might not always work as intended
			//we are preserving structure when inserting EntityGroups, not "merging" them
//			if (e instanceof EntityGroup && child.intersects(e)) {
//				delete(child);
//				((EntityGroup) e).insert(child);
//				insert(e);
//				return;
//			}

		}

		add(e);
		return true;

	}

	public boolean delete(Entity e) {

		if (remove(e)) {
			return true;
		}

		for (Entity child : getSafeArray()) {
			if (child instanceof OldEntityGroup && ((OldEntityGroup) child).delete(e)) {
				if (((OldEntityGroup) child).isEmpty()) {
					remove(child);
				}
				return true;
			}
		}

		return false;

	}

	/**
	 * this method is now useful since the merge-like behavior of insert has been removed
	 * @param group
	 * 	the group of entities to be merged into this one
	 */
	public void merge(OldEntityGroup group) {

		for (Entity next : getSafeArray()) {

			if (next instanceof OldEntityGroup) {
				merge((OldEntityGroup) next);
			} else {
				insert(next);
			}

		}

	}

	@Deprecated
	public boolean move(Entity e, Vector trans) {

		if (trans.notInPlane()) {
			return false;
		}

		delete(e);
		e.pos.add(trans);
		insert(e);
		return true;

	}

	public Entity getFirstCollision(Entity c) {

		for (Entity child : entities) {

			if (child.shouldIgnoreCollisions()) {
				continue;
			}
			
			if (child instanceof OldEntityGroup) {
				Entity result = ((OldEntityGroup) child).getFirstCollision(c);
				if (result == null) {
					continue;
				}
				return result;
			}

			if (child.intersects(c)) {
				return child;
			}

		}

		return null;

	}

	// could maybe write this more efficiently
	public CopyOnWriteArrayList<Entity> getAllCollisions(Entity c) {

		CopyOnWriteArrayList<Entity> result = new CopyOnWriteArrayList<Entity>();

		for (Entity child : entities) {

			if (child.shouldIgnoreCollisions()) {
				continue;
			}
			
			if (child instanceof OldEntityGroup) {
				result.addAll(((OldEntityGroup) child).getAllCollisions(c));
				continue;
			}

			if (child.intersects(c)) {
				result.add(child);
			}

		}

		return result;

	}

	private void set(Entity e) {
		pos = e.pos;
		((CircleCollider) collider).setRadius(e.collider.getMaxRadius());
		//^this line is what enables/disables shit
		//PROBABLY have to fix insert method?
		if (e instanceof OldEntityGroup) {
			entities = ((OldEntityGroup) e).entities;
		}
	}
	
	public void recalculate(Entity update) {
		if (delete(update)) {
			insert(update);
			setEnclosing();
		} else {
			Main.error("move failed on " + update);
		}
	}
	
	@Override
	public void renderAround(FocusedWindow g, Graphics gr) {
		Vector playerPos = g.getFocus().getPos();
		Vector dim = g.getDimensions();
		Rectangle view = new Rectangle(playerPos.getX() - dim.getX() / 2, playerPos.getY() - dim.getY() / 2, dim.getX(), dim.getY());
		try {
			for (Entity e : entities.toArray(new Entity[0])) {
				if (e.intersects(view)) {
					// the is in renderHitbox (Entity)
					e.renderAround(g, gr);
				}
			}
		} catch (ConcurrentModificationException e) {
			Main.error("concurrent modification detected");
		}
	}

	@Override
	protected void traverse(int depth) {
		super.traverse(depth);

		if (entities == null) {
			return;
		}

		for (Entity e : entities) {
			e.traverse(depth + 1);
		}
	}

	/**
	 * update in a radius around a target entity
	 * @param world
	 * 	the world where the update is taking place
	 * @param deltaTime
	 * 	time since the last update (in seconds)
	 * @param focus
	 * 	the entity to update around
	 */
	public void updateAround(World world, double deltaTime, Entity focus) {
		
		if (focus == null) {
			return;
		}
		
		for (Entity e : entities) {
			if (e.intersects(new Entity(focus.pos, World.UPDATE_RADIUS))) {
				if (e instanceof OldEntityGroup) {
					((OldEntityGroup) e).updateAround(world, deltaTime, focus);
				} else {
					e.update(world, deltaTime);
				}
			}
		}
	}

	/**
	 * use updateAround instead
	 */
	@Deprecated @Override
	public void update(World world, double d) {
		for (Entity e : entities) {
			e.update(world, d);
		}
	}

}