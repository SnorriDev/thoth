package snorri.world;

import java.awt.Graphics2D;
import java.io.File;
import java.io.IOException;
import java.util.Map;

import snorri.entities.Entity;
import snorri.entities.EntityTree;
import snorri.main.Debug;
import snorri.main.FocusedWindow;

public class EntityLayer implements SavableLayer {
	
	private EntityTree entityTree;
	private Vector dimensions;
	
	public EntityLayer(EntityTree entityTree, Vector dimensions) {
		this.entityTree = entityTree;
		this.dimensions = dimensions;
	}
	
	public EntityLayer(World world) {
		this(EntityTree.coverLevel(world.getTileLayer()), world.getDimensions());
	} 
	
	public static EntityLayer fromYAML(World world, Map<String, Object> params) throws IOException {
		EntityLayer entityLayer = new EntityLayer(world);
		File file = new File(world.getDirectory(), (String) params.get("path"));
		Debug.logger.info("Loading " + file + "...");
		entityLayer.entityTree.loadEntities(file);
		return entityLayer;
	}
	
	/** The same as fromYAML, but wrapped to catch exceptions.
	 * 
	 * This is useful for passing the function into an enum in Layer.LayerType.
	 * 
	 */
	public static EntityLayer wrappedFromYAML(World world, Map<String, Object> params) {
		try {
			return fromYAML(world, params);
		} catch (IOException e) {
			Debug.logger.log(java.util.logging.Level.SEVERE, "Could not load load EntityLayer from YAML.", e);
			return null;
		}
	}
	
	public EntityLayer copy() {
		EntityLayer entityLayer = new EntityLayer(this.entityTree, this.dimensions);
		entityTree.mapOverEntities(entity -> {
			entityLayer.entityTree.insert(entity);
		});
		return entityLayer;
	}
	
	public boolean add(Entity entity) {
		return entityTree.insert(entity);
	}
	
	public boolean remove(Entity entity) {
		return entityTree.delete(entity);
	}
	
	public void updateAround(World world, double deltaTime, Entity focus) {
		entityTree.updateAround(world, deltaTime, focus);
	}
	
	/** Should try to use the public API wherever possible. */
	public EntityTree getEntityTree() {
		return entityTree;
	}

	@Override
	public void render(FocusedWindow<?> levelEditor, Graphics2D gr, double deltaTime, boolean b) {
		entityTree.renderAround(levelEditor, gr, deltaTime);
	}

	@Override
	public int getHeight() {
		return dimensions.getY();
	}

	@Override
	public int getWidth() {
		return dimensions.getX();
	}

	@Override
	public boolean canShootOver(Vector position) {
		return true;
	}

	@Override
	public String getFilename() {
		return "entity.layer";
	}

	@Override
	public void save(File f, boolean recomputeGraphs) throws IOException {
		entityTree.saveEntities(f);
	}

	@Override
	public EntityLayer getTransposed() {
		EntityLayer entityLayer = copy();
		entityLayer.entityTree.mapOverEntities(entity -> {
			Entity newEntity = entity.copy();
			newEntity.setPos(entity.getPos().getInverted());
			entityLayer.entityTree.insert(newEntity);
		});
		return entityLayer;
	}

	@Override
	public EntityLayer getXReflected() {
		EntityLayer entityLayer = copy();
		entityLayer.entityTree.mapOverEntities(entity -> {
			Entity newEntity = entity.copy();
			newEntity.setPos(entity.getPos().getXReflected(dimensions));
			entityLayer.entityTree.insert(newEntity);
		});
		return entityLayer;
	}

	/** Resizing doesn't affect the position of entities. */
	@Override
	public EntityLayer getResized(int newWidth, int newHeight) {
		return copy();
	}
	
}
