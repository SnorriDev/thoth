package snorri.world;

import java.awt.FileDialog;
import java.awt.Graphics;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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

	public static final int DEFAULT_LEVEL_SIZE = 144;
	public static final Vector DEFAULT_SPAWN = new Vector(100, 100);
	private static final int RANDOM_SPAWN_ATTEMPTS = 100;
	public static final int UPDATE_RADIUS = 4000;
	
	private Level background;
	private Level midground;
	private Level foreground;
	private EntityGroup col;
	private List<Team> teams;
	
	private TriggerMap triggers;

	public World() {
		this(DEFAULT_LEVEL_SIZE, DEFAULT_LEVEL_SIZE);
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
		background = new Level(width, height, 0); // TODO: pass a level file to read
		midground = new Level(width, height, 1);
		foreground = new Level(width, height, 2);
		//level.computePathfinding();
		col = QuadTree.coverLevel(background);
		
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
		background.computePathability(); //FIXME: is that function still capplicable?
		
		Pathfinding.setWorld(this);
		
		if (computeFocus() == null) {
			Main.log("world without player detected");
		}
		
	}
	
	public World(Level l0, Level l1, Level l2) {

		Main.log("creating new world background of size " + l0.getDimensions().getX() + " x " + l0.getDimensions().getY() + "...");
		background = l0;
		Main.log("creating new world midground of size " + l1.getDimensions().getX() + " x " + l1.getDimensions().getY() + "...");
		midground = l1;
		Main.log("creating new world foreground of size " + l2.getDimensions().getX() + " x " + l2.getDimensions().getY() + "...");
		foreground = l2;
		
		col = QuadTree.coverLevel(background);
		
		Pathfinding.setWorld(this); //TODO make pathfinding not static
		l0.computePathfinding(); //FIXME: do we need to change how this function works?
		
		Main.log("new world created!");
	}

	//TODO input the unit as an arg?
	public Vector getRandomSpawnPos(int radius) {
		for (int i = 0; i < RANDOM_SPAWN_ATTEMPTS; i++) {
			Vector pos = background.getGoodSpawn(background.getDimensions().random()); //FIXME: good spawn?
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

	}

	@Override
	public synchronized void render(FocusedWindow g, Graphics gr, double deltaTime, boolean showOutlands) {	
		background.render(g, gr, deltaTime, showOutlands);
		midground.render(g, gr, deltaTime, false);
		col.renderAround(g, gr, deltaTime);
		foreground.render(g, gr, deltaTime, false);
	}

	public EntityGroup getEntityTree() {
		return col;
	}

	public Level getLevel() { //what is this for?
		return background;
	}
	
	public Level[] getLevels() {
		return new Level[] {background, midground, foreground};
	}

	public void add(Entity e) {
		col.insert(e, background);
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
			background.removeEntity(e);
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
		background.save(new File(path, "background.lvl"), recomputeGraphs);
		midground.save(new File(path, "midground.lvl"), recomputeGraphs);
		foreground.save(new File(path, "foreground.lvl"), recomputeGraphs);
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

		background = new Level(new File(f, "background.lvl"), BackgroundElement.class);
		midground = new Level(new File(f, "midground.lvl"), MidgroundElement.class);
		foreground = new Level(new File(f, "foreground.lvl"), ForegroundElement.class);
		col = QuadTree.coverLevel(background);
		col.loadEntities(new File(f, "entities.dat"), background);
		
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
		background = background.getResized(newWidth, newHeight);
	}
	
	@Override
	public World getTransposed() {
		World w = new World(background.getTransposed(), midground.getTransposed(), foreground.getTransposed());
		for (Entity e : col.getAllEntities()) {
			Entity e2 = e.copy();
			e2.getPos().invert();
			w.add(e2);
		}
		return w;
	}
	
	@Override
	public World getXReflected() {
		World w = new World(background.getXReflected(), midground.getXReflected(), foreground.getXReflected());
		for (Entity e : col.getAllEntities()) {
			Entity e2 = e.copy();
			e2.setPos(e2.getPos().getXReflected(background.getDimensions().copy().toGlobalPos()));
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

}
