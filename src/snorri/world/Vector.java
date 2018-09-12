package snorri.world;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Point2D;

import java.lang.Math;

import snorri.main.FocusedWindow;
import snorri.main.GamePanel;
import snorri.semantics.Nominal;

public class Vector implements Nominal, Comparable<Vector> {
	
	private static final long serialVersionUID = 1L;

	public static final Vector ZERO = new Vector(0, 0);
	public static final Vector ORIENTATION = new Vector(1, 0);
	
	public static final Vector RIGHT = new Vector(1, 0);
	public static final Vector DOWN = new Vector(0, 1);
	public static final Vector DOWN_RIGHT = new Vector(1, 1);
	public static final Vector DOWN_LEFT = new Vector(-1, 1);
	
	public double x, y;
	
	public Vector(double x, double y) {
		this.x = x;
		this.y = y;
	}
	
	public Vector(Point point) {
		this.x = point.getX();
		this.y = point.getY();
	}
	
	public Vector(Rectangle rect) {
		this.x = rect.getWidth();
		this.y = rect.getHeight();
	}
	
	public Vector(Vector v) {
		this.x = v.x;
		this.y = v.y;
	}
	
	public Vector(Dimension d) {
		this(d.width, d.height);
	}
	
	public Vector(GamePanel panel) {
		this(panel.getSize());
	}
	
	/** Copy a non-null vector, and return null otherwise. */
	public static Vector nullsafeCopy(Vector vector) {
		return vector == null ? null : vector.copy();
	}
	
	/** Assuming the vector represents a position in absolute grid coordinates, return a translated vector in relative grid coordinates. */
	public Vector getRelPosGrid(FocusedWindow<?> g) {
		return copy().multiply_(Tile.WIDTH).getRelPos(g);
	}
	
	public Vector getRelPos(FocusedWindow<?> g) {
		Vector focusPos = g.getCenterObject().getPos(); //returns whatever is being used to draw the center
		return copy().sub_(focusPos).add_(g.getDimensions().divide_(2));
	}

	public int getX() {
		return (int) x;
	}
	
	public int getY() {
		return (int) y;
	}
	
	public Vector multiply(double n) {
		return copy().multiply_(n);
	}
	
	public Vector divide(double n) {
		return copy().divide_(n);
	}
	
	public Vector add(Vector pos) {
		return copy().add_(pos);
	}
	
	public Vector sub(Vector pos) {
		return copy().sub_(pos);
	}
	
	public Vector multiply_(double n) {
		x *= n;
		y *= n;
		return this;
	}
	
	public Vector divide_(double n) {
		x /= n;
		y /= n;
		return this;
	}
	
	public Vector add_(Vector pos) {
		x += pos.x;
		y += pos.y;
		return this;
	}
	
	public Vector sub_(Vector pos) {
		x -= pos.x;
		y -= pos.y;
		return this;
	}
	
	public Vector scale_(double magnitude) {
		if (equals(ZERO)) {
			return ZERO.copy();
		}
		return multiply_(magnitude / distance(ZERO));
	}
	
	public Vector normalize_() {
		return scale_(1);
	}
	
	public Vector abs_() {
		x = Math.abs(x);
		y = Math.abs(y);
		return this;
	}
	
	public double distance(Vector pos) {
		return Math.sqrt(distanceSquared(pos));
	}
	
	public double distanceSquared(Vector pos) {
		if (pos == null) {
			return 0;
		}
		return (x - pos.x) * (x - pos.x) + (y - pos.y) * (y - pos.y);
	}
	
	public double magnitude() {
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

	public Vector add_(int i, int j) {
		return add_(new Vector(i, j));
	}
	
	public Vector sub_(int i, int j) {
		return sub_(new Vector(i, j));
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
	
	public double getAngleTo(Vector v) {
		return Math.acos(dot(v) / (magnitude() * v.magnitude()));
	}
	
	public Vector getProjection(Vector axis) {
		return axis.copy().scale_(dot(axis) / axis.magnitude());
	}
	
	public Vector getPerpendicular() {
		if (y == 0) {
			return ZERO.copy();
		}
		return new Vector(1, -x/y).normalize_();
	}
	
	public Vector gridPos() {
		return copy().gridPos_();
	}
	
	public Vector gridPosRounded() {
		return copy().gridPosRounded_();
	}
	
	public Vector globalPos() {
		return copy().globalPos_();
	}
	
	public Vector gridPos_() {
		x = getX() / Tile.WIDTH;
		y = getY() / Tile.WIDTH;
		return this;
	}
	
	public Vector gridPosRounded_() {
		x = Math.round(x / Tile.WIDTH);
		y = Math.round(y / Tile.WIDTH);
		return this;
	}
	
	public Vector globalPos_() {
		x = getX() * Tile.WIDTH;
		y = getY() * Tile.WIDTH;
		return this;
	}
	
	/** Returns a random vector chosen uniformly from <code>[0, x) x [0, y)</code>. */
	public Vector random() {
		return new Vector(Math.random() * x, Math.random() * y);
	}

	/** Comparator for sorting vectors by magnitude. 
	 * 
	 * Note that there is no complete ordering over the plane.
	 */
	@Override
	public int compareTo(Vector o) {
		return Double.compare(magnitude(), o.magnitude());
	}
	
	/**
	 * Hash function taken from {@link http://stackoverflow.com/questions/5928725/hashing-2d-3d-and-nd-vectors}.
	 * 100030001 and 100050001 are arbitrary palindromic primes. For more palindromic primes, see {@link https://www.rsok.com/~jrm/9_digit_palindromic_primes.html}.
	 * @return A hash code for this vector.
	 */
	@Override
	public int hashCode() {
		return getX() * 100030001 ^ getY() * 100050001;
	}
	
	/** Returns a hash code for a pair of vectors. */
	public static int hashCodeForPair(Vector v1, Vector v2) {
		return v1.getX() * 100030001 ^ v1.getY() * 100050001 ^ v2.getX() * 100060001 ^ v2.getY() * 100111001;
	}
		
	public Point2D getPoint() {
		return new Point(getX(), getY());
	}
	
	/**
	 * This method does not mutate the underlying object.
	 * @see <code>Vector.invert()</code> for the mutating version.
	 */
	public Vector getInverted() {
		return new Vector(y, x);
	}
	
	/** @see <code>getInverted()</code>. */
	public Vector getXReflected(Vector dim) {
		return new Vector(dim.getX() - 1 - x, y);
	}

	public double getStandardAngle() {
		double theta = getAngleTo(ORIENTATION);
		return y > 0 ? theta : 2 * Math.PI - theta;
	}
		
}
