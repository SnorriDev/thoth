package snorri.pathfinding;

import java.util.ArrayDeque;

import snorri.world.Vector;

public interface Pathfinder {

	public void setPath(ArrayDeque<PathNode> stack);
	
	public Vector getGridBounds();
	
}
