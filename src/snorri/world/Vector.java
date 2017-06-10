package snorri.world;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Point2D;

import java.lang.Math;

import java.util.ArrayList;

import snorri.main.FocusedWindow;
import snorri.main.GamePanel;
import snorri.semantics.Nominal;

public class Vector implements Nominal, Comparable<Vector> {
	
	private static final long serialVersionUID = 1L;

	public static final Vector ZERO = new Vector(0, 0);
	
	public static final Vector RIGHT = new Vector(1, 0);
	public static final Vector DOWN = new Vector(0, 1);
	public static final Vector DOWN_RIGHT = new Vector(1, 1);
	public static final Vector DOWN_LEFT = new Vector(-1, 1);
	
	public double x, y;
	
	/**
	 * no argument constructor used for reading
	 * door positions from YAML
	 */
	public Vector() {
	}
	
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
	
	public Vector (Dimension d) {
		this(d.width, d.height);
	}
	
	public Vector(GamePanel panel) {
		this(panel.getSize());
	}
	
	/**
	 * Note: this method is intended for
	 * vectors in grid coordinates
	 */
	public Vector getRelPosGrid(FocusedWindow<?> g) {
		return copy().multiply(Tile.WIDTH).getRelPos(g);
	}
	
	public Vector getRelPos(FocusedWindow<?> g) {
		Vector focusPos = g.getFocus().getPos();
		return copy().sub(focusPos).add(g.getDimensions().divide(2));
	}

	public int getX() {
		return (int) x;
	}
	
	public int getY() {
		return (int) y;
	}
	
	public int getXGrid() {
		return (int) x / Tile.WIDTH;
	}
	
	public int getYGrid() {
		return (int) y / Tile.WIDTH;
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
	
	@Override
	public String toString() {
		return "(x: " + x + ", y: " + y + ")";
	}
	
	public String toIntString() {
		return "(x: " + getX() + ", y: " + getY() + ")";
	}
	
	/**
	 * @param pos The position to compare against
	 * @return Whether or not two positions are equal
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
	
	public Vector toGridPosRounded() {
		x = Math.round(x / Tile.WIDTH);
		y = Math.round(y / Tile.WIDTH);
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

	/**
	 * use this to sort by magnitude
	 */
	@Override
	public int compareTo(Vector o) {
		return Double.compare(magnitude(), o.magnitude());
	}
	
	/**
	 * Hash function taken from {@link http://stackoverflow.com/questions/5928725/hashing-2d-3d-and-nd-vectors}.
	 * 100030001 and 100050001 are arbitrary primes. For more palindromic primes, see {@link https://www.rsok.com/~jrm/9_digit_palindromic_primes.html}.
	 * @return Hash code
	 */
	@Override
	public int hashCode() {
		return getX() * 100030001 ^ getY() * 100050001;
//		return Level.MAX_SIZE * getY() + getX();
	}
	
	public static int hashVectorPair(Vector v1, Vector v2) {
		return v1.getX() * 100030001 ^ v1.getY() * 100050001 ^ v2.getX() * 100060001 ^ v2.getY() * 100111001;
	}
	
	//TODO maybe should have just used these instead of vectors lol
	
	public Point2D getPoint() {
		return new Point(getX(), getY());
	}
	
	public Vector incr() {
		if (x != 0) {
			x += 1;
		}
		if (y != 0) {
			y += 1;
		}
		return this;
	}

	public Vector invert() {
		double temp = y;
		y = x;
		x = temp;
		return this;
	}
	
	/**
	 * Unlike other vector operations, <code>getInverted()</code> does not have any side effect.
	 * @see <code>Vector.invert()</code>
	 */
	public Vector getInverted() {
		return new Vector(y, x);
	}
	
	/**
	 * @see <code>getInverted()</code>
	 */
	public Vector getXReflected(Vector dim) {
		return new Vector(dim.getX() - 1 - x, y);
	}
	
	public ArrayList<Vector> getSquareAround(int r) {
		
		ArrayList<Vector> out = new ArrayList<>();
		
		if (r == 0) {
			out.add(copy());
			return out;
		}
		
		//sides
		for (int i = -r + 1; i < r; i++) {
			out.add(copy().add(-r, i));
			out.add(copy().add(r, i));
			out.add(copy().add(i, -r));
			out.add(copy().add(i, r));
		}
		
		//corners
		out.add(copy().add(r, r));
		out.add(copy().add(r, -r));
		out.add(copy().add(-r, -r));
		out.add(copy().add(-r, r));
		
		return out;
		
	}

	public boolean isToCloseTo(int x2, int y2) {
		//if (Math.abs(getX() - x2) < 3 && Math.abs(getY() - y2) < 3) {
		//	return true;
		//}
		return false;
	}

	public boolean isNormalTo(int x2, int y2) {
		return (getX() == x2 || getY() == y2);
	}
		
}
