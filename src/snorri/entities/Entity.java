package snorri.entities;

import java.awt.Graphics;

import snorri.world.Position;

public class Entity {
	
	protected Position pos;
	protected int r;
	
	public Entity(Entity e) {
		this.pos = e.pos.copy();
		this.r = e.r;
	}
	
	public Entity(Position pos, int r) {
		this.pos = pos;
		this.r = r;
	}
	
	public Entity(Position pos) {
		this(pos, 5);
	}

	public Position getPos() {
		return pos;
	}
	
	public int getRadius() {
		return r;
	}

	public boolean intersects(Position pos1) {
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
		System.out.println(indent + pos.toString());
		System.out.println(indent + "r: " + r);
	}
	
	public void renderHitbox(Graphics g, Entity focus) {
		renderAround(g, focus);
	}
	
	//returns true if the two entities are spatially equal
	public boolean equals(Entity e) {
		return e.pos.equals(pos) && e.r == r;
	}
	
	public void renderAround(Graphics g, Entity e) {
		Position rel = pos.copy();
		rel.sub(e.pos);
		g.drawOval(rel.x - r, rel.y - r, 2 * r, 2 * r);
	}
	
	public void traverse() {
		this.traverse(0);
	}
	
	public void walk(Position direction, EntityGroup world) {
		Position dir = direction.copy();
		
		if (dir.equals(Position.ZERO)) {
			return;
		}
		
		dir.multiply(getSpeed());
		world.move(this, dir);
	}
	
	//override this for faster entities
	protected int getSpeed() {
		return 1;
	}

}
