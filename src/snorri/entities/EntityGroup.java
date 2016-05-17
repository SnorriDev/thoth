package snorri.entities;

import java.awt.Graphics;
import java.util.ArrayList;
import java.util.Iterator;

import snorri.main.GameWindow;
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
	public EntityGroup(Entity root, Entity r2) {
		this(root);
		add(r2);
	}
	
	public EntityGroup() {
		super(null, 0);
		entities = new ArrayList<Entity>();
	}
	
	private Iterator<Entity> getChildren() {
		return entities.iterator();
	}
	
	private void merge(EntityGroup group) {
		
		for (Entity e : group.entities) {
			if (e instanceof EntityGroup) {
				merge((EntityGroup) e);
			} else {
				add(e);
			}
		}
		
	}
	
	//blind-add an entity to the group
	//adjust average
	private void add(Entity n) {
		
		Position oldPos = pos.copy();
		
		if (! contains(n)) {
		
			//TODO: don't use average, just min/max
			
			//set the new middle
			pos.multiply(entities.size());
			pos.add(n.pos);
			pos.divide(entities.size() + 1);
		
		}
		
		//upper bound on the new radius
		r = Math.max(pos.distance(oldPos) + r, n.pos.distance(oldPos) + n.r);
		
			
		entities.add(n);
	}
	
	//blind-remove an entity from group
	//adjust average and return whether or not it is empty
	private boolean remove(Entity n) {
			
		return entities.remove(n);
		
		//don't readjust size at all
		
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
			
			EntityGroup group = new EntityGroup();
			group.set(this);
			set(new EntityGroup(group, e));
			return;
		}
		
		//TODO: maybe rewrite more elegantly in terms of merge?
//		if (e instanceof EntityGroup) {
//			merge((EntityGroup) e);
//			return;
//		}
								
		Iterator<Entity> iter = getChildren();
		while (iter.hasNext()) {
			Entity child = iter.next();
			if (child instanceof EntityGroup) {
				
				if (child.contains(e, REACH)) {
					remove(child);
					((EntityGroup) child).insert(e);
					add(child);
					return;
				} else if (child.intersects(e, REACH)) {
					remove(child);
					merge((EntityGroup) child);
					add(e);
					return;
				}
								
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
				//TODO: change this stuff?
				return true;
			}
		}
		
		return false;
		
	}
	
	public void move(Entity e, Position trans) {
		delete(e);
		e.pos.add(trans);
		insert(e);
	}
	
	//TODO
	//update method
	//collides method
	
	private void set(EntityGroup e) {
		
		pos = e.pos;
		r = e.r;
		entities = e.entities;
		
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
		for (Entity e : entities) {
			e.traverse(depth + 1);
		}
	}
	
}
