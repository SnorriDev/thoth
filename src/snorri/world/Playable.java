package snorri.world;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;

import net.sourceforge.yamlbeans.YamlException;
import net.sourceforge.yamlbeans.YamlReader;
import snorri.entities.Center;
import snorri.entities.Entity;
import snorri.entities.Player;
import snorri.main.Debug;
import snorri.main.Main;

/**
 * This interface is used for game objects that can be played by the player.
 * @author snorri
 *
 */

public interface Playable extends Loadable, Savable {
	
	public enum PlayableType {
		WORLD(World.class),
		WORLD_GRAPH(WorldGraph.class);
		
		private Class<? extends Playable> playableClass;
		
		PlayableType(Class<? extends Playable> playableClass) {
			this.playableClass = playableClass;
		}
		
		public Playable newInstance(File file, Player player) {
			Constructor<? extends Playable> constructor;
			try {
				constructor = playableClass.getConstructor(File.class, Player.class);
				return constructor.newInstance(file, player);
			} catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException
					| InvocationTargetException e) {
				Debug.logger.log(java.util.logging.Level.SEVERE, "Playable of type " + this + " could not be created.",
						e);
				return null;
			}
		}
		
	}
	
	World getCurrentWorld();
	
	public void update(Entity focus, double deltaTime);

	public Player computeFocus();
	
	/** Should return a human-readable name (e.g. filename) **/
	String toString();
	
	public void load(File folder, Map<String, Object> yaml) throws FileNotFoundException, IOException, YamlException;
	
	@Override
	default void load(File folder) throws IOException {
		try {
			load(folder, null);
		} catch (YamlException e) {
			Debug.logger.log(java.util.logging.Level.SEVERE, "Couldn't parse config.yml.", e);
		}
	}
	
	static void tryCreatingDefaultConfig(File folder, PlayableType type) throws FileNotFoundException {
		File config = new File(folder, "config.yml");
		if (type == null || config.exists()) {
			return;
		}
		
		// If the config file doesn't exist, create a default one.
		PrintWriter pw = new PrintWriter(config);
		pw.write("type: " + type.toString() + "\n");
		if (type == PlayableType.WORLD) {
			pw.write("layers:\n");
			pw.write("  - type: BACKGROUND\n");
			pw.write("    path: /textures/backgrounds/splash.png\n");
			pw.write("  - type: TILE\n");
			pw.write("    path: tile.layer\n");
			pw.write("  - type: ENTITY\n");
			pw.write("    path: entity.layer\n");
		}
		pw.close();
	}
	
	/**
	 * Loads config.yml.
	 * @param folder
	 * 	The world folder
	 * @param type
	 * If <code>type</code> is non-null, then this will:
	 * <ul>
	 * 	<li>Create a default config with the specified type if none is found.</li>
	 * 	<li>Throw an error if the type found in a config doesn't match the type argument.</li>
	 * @return The parsed YAML
	 * @throws YamlException
	 */
	@SuppressWarnings("unchecked")
	static Map<String, Object> getConfig(File folder, PlayableType type) throws FileNotFoundException, IOException, YamlException {
		
		File config = new File(folder, "config.yml");
		tryCreatingDefaultConfig(folder, type);
		
		YamlReader reader = Main.getYamlReader(config);
		Map<String, Object> yamlRoot = (Map<String, Object>) reader.read();
		
		PlayableType yamlType = PlayableType.valueOf((String) yamlRoot.get("type"));
		if (type != null && !yamlType.equals(type)) {
			throw new IllegalArgumentException("invalid world type");
		}
		reader.close();
		
		return yamlRoot;
		
	}
	
	static Map<String, Object> getConfig(File folder) throws FileNotFoundException, IOException, YamlException {
		return getConfig(folder, null);
	}
	
	/**
	 * Factory function to load any Playable (world or graph) from a folder.
	 * @param file File to load from.
	 * @return A newly created Playable instance.
	 * @throws IOException
	 */
	static Playable getLoaded(File file, Player p) throws FileNotFoundException, IOException, YamlException {
		Map<String, Object> yaml = getConfig(file);
		PlayableType type = PlayableType.valueOf((String) yaml.get("type"));
		return type.newInstance(file, p);		
	}

	public Center findCenter();
	
}
