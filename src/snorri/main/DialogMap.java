package snorri.main;

import java.util.HashMap;
import java.util.logging.Level;

import javax.swing.JTextField;

import snorri.inventory.Droppable;

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
			Debug.logger.log(Level.SEVERE, key + " is not a class", e);
			return null;
		}
	}

	public Droppable getDroppable(String key) {
		return Droppable.fromString(key);
	}

}
