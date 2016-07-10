package snorri.collisions;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.io.Serializable;

import snorri.entities.Entity;
import snorri.main.FocusedWindow;
import snorri.world.Vector;

public abstract class Collider implements Serializable {

	private static final long serialVersionUID = 1L;
	protected Vector pos;
	
	protected Collider(Vector pos) {
		this.pos = pos;
	}
	
	public Vector getPos() {
		return pos;
	}
	
	/**
	 * @return whether this collider intersects a point given by pos
	 */
	public abstract boolean intersects(Vector pos);
	
	/**
	 * @return whether this collider is intersecting another one
	 */
	public boolean intersects(Collider other) {
		if (other instanceof CircleCollider) {
			return intersects((CircleCollider) other);
		}
		if (other instanceof RectCollider) {
			return intersects((RectCollider) other);
		}
		return false;
	}
		
	/**
	 * @return whether other is contained in this collider
	 */
	public boolean contains(Collider other) {
		if (other instanceof CircleCollider) {
			return contains((CircleCollider) other);
		}
		if (other instanceof RectCollider) {
			return contains((RectCollider) other);
		}
		return false;
	}
	
	public abstract boolean intersects(Rectangle rect);
	
	protected abstract boolean intersects(CircleCollider other);
	
	protected abstract boolean intersects(RectCollider other);
	
	protected abstract boolean contains(CircleCollider other);
	
	protected abstract boolean contains(RectCollider other);
	
	public abstract void render(FocusedWindow g, Graphics gr);
	
	public abstract Collider cloneOnto(Entity root);
	
	public abstract int getMaxWidth();
	
}
