package snorri.world;

import java.awt.Graphics;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import snorri.entities.Collider;
import snorri.entities.Entity;
import snorri.entities.EntityGroup;
import snorri.events.CollisionEvent;
import snorri.main.GameWindow;

public class World {

	private Level level;
	private EntityGroup col;
	private List<Collider> colliders;
	private Queue<Entity> deleteQ;
	
	public World() {
		level = new Level(100, 100); //TODO: pass a level file to read
		col = new EntityGroup();
		colliders = new ArrayList<Collider>();
		deleteQ = new LinkedList<Entity>();
	}
	
	public void update(float f) {
		
		//TODO: update all the entities
		
		for (Collider p : colliders) {
						
			p.update(this, f);
			
			Entity hit = col.getFirstCollision(p);
			if (hit != null) {
				p.onCollision(new CollisionEvent(p, hit, this));
				return;
			}
			
		}
		
		while (! deleteQ.isEmpty()) {
			deleteHard(deleteQ.poll());
		}
		
	}
	
	public void render(GameWindow window, Graphics g) {
		
		//TODO: draw grid
		//TODO: render, not render hitboxes
		
		col.renderAround(window, g);
		
		for (Collider p : colliders) {
			p.renderHitbox(window, g);
		}
				
	}
	
	public EntityGroup getEntityTree() {
		return col;
	}
	
	public Level getLevel() {
		return level;
	}
	
	/**
	 * Add an Entity to the World.
	 * Detects whether Entity is a Collider or otherwise,
	 * and handles it appropriately
	 * 
	 */
	public void add(Entity e) {
		
		if (e instanceof Collider) {
			colliders.add((Collider) e);
			return;
		}
		
		col.insert(e);
		
	}
	
	/**
	 * Use deleteSoft method in update
	 * deleteHard is a bit faster, and can be used in
	 * CollisionEvents and other contexts
	 * @param e the entity to delete
	 */
	public void deleteSoft(Entity e) {
		deleteQ.add(e);
	}
	
	/**
	 * Use deleteSoft method in update
	 * deleteHard is a bit faster, and can be used in
	 * CollisionEvents and other contexts
	 * @param e the entity to delete
	 */
	public boolean deleteHard(Entity e) {
		
		if (e instanceof Collider) {
			return colliders.remove(e);
		}
		
		return col.delete(e);
		
	}
	
}
