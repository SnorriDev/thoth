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
import snorri.entities.EntityTree;
import snorri.entities.Spawn;
import snorri.entities.Unit;
import snorri.main.Debug;
import snorri.main.FocusedWindow;
import snorri.main.Main;
import snorri.triggers.TriggerMap;

public class World implements Playable, Editable {

	public static final Vector DEFAULT_LEVEL_SIZE = new Vector(13, 8);
	public static final Vector DEFAULT_SPAWN = new Vector(100, 100);
	public static final int UPDATE_RADIUS = 4000;
	private static final Vector EDGE_TP_DELTA = new Vector(Unit.RADIUS_X + 10, Unit.RADIUS_Y + 10);

	private File directory;
	private String name;
	private int width;
	private int height;
	
	private List<Layer> layers;
	private TileLayer tileLayer;
	private EntityLayer entityLayer;

	private TriggerMap triggers;
	
	private WorldGraph universe;
	private World[] neighbors = new World[4];

	
	public World() {
		this(DEFAULT_LEVEL_SIZE.getX(), DEFAULT_LEVEL_SIZE.getY());
	}

	/**
	 * Constructor which creates a blank world with no layers.
	 * 
	 * The static factory methods should be called externally to create a more interesting world.
	 * 
	 * @param width Width of the new world.
	 * @param height Height of the new world.
	 */
	protected World(int width, int height) {
		this.width = width;
		this.height = height;
		layers = new ArrayList<>();
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
	
	/**
	 * Creates a starting world with a TileLayer and EntityLayer.
	 * 
	 * This factory function should be used to create blank worlds externally.
	 * 
	 * @param dims Dimensions of the world to be created (in grid coordinates).
	 */
	public static World createDefaultWorld(Vector dims) {
		World world = new World(dims.getX(), dims.getY());
		world.addLayer(new BackgroundLayer(world, BackgroundLayer.DEFAULT_BACKGROUND));
		world.addLayer(new TileLayer(dims.getX(), dims.getY(), UnifiedTileType.EMPTY));
		world.addLayer(new EntityLayer(world));
		return world;
	}
	
	private void addLayer(Layer layer) {
		layers.add(layer);
		if (layer instanceof EntityLayer) {
			if (entityLayer == null) {
				entityLayer = (EntityLayer) layer;
			} else {
				throw new WorldLayerException("Cannot have two EntityLayers in the same Level.");
			}
		} else if (layer instanceof TileLayer) {
			if (tileLayer == null) {
				tileLayer = (TileLayer) layer;
			} else {
				throw new WorldLayerException("Cannot have two TileLayers in the same Level.");
			}
		}
	}

	public static File wrapLoad() {
		File file = Main.getFileDialog("Select file to load", FileDialog.LOAD);
		return file == null ? null : file;
	}

	public synchronized void update(Entity focus, double d) {
		getEntityLayer().updateAround(this, d, focus);
		
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
		if (entityLayer.add(e)) {
			e.onSpawn(this);
			return true;
		}
		return false;
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
		if (f == null) {
			throw new IllegalArgumentException("Save path for World must be non-null.");
		}
		
		if (f.exists() && !f.isDirectory()) {
			throw new IOException("Tried to save world to non-directory.");
		}

		if (!f.exists()) {
			Debug.logger.info("Creating new world directory...");
			f.mkdir();
		}
		
		Playable.tryCreatingDefaultConfig(f, PlayableType.WORLD);
		
		String path = f.getPath();
		layers.forEach(layer -> {
			if (layer instanceof SavableLayer) {
				SavableLayer savableLayer = (SavableLayer) layer;
				File saveFile = new File(path, savableLayer.getFilename());
				try {
					Debug.logger.info("Saving " + saveFile.getName() + "...");
					savableLayer.save(saveFile, recomputeGraphs);
				} catch (Exception e) {
					Debug.logger.log(java.util.logging.Level.SEVERE, "Could not load " + saveFile.getAbsolutePath() + ".", e);
				}
			}
		});
	}

	@Override @SuppressWarnings("unchecked")
	public void load(File f, Map<String, Object> yaml) throws IOException, YamlException {
		if (f == null) {
			throw new IllegalArgumentException("Trying to load World from null file.");
		}
		if (!f.exists()) {
			throw new FileNotFoundException("No world called " + f.getName() + ".");
		}
		if (!f.isDirectory()) {
			throw new IOException("World file " + f.getName() + " is not a directory");
		}
		
		directory = f;
		name = f.getName();
		
		if (yaml == null) {
			yaml = Playable.getConfig(f, PlayableType.WORLD);
		}
		
		this.layers = new ArrayList<>();
		List<Map<String, Object>> layers = (List<Map<String, Object>>) yaml.get("layers");
		if (layers == null) {
			File configFile = new File(f, "config.yml");
			throw new IllegalArgumentException("No layers specified in " + configFile.getAbsolutePath() + ".");
		}
		layers.forEach(params -> {
			try {
				Layer layer = Layer.fromYAML(this, params);
				addLayer(layer);
			} catch (Exception e) {
				// Having a lambda here seems to force the exception to be caught. This is not
				// the end of the world, but might be something to consider if we refactor it.
				Debug.logger.log(java.util.logging.Level.SEVERE, "Could not read layers in World.", e);
			}
		});

		String outside = (String) yaml.get("outsideTile");
		if (outside != null) {
			getTileLayer().setOutsideTile(new Tile(outside));
		}
		
		triggers = TriggerMap.load((Map<String, Object>) yaml.get("triggers"), this);
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

	public void wrapUpdate(Vector pos, Tile tile) {
		wrapGridUpdate(pos.copy().gridPos_(), tile);
	}
	
	public synchronized void wrapGridUpdate(Vector posGrid, Tile tile) {
		TileLayer tileLayer = getTileLayer();
		Tile oldTile = tileLayer.getTileGrid(posGrid);
		if (oldTile == null) {
			return;
		}
		tileLayer.setTileGrid(posGrid, tile);
	}
	
	/** Returns true if bullets can pass over pos. */
	public boolean canShootOver(Vector pos) {
		return getTileLayer().canShootOver(pos.gridPos());
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
		return name;
	}
	
	public EntityTree getEntityTree() {
		return getEntityLayer().getEntityTree();
	}
	
	public EntityLayer getEntityLayer() {
		return entityLayer;
	}
	
	public TileLayer getTileLayer() {
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
	
	/** Get the directory from which this World was loaded. */
	public File getDirectory() {
		return directory;
	}
	
	public boolean isOccupied(int x, int y) {
		return getTileLayer().isOccupied(x, y);
	}
	
	public boolean isOccupied(Vector v) {
		return isOccupied(v.getX(), v.getY());
	}
}
