package snorri.world;

import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;

import snorri.entities.Collider;
import snorri.entities.CollisionEvent;
import snorri.entities.Entity;
import snorri.entities.EntityGroup;
import snorri.main.GameWindow;

public class World {

	private Level level;
	private EntityGroup col;
	private List<Collider> colliders;
	
	public World() {
		level = new Level(100, 100); //TODO: pass a level file to read
		col = new EntityGroup();
		colliders = new ArrayList<Collider>();
	}
	
	public void update() {
		
		//TODO: update all the entities
		
		for (Collider p : colliders) {
			p.update();
			
			Entity hit = col.getFirstCollision(p);
			if (hit != null) {
				p.onCollision(new CollisionEvent(p, hit, this));
				return;
			}
			
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
	
	public boolean delete(Entity e) {
		
		if (e instanceof Collider) {
			return colliders.remove(e);
		}
		
		return col.delete(e);
		
	}
	
}
