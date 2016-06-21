package snorri.world;

import java.awt.Point;
import java.awt.Rectangle;
import java.io.Serializable;

import snorri.main.FocusedWindow;

public class Vector implements Serializable {
	
	private static final long serialVersionUID = 1L;

	public static final Vector ZERO = new Vector(0, 0);
	
	public static final Vector RIGHT = new Vector(1, 0);
	public static final Vector DOWN = new Vector(0, 1);
	public static final Vector DOWN_RIGHT = new Vector(1, 1);
	public static final Vector DOWN_LEFT = new Vector(-1, 1);
	
	public double x, y;
	
	public Vector(double x, double y) {
		this.x = x;
		this.y = y;
	}
	
	public Vector(Point location) {
		this.x = location.getX();
		this.y = location.getY();
	}
	
	public Vector(Rectangle r) {
		this.x = r.getWidth();
		this.y = r.getHeight();
	}
	
	public Vector(Vector v) {
		this.x = v.x;
		this.y = v.y;
	}
	
	public Vector getRelPos(FocusedWindow g) {
		Vector focusPos = g.getFocus().getPos();
		Vector relPos = this.multiply(Tile.WIDTH).sub(focusPos).add(g.getDimensions().divide(2));
		return relPos;
	}

	public int getX() {
		return (int) x;
	}
	
	public int getY() {
		return (int) y;
	}
	
	/*public double getXdouble() {
		return x;
	}
	
	public double getYdouble() {
		return y;
	}*/
	
	//all of this stuff could be cleaned up, maybe
	//these all have side effects and return a pointer to obj
	
	public Vector multiply(double n) {
		x *= n;
		y *= n;
		return this;
	}
	
	public Vector divide(double n) {
		x /= n;
		y /= n;
		return this;
	}
	
	public Vector add(Vector pos) {
		x += pos.x;
		y += pos.y;
		return this;
	}
	
	public Vector sub(Vector pos) {
		x -= pos.x;
		y -= pos.y;
		return this;
	}
	
	public Vector scale(double magnitude) {
		
		if (equals(ZERO)) {
			return ZERO.copy();
		}
		
		return multiply(magnitude / distance(ZERO));
	}
	
	public Vector normalize() {
		return scale(1);
	}
	
	public Vector abs() {
		x = Math.abs(x);
		y = Math.abs(y);
		return this;
	}
	
	//rounds to an int for convenience
	public int distance(Vector pos) {
		return (int) Math.sqrt(distanceSquared(pos));
	}
	
	public double distanceSquared(Vector pos) {
		if (pos == null) {
			return 0;
		}
		return (x - pos.x) * (x - pos.x) + (y - pos.y) * (y - pos.y);
	}
	
	public int magnitude() {
		return distance(ZERO);
	}
	
	public boolean notInPlane() {
		return Double.isNaN(x) || Double.isNaN(y) || Double.isInfinite(x) || Double.isInfinite(y);
	}
	
	public Vector copy() {
		return new Vector(this);
	}
	
	public String toString() {
		return "(x: " + x + ", y: " + y + ")";
	}
	
	/**
	 * we need to define this to take an Object so it works with built-in Java stuff like Collection.contains
	 */
	@Override
	public boolean equals(Object pos) {
		if (pos instanceof Vector) {
			return x == ((Vector) pos).x && y == ((Vector) pos).y;
		}
		return false;
	}

	public Vector add(int i, int j) {
		return add(new Vector(i, j));
	}
	
	public Vector sub(int i, int j) {
		return sub(new Vector(i, j));
	}
	
	public Vector getProjectionX() {
		return new Vector(x, 0);
	}
	
	public Vector getProjectionY() {
		return new Vector(0, y);
	}
	
	public double dot(Vector v) {
		return x * v.x + y * v.y;
	}
	
	public double getAngleBetween(Vector v) {
		return Math.acos(dot(v) / (magnitude() * v.magnitude()));
	}
	
	public Vector getProjection(Vector axis) {
		return axis.copy().scale(dot(axis) / axis.magnitude());
	}
	
	public Vector getPerpendicular() {
		if (y == 0) {
			return ZERO.copy();
		}
		return new Vector(1, -x/y).normalize();
	}
	
	public Vector toGridPos() {
		x = getX() / Tile.WIDTH;
		y = getY() / Tile.WIDTH;
		return this;
	}
	
	public Vector toGlobalPos() {
		x = getX() * Tile.WIDTH;
		y = getY() * Tile.WIDTH;
		return this;
	}
	
	/**
	 * @return a random vector chosen uniformly from [0, x) x [0, y)
	 */
	public Vector random() {
		return new Vector(Math.random() * x, Math.random() * y);
	}
	
	//use Level.getTileGrid(v) != null
	@Deprecated
	public boolean isInBounds(Level level) {
		Vector dim = level.getDimensions();
		return getX() >= 0 && getX() < dim.getX() && getY() >= 0 && getY() < dim.getY();
	}
		
}
