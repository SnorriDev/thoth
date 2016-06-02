package snorri.world;

import java.awt.Graphics;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import snorri.entities.Collider;
import snorri.entities.Entity;
import snorri.entities.EntityGroup;
import snorri.events.CollisionEvent;
import snorri.main.GameWindow;
import snorri.main.Main;

public class World {

	private Level level;
	private EntityGroup col;
	private List<Collider> colliders;
	private Queue<Entity> deleteQ;

	public World() {
		level = new Level(100, 100); // TODO: pass a level file to read
		col = new EntityGroup();
		colliders = new ArrayList<Collider>();
		deleteQ = new LinkedList<Entity>();
	}

	public World(File f) throws FileNotFoundException, IOException {
		load(f);
		colliders = new ArrayList<Collider>();
		deleteQ = new LinkedList<Entity>();
	}

	public World(String fileName) throws FileNotFoundException, IOException {
		this(new File(fileName));
	}

	public void update(float f) {

		col.update(this, f);

		for (Collider p : colliders) {

			p.update(this, f);

			for (Entity hit : col.getAllCollisions(p)) {
				if (hit != null) {
					p.onCollision(new CollisionEvent(p, hit, this));
				}
			}

		}

		while (!deleteQ.isEmpty()) {
			deleteHard(deleteQ.poll());
		}

	}

	public void render(GameWindow window, Graphics g) {

		// TODO: draw grid
		// TODO: render, not render hitboxes

		col.renderAround(window, g);

		for (Collider p : colliders) {
			p.renderHitbox(window, g);
		}

	}

	public EntityGroup getEntityTree() {
		return col;
	}

	public Level getLevel() {
		return level;
	}

	/**
	 * Add an Entity to the World. Detects whether Entity is a Collider or
	 * otherwise, and handles it appropriately
	 * 
	 */
	public void add(Entity e) {

		if (e instanceof Collider) {
			colliders.add((Collider) e);
			return;
		}

		col.insert(e);

	}

	/**
	 * Use deleteSoft method in update deleteHard is a bit faster, and can be
	 * used in CollisionEvents and other contexts
	 * 
	 * @param e
	 *            the entity to delete
	 */
	public void deleteSoft(Entity e) {
		deleteQ.add(e);
	}

	/**
	 * Use deleteSoft method in update deleteHard is a bit faster, and can be
	 * used in CollisionEvents and other contexts
	 * 
	 * @param e
	 *            the entity to delete
	 */
	private boolean deleteHard(Entity e) {

		if (e instanceof Collider) {
			return colliders.remove(e);
		}

		return col.delete(e);

	}

	public void save(String folderName) throws IOException {

		File f = new File(folderName);

		if (f.exists() && !f.isDirectory()) {
			Main.error("tried to save world " + folderName + " to non-directory");
			throw new IOException();
		}

		if (!f.exists()) {
			Main.log("creating new world directory...");
			f.mkdir();
		}

		String path = f.getPath();
		col.saveEntities(path + "/entities.dat");
		level.save(path + "/level.dat");

	}

	public void load(File f) throws FileNotFoundException, IOException {

		if (!f.exists()) {
			Main.error("could not find world " + f.getName());
			throw new FileNotFoundException();
		}

		if (!f.isDirectory()) {
			Main.error("world file " + f.getName() + " is not a directory");
			throw new IOException();
		}

		col = new EntityGroup(new File(f, "entities.dat"));
		level = new Level(new File(f, "level.dat"));

	}

}
