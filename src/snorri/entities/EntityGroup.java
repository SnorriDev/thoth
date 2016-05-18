package snorri.entities;

import java.awt.Graphics;
import java.util.ArrayList;
import java.util.Iterator;

import snorri.main.GameWindow;
import snorri.main.Main;
import snorri.world.Position;

public class EntityGroup extends Entity {
	
	private static final int REACH = 0;
	//TODO: adjust this based on density/radius of higher groups

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
				((EntityGroup) r1).merge((EntityGroup) r2);
				set(r1);
				return;
			} else if (r1 instanceof EntityGroup) {
				((EntityGroup) r1).add(r2);
				set(r1);
				return;
			} else if (r2 instanceof EntityGroup) {
				set(r2);
				return;
			}
		}
		
		entities.add(r2);
		
		//compute axis vector between the two centers
		Position axis = r2.pos.copy();
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
	
	public EntityGroup(Position center, int rad) {
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
		
		Position p1 = e1.pos;
		Position p2 = e2.pos;
		Position p3 = e3.pos;
		
		int a = p2.x - p1.x;
		int b = p2.y - p1.y;
		int c = p3.y - p1.y;
		int d = p3.y - p1.y;
		double e = a * (p2.x + p1.x) * 0.5 + b * (p2.y + p1.y) * 0.5;
		double f = c * (p3.x + p1.x) * 0.5 + d * (p3.y + p1.y) * 0.5;
		int det = a * d - b * c;
		
		pos = new Position((int) ((d * e - b * f) / det), (int) ((-c * e + a * f) / det));
		r = p1.distance(pos) + Integer.max(e3.r, Integer.max(e1.r, e2.r));
		
	}
	private Iterator<Entity> getChildren() {
		return entities.iterator();
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
		
		pos = enclosing.pos.copy();
		r = enclosing.r;
		
		if (entities.size() == 1 && equals(entities.get(0))) {
			set(entities.get(0));
		}
		
		//iterate through children and do this?
		
	}
	
	//linear time algorithm for getting enclosing entity
	private static EntityGroup getEnclosing(Entity[] points, int n, Entity[] boundary, int b) {
		
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
				
		if (! circle.intersects(points[n - 1])) {
			
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
		
		if (!intersects(e, REACH)) {
			
//			EntityGroup group = new EntityGroup(this);
//			group.add(e);
//			set(group);
			
			EntityGroup group = new EntityGroup();
			group.set(this);
			set(new EntityGroup(group, e));
			return;
		}
								
		Iterator<Entity> iter = getChildren();
		while (iter.hasNext()) {
			Entity child = iter.next();
			if (child instanceof EntityGroup) {
				
				if (child.contains(e, REACH)) {
					remove(child);
					((EntityGroup) child).insert(e);
					add(child);
					return;
				}
				if (child.intersects(e, REACH)) {
					remove(child);
					merge((EntityGroup) child);
					insert(e);	
					return;
				}
								
			}
			
			if (child.intersects(e, REACH) && e instanceof EntityGroup) {
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
			insert(iter.next());
			//nice version
		}
		
	}
	
	public void move(Entity e, Position trans) {
		
		delete(e);
		
		e.pos.add(trans);
		
		Main.log(e);	
		traverse();
		
		insert(e);
		
		traverse();
				
	}
	
	//TODO
	//update method
	//collides method
	
	private void set(Entity e) {
		
		pos = e.pos;
		r = e.r;
		if (e instanceof EntityGroup) {
			entities = ((EntityGroup) e).entities;
		}
		
	}
	
	@Override
	public void renderHitbox(GameWindow g, Graphics gr) {
		super.renderHitbox(g, gr);
		for (Entity e : entities) {
			e.renderHitbox(g, gr);
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
	
}