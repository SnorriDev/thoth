package snorri.world;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sourceforge.yamlbeans.YamlWriter;
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
	
	public void createLink(String w2, int type) {
		String w1 = current.toString();
		createLink(w1, w2, type);
	}
	
	public void createLink(String w1, String w2, int type) {
		File f = new File(path);
		createLink(f, w1, w2, type);
	}
	
	/**
	 * @param type integer defining connection type, 0=l-r, 1=top-bot, 2=r-l 3=bot-top
	 */
	public void createLink(File f, String w2, int type) {
		String w1 = current.toString();
		createLink(f, w1, w2, type);
	}
	
	/**
	 * @param type integer defining connection type, 0=l-r, 1=top-bot, 2=r-l 3=bot-top
	 */
	@SuppressWarnings("unchecked")
	public void createLink(File f, String w1, String w2, int type) {
		Map<String, Object> yaml;
		List<Map<String, String>> edges;
		try	{
			yaml = Playable.getConfig(f);
			edges = (List<Map<String, String>>) yaml.get("edges");
			
			Map<String, String> newEdge = new HashMap<String, String>();		
			int type_4 = type % 4;
			switch(type_4) {
				case 0:
					worlds.get(w1).setRightNeighbor(worlds.get(w2));
					worlds.get(w2).setLeftNeighbor(worlds.get(w1));
					newEdge.put("left", w1);
					newEdge.put("right", w2);
					Debug.log("inserting new l-r edge between " + w1 + " & " + w2);
					break;
				case 1:
					worlds.get(w1).setBottomNeighbor(worlds.get(w2));
					worlds.get(w2).setTopNeighbor(worlds.get(w1));
					newEdge.put("top", w1);
					newEdge.put("bottom", w2);
					Debug.log("inserting new top-bot edge between " + w1 + " & " + w2);
					break;
				case 2:
					worlds.get(w1).setLeftNeighbor(worlds.get(w2));
					worlds.get(w2).setRightNeighbor(worlds.get(w1));
					newEdge.put("right", w1);
					newEdge.put("left", w2);
					Debug.log("inserting new r-l edge between " + w1 + " & " + w2);
					break;
				case 3:
					worlds.get(w1).setTopNeighbor(worlds.get(w2));
					worlds.get(w2).setBottomNeighbor(worlds.get(w1));
					newEdge.put("bottom", w1);
					newEdge.put("top", w2);
					Debug.log("inserting new bot-top edge between " + w1 + " & " + w2);
					break;
				default:
					worlds.get(w1).setRightNeighbor(worlds.get(w2));
					worlds.get(w2).setLeftNeighbor(worlds.get(w1));
					newEdge.put("left", w1);
					newEdge.put("right", w2);
					Debug.log("inserting new l-r edge between " + w1 + " & " + w2 + " (BY DEFAULT)");
					break;
			}
			
			edges.add(newEdge);
			yaml.put("edges", edges);
			
			YamlWriter writer = new YamlWriter(new FileWriter(path + "/config.yml"));
	        writer.write(yaml);
	        writer.close();
		}
		catch (YamlException e) {
			Debug.error("invalid yaml", e);
		}
		catch (FileNotFoundException e) {
			Debug.error("invalid yaml?", e);
		}
		catch (IOException e) {
			Debug.error("invalid yaml??", e);
		}
	}
	
	@Override @Deprecated //TODO: should save to config.yml
	public void save(File f, boolean recomputeGraphs) throws IOException {
		Map<String,Object> yaml;
		try	{
			yaml = Playable.getConfig(f);
			
			YamlWriter writer = new YamlWriter(new FileWriter(path + "/config.yml"));
	        writer.write(yaml);
	        writer.close();
		}
		catch (YamlException e) {
			Debug.error("invalid yaml", e);
		}
		
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
