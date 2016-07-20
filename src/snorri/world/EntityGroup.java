package snorri.world;

import java.awt.Graphics;
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

public interface EntityGroup {

	public boolean insert(Entity e);
	
	public boolean delete(Entity e);

	public Entity getFirstCollision(Entity entity, boolean hitAll);
	
	default Entity getFirstCollision(Entity e) {
		return getFirstCollision(e, false);
	}
	
	public List<Entity> getAllCollisions(Entity e, boolean hitAll);
	
	default List<Entity> getAllCollisions(Entity e) {
		return getAllCollisions(e, false);
	}

	public void updateAround(World world, double d, Entity focus);

	public void renderAround(FocusedWindow g, Graphics gr);

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
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	default void loadEntities(File file) throws FileNotFoundException, IOException {
		ObjectInputStream in = new ObjectInputStream(new FileInputStream(file));
		while (true) {
			try {
				insert((Entity) in.readObject());
			} catch (Exception e) {
				break;
			}
		}
		in.close();
	}

	public List<Entity> getAllEntities();

	public void move(Entity obj, Vector newPos);
	
}
