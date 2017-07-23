package snorri.terrain;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Queue;

import snorri.entities.Entity;
import snorri.main.Debug;
import snorri.world.Editable;
import snorri.world.Level;
import snorri.world.Vector;

/**
 * Edges in structures can be overridden.
 */

public class Structure {

	protected static final List<String> DEFAULT_CHILDREN;
	
	static {
		DEFAULT_CHILDREN = new ArrayList<String>();
		DEFAULT_CHILDREN.add("mainRoom");
		DEFAULT_CHILDREN.add("burialChamber");
		DEFAULT_CHILDREN.add("bigMainRoom");
		DEFAULT_CHILDREN.add("treasury");
		//DEFAULT_CHILDREN.add("junction");
//		burial chamber
//		annex
//		treasury
	}
	
	private Editable template;
	public List<Vector> doors;
	public List<Vector> spawns;
	public Vector start;
	public List<String> children;
	
	public List<Vector> drops;
	
	/**
	 * Constructor which is called by YAML parser.
	 * This sets default values for each field.
	 */
	public Structure() {
		doors = new ArrayList<>();
		spawns = new ArrayList<>();
		children = DEFAULT_CHILDREN;
	}
	
	public boolean isSpawn() {
		return start != null;
	}
	
	public void addChildren(List<Structure> c) {
		for (String key : children) {
			c.add(DungeonGen.get(key));
		}
	}
	
	public Structure getTransposed() {
		
		Structure newStruct = new Structure();
		
		newStruct.setTemplate(template.getTransposed()); //TODO change positions of entities/add this method to Editable
		for (Vector door : doors) {
			newStruct.doors.add(door.getInverted());
		}
		for (Vector spawn : spawns) {
			newStruct.doors.add(spawn.getInverted());
		}
		newStruct.start = (start == null) ? null : start.getInverted();
		
		return newStruct;
		
	}
	
	public Structure getXReflected() {
		
		Structure newStruct = new Structure();
		
		for (Vector door : doors) {
			newStruct.doors.add(door.getXReflected(getLevel().getDimensions()));
		}
		for (Vector spawn : spawns) {
			newStruct.doors.add(spawn.getXReflected(getLevel().getDimensions()));
		}
		newStruct.start = (start == null) ? null : start.getXReflected(getLevel().getDimensions());
		newStruct.setTemplate(template.getXReflected());
		
		return newStruct;
		
	}
	
	public void setTemplate(Editable l) {
		template = l;
	}
	
	public Level getLevel() {
		return template.getLevel();
	}
	
	/**
	 * Get a list of doors which aren't the entrance we came in from.
	 * @param pos
	 * The position of the structure in the world
	 * @param entrance
	 * The position of the entrance we came into
	 */
	public List<Vector> getExits(Vector pos, Vector entrance) {
		List<Vector> exits = new ArrayList<>();
		for (Vector door : doors) {
			if (!door.equals(entrance)) {
				exits.add(pos.copy().add(door));
			}
		}
		Collections.shuffle(exits);
		return exits;
	}
	
	public List<Vector> getStarts(Vector exit) {
		List<Vector> starts = new ArrayList<>();
		if (isSpawn()) {
			starts.add(exit);
			return starts;
		}
		for (Vector door : doors) {
			starts.add(exit.copy().sub(door));
		}
		Collections.shuffle(starts);
		return starts;
	}
	
	/**
	 * Check if we are able to draw the structure on a level given the filled regions that already exist.
	 * If we can, filledRegions will be updated, and the level wil be modified.
	 * @return whether or not the structure was drawn
	 */
	public boolean drawAt(Level l, List<Rectangle> filledRegions, Queue<Entity> spawnQ, Vector pos) {
		
		Vector levelDim = l.getDimensions();
		Vector dim = getLevel().getDimensions();
		
		if (pos == null) {
			Debug.warning("tried to spawn room at null");
			return false;
		}
		
		if (pos.getX() < 0 || pos.getY() < 0 || levelDim.getX() <= pos.getX() + dim.getX()
			|| levelDim.getY() <= pos.getY() + dim.getY()) {
			return false;
		}
		
		Rectangle newRegion = new Rectangle(pos.getX() + 1, pos.getY() + 1, dim.getX() - 1, dim.getY() - 1);
		for (Rectangle region : filledRegions) {
			if (newRegion.intersects(region)) {
				return false;
			}
		}
		
		//actually do stuff!
		filledRegions.add(newRegion);
		
		for (int x = 0; x < dim.getX(); x++) {
			for (int y = 0; y < dim.getY(); y++) {
				l.setTileGrid(pos.getX() + x, pos.getY() + y, getLevel().getNewTileGrid(x, y));
			}
		}
		
		Vector global = pos.copy().toGlobalPos();
		for (Entity e : template.getEntities()) {
			Entity e2 = e.copy();
			e2.getPos().add(global);
			spawnQ.add(e2);
		}
		
		return true;
		
	}
	
}
