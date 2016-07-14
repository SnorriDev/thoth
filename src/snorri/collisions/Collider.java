package snorri.collisions;

import java.awt.Graphics;
import java.io.Serializable;

import java.awt.geom.Area;
import java.awt.Shape;

import snorri.entities.Entity;
import snorri.main.FocusedWindow;
import snorri.world.Vector;

public abstract class Collider implements Serializable {
	
	private static final long serialVersionUID = 1L;
	protected Vector pos;
	
	protected Collider(Vector pos) {
		this.pos = pos;
	}
	
	public abstract Shape getShape();
	
	public Vector getPos() {
		return pos;
	}
		
	/**
	 * @return whether other is contained in this collider
	 */
	public final boolean contains(Collider other) {
		return contains(other.getShape());
	}
	
	public final boolean contains(Shape other) {
		if (pos == null) {
			return true;
		}
		Area a = new Area(other);
		a.subtract(new Area(getShape()));
		return a.isEmpty();
	}
		
	/**
	 * @return whether this collider intersects a point given by pos
	 */
	public final boolean intersects(Vector pos) {
		if (this.pos == null) {
			return true;
		}
		return getShape().contains(pos.getPoint());
	}
	
	public final boolean intersects(Shape other) {
		if (pos == null) {
			return true;
		}
		Area a = new Area(getShape());
		a.intersect(new Area(other));
		return !a.isEmpty();
	}
	
	public final boolean intersects(Collider other) {
		return intersects(other.getShape());
	}
	
	public abstract void render(FocusedWindow g, Graphics gr);
	
	public abstract Collider cloneOnto(Entity root);
	
	public abstract int getMaxRadius();
	
	public CircleCollider getInscribing() {
		int r = getMaxRadius();
		return new CircleCollider(pos, r);
	}
	
	public abstract Collider copy();
	
	@Override
	public boolean equals(Object other) {
		if (other instanceof Collider) {
			Area a1 = new Area(getShape());
			Area a2 = new Area(((Collider) other).getShape());
			return a1.equals(a2);
		}
		return false;
	}
	
}
