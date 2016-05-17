package snorri.world;

public class Position {

	//TODO: extend Java Point
	
	public int x, y;
	
	public Position(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	public void multiply(int n) {
		x *= n;
		y *= n;
	}
	
	public void divide(int n) {
		x /= n;
		y /= n;
	}
	
	public void add(Position pos) {
		x += pos.x;
		y += pos.y;
	}
	
	public void sub(Position pos) {
		x -= pos.x;
		y -= pos.y;
	}
	
	public int distance(Position pos) {
		return (int) Math.sqrt((x - pos.x) * (x - pos.x) + (y - pos.y) * (y - pos.y));
	}
	
	public Position copy() {
		return new Position(x, y);
	}
	
	public String toString() {
		return "x, y: " + x + ", " + y;
	}
	
	public boolean equals(Position pos) {
		return x == pos.x && y == pos.y;
	}
	
}
