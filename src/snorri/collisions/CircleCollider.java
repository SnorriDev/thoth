package snorri.collisions;

import java.awt.Graphics;
import java.awt.Rectangle;

import snorri.entities.Entity;
import snorri.main.FocusedWindow;
import snorri.world.Vector;

public class CircleCollider extends Collider {

	private static final long serialVersionUID = 1L;
	private int r;

	public CircleCollider(Vector pos, int r) {
		super(pos);
		this.r = r;
	}
	
	public void setRadius(int r) {
		this.r = r;
	}
	
	public void increaseRadius(int incr) {
		r += incr;
	}
	
	public int getRadius() {
		return r;
	}
	
	public Rectangle getBoundingRect() {
		return new Rectangle(pos.getX() - r, pos.getY() - r, 2 * r, 2 * r);
	}

	@Override
	protected boolean intersects(CircleCollider other) {
		return other.pos.distance(pos) <= r + other.r;
	}

	@Override
	protected boolean intersects(RectCollider other) {
		return intersects(other.getRect());
	}

	/**
	 * returns true if either collider is infinite/unbounded
	 */
	@Override
	protected boolean contains(CircleCollider other) {
		if (pos == null) {
			return true;
		}
		if (other.pos == null) {
			return false;
		}
		return other.pos.distance(pos) + other.r <= r;
	}

	@Override
	protected boolean contains(RectCollider other) {
		if (other == null) {
			return false;
		}
		return intersects(other.getTopLeft()) && intersects(other.getBottomRight()) &&
				intersects(other.getBottomLeft()) && intersects(other.getTopRight());
	}

	@Override
	public boolean intersects(Vector pos) {
		if (this.pos == null) {
			return true;
		}
		return this.pos.distance(pos) <= r;
	}

	@Override
	public boolean intersects(Rectangle rect) {

		if (pos == null) {
			return true;
		}

		Vector circleDistance = new Vector(rect.getX(), rect.getY()).add(new Vector(rect).divide(2)).sub(pos).abs();

		if (circleDistance.getX() > rect.getWidth() / 2 + r) {
			return false;
		}

		if (circleDistance.getY() > rect.getHeight() / 2 + r) {
			return false;
		}

		if (circleDistance.getX() <= rect.getWidth() / 2) {
			return true;
		}

		if (circleDistance.getY() <= rect.getHeight() / 2) {
			return true;
		}

		double cornerDistance = new Vector(rect).divide(2).distance(circleDistance);
		return cornerDistance <= r;

	}

	@Override
	public void render(FocusedWindow g, Graphics gr) {
		if (pos == null || g.getFocus().getPos() == null) {
			return;
		}
		Vector rel = pos.copy().sub(g.getFocus().getPos());
		gr.drawOval(rel.getX() - r + g.getBounds().width / 2, rel.getY() - r + g.getBounds().height / 2, 2 * r, 2 * r);
	}

	@Override
	public Collider cloneOnto(Entity root) {
		return new CircleCollider(root.getPos(), getRadius());
	}

	@Override
	public int getMaxRadius() {
		return r;
	}
	
	@Override
	public String toString() {
		return "r" + r;
	}

}
