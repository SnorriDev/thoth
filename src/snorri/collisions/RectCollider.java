package snorri.collisions;

import java.awt.Graphics;
import java.awt.Rectangle;

import snorri.entities.Entity;
import snorri.main.FocusedWindow;
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
	public RectCollider(Vector pos, Vector dim) {
		super(pos);
		this.dim = dim;
	}
	
	public int getWidth() {
		return dim.getX() / 2;
	}
	
	public int getHeight() {
		return dim.getY() / 2;
	}
	
	public Vector getTopLeft() {
		return pos.copy().sub(dim.copy().divide(2));
	}
	
	public Vector getBottomRight() {
		return pos.copy().add(dim.copy().divide(2));
	}
	
	public Vector getTopRight() {
		return pos.copy().add(-dim.getX() / 2, dim.getY() / 2);
	}
	
	public Vector getBottomLeft() {
		return pos.copy().add(dim.getX() / 2, -dim.getY() / 2);
	}
	
	public Rectangle getRect() {
		Vector top = getTopLeft();
		return new Rectangle(top.getX(), top.getY(), dim.getX(), dim.getY());
	}

	@Override
	protected boolean intersects(CircleCollider other) {
		if (other == null) {
			return false;
		}
		return other.intersects(this);
	}

	@Override
	protected boolean intersects(RectCollider other) {
		return getRect().intersects(other.getRect());
	}

	//TODO: we can potentially combine these contains methods so that they don't differentiate
	
	@Override
	protected boolean contains(CircleCollider other) {
		return getRect().contains(other.getBoundingRect());
	}

	@Override
	protected boolean contains(RectCollider other) {
		return getRect().contains(other.getRect());
	}

	@Override
	public boolean intersects(Vector pos) {
		Vector lower = getTopLeft();
		Vector upper = getBottomRight();
		return lower.getX() <= pos.getX() && pos.getX() <= upper.getX() &&
				lower.getY() <= pos.getY() && pos.getY() <= upper.getY();
		
	}

	@Override
	public void render(FocusedWindow g, Graphics gr) {
		//TODO: turn all gr into Graphics2D objects?
		if (pos == null || g.getFocus().getPos() == null) {
			return;
		}
		Vector rel = pos.copy().sub(g.getFocus().getPos());
		Rectangle rect = getRect();
		gr.drawRect(rel.getX() + g.getCenter().getX() - rect.width / 2, rel.getY() + g.getCenter().getY() - rect.height / 2, rect.width, rect.height);
	}

	@Override
	public Collider cloneOnto(Entity root) {
		return new RectCollider(root.getPos(), dim.copy());
	}

	@Override
	public boolean intersects(Rectangle rect) {
		return getRect().intersects(rect);
	}

	@Override
	public int getMaxRadius() {
		return (int) Math.sqrt(getWidth() * getWidth() + getHeight() * getHeight());
	}
	
	@Override
	public String toString() {
		return dim.getY() + "x" + dim.getX();
	}

}
