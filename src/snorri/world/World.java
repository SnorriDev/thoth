package snorri.world;

import java.awt.FileDialog;
import java.awt.Graphics2D;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import net.sourceforge.yamlbeans.YamlException;
import snorri.entities.Center;
import snorri.entities.Entity;
import snorri.entities.Player;
import snorri.entities.Spawn;
import snorri.entities.Unit;
import snorri.entities.LongRangeAIUnit.ShootAttempt;
import snorri.main.Debug;
import snorri.main.FocusedWindow;
import snorri.main.Main;
import snorri.pathfinding.Pathfinding;
import snorri.pathfinding.Team;
import snorri.triggers.Trigger;
import snorri.triggers.TriggerMap;

public class World implements Playable, Editable {

	public static final Vector DEFAULT_LEVEL_SIZE = new Vector(13, 8);
	public static final Vector DEFAULT_SPAWN = new Vector(100, 100);
	public static final int UPDATE_RADIUS = 4000;
	private static final int SPAWN_SEARCH_RADIUS = 12;
	private static final Vector EDGE_TP_DELTA = new Vector(Unit.RADIUS_X + 10, Unit.RADIUS_Y + 10);

	private String path;
	private int width;
	private int height;
	
	private List<Layer> layers;
	private Level tileLayer;
	private EntityLayer entityLayer;

	private final Pathfinding pathfinding;

	private List<Team> teams;
	private TriggerMap triggers;
	
	private WorldGraph universe;
	private World[] neighbors = new World[4];

	
	public World() {
		this(DEFAULT_LEVEL_SIZE.getX(), DEFAULT_LEVEL_SIZE.getY());
	}

	/**
	 * constructor to create a blank world in the level editor
	 * 
	 * @param width
	 *            width of the new world
	 * @param height
	 *            height of the new world
	 */
	public World(int width, int height) {
		this.width = width;
		this.height = height;
		pathfinding = createPathfinding();
	}

	public World(String folderName) throws FileNotFoundException, IOException {
		this(folderName, null);
	}
	
	public World(String folderName, Player p) throws FileNotFoundException, IOException {
		this(new File(folderName), p);
	}

	public World(File file, Player p) throws FileNotFoundException, IOException {
		load(file);
		if (p != null) {
			spawnPlayer(p);
		}
		pathfinding = createPathfinding();
	}
		
	public World(File file) throws FileNotFoundException, IOException {
		this(file, null);
	}
	
	public final class WorldLayerException extends RuntimeException {
		private static final long serialVersionUID = 1L;
		
		public WorldLayerException(String msg) {
			super(msg);
		}

	}
	
	private void addLayer(Layer layer) {
		layers.add(layer);
		if (layer instanceof EntityLayer) {
			if (entityLayer == null) {
				entityLayer = (EntityLayer) layer;
			} else {
				throw new WorldLayerException("Cannot have two EntityLayers in the same Level.");
			}
		} else if (layer instanceof Level) {
			if (tileLayer == null) {
				tileLayer = (Level) layer;
			} else {
				throw new WorldLayerException("Cannot have two TileLayers in the same Level.");
			}
		}
	}
	
	private Pathfinding createPathfinding() {
		List<Level> pathfindingLayers = new ArrayList<>();
		pathfindingLayers.add(tileLayer);
		return new Pathfinding(pathfindingLayers);
	}

	public static File wrapLoad() {
		File file = Main.getFileDialog("Select file to load", FileDialog.LOAD);
		return file == null ? null : file;
	}

	public synchronized void update(Entity focus, double d) {

		if (Debug.worldLogged()) {
			Debug.logger.info("World updated.");
		}

		entityLayer.updateAround(this, d, focus);
		
		World neighbor;
		if (getRightNeighbor() != null && touchingRight(focus)) {
			universe.crossInto(getRightNeighbor(), EDGE_TP_DELTA.getX(), focus.getPos().getY());
		} else if (getBottomNeighbor() != null && touchingBottom(focus)) {
			universe.crossInto(getBottomNeighbor(), focus.getPos().getX(), EDGE_TP_DELTA.getY());
		} else if ((neighbor = getLeftNeighbor()) != null && touchingLeft(focus)) {
			universe.crossInto(neighbor, neighbor.getWidth() - EDGE_TP_DELTA.getX(), focus.getPos().getY());
		} else if ((neighbor = getTopNeighbor()) != null && touchingTop(focus)) {
			universe.crossInto(neighbor, focus.getPos().getX(), neighbor.getHeight() - EDGE_TP_DELTA.getY());
		}

	}

	@Override
	public synchronized void render(FocusedWindow<?> g, Graphics2D gr, double deltaTime, boolean showOutlands) {
		layers.forEach(layer -> {
			layer.render(g, gr, deltaTime, showOutlands);
		});
	}
	
	public boolean add(Entity e) {
		return entityLayer.add(e, pathfinding);
	}

	/** Add a bunch of things to the world. */
	public void addAll(List<Entity> ents) {
		for (Entity e : ents) {
			add(e);
		}
	}

	/**
	 * @param e the entity to delete
	 * @return whether the delete was successful
	 */
	public synchronized boolean delete(Entity e) {
		if (e == null) {
			return false;
		}
		e.onDelete(this);
		return entityLayer.remove(e);
	}

	@Override
	public void save(File f, boolean recomputeGraphs) throws IOException {

		if (f.exists() && !f.isDirectory()) {
			throw new IOException("tried to save world to non-directory");
		}

		if (!f.exists()) {
			Debug.logger.info("Creating new world directory...");
			f.mkdir();
		}
		
		Playable.tryCreatingDefaultConfig(f, "world");
		
		String path = (f == null) ? this.path : f.getPath();
		layers.forEach(layer -> {
			if (layer instanceof SavableLayer) {
				SavableLayer savableLayer = (SavableLayer) layer;
				File saveFile = new File(path, savableLayer.getFilename());
				try {
					savableLayer.save(saveFile, recomputeGraphs);
				} catch (Exception e) {
					Debug.logger.log(java.util.logging.Level.SEVERE, "Could not load " + saveFile.getAbsolutePath() + ".", e);
				}
			}
		});
		Team.save(new File(path, "teams.dat"), teams);
	}

	@Override @SuppressWarnings("unchecked")
	public void load(File f, Map<String, Object> yaml) throws IOException, YamlException {
		
		if (f == null) {
			throw new NullPointerException("trying to load null world");
		}
		
		if (!f.exists()) {
			throw new FileNotFoundException("no world called " + f.getName());
		}

		if (!f.isDirectory()) {
			throw new IOException("world file " + f.getName() + " is not a directory");
		}
		
		path = f.getName();
		
		if (yaml == null) {
			yaml = Playable.getConfig(f, "world");
		}
		
		Map<String, Map<String, Object>> layers = (Map<String, Map<String, Object>>) yaml.get("layers");
		layers.forEach((type, params) -> {
			try {
				Layer layer = Layer.fromYAML(this, type, params);
				addLayer(layer);
			} catch (Exception e) {
				// FIXME(lambdaviking): Does getting rid of forEach allow us not to use this clause?
				Debug.logger.log(java.util.logging.Level.SEVERE, "Could not read layers in World.", e);
			}
		});

		String outside = (String) yaml.get("outsideTile");
		if (outside != null) {
			getTileLayer().setOutsideTile(new Tile(outside));
		}
		
		triggers = Trigger.load((Map<String, Object>) yaml.get("triggers"), this);

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
		return getEntityTree().getFirst(Player.class);
	}

	@Override
	public World getCurrentWorld() {
		return this;
	}

	private World getTransformed(Function<Layer, Layer> layerTransformer) {
		World world = new World(width, height);
		layers.forEach(layer -> {
			world.addLayer(layerTransformer.apply(layer));
		});
		return world;
	}
	
	@Override
	public World getTransposed() {
		return getTransformed(layer -> {
			return (Layer) layer.getTransposed();
		});
	}

	@Override
	public World getXReflected() {
		return getTransformed(layer -> {
			return (Layer) layer.getXReflected();
		});
	}
	
	@Override
	public World getResized(int newWidth, int newHeight) {
		return getTransformed(layer -> {
			return (Layer) layer.getResized(newWidth, newHeight);
		});
	}

	/**
	 * Check if a tile is occupied by any entity
	 * 
	 * @param pos
	 *            The tile in grid coordinates
	 */
	public boolean tileHasEntity(Vector pos) {
		Entity hit = getEntityTree().getFirstCollision(Tile.getRectangle(pos.getX(), pos.getY()), true);
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
		return getGoodSpawn(start, new Vector(1, 2));
	}

	public Vector getGoodSpawn(Vector start, Vector gridSize) {

		for (int r = 0; r < SPAWN_SEARCH_RADIUS; r++) {
			changeStart: for (Vector v : start.getSquareAround(r)) {

				int x = v.getX();
				int y = v.getY();

				for (int x1 = (x * Tile.WIDTH - 2 * Unit.RADIUS_X)
						/ Tile.WIDTH; x1 <= (x * Tile.WIDTH + 2 * Unit.RADIUS_X) / Tile.WIDTH; x1++) {
					for (int y1 = (y * Tile.WIDTH - 2 * Unit.RADIUS_Y)
							/ Tile.WIDTH; y1 <= (y * Tile.WIDTH + 2 * Unit.RADIUS_Y) / Tile.WIDTH; y1++) {
						if (!pathfinding.getGraph(gridSize).isContextPathable(x1, y1)) {
							continue changeStart;
						}
					}
				}

				return v.copy().globalPos_();

			}
		}

		return null;

	}

	public Vector getGoodSpawn(int x, int y) {
		return getGoodSpawn(new Vector(x, y));
	}

	public void wrapUpdate(Vector pos, Tile tile) {
		wrapGridUpdate(pos.copy().gridPos_(), tile);
	}
	
	public synchronized void wrapGridUpdate(Vector posGrid, Tile tile) {
		Level l = getTileLayer();
		Tile oldTile = l.getTileGrid(posGrid);

		if (oldTile == null || pathfinding.isOccupied(posGrid)) {
			return;
		}

		l.setTileGrid(posGrid, tile);
		pathfinding.wrapPathingUpdate(posGrid, oldTile, tile);
		ShootAttempt.reset();
	}

	public Pathfinding getPathfinding() {
		return pathfinding;
	}
	
	/**
	 * @param v grid coordinates
	 * @return whether or not the tile at <code>v</code> is pathable
	 */
	public boolean isPathable(Vector v) {
		return isPathable(v.getX(), v.getY());
	}
	
	/** Returns true if bullets can pass over pos. */
	public boolean canShootOver(Vector pos) {
		return getTileLayer().canShootOver(pos.gridPos());
	}

	/**
	 * @param x grid coordinate
	 * @param y grid coordinate
	 * @return whether the tile at x, y is pathable
	 */
	public boolean isPathable(int x, int y) {
		return pathfinding.isPathable(x, y);
	}

	public void wrapGridUpdate(int x, int y, Tile tile) {
		wrapGridUpdate(new Vector(x, y), tile);
	}
	
	/**
	 * Search for a spawn marker, and put the player at that location
	 * @param player
	 * 	The player to spawn
	 * @return
	 * 	Whether or not the spawn marker was found
	 */
	public boolean spawnPlayer(Player player) {
		Spawn spawn = getEntityTree().getFirst(Spawn.class);
		if (spawn == null) {
			return false;
		}
		player.setPos(spawn.getPos());
		add(player);
		return true;
	}
	
	//TODO just need to implement the cross-world panning mechanic
	
	public World getRightNeighbor() {
		return neighbors[0];
	}
	
	public World getBottomNeighbor() {
		return neighbors[1];
	}
	
	public World getLeftNeighbor() {
		return neighbors[2];
	}
	
	public World getTopNeighbor() {
		return neighbors[3];
	}
	
	public void setRightNeighbor(World world) {
		neighbors[0] = world;
	}
	
	public void setBottomNeighbor(World world) {
		neighbors[1] = world;
	}
	
	public void setLeftNeighbor(World world) {
		neighbors[2] = world;
	}
	
	public void setTopNeighbor(World world) {
		neighbors[3] = world;
	}
	
	public boolean touchingRight(Entity e) {
		return getWidth() - e.getPos().getX() < EDGE_TP_DELTA.getX();
	}
	
	public boolean touchingBottom(Entity e) {
		return getHeight() - e.getPos().getY() < EDGE_TP_DELTA.getY();
	}
	
	public boolean touchingLeft(Entity e) {
		return e.getPos().getX() < EDGE_TP_DELTA.getX();
	}
	
	public boolean touchingTop(Entity e) {
		return e.getPos().getY() < EDGE_TP_DELTA.getY();
	}
	
	public void setUniverse(WorldGraph universe) {
		this.universe = universe;
	}
	
	public Playable getUniverse() {
		return universe;
	}
	
	public int getWidth() {
		return getTileLayer().getWidth() * Tile.WIDTH;
	}
	
	public int getHeight() {
		return getTileLayer().getHeight() * Tile.WIDTH;
	}
	
	@Override
	public String toString() {
		return path;
	}
	
	@Deprecated
	public EntityGroup getEntityTree() {
		return getEntityLayer().getEntityTree();
	}
	
	public EntityLayer getEntityLayer() {
		return entityLayer;
	}
	
	public Level getTileLayer() {
		return tileLayer;
	}
	
	@Override
	public Center findCenter() {
		return getEntityTree().getFirst(Center.class);
	}

	@Override
	public WorldGraph getWorldGraph() {
		return universe;
	}
	
}
