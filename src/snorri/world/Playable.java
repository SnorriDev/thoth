package snorri.world;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
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

public interface Playable extends Savable {
	
	World getCurrentWorld();
	
	public void update(Entity focus, double deltaTime);

	public Player computeFocus();
	
	/** Should return a human-readable name (e.g. filename) **/
	String toString();
	
	public void load(File folder, Map<String, Object> yaml) throws FileNotFoundException, IOException, YamlException;
	
	@Override
	default void load(File folder) throws FileNotFoundException, IOException {
		try {
			load(folder, null);
		} catch (YamlException e) {
			Debug.error("couldn't parse config.yml", e);
		}
	}
	
	static void tryCreatingDefaultConfig(File folder, String type) throws FileNotFoundException {
		File config = new File(folder, "config.yml");
		if (type != null && !config.exists()) { //if the config file doesn't exist, create a default one
			PrintWriter pw = new PrintWriter(config);
			pw.write("type: " + type);
			pw.close();
		}
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
	static Map<String, Object> getConfig(File folder, String type) throws FileNotFoundException, IOException, YamlException {
		
		File config = new File(folder, "config.yml");
		tryCreatingDefaultConfig(folder, type);
		
		YamlReader reader = Main.getYamlReader(config);
		Map<String, Object> yamlRoot = (Map<String, Object>) reader.read();
		
		if (type != null && !yamlRoot.get("type").equals(type)) {
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
	 * @param file
	 * @return
	 * @throws IOException
	 */
	static Playable getLoaded(File file, Player p) throws FileNotFoundException, IOException, YamlException {
		
		//TODO require players to select config.yml in order to load worlds
//		if (!file.getName().endsWith(".yml")) {
//			throw new IOException();
//		}
		
		Map<String, Object> yaml = getConfig(file);
		switch ((String) yaml.get("type")) {
		case "world":
			return new World(file, p);
		case "graph":
			return new WorldGraph(file, p);
		default:
			return null;
		}
		
	}

	public Center findCenter();
	
}
