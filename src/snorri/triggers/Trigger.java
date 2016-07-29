package snorri.triggers;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import net.sourceforge.yamlbeans.YamlReader;
import snorri.entities.Entity;
import snorri.main.Main;

public class Trigger {

	private static final Map<String, Entity> tags = new HashMap<String, Entity>();
	
	private final Queue<Runnable> actions;
	private final Object object;
	
	public enum TriggerType {
	
		START,
		BROADCAST,
		COLLISION,
		DOOR_OPEN;
		
		private List<Trigger> active = new ArrayList<Trigger>();
		
		public void add(Trigger t) {
			active.add(t);
		}
		
		public void remove(Trigger t) {
			active.remove(t);
		}
		
		public void activate(Object object) {
			for (Trigger t : active.toArray(new Trigger[0])) {
				if (t.object.equals(object)) {
					t.exec();
					remove(t);
				}
			}
		}
		
	}
	
	/**
	 * @param action
	 * The action to be run when this trigger is activated
	 * @param object
	 * The object this trigger is is attached to.
	 * For example, for an on collision trigger, this would be the
	 * Entity of interest.
	 */
	public Trigger(Object object, Queue<Runnable> actions) {
		this.actions = actions;
		this.object = object;
	}
	
	/**
	 * @see <code>Trigger(Object, Runnable)</code>
	 */
	public Trigger(String tag, Queue<Runnable> actions) {
		this(getByTag(tag), actions);
	}
		
	public void load(File file) {
		
		try {
			YamlReader reader = new YamlReader(new FileReader(file));
		} catch (FileNotFoundException e) {
			Main.error("trigger file " + file.getName() + " does not exist");
		}
		
	}
	
	public static void setTag(String tag, Entity e) {
		tags.put(tag, e);
	}
	
	public static Entity getByTag(String tag) {
		return tags.get(tag);
	}
	
	/**
	 * Execute the 
	 */
	private void exec() {
		while (!actions.isEmpty()) {
			actions.poll().run();
			//new Thread().start(); if we want to do async
		}
	}
	
}
