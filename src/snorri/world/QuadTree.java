package snorri.world;

import java.util.ArrayList;
import java.util.List;

import snorri.collisions.RectCollider;
import snorri.entities.Entity;

public class QuadTree {

	private static final int MAX_ENTITIES = 10;
	private static final int MAX_LEVELS = 5;
	
	private int level;
	private List<Entity> entities;
	private RectCollider bounds;
	private QuadTree[] nodes;
	
	public QuadTree(int level, RectCollider bounds) {
		this.level = level;
		entities = new ArrayList<>();
		this.bounds = bounds;
		nodes = new QuadTree[4];
	}
	
	public void clear() {
		entities.clear();
		for (QuadTree node : nodes) {
			if (node != null) {
				node.clear();
				node = null;
			}
		}
	}
	
}
