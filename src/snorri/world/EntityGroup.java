package snorri.world;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;

import snorri.entities.Entity;
import snorri.main.FocusedWindow;
import snorri.main.LevelEditor;
import snorri.main.Main;
import snorri.pathfinding.PathGraph;
import snorri.pathfinding.Pathfinding;
import snorri.triggers.Trigger;

public interface EntityGroup {
	
	default boolean insert(Entity e) {
		if (e.getTag() != null) {
			Trigger.setTag(e.getTag(), e);
		}
		return true;
	}
	
	public boolean delete(Entity e);

	public Entity getFirstCollision(Entity entity, boolean hitAll);
	
	default Entity getFirstCollision(Entity e) {
		return getFirstCollision(e, false);
	}
	
	public void mapOverCollisions(Entity e, boolean hitAll, Executable<Entity> exec);
	
	default void mapOverCollisions(Entity e, Executable<Entity> exec) {
		mapOverCollisions(e, false, exec);
	}
	
	@Deprecated
	public List<Entity> getAllCollisions(Entity e, boolean hitAll);
	
	@Deprecated
	default List<Entity> getAllCollisions(Entity e) {
		return getAllCollisions(e, false);
	}

	//TODO can probably rewrite these methods using the map functionality
	public void updateAround(World world, double d, Entity focus);

	public void renderAround(FocusedWindow<?> g, Graphics gr, double deltaTime);

	/**
	 * This method does not just search immediate children, but all entities which are transitively children of the root EntityGroup
	 * @return a randomly selected entity with all entities having equal probability
	 */
	default Entity getRandomEntity() {
		List<Entity> all = getAllEntities();
		return all.get((int) (Math.random() * all.size()));
	}
	
	default void saveEntities(File file) throws IOException {
		ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(file));
		for (Entity e : getAllEntities()) {
			out.writeObject(e);
		}
		out.close();
	}
	
	/**
	 * Add all entities stored in a file to this EntityGroup.
	 * @param file
	 * file to read
	 * @param level 
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	default void loadEntities(File file) throws FileNotFoundException, IOException {
		
		if (!file.exists()) {
			return;
		}
		
		ObjectInputStream in = new ObjectInputStream(new FileInputStream(file));
		while (true) {
			try {
				Entity e = (Entity) in.readObject();
				insert(e);
			} catch (EOFException | ClassNotFoundException e) {
				break;
			}
		}
		in.close();
	}

	@Deprecated
	public List<Entity> getAllEntities();
	
	public void mapOverEntities(Executable<Entity> exec);

	public void move(Entity obj, Vector newPos);

	public Entity getFirstCollisionOtherThan(Entity e, Entity other);

	public Entity getFirstCollision(Rectangle rectangle, boolean hitAll);
	
	default boolean insert(Entity e, Pathfinding pathfinding) {
		boolean result = insert(e);
		if (e.isStaticObject() && !(Main.getWindow() instanceof LevelEditor)) {
			pathfinding.addEntity(e);
		}
		return result;
	}
	
	@Deprecated
	default Entity insertAndReturn(Entity e, Pathfinding pathfinding) {
		if (e.isStaticObject() && !(Main.getWindow() instanceof LevelEditor)) {
			return pathfinding.addEntityAndReturn(e);
		}
		else {
			return null;
		}
	}
	
	default boolean delete(Entity e, PathGraph graph) {
		boolean result = delete(e);
		if (e.isStaticObject() && !(Main.getWindow() instanceof LevelEditor)) {
			graph.removeEntity(e);
		}
		return result;
	}

	default Entity getFirstCollision(Vector mousePos) {
		return getFirstCollision(new Entity(mousePos));
	}

	/**
	 * P is unrestricted to subclasses of entity so that it is also possible to search for interfaces.
	 * @param checker
	 * 	The entity whose collider to use
	 * @param class1
	 * 	The class to check for
	 * @param hitAll
	 * 	Whether or not to ignore entities that have been flagged as collision-ignorable
	 * @return
	 * 	The first entity found, or null if no such entity exists
	 */
	public <P> P getFirstCollision(Entity checker, boolean hitAll, Class<P> class1);
	
	/**
	 * P is unrestricted to subclasses of entity so that it is also possible to search for interfaces.
	 * @param checker
	 * 	The entity whose collider to use
	 * @param class1
	 * 	The class to check for
	 * @return
	 * 	The first entity found, or null if no such entity exists
	 */
	default <P> P getFirstCollision(Entity checker, Class<P> class1) {
		return getFirstCollision(checker, false, class1);
	}
	
	/**
	 * P is unrestricted to subclasses of entity so that it is also possible to search for interfaces.
	 * @param class1
	 * 	The class to check for.
	 * @return
	 * 	The first entity found, or null if no such entity exists
	 */
	public <P> P getFirst(Class<P> class1);
	
}
