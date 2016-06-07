package snorri.entities;

import java.awt.Graphics;
import java.io.Serializable;

import snorri.inventory.Timer;
import snorri.main.FocusedWindow;
import snorri.main.Main;
import snorri.semantics.Nominal;
import snorri.world.Vector;
import snorri.world.World;

public class Entity implements Nominal, Serializable {

	private static final long serialVersionUID = 1L;
	protected Vector pos;
	protected int r;
	
	private Timer burnTimer = new Timer(5);
	
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
	
	public void burn() {
		burnTimer.hardReset();
	}
	
	public boolean isBurning() {
		return ! burnTimer.isOffCooldown();
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
		burnTimer.update(f);
	}
	
	public void renderHitbox(FocusedWindow g, Graphics gr) {
		
		if (pos == null) {
			return;
		}
		
		Vector rel = pos.copy();
		rel.sub(g.getFocus().pos);
		gr.drawOval(rel.getX() - r + g.getBounds().width / 2, rel.getY() - r + g.getBounds().height / 2, 2 * r, 2 * r);
	}
	
	//returns true if the two entities are spatially equivalent
	public boolean equals(Entity e) {
		return e.pos.equals(pos) && e.r == r;
	}
	
	public void renderAround(FocusedWindow g, Graphics gr) {
		renderHitbox(g, gr);
	}

	@Override
	public Object get(World world, AbstractSemantics attr) {
		
		if (attr == AbstractSemantics.POSITION) {
			return pos;
		}
		if (attr == AbstractSemantics.TILE) {
			return world.getLevel().getTile(pos);
		}
		if (attr == AbstractSemantics.NAME) {
			return toString();
		}
		
		return null;
	}

}
