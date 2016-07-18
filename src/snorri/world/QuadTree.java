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

	private void split() {

		Vector newDim = bounds.getDimensions().copy().divide(2);
		Vector trans = newDim.copy().divide(2);
		Vector topLeft = bounds.getTopLeft();

		int width = newDim.getX();
		int height = newDim.getY();

		nodes[0] = new QuadTree(level + 1, new RectCollider(topLeft.copy().add(trans), newDim.copy()));
		nodes[1] = new QuadTree(level + 1, new RectCollider(topLeft.copy().add(width, 0).add(trans), newDim.copy()));
		nodes[2] = new QuadTree(level + 1, new RectCollider(topLeft.copy().add(0, height).add(trans), newDim.copy()));
		nodes[3] = new QuadTree(level + 1, new RectCollider(topLeft.copy().add(newDim).add(trans), newDim.copy()));

	}
	
	public boolean intersects(Entity e) {
		return bounds.intersects(e.getCollider());
	}
	
	public boolean contains(Entity e) {
		return bounds.contains(e.getCollider());
	}

	/**
	 * Determine which node the object belongs to. -1 means object cannot
	 * completely fit within a child node and is part of the parent node
	 */
	private int getIndex(Entity e) {
		
		for (int i = 0; i < nodes.length; i++) {
			if (nodes[i] != null && nodes[i].contains(e)) {
				return i;
			}
		}
		
		return -1;
		
	}
	
	public void insert(Entity e) {
		
		if (nodes[0] != null) {
			int index = getIndex(e);
			if (index != -1) {
				nodes[index].insert(e);
				return;
			}
		}
		
		entities.add(e);
		if (entities.size() > MAX_ENTITIES && level < MAX_LEVELS) {
			if (nodes[0] == null) {
				split();
			}
			
			int i = 0;
			while (i < entities.size()) {
				int index = getIndex(entities.get(i));
				if (index != -1) {
					nodes[index].insert(entities.remove(i));
				} else {
					i++;
				}
			}
			
		}
	}
	
	private List<Entity> retrieve(List<Entity> out, Entity e) {
		int index = getIndex(e);
		if (index != -1 && nodes[0] != null) {
			nodes[index].retrieve(out, e);
		}
		out.addAll(entities);
		return out;
	}
	
	public List<Entity> getAllCollisions(Entity e) {
		List<Entity> cols = new ArrayList<>();
		for (Entity each : retrieve(new ArrayList<Entity>(), e)) {
			if (e.intersects(each)) {
				cols.add(each);
			}
		}
		return cols;
	}
	
	public Entity getFirstCollision(Entity e) {
		for (Entity each : retrieve(new ArrayList<Entity>(), e)) {
			if (e.intersects(each)) {
				return each;
			}
		}
		return null;
	}

}
