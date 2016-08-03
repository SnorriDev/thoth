package snorri.pathfinding;

import java.awt.Graphics;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import snorri.main.FocusedWindow;
import snorri.world.Level;
import snorri.world.Tile;
import snorri.world.Vector;

public class PathNode implements Comparable<PathNode> {

	private static final double D = 1;
	private static final double D2 = 1;//Math.sqrt(2); we get better performance with 1
	
	//change this to edit whether or not we can go diagonally
	public static final Vector[] NEIGHBORS = new Vector[] {
			new Vector(-1, 0),
			new Vector(1, 0),
			new Vector(0, 1),
			new Vector(0, -1),
			new Vector(-1, -1),
			new Vector(1, -1),
			new Vector(1, 1),
			new Vector(-1, 1)};
	
	private Vector gridPos;
	
	private Double g;
	private Double f;
	private PathNode origin;
	
	public PathNode(Vector gridPos, double g, Vector goal) {
		this.gridPos = gridPos;
		updateHeuristics(g, goal);
	}
	
	public PathNode(Vector gridPos) {
		this.gridPos = gridPos;
		updateHeuristics(Double.MAX_VALUE, Double.MAX_VALUE);
	}

	public void setOrigin(PathNode node) {
		origin = node;
	}
	
	public PathNode getOrigin() {
		return origin;
	}
	
	public void updateHeuristics(double g, Vector goal) {
		this.g = g;
		this.f = g + getHeuristic(goal);
	}
	
	public void updateHeuristics(double g, double f) {
		this.g = g;
		this.f = f;
	}
	
	public Double getG() {
		return g;
	}
	
	public Double getF() {
		return f;
	}
	
	@Override
	public int compareTo(PathNode o) {
		return f.compareTo(o.f);
	}
	
	public Vector getGridPos() {
		return gridPos;
	}
	
	public Vector getGlobalPos() {
		return gridPos.copy().toGlobalPos();
	}

	//TODO: randomize the order of neighbors so we get random paths
	public Queue<PathNode> getNeighbors(PathNode[][] map, Level level) {
		
		Queue<PathNode> neighbors = new LinkedList<PathNode>();
		Vector newPos;
		
		for (int i = 0; i < NEIGHBORS.length; i++) {
			newPos = gridPos.copy().add(NEIGHBORS[i]);
			if (level.isContextPathable(newPos)) {
				if (map[newPos.getX()][newPos.getY()] == null) {
					map[newPos.getX()][newPos.getY()] = new PathNode(newPos);
				}
				neighbors.add(map[newPos.getX()][newPos.getY()]);
			}
		}
		
		return neighbors;
		
	}
	
	public static List<Vector> getNeighbors(Vector pos) {
		List<Vector> out = new ArrayList<>();
		for (Vector trans : NEIGHBORS) {
			out.add(pos.copy().add(trans));
		}
		return out;
	}

	public double distance(PathNode neighbor) {
		//return 1d;
		//return gridPos.copy().distance(neighbor.gridPos);
		Vector d = gridPos.copy().sub(neighbor.gridPos).abs();
		return d.x + d.y;
	}
	
	//no diagonal movement
	//TODO: move this to PathNode maybe
	public double getHeuristic(Vector goal) {
		Vector d = gridPos.copy().sub(goal).abs();
		return D * (d.x + d.y) + (D2 - 2 * D) * Double.min(d.x, d.y);
	}
	
	public void render(Graphics gr, FocusedWindow window) {
		
		if (origin == null) {
			return;
		}
		
		Vector p1 = origin.getGridPos().copy().toGlobalPos().sub(window.getFocus().getPos()).add(window.getCenter());
		Vector p2 = getGridPos().copy().toGlobalPos().sub(window.getFocus().getPos()).add(window.getCenter());
		gr.drawLine(p1.getX() + Tile.WIDTH / 2, p1.getY() + Tile.WIDTH / 2, p2.getX() + Tile.WIDTH / 2, p2.getY() + Tile.WIDTH / 2);
		
	}

}
