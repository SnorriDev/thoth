package snorri.triggers;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Queue;

import snorri.entities.Entity;
import snorri.main.Debug;
import snorri.world.World;

public class Trigger {

	private static final Map<String, Entity> tags = new HashMap<>();
	
	private final Queue<Runnable> runnableActions;
	private final String name;
	private final World world;
	private final HashMap<TriggerType, Object> objects;
			
	private Entry<String, Map<String, Object>> getFirstEntry(Map<String, Map<String, Object>> map) {
		return map.entrySet().iterator().next();
	}
	
	/**
	 * @param triggers 
	 * @param action
	 * The action to be run when this trigger is activated
	 * @param object
	 * The object this trigger is is attached to.
	 * For example, for an on collision trigger, this would be the
	 * Entity of interest.
	 */
	public Trigger(World world, String name, Map<String, List<Map<String, Map<String, Object>>>> data, TriggerMap triggers) {
		
		this.name = name;
		this.world = world;
		
		runnableActions = new LinkedList<>();
		for (Map<String, Map<String, Object>> action : data.get("actions")) {
			Entry<String, Map<String, Object>> e = getFirstEntry(action);
			runnableActions.add(Action.getRunnable(e.getKey(), world, e.getValue()));
		}
		
		objects = new HashMap<>();
		if (data.get("events") != null) {
			for (Map<String, Map<String, Object>> event : data.get("events")) {
				Entry<String, Map<String, Object>> e = getFirstEntry(event);
				TriggerType type = TriggerType.valueOf(e.getKey());
				if (type == null) {
					Debug.logger.warning("Unknown event type " + e.getKey() + ".");
					return;
				}
				if (type == TriggerType.BROADCAST) {
					Debug.logger.warning("Ignoring BROADCAST in events list.");
					continue;
				}
				objects.put(type, e.getValue().get("object"));
				triggers.add(type, this);
			}
		}
		objects.put(TriggerType.BROADCAST, name);
		triggers.add(TriggerType.BROADCAST, this);
	}
	

	public Object getObject(TriggerType type) {
		return objects.get(type);
	}
	
	public String getName() {
		return name;
	}
	
	public World getWorld() {
		return world;
	}
	
	public static void setTag(String tag, Entity e) {
		tags.put(tag, e);
	}
	
	public static Entity getByTag(String tag) {
		return tags.get(tag);
	}
	
	/** Execute the action queue. */
	public void exec() {
		Debug.logger.info("Firing trigger " + name + "...");
		while (!runnableActions.isEmpty()) {
			new Thread(runnableActions.poll()).start();
		}
	}
	
}
