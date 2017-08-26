package snorri.world;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sourceforge.yamlbeans.YamlException;
import snorri.entities.Entity;
import snorri.entities.Player;
import snorri.main.Debug;

/**
 * Class that holds multiple worlds.
 * @author lambdaviking
 * Some of the details for how this works are as follows:
 * <ul>
 * 	<li>Each world has four slots for neighbors, which are null be default. The world graph assigns values to these slots.</li>
 *  <li>Neighbors can be reached by touching the edge of the screen</li>
 * </ul>
 */

public class WorldGraph implements Playable {
	
	private String path;
	
	private Map<String, World> worlds;
	private World current;
	
	private Player player;
	
	public WorldGraph(File folder, Player p) throws FileNotFoundException, IOException, YamlException {
		load(folder);
		player = p;
	}
	
	@Override @SuppressWarnings("unchecked")
	public void load(File folder, Map<String, Object> yaml) throws FileNotFoundException, IOException, YamlException {
		
		path = folder.getName();
				
		if (yaml == null) { // if config.yml hasn't already been parsed, parse it
			yaml = Playable.getConfig(folder, "graph");
		}
		
		File[] files = folder.listFiles(File::isDirectory);
		worlds = new HashMap<String, World>();
		for (File file : files) {
			worlds.put(file.getName(), new World(file));
		}
		current = worlds.get(yaml.get("root"));
		current.spawnPlayer(player);
				
		List<Map<String, String>> edges = (List<Map<String, String>>) yaml.get("edges");
		Debug.log("loading " + edges.size() + " edges");
		for (Map<String, String> edge : edges) {
			String w1, w2;
			if ((w1 = edge.get("left")) != null && (w2 = edge.get("right")) != null) {
				worlds.get(w1).setRightNeighbor(worlds.get(w2));
				worlds.get(w2).setLeftNeighbor(worlds.get(w1));
			} else if ((w1 = edge.get("top")) != null && (w2 = edge.get("bottom")) != null) {
				worlds.get(w1).setBottomNeighbor(worlds.get(w2));
				worlds.get(w2).setTopNeighbor(worlds.get(w1));
			}
		}
		
	}

	@Override @Deprecated
	public void save(File f, boolean recomputeGraphs) throws IOException {
		for (World world : worlds.values()) {
			world.save((File) null);
		}
	}

	@Override
	public World getCurrentWorld() {
		return current;
	}

	@Override
	public Player computeFocus() {
		return getCurrentWorld().computeFocus();
	}
	
	@Override
	public void update(Entity focus, double deltaTime) {
		getCurrentWorld().update(focus, deltaTime);
	}
	
	@Override
	public String getFilePath() {
		return path;
	}
	
	public void crossInto(World world, Vector pos) {
		current = world;
		player.setPos(pos);
	}
	
	public void crossInto(World world, int x, int y) {
		crossInto(world, new Vector(x, y));
	}

}
