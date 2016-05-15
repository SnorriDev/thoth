package snorri.entities;

import java.util.ArrayList;

import snorri.world.Position;

public class EntityGroup extends Entity {

	ArrayList<Entity> entities;
	
	public EntityGroup() {
		//arbitrary
		super(new Position(500, 500), 500);
		entities = new ArrayList<Entity>();
	}
	
	public EntityGroup(Entity root) {
		super(root);
		entities = new ArrayList<Entity>();
		entities.add(root);
	}
		
	//blind-add an entity to the group
	//adjust average
	private void add(Entity n) {
		
		Position oldPos = pos.copy();
		
		//set the new middle
		pos.multiply(entities.size());
		pos.add(n.pos);
		pos.divide(entities.size() + 1);
		
		//upper bound on the new radius
		r = Math.max(pos.distance(oldPos) + r, n.pos.distance(oldPos) + n.r);
		
		entities.add(n);
	}
	
	//blind-remove an entity from group
	//adjust average
	private void remove(Entity n) {
				
		//set the new middle
		pos.multiply(entities.size());
		pos.sub(n.pos);
		pos.divide(entities.size() + 1);
		
		//TODO: maybe adjust radius?
		
		entities.remove(n);
	}
	
	//recursively insert 
	public void insert(Entity n) {
		
		for (Entity e : entities) {
						
			if (! e.intersects(n)) {
				continue;
			}
			
			if (e instanceof EntityGroup) {
				((EntityGroup) e).insert(n);
			} else {
				EntityGroup group = new EntityGroup(e);
				group.add(n);
				entities.remove(e);
				entities.add(group);
			}
			
			return;
			
		}
		
		this.add(n);
		
	}
		
	//recursively look to delete
	public Entity delete(Entity n) {
		
		for (int i = 0; i < entities.size(); i++) {
			
			Entity e = entities.get(i);
			
			if (e == n) {
				this.remove(e);
				return (entities.size() > 1 ? null : entities.get(0));
			}
			
			if (e instanceof EntityGroup) {
				Entity result = ((EntityGroup) e).delete(n);
				if (result != null) {
					remove(e);
					entities.add(result);
				}
				return (entities.size() > 1 ? null : entities.get(0));
			}
			
		}
		
		return null;
		
	}
	
	@Override
	protected void traverse(int depth) {
		super.traverse(depth);
		for (Entity e : entities) {
			e.traverse(depth + 1);
		}
	}
	
}
