package snorri.main;

import java.util.HashMap;

import javax.swing.JTextField;

import snorri.inventory.Droppable;
import snorri.pathfinding.Team;
import snorri.world.World;

public class DialogMap extends HashMap<String, JTextField> {

	/**
	 * used for easily creating custom input windows in the level editor
	 */
	private static final long serialVersionUID = 1L;
	
	public DialogMap() {
		super();
	}
	
	public DialogMap(HashMap<String, String> raw) {
		this();
		for (String key : raw.keySet()) {
			put(key, raw.get(key));
		}
	}

	public void put(String key, String value) {
		put(key, new JTextField(value));
	}
	
	public String getText(String key) {
		return get(key).getText();
	}
	
	public double getDouble(String key) {
		return Util.getDouble(getText(key));
	}

	public int getInteger(String key) {
		return Util.getInteger(getText(key));
	}
	
	public Class<?> getClass(String key) {
		try {
			return Class.forName(getText(key));
		} catch (ClassNotFoundException e) {
			Main.error("coud not load class " + key);
			return null;
		}
	}

	public Team getTeam(String key) {
		World world = ((FocusedWindow) Main.getWindow()).getWorld();
		return Team.getByName(key, world);
	}

	public Droppable getDroppable(String key) {
		return Droppable.fromString(key);
	}

}
