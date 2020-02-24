package snorri.collisions;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.geom.Ellipse2D;

import snorri.entities.Entity;
import snorri.windows.FocusedWindow;
import snorri.world.Vector;

public class CircleCollider extends Collider {

	private static final long serialVersionUID = 1L;
	private int r;

	public CircleCollider(int r) {
		this.r = r;
	}
	
	public CircleCollider(Entity e, int r) {
		this(r);
		setFocus(e);
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
		Vector pos = getPos();
		return new Rectangle(pos.getX() - r, pos.getY() - r, 2 * r, 2 * r);
	}
	
	@Override
	public Ellipse2D getShape() {
		Vector pos = getPos();
		return new Ellipse2D.Double(pos.x - r, pos.y - r, 2 * r, 2 * r);
	}

	@Override
	public void render(FocusedWindow<?> g, Graphics gr) {
		Vector pos = getPos();
		if (pos == null || g.getFocus().getPos() == null) {
			return;
		}
		Vector rel = pos.copy().sub_(g.getFocus().getPos());
		gr.setColor(BORDER_COLOR);
		gr.drawOval(rel.getX() - r + g.getBounds().width / 2, rel.getY() - r + g.getBounds().height / 2, 2 * r, 2 * r);
		gr.setColor(Color.BLACK);
	}

	@Override
	public Collider cloneOnto(Entity root) {
		return new CircleCollider(root, getRadius());
	}

	@Override
	public int getMaxRadius() {
		return r;
	}
	
	@Override
	public String toString() {
		return "r" + r;
	}
	
	@Override
	public CircleCollider copy() {
		return new CircleCollider(focus, r);
	}

	@Override
	public int getRadiusX() {
		return r;
	}

	@Override
	public int getRadiusY() {
		return r;
	}

}
