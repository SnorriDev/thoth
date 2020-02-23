package snorri.main;

import java.util.HashMap;
import java.util.logging.Level;

import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JTextField;

import snorri.inventory.Droppable;

public class DialogMap extends HashMap<String, JComponent> {

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

	public void put(String key, String defaultValue) {
		put(key, new JTextField(defaultValue));
	}
	
	public void putSelection(String key, final String[] options) {
		JComboBox<String> box = new JComboBox<>(options);
		box.setEditable(true);
		put(key, box);
	}
	
	@SuppressWarnings("unchecked")
	public String getText(String key) {
		JComponent component = get(key);
		if (component instanceof JTextField) {
			return ((JTextField) component).getText();
		} else if (component instanceof JComboBox) {
			JComboBox<String> box = (JComboBox<String>) component;
			return (String) box.getSelectedItem();
		} else {
			Debug.logger.severe("Unknown component type added to DialogMap.");
			return null;
		}
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
