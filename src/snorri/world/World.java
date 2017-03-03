package snorri.world;

import java.awt.FileDialog;
import java.awt.Graphics;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.CopyOnWriteArrayList;

import snorri.collisions.Collider;
import snorri.entities.Detector;
import snorri.entities.Mummy;
import snorri.entities.Entity;
import snorri.entities.Player;
import snorri.entities.QuadTree;
import snorri.entities.Unit;
import snorri.main.Debug;
import snorri.main.FocusedWindow;
import snorri.main.LevelEditor;
import snorri.main.Main;
import snorri.pathfinding.Pathfinding;
import snorri.pathfinding.Team;
import snorri.triggers.Trigger;
import snorri.triggers.TriggerMap;

public class World implements Playable, Editable {

	public static final Vector DEFAULT_SPAWN = new Vector(100, 100);
	private static final int RANDOM_SPAWN_ATTEMPTS = 100;
	public static final int UPDATE_RADIUS = 4000;
	
	private static final int SPAWN_SEARCH_RADIUS = 10;
	
	private Level level;
	private EntityGroup col;
	private CopyOnWriteArrayList<Detector> colliders;
	private List<Team> teams;
	
	private TriggerMap triggers;

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
		col = QuadTree.coverLevel(level);
		colliders = new CopyOnWriteArrayList<Detector>();
		
		Pathfinding.setWorld(this);

		// temporary
		add(new Player(DEFAULT_SPAWN.copy()));
		add(new Mummy(new Vector(600, 600), computeFocus()));

		Main.log("new world created!");
		
	}

	public World(String folderName) throws FileNotFoundException, IOException {
		this(new File(folderName));
	}
	
	public World(File file) throws FileNotFoundException, IOException {
		
		load(file);
		level.computePathability();
		colliders = new CopyOnWriteArrayList<Detector>();
		
		Pathfinding.setWorld(this);
		
		if (computeFocus() == null) {
			Main.log("world without player detected");
		}
		
	}
	
	public World(Level l) {

		Main.log("creating new world of size " + l.getDimensions().getX() + " x " + l.getDimensions().getY() + "...");
		level = l;
		col = QuadTree.coverLevel(level);
		colliders = new CopyOnWriteArrayList<Detector>();
		
		Pathfinding.setWorld(this); //TODO make pathfinding not static
		l.computePathfinding();
		
		Main.log("new world created!");
	}

	//TODO input the unit as an arg?
	public Vector getRandomSpawnPos(int radius) {
		for (int i = 0; i < RANDOM_SPAWN_ATTEMPTS; i++) {
			Vector pos = level.getGoodSpawn(level.getDimensions().random());
			if (pos != null && col.getFirstCollision(new Entity(pos, radius)) == null) {
				return pos;
			}
		}
		Main.error("could not find suitable spawn");
		return null;
	}
	
	public Vector getRandomSpawnPos() {
		return getRandomSpawnPos(Unit.RADIUS);
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

	}

	@Override
	public synchronized void render(FocusedWindow g, Graphics gr, double deltaTime, boolean showOutlands) {	
		level.render(g, gr, deltaTime, showOutlands);
		col.renderAround(g, gr, deltaTime, colliders);
	}

	public EntityGroup getEntityTree() {
		return col;
	}

	public Level getLevel() {
		return level;
	}

	public void add(Entity e) {
		
		if (e instanceof Detector && !((Detector) e).isTreeMember()) {
			colliders.add((Detector) e);
			return;
		}

		col.insert(e, level);
		
	}
	
	/**
	 * Add a bunch of things to the world
	 */
	public void addAll(List<Entity> ents) {
		for (Entity e : ents) {
			add(e);
		}
	}

	/**
	 * Use deleteSoft method in update deleteHard is a bit faster, and can be
	 * used in CollisionEvents and other contexts
	 * 
	 * @param e
	 *            the entity to delete
	 */
	public boolean delete(Entity e) {
		
		//TODO possibly move this to EntityGroup
		if (e != null && e.isStaticObject() && !(Main.getWindow() instanceof LevelEditor)) {
			level.removeEntity(e);
		}
		
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
		Team.save(new File(path, "teams.dat"), teams);

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

		level = new Level(new File(f, "level.dat"));
		col = QuadTree.coverLevel(level);
		col.loadEntities(new File(f, "entities.dat"), level);
		
		File triggerFile = new File(f, "triggers.yml");
		if (triggerFile.exists()) {
			triggers = Trigger.load(triggerFile, this);
		}
		
		File teamsFile = new File(f, "teams.dat");
		if (teamsFile.exists()) {
			teams = Team.load(teamsFile);
		}

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
		level = level.getResized(newWidth, newHeight);
	}
	
	@Override
	public World getTransposed() {
		World w = new World(level.getTransposed());
		for (Entity e : col.getAllEntities()) {
			Entity e2 = e.copy();
			e2.getPos().invert();
			w.add(e2);
		}
		return w;
	}
	
	@Override
	public World getXReflected() {
		World w = new World(level.getXReflected());
		for (Entity e : col.getAllEntities()) {
			Entity e2 = e.copy();
			e2.setPos(e2.getPos().getXReflected(level.getDimensions().copy().toGlobalPos()));
			w.add(e2);
		}
		return w;
	}

	@Override
	public List<Entity> getEntities() {
		return col.getAllEntities();
	}
	
	/**
	 * Check if a tile is occupied by any entity
	 * @param pos
	 * The tile in grid coordinates
	 */
	public boolean tileHasEntity(Vector pos) {
		Entity hit = getEntityTree().getFirstCollision(Level.getRectangle(pos.getX(), pos.getY()), true);
		return hit != null;
	}

	public TriggerMap getTriggerMap() {
		return triggers;
	}
	
	public List<Team> getTeams() {
		return teams;
	}
	
	public void addTeam(Team team) {
		if (teams == null) {
			teams = new ArrayList<>();
		}
		teams.add(team);
	}
	
	public Vector getGoodSpawn(Vector start) {
		
		for (int r = 0; r < SPAWN_SEARCH_RADIUS; r++) {
			changeStart: for (Vector v : start.getSquareAround(r)) {
				
				int x = v.getX();
				int y = v.getY();
				
				for (int x1 = (x * Tile.WIDTH - 2 * Unit.RADIUS_X) / Tile.WIDTH; x1 <= (x * Tile.WIDTH + 2 * Unit.RADIUS_X) / Tile.WIDTH; x1++) {
					for (int y1 = (y * Tile.WIDTH - 2 * Unit.RADIUS_Y) / Tile.WIDTH; y1 <= (y * Tile.WIDTH + 2 * Unit.RADIUS_Y) / Tile.WIDTH; y1++) {
						if (!isContextPathable(x1, y1)) {
							continue changeStart;
						}
					}
				}
				
				return v.copy().toGlobalPos();
				
			}
		}
		
		return null;
		
	}
	
	public Vector getGoodSpawn(int x, int y) {
		return getGoodSpawn(new Vector(x, y));
	}
	
	public void wrapGridUpdate(Vector pos, Tile newTile) {
		
		Tile oldTile = getTileGrid(pos);
		
		//if there is no tile at this position or a static entity is there, don't let the player mess with it
		if (oldTile == null || oldTile.isOccupied()) {
			return;
		}
		
		setTileGrid(pos, newTile);
		updateSurroundingContext(pos); //update context pathability on nearby tiles
		
		if (oldTile.isPathable() == newTile.isPathable()) {
			return;
		}
		
		if (newTile.isPathable()) {
			setPathableGrid(pos);
		} else {
			setUnpathableGrid(pos);
		}
				
	}
	
	public void wrapUpdate(Vector pos, Tile tile) {
		wrapGridUpdate(pos.copy().toGridPos(), tile);
	}
	
	public void addEntity(Entity e) {
		
		int x = e.getPos().getX();
		int y = e.getPos().getY();
		Collider c = e.getCollider();
				
		//mark all tiles which are occupied
		for (int x1 = (x - c.getRadiusX()) / Tile.WIDTH; x1 <= (x + c.getRadiusX()) / Tile.WIDTH; x1++) {
			for (int y1 = (y - c.getRadiusY()) / Tile.WIDTH; y1 <= (y + c.getRadiusY()) / Tile.WIDTH; y1++) {
				
				if (c.intersects(getRectangle(x1, y1))) {
					getTileGrid(x1, y1).addEntity(e);
				}
				
			}
		}
		
		Queue<Vector> unpathableQ = new LinkedList<>();
		
		// update all tiles in range of occupied tiles
		for (int x1 = (x - c.getRadiusX() - 2 * Unit.RADIUS_X)
				/ Tile.WIDTH; x1 <= (x + c.getRadiusX() + 2 * Unit.RADIUS_X) / Tile.WIDTH; x1++) {
			for (int y1 = (y - c.getRadiusY() - 2 * Unit.RADIUS_Y)
					/ Tile.WIDTH; y1 <= (y + c.getRadiusY() + 2 * Unit.RADIUS_Y) / Tile.WIDTH; y1++) {

				boolean wasContextPathable = isContextPathable(x1, y1);
				getTileGrid(x1, y1).computeSurroundingsPathable(x1, y1, this);
				if (wasContextPathable && !getTileGrid(x1, y1).isContextPathable()) {
					unpathableQ.add(new Vector(x1, y1));
				}

			}
		}

		//recalculate graphs around unpathable tiles
		while (!unpathableQ.isEmpty()) {
			this.setUnpathableGrid(unpathableQ.poll());
		}
		
	}
	
	public void removeEntity(Entity e) {
		
		int x = e.getPos().getX();
		int y = e.getPos().getY();
		Collider c = e.getCollider();

		//unmark all tiles which are occupied
		for (int x1 = (x - c.getRadiusX()) / Tile.WIDTH; x1 <= (x + c.getRadiusX()) / Tile.WIDTH + 1; x1++) {
			for (int y1 = (y - c.getRadiusY()) / Tile.WIDTH; y1 <= (y + c.getRadiusY()) / Tile.WIDTH + 1; y1++) {

				if (getTileGrid(x1, y1) != null && c.intersects(getRectangle(x1, y1))) {
					getTileGrid(x1, y1).removeEntity(e);
				}

			}
		}

		Queue<Vector> unpathableQ = new LinkedList<>();

		// update all tiles in range of occupied tiles
		for (int x1 = (x - c.getRadiusX() - 2 * Unit.RADIUS_X)
				/ Tile.WIDTH; x1 <= (x + c.getRadiusX() + 2 * Unit.RADIUS_X) / Tile.WIDTH; x1++) {
			for (int y1 = (y - c.getRadiusY() - 2 * Unit.RADIUS_Y)
					/ Tile.WIDTH; y1 <= (y + c.getRadiusY() + 2 * Unit.RADIUS_Y) / Tile.WIDTH; y1++) {
				
				if (getTileGrid(x1, y1) == null) {
					continue;
				}
				
				boolean wasContextPathable = isContextPathable(x1, y1);
				getTileGrid(x1, y1).computeSurroundingsPathable(x1, y1, this);
				if (!wasContextPathable && getTileGrid(x1, y1).isContextPathable()) {
					unpathableQ.add(new Vector(x1, y1));
				}

			}
		}

		// recalculate graphs around unpathable tiles
		while (!unpathableQ.isEmpty()) {
			this.setPathableGrid(unpathableQ.poll());
		}

	}

}
