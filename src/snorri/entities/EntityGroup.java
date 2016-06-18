package snorri.entities;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.Iterator;

import snorri.main.FocusedWindow;
import snorri.main.Main;
import snorri.world.Vector;
import snorri.world.World;

public class EntityGroup extends Entity {
	
	private static final long serialVersionUID = 1L;

	private static final int REACH = 0;
	private static final int UPDATE_RADIUS = 4000;
	
	//TODO: within each EntityGroup, store entities in a PriorityQueue so we can draw them in the correct order
	ArrayList<Entity> entities;
	
	//can make this stuff more elegant
	public EntityGroup(Entity root) {
		super(root);
		entities = new ArrayList<Entity>();
		entities.add(root);
	}
	
	public EntityGroup(Entity r1, Entity r2) {
		
		this(r1);
		
		//this is the problem; when inserting EntityGroups
		
		//need to check if the things intersect each other in the constructor
		//could clean this up?
		if (r1.intersects(r2)) {
			if (r1 instanceof EntityGroup && r2 instanceof EntityGroup) {
				((EntityGroup) r1).merge((EntityGroup) r2); //merge and insert don't quite do the same thing
				set(r1);
				return;
			} else if (r1 instanceof EntityGroup) {
				((EntityGroup) r1).add(r2);
				set(r1);
				return;
			} else if (r2 instanceof EntityGroup) {
				((EntityGroup) r2).add(r1);//this fix?
				set(r2);
				return;
			}
		}
		
		entities.add(r2);
		
		//compute axis vector between the two centers
		Vector axis = r2.pos.copy();
		axis.sub(r1.pos);
		
		//compute the new radius
		r = (r1.pos.distance(r2.pos) + r1.r + r2.r) / 2;
		
		//scale the axis to the vector from r1 center to the new center
		axis.scale(r - r1.r);	
		pos.add(axis);
		
	}
	
	public EntityGroup() {
		super(null, 0);
		entities = new ArrayList<Entity>();
	}
	
	public EntityGroup(File file) throws FileNotFoundException, IOException {
		this();
		loadEntities(file);
	}
	
	public EntityGroup(Vector center, int rad) {
		super(center, rad);
		entities = new ArrayList<Entity>();
	}
	
	public EntityGroup(Entity e1, Entity e2, Entity e3) {

		//TODO: could potentially be made better if we don't treat entities as points
		
		this();
		
		//need to check if the things intersect each other in the constructor?
		
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
		r = p1.distance(pos) + Integer.max(e3.r, Integer.max(e1.r, e2.r));
		
	}
	
	private Iterator<Entity> getChildren() {
		return entities.iterator();
	}
	
	public ArrayList<Entity> getAllEntities() {
		ArrayList<Entity> res = new ArrayList<Entity>();
		for (Entity e : entities) {
			if (e instanceof EntityGroup) {
				res.addAll(((EntityGroup) e).getAllEntities());
				continue;
			}
			res.add(e);
		}
		return res;
	}
	
	//sadly, doing it this inefficient way is the only way to ensure uniform distribution
	public Entity getRandomEntity() {
		ArrayList<Entity> all = getAllEntities();
		return all.get((int) (Math.random() * all.size()));
	}
	
	public boolean hasChild(Entity e) {
		
		for (Entity e2 : entities) {
			if (e2 instanceof EntityGroup) {
				if (e2.contains(e)) {
					return true;
				}
			}
			if (e2 == e) {
				return true;
			}
		}
		
		return false;
		
	}
	
	//blind-add an entity to the group
	//adjust average
	private void add(Entity n) {		
		entities.add(n);
		setEnclosing();
	}
	
	//blind-remove an entity from group
	//adjust average and return whether or not it is empty
	private boolean remove(Entity n) {
					
		if (entities.remove(n)) {
			setEnclosing();
			return true;
		}
		
		return false;
				
	}
		
	public void setEnclosing() {
		
		Entity[] points = (Entity[]) entities.toArray(new Entity[] {});
		Entity[] boundary = new Entity[3];
		
		EntityGroup enclosing = EntityGroup.getEnclosing(points, points.length, boundary, 0);
		
		pos = (enclosing.pos == null) ? null : enclosing.pos.copy();
		r = enclosing.r + REACH; //"MARGIN"
		
		r += getMaxRadius(points);
				
		if (entities.size() == 1 && spatialEquals(entities.get(0))) {
			set(entities.get(0));
		}
		
		//TODO: points getting deleted not related to this
		
	}
	
	private int getMaxRadius(Entity[] boundary) {
		int max = 0;
		for (int i = 0; i < boundary.length; i++) {
			if (boundary[i] != null && ! contains(boundary[i]) && Double.isFinite(boundary[i].r)) {
				max = boundary[i].r > max ? boundary[i].r : max;
			}			
		}
		return max;
	}

	//linear time algorithm for getting enclosing entity
	private static EntityGroup getEnclosing(Entity[] points, int n, Entity[] boundary, int b) {
		
		if (points.length == 0) {
			return new EntityGroup();
		}
		
		if (n == 0 && b == 2) {
			return new EntityGroup(boundary[0], boundary[1]);
		}
		if (n == 1 && b == 0) {
			return new EntityGroup(points[0]);
		}
		if (n == 1 && b == 1) {
			return new EntityGroup(boundary[0], points[0]);
		}
		if (b == 3) {
			return new EntityGroup(boundary[0], boundary[1], boundary[2]);
		}
		 
		EntityGroup circle = getEnclosing(points, n - 1, boundary, b);
		
		//TODO: figure out what to do. contains?
		//this is it
		if (! circle.intersects(points[n - 1].pos)) {
			
			boundary[b++] = points[n - 1];
			circle = getEnclosing(points, n - 1, boundary, b);
			
		}
		
		return circle;
					
	}
	
	public boolean isEmpty() {
		return entities.isEmpty();
	}
	
	public void insert(Entity e) {
		
		if (pos == null) {
			set(new EntityGroup(e));
			return;
		}
		
		if (entities.size() == 1) {
			set(new EntityGroup(entities.get(0), e));
			return;
		}
		
		if (!intersects(e, REACH)) {
						
			EntityGroup group = new EntityGroup();
			group.set(this);
			set(new EntityGroup(group, e));
			return;
		}
		
		if (e instanceof EntityGroup) {
			for (Entity child : ((EntityGroup) e).entities) {
				insert(child);
			}
			return;
		}
		
		//TODO MAJOR PROGRESS
		//The issue is here, with intersects
								
		Iterator<Entity> iter = getChildren();
		while (iter.hasNext()) {
			Entity child = iter.next();
			if (child instanceof EntityGroup) {
				
				if (child.contains(e, REACH)) {					
					remove(child);
					((EntityGroup) child).insert(e);
					add(child); //some weird cases might make add not work here; change the behavior of insert?
					return;
				}
				if (child.intersects(e, REACH)) {
										
					remove(child);					
					((EntityGroup) child).insert(e);
					insert(child);
					
					return;
				}
								
			}
			
			if (child.intersects(e, REACH) && e instanceof EntityGroup) {
				
				//Main.log(child + " fancy intersects " + e);
				
				delete(child);
				((EntityGroup) e).insert(child);
				insert(e);
				return;
			}
			
		}
		
		add(e);
		
	}
		
	public boolean delete(Entity e) {
		
		if (remove(e)) {
			return true;
		}
		
		Iterator<Entity> iter = getChildren();
		while (iter.hasNext()) {
			Entity child = iter.next();
			if (child instanceof EntityGroup && ((EntityGroup) child).delete(e)) {
				if (((EntityGroup) child).isEmpty()) {
					remove(child);
				}
				return true;
			}
		}
		
		return false;
		
	}
	
	private void merge(EntityGroup group) {
		
		Iterator<Entity> iter = group.getChildren();
		
		while (iter.hasNext()) {
			Entity next = iter.next();
			
			if (next instanceof EntityGroup) {
				merge((EntityGroup) next);
			} else {
				insert(next);
			}
			
		}
		
	}
	
	public boolean move(Entity e, Vector trans) {
		
		if (trans.notInPlane()) {
			return false;
		}
		
		delete(e);
		e.pos.add(trans);
		insert(e);
		return true;
		
	}
	
	public boolean teleportTo(Entity e, Vector pos) {
		
		if (pos.notInPlane()) {
			return false;
		}
		
		delete(e);
		e.pos = pos.copy();
		insert(e);
		return true;
		
	}
	
	public Entity getFirstCollision(Entity c) {
		
		for (Entity child : entities) {
			
			if (child instanceof EntityGroup) {
				Entity result = ((EntityGroup) child).getFirstCollision(c);
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
	
	
	//could maybe write this more efficiently
	public ArrayList<Entity> getAllCollisions(Entity c) {
		
		ArrayList<Entity> result = new ArrayList<Entity>();
		
		for (Entity child : entities) {
			
			if (child instanceof EntityGroup) {
				result.addAll(((EntityGroup) child).getAllCollisions(c));
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
		r = e.r;
		if (e instanceof EntityGroup) {
			entities = ((EntityGroup) e).entities;
		}
		
	}
	
	public void recalculate() {
		
		for (Entity e : entities) {
			if (e instanceof EntityGroup) {
				((EntityGroup) e).recalculate();
			}
		}
		
		setEnclosing();
		
	}
	
	@Override
	public void renderAround(FocusedWindow g, Graphics gr) {
		Vector playerPos = g.getFocus().getPos();
		Vector dim = g.getDimensions();
		Rectangle view = new Rectangle(playerPos.getX() - dim.getX() / 2, playerPos.getY() - dim.getY() / 2, dim.getX(), dim.getY());
		synchronized(this) {
			try {
				for (Entity e : entities) {
					if (e.intersects(view)) {
						//the is in renderHitbox (Entity)
						e.renderAround(g, gr);
					}
				}
			} catch (ConcurrentModificationException e) {
				Main.error("concurrent modification detected");
			}
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
	
	public void updateAround(World world, double d, Entity focus) {
		for (Entity e : entities) {
			if (e.intersects(new Entity(focus.pos, UPDATE_RADIUS))) {
				if (e instanceof EntityGroup) {
					((EntityGroup) e).updateAround(world, d, focus);
				} else {
					e.update(world, d);
				}
			}
		}
	}
	
	@Override
	public void update(World world, double d) {
		//TODO: recalculate border only in this outer function?
		for (Entity e : entities) {
			e.update(world, d);
		}
	}
	
	public void saveEntities(File file) throws IOException {
		ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(file));
		for (Entity e : getAllEntities()) {
			out.writeObject(e);
		}
		out.close();
	}
	
	/**
	 * Add all entities stored in a file to this EntityGroup
	 * @param file file to read
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public void loadEntities(File file) throws FileNotFoundException, IOException {
		ObjectInputStream in = new ObjectInputStream(new FileInputStream(file));
		while (true) {
			try {
				add((Entity) in.readObject());
			} catch (Exception e) {
				break;
			}
		}
		in.close();
	}
	
}
