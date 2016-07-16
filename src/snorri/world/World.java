package snorri.world;

import java.awt.FileDialog;
import java.awt.Graphics;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

import snorri.entities.Desk;
import snorri.entities.Detector;
import snorri.entities.Drop;
import snorri.entities.Enemy;
import snorri.entities.Entity;
import snorri.entities.EntityGroup;
import snorri.entities.Player;
import snorri.inventory.VocabDrop;
import snorri.main.Debug;
import snorri.main.FocusedWindow;
import snorri.main.Main;
import snorri.parser.Lexicon;
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
		addHard(new Enemy(new Vector(600, 600), this.computeFocus()));

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
		
		if (computeFocus() == null) {
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
		l.computePathfinding();
		
		//TODO pick the graph the player is in and only spawn there.
		
		List<Entity> toBeSpawned = new ArrayList<>();
		
		Player p = new Player(l.getGoodSpawn(level.getDimensions().random()));
		addHard(p);
		
		for (int i = 0; i < 40; i++) {
			Vector spawnPos = l.getGoodSpawn(level.getDimensions().random());
			if (spawnPos != null) { //spawning enemies at null positions is gross and caused lots of issues
				toBeSpawned.add(new Enemy(spawnPos, p));
			}
		}
		
		Set<String> drops = Lexicon.getELang();
		for (String possibleDrop : drops) {
			if (Math.random() > 0.2) {
				Vector spawnPos = l.getGoodSpawn(level.getDimensions().random());
				if (spawnPos != null) { //spawning enemies at null positions is gross and caused lots of issues
					toBeSpawned.add(new Drop(spawnPos, new VocabDrop(possibleDrop)));
				}
			}
		}
		
		for (int i = 0; i < 10; i++) {
			Vector spawnPos = l.getGoodSpawn(level.getDimensions().random());
			if (spawnPos != null) { //spawning enemies at null positions is gross and caused lots of issues
				toBeSpawned.add(new Desk(spawnPos));
			}
		}
		
		addAllHard(toBeSpawned, p);

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
		
		if (Debug.LOG_WORLD) {
			Main.log("world update");
		}
		col.updateAround(this, d, ((FocusedWindow) Main.getWindow()).getFocus());
				
		for (Detector p : colliders) {
			p.update(this, d);
		}
		
		//TODO can probably get rid of these queues
		
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

		if (e instanceof Detector && !((Detector) e).isTreeMember()) {
			colliders.add((Detector) e);
			return;
		}

		col.insert(e);

	}
	
	/**
	 * uses a magnitude-sort heuristic to add a list of entities
	 * in an order that will produce a nicer tree structure
	 * @param ents
	 * 	the list of entities to be added
	 * 	this list will be sorted into the order in which the entities are added
	 */
	public void addAllHard(List<Entity> ents, Entity focus) {
		Collections.sort(ents, new Comparator<Entity>() {
			@Override
			public int compare(Entity o1, Entity o2) {
				return Double.compare(o1.getPos().distance(focus.getPos()), o2.getPos().distance(focus.getPos()));
			}
		});
		for (Entity e : ents) {
			Main.log("starting traverse..");
			addHard(e);
			col.traverse();
			Main.log("traverse done!");
		}
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
		if (e instanceof Detector && !((Detector) e).isTreeMember()) {
			return colliders.remove(e);
		}
		return col.delete(e);
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

	/**
	 * search through all the entities to find the first player
	 */
	@Override
	public Player computeFocus() {
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
