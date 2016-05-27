package snorri.entities;

import java.awt.Graphics;

import snorri.main.GameWindow;
import snorri.main.Main;
import snorri.semantics.Nominal;
import snorri.world.Vector;
import snorri.world.World;

public class Entity implements Nominal {
		
	protected Vector pos;
	protected int r;
	
	public Entity(Entity e) {
		this.pos = e.pos.copy();
		this.r = e.r;
	}
	
	public Entity(Vector pos, int r) {
		this.pos = pos;
		this.r = r;
	}
	
	public Entity(Vector pos) {
		this(pos, 2);
	}

	public Vector getPos() {
		return pos;
	}
	
	public int getRadius() {
		return r;
	}

	public boolean intersects(Vector pos1) {
		return pos.distance(pos1) <= r;
	}
	
	public boolean intersects(Entity e) {
		return e.pos.distance(pos) <= r + e.r;
	}
	
	public boolean intersects(Entity e, int rad) {
		return e.pos.distance(pos) <= r + e.r + rad;
	}
	
	public boolean contains(Entity e) {
		return e.pos.distance(pos) + e.r <= r;
	}
	
	public boolean contains(Entity e, int rad) {
		return e.pos.distance(pos) + e.r <= r + rad;
	}
	
	protected void traverse(int depth) {
		String indent = "";
		for (int i = 0; i < depth; i++) {
			indent += "  ";
		}
		Main.log(indent + this.toString());
	}
	
	public void traverse() {
		traverse(0);
	}
	
	public String toString() {
		return this.getClass().getSimpleName() + "{pos: " + pos.toString() + ", r: " + r + "}";
	}
	
	//TODO: make this into a boolean so we can know whether or not to recalculate collision bubbles
	public void update(World world, float f) {
	}
	
	public void renderHitbox(GameWindow g, Graphics gr) {
		Vector rel = pos.copy();
		rel.sub(g.getFocus().pos);
		gr.drawOval(rel.getX() - r + g.getBounds().width / 2, rel.getY() - r + g.getBounds().height / 2, 2 * r, 2 * r);
	}
	
	//returns true if the two entities are spatially equal
	public boolean equals(Entity e) {
		return e.pos.equals(pos) && e.r == r;
	}
	
	public void renderAround(GameWindow g, Graphics gr) {
		renderHitbox(g, gr);
	}

	public Object get(World world, AbstractSemantics attr) {
		
		if (attr == AbstractSemantics.POSITION) {
			return pos;
		}
		if (attr == AbstractSemantics.TILE) {
			return world.getLevel().getTile(pos);
		}
		
		return null;
	}

}
