package snorri.world;

public class Position {

	//TODO: extend point?
	
	public static final Position ZERO = new Position(0, 0);
	
	public int x, y;
	
	public Position(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	//using each of these introduces a bit of roundoff error, but it's fine
	
	public void multiply(double n) {
		x *= n;
		y *= n;
	}
	
	public void divide(double n) {
		x *= n;
		y *= n;
	}
	
	public void add(Position pos) {
		x += pos.x;
		y += pos.y;
	}
	
	public void sub(Position pos) {
		x -= pos.x;
		y -= pos.y;
	}
	
	public void scale(int magnitude) {
		multiply(((double) magnitude) / distance(ZERO));
	}
	
	public int distance(Position pos) {
		return (int) Math.sqrt((x - pos.x) * (x - pos.x) + (y - pos.y) * (y - pos.y));
	}
	
	public Position copy() {
		return new Position(x, y);
	}
	
	public String toString() {
		return "(x: " + x + ", y: " + y + ")";
	}
	
	public boolean equals(Position pos) {
		return x == pos.x && y == pos.y;
	}
	
}
