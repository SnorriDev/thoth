package snorri.world;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sourceforge.yamlbeans.YamlException;
import snorri.entities.Center;
import snorri.entities.Entity;
import snorri.entities.Player;
import snorri.main.Debug;
import snorri.main.FocusedWindow;
import snorri.main.Main;

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
		player = p; // arbitrary player constructed in case world is playerless
		load(folder);
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
			World world = new World(file, player);
			world.setUniverse(this);
			worlds.put(file.getName(), world);
		}
		setCurrentWorld(worlds.get(yaml.get("root")));
				
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
		
		// after we have passed in the rando player, set it to the one we are actually using
		player = computeFocus();
		
	}

	@Override @Deprecated
	public void save(File f, boolean recomputeGraphs) throws IOException {
		for (World world : worlds.values()) {
			world.save((File) null);
		}
	}

	private void setCurrentWorld(World world) {
		current = world;
		if (Main.getWindow() instanceof FocusedWindow) {
			((FocusedWindow<?>) Main.getWindow()).setCustomCenter(findCenter());
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
	public String toString() {
		return path;
	}
	
	public void crossInto(World world, Vector pos) {
		
		// first remove any extraneous player in new world
		world.delete(world.getEntityTree().getFirst(Player.class));
		
		// move the player into the new world
		getCurrentWorld().delete(player);
		setCurrentWorld(world);
		player.setPos(pos);
		getCurrentWorld().add(player);
		
		// TODO save string names for worlds
		
		if (Debug.changeWorldEventsLogged()) {
			Debug.log("entered world " + world + ":");
			Debug.log(" * right neighbor: " + world.getRightNeighbor());
			Debug.log(" * left neighbor: " + world.getLeftNeighbor());
			Debug.log(" * top neighbor: " + world.getTopNeighbor());
			Debug.log(" * bottom neighbor: " + world.getBottomNeighbor());
		}
		
	}
	
	public void crossInto(World world, int x, int y) {
		crossInto(world, new Vector(x, y));
	}
	
	public Player getPlayer() {
		return player;
	}
	
	@Override
	public Center findCenter() {
		return getCurrentWorld().findCenter();
	}

}
