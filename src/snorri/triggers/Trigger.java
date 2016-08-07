package snorri.triggers;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Queue;

import net.sourceforge.yamlbeans.YamlException;
import net.sourceforge.yamlbeans.YamlReader;
import snorri.entities.Entity;
import snorri.main.Main;
import snorri.world.World;

public class Trigger {

	private static final Map<String, Entity> tags = new HashMap<>();
	private static final List<Trigger> triggers = new ArrayList<>();
	
	private final Queue<Runnable> actions;
	private final String name;
	private final HashMap<TriggerType, Object> objects;
	
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
				if (t.getObject(this).equals(object)) {
					t.exec();
					remove(t);
				}
			}
		}
		
	}
	
	private Entry<String, Map<String, Object>> getFirstEntry(Map<String, Map<String, Object>> map) {
		return map.entrySet().iterator().next();
	}
	
	/**
	 * @param action
	 * The action to be run when this trigger is activated
	 * @param object
	 * The object this trigger is is attached to.
	 * For example, for an on collision trigger, this would be the
	 * Entity of interest.
	 */
	public Trigger(World world, String name, Map<String, List<Map<String, Map<String, Object>>>> data) {
		
		this.name = name;
		
		actions = new LinkedList<>();
		for (Map<String, Map<String, Object>> action : data.get("actions")) {
			Entry<String, Map<String, Object>> e = getFirstEntry(action);
			actions.add(Action.getNew(e.getKey(), world, e.getValue()));
		}
		
		objects = new HashMap<>();
		for (Map<String, Map<String, Object>> event : data.get("events")) {
			Entry<String, Map<String, Object>> e = getFirstEntry(event);
			TriggerType type = TriggerType.valueOf(e.getKey());
			if (type == null) {
				Main.error("unknown event type " + e.getKey());
				return;
			}
			objects.put(type, e.getValue().get("object"));
			type.add(this);
		}

	}
	
	@SuppressWarnings("unchecked")
	public static void load(File triggerFile, World world) {
		
		try {
			YamlReader reader = Main.getYamlReader(triggerFile);
			Map<String, Object> rawTriggers = (Map<String, Object>) reader.read();
			if (rawTriggers == null) {
				return;
			}
			for (Entry<String, Object> rawTrigger : rawTriggers.entrySet()) {
				String name = rawTrigger.getKey();
				Map<String, List<Map<String, Map<String, Object>>>> data = (Map<String, List<Map<String, Map<String, Object>>>>) rawTrigger.getValue();
				triggers.add(new Trigger(world, name, data));
			}
		} catch (FileNotFoundException e) {
			Main.error("could not find trigger file " + triggerFile);
		} catch (YamlException e) {
			Main.error("could not parse YAML");
			e.printStackTrace();
		}
		
		Main.log(triggers);
		
	}
	
	public Object getObject(TriggerType type) {
		return objects.get(type);
	}
	
	public String getName() {
		return name;
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
