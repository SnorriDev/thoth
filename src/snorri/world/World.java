package snorri.world;

import java.awt.FileDialog;
import java.awt.Graphics;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.CopyOnWriteArrayList;

import snorri.entities.Detector;
import snorri.entities.Enemy;
import snorri.entities.Entity;
import snorri.entities.EntityGroup;
import snorri.entities.Player;
import snorri.entities.Unit;
import snorri.events.CollisionEvent;
import snorri.main.FocusedWindow;
import snorri.main.Main;
import snorri.pathfinding.Pathfinding;

public class World implements Playable {

	private static final Vector DEFAULT_SPAWN = new Vector(100, 100);
	
	private Level level;
	private EntityGroup col;
	private CopyOnWriteArrayList<Detector> colliders;
	private Queue<Entity> deleteQ;
	private Queue<Entity> addQ;

	public World() {
		this(300, 300);
	}

	/**
	 * constructor to create a blank world in the level editor
	 * @param width
	 * 	width of the new world
	 * @param height
	 * 	height of the new world
	 */
	public World(int width, int height) {

		Main.log("creating new world of size " + width + " x " + height + "...");
		level = new Level(width, height); // TODO: pass a level file to read
		//level.computePathfinding();
		col = new EntityGroup();
		colliders = new CopyOnWriteArrayList<Detector>();
		deleteQ = new LinkedList<Entity>();
		addQ = new LinkedList<Entity>();
		
		Pathfinding.setWorld(this);

		// temporary
		addHard(new Player(DEFAULT_SPAWN.copy()));
		addHard(new Enemy(new Vector(600, 600), this.getFocus()));

		Main.log("new world created!");
		
	}

	public World(String folderName) throws FileNotFoundException, IOException {
		this(new File(folderName));
	}
	
	public World(File file) throws FileNotFoundException, IOException {
		
		load(file);
		level.computePathability();
		colliders = new CopyOnWriteArrayList<Detector>();
		deleteQ = new LinkedList<Entity>();
		addQ = new LinkedList<Entity>();
		
		Pathfinding.setWorld(this);
		
		if (getFocus() == null) {
			Main.error("world without player detected");
		}
		
	}
	
	public World(Level l) {

		Main.log("creating new world of size " + l.getDimensions().getX() + " x " + l.getDimensions().getY() + "...");
		level = l;
		col = new EntityGroup();
		colliders = new CopyOnWriteArrayList<Detector>();
		deleteQ = new LinkedList<Entity>();
		addQ = new LinkedList<Entity>();

		Pathfinding.setWorld(this);
		
		Player p = new Player(l.getGoodSpawn(level.getDimensions().random()));
		addHard(p);
		for (int i = 0; i < 80; i++) {
			Vector spawnPos = l.getGoodSpawn(level.getDimensions().random());
			if (spawnPos != null) { //spawning enemies at null positions is gross and caused lots of issues
				int oldSize = getEntityTree().getAllEntities().size();
				addHard(new Enemy(spawnPos, p));
				if (getEntityTree().getAllEntities().size() - oldSize > 1) {
					Main.log(i + " has " + (getEntityTree().getAllEntities().size() - oldSize - 1) + " extra units");
				}
			}
		}

		Main.log("new world created!");
	}

	public static World wrapLoad() {

		File file = Main.getFileDialog("Select file to load", FileDialog.LOAD);

		if (file == null) {
			return null;
		}

		try {
			return new World(file);
		} catch (IOException er) {
			Main.error("error opening world " + file.getName());
			return null;
		}
	}

	public void update(double d) {

		//TODO: recalculate could be causing issues.. duplication?
		//print pointers of objects that get updated?
		
		if (!(Main.getWindow() instanceof FocusedWindow)) {
			return;
		}
		
		col.updateAround(this, d, ((FocusedWindow) Main.getWindow()).getFocus());
		
		//this is for detecting duplicate entities
		for (Entity ent : col.getAllEntities()) {
			if (ent instanceof Unit) {
				((Unit) ent).resetUpdated();
			}
		}
				
		for (Detector p : colliders) {
			p.update(this, d);
			for (Entity hit : col.getAllCollisions(p)) {
				if (hit != null) {
					p.onCollision(new CollisionEvent(p, hit, this));
				}
			}
		}
		
		while (!deleteQ.isEmpty()) {
			deleteHard(deleteQ.poll());
		}

		while (!addQ.isEmpty()) {
			addHard(addQ.poll());
		}

	}

	public void render(FocusedWindow g, Graphics gr, boolean showOutlands) {
		
		level.renderMap(g, gr, showOutlands);
		col.renderAround(g, gr);

		for (Detector p : colliders.toArray(new Detector[0])) {
			p.renderAround(g, gr);
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
		addQ.add(e);
	}

	public void addHard(Entity e) {

		if (e instanceof Detector) {
			colliders.add((Detector) e);
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
	public void delete(Entity e) {
		deleteQ.add(e);
	}

	/**
	 * Use deleteSoft method in update deleteHard is a bit faster, and can be
	 * used in CollisionEvents and other contexts
	 * 
	 * @param e
	 *            the entity to delete
	 */
	public boolean deleteHard(Entity e) {

		if (e instanceof Detector) {
			return colliders.remove(e);
		}

		return col.delete(e);

	}

	@Override
	public void save(File f) throws IOException {
		save(f, true);
	}
	
	@Override
	public void save(File f, boolean recomputeGraphs) throws IOException {

		if (f.exists() && !f.isDirectory()) {
			Main.error("tried to save world " + f.getName() + " to non-directory");
			throw new IOException();
		}

		if (!f.exists()) {
			Main.log("creating new world directory...");
			f.mkdir();
		}

		String path = f.getPath();
		col.saveEntities(new File(path, "entities.dat"));
		level.save(new File(path, "level.dat"), recomputeGraphs);

	}

	@Override
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

	@Override
	public Player getFocus() {
		for (Entity e : col.getAllEntities()) {
			if (e instanceof Player) {
				return (Player) e;
			}
		}
		return null;
	}

	@Override
	public World getCurrentWorld() {
		return this;
	}
	
	public void resize(int newWidth, int newHeight) {
		level = level.resize(newWidth, newHeight);
	}

}
