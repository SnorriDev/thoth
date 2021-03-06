package snorri.collisions;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;

import snorri.entities.Entity;
import snorri.windows.FocusedWindow;
import snorri.world.Vector;

public class RectCollider extends Collider {

	private static final long serialVersionUID = 1L;
	private Vector dim;
	
	/**
	 * @param pos
	 * 	the center of the rectangle
	 * @param dim
	 * 	the dimensions of the rectangle
	 */
	public RectCollider(Vector dim) {
		this.dim = dim;
	}
	
	public RectCollider(int width, int height) {
		this(new Vector(width, height));
	}
	
	public RectCollider(Entity e, Vector dim) {
		this(dim);
		setFocus(e);
	}
	
	public Vector getDimensions() {
		return dim;
	}
	
	@Override
	public int getRadiusX() {
		return dim.getX() / 2;
	}
	
	@Override
	public int getRadiusY() {
		return dim.getY() / 2;
	}
	
	public Vector getTopLeft() {
		return getPos().copy().sub_(dim.copy().divide_(2));
	}
	
	public Vector getBottomRight() {
		return getPos().copy().add_(dim.copy().divide_(2));
	}
	
	public Vector getTopRight() {
		return getPos().copy().add_(-dim.getX() / 2, dim.getY() / 2);
	}
	
	public Vector getBottomLeft() {
		return getPos().copy().add_(dim.getX() / 2, -dim.getY() / 2);
	}
	
	@Override
	public Rectangle getShape() {
		Vector pos = getPos();
		return new Rectangle(pos.getX() - dim.getX() / 2, pos.getY() - dim.getY() / 2, dim.getX(), dim.getY());
	}

	@Override
	public void render(FocusedWindow<?> g, Graphics gr) {
		Vector pos = getPos();
		if (pos == null || g.getFocus().getPos() == null) {
			return;
		}
		Vector rel = pos.copy().sub_(g.getFocus().getPos());
		Rectangle rect = getShape();
		int x = (int) (rel.getX() + g.getCenter().getX() - rect.getWidth() / 2);
		int y = (int) (rel.getY() + g.getCenter().getY() - rect.getHeight() / 2);
		gr.setColor(BORDER_COLOR);
		gr.drawRect(x, y, (int) rect.getWidth(), (int) rect.getHeight());
		gr.setColor(Color.BLACK);
	}

	@Override
	public Collider cloneOnto(Entity root) {
		return new RectCollider(root, dim.copy());
	}
	
	@Override
	public int getMaxRadius() {
		return (int) (Math.sqrt(getRadiusX() * getRadiusX() + getRadiusY() * getRadiusY()) + 1);
	}
	
	@Override
	public String toString() {
		return dim.getY() + "x" + dim.getX() + "(r" + getMaxRadius() + ")";
	}
	
	@Override
	public RectCollider copy() {
		return new RectCollider(focus, dim.copy());
	}

}
