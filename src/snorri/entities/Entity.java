package snorri.entities;

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

	public Position getPos() {
		return pos;
	}
	
	public int getRadius() {
		return r;
	}

	public boolean intersects(Position pos1) {
		return pos.distance(pos1) < r;
	}
	
	public boolean intersects(Entity e) {
		return e.pos.distance(pos) < r + e.r;
	}
	
	protected void traverse(int depth) {
		String indent = "";
		for (int i = 0; i < depth; i++) {
			indent += "  ";
		}
		System.out.println(indent + pos.toString());
		System.out.println(indent + "r: " + r);
	}
	
	public void traverse() {
		this.traverse(0);
	}

}
