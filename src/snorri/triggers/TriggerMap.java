package snorri.triggers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import snorri.main.Debug;
import snorri.world.World;

public class TriggerMap extends HashMap<TriggerType, List<Trigger>> {

	private static final long serialVersionUID = 1L;
	
	private boolean loaded = false;

	public TriggerMap() {
		for (TriggerType type : TriggerType.values()) {
			put(type, new ArrayList<>());
		}
	}
	
	public void setLoaded() {
		loaded = true;
	}
	
	public boolean isLoaded() {
		return loaded;
	}
	
	public void add(TriggerType type, Trigger t) {
		get(type).add(t);
	}

	public void activate(TriggerType type, Object object) {
		for (Trigger t : get(type).toArray(new Trigger[0])) {
			if (t.getObject(type).equals(object)) {
				t.exec();
				get(type).remove(t);
			}
		}
	}
	
	public boolean contains(TriggerType type, Object object) {
		for (Trigger t : get(type)) {
			if (t.getObject(type) == object) {
				return true;
			}
		}
		return false;
	}

	@SuppressWarnings("unchecked")
	public static TriggerMap load(Map<String, Object> rawTriggers, World world) {
		TriggerMap triggers = new TriggerMap();
		
		if (rawTriggers == null) {
			return triggers;
		}
		for (Entry<String, Object> rawTrigger : rawTriggers.entrySet()) {
			String name = rawTrigger.getKey();
			Map<String, List<Map<String, Map<String, Object>>>> data = (Map<String, List<Map<String, Map<String, Object>>>>) rawTrigger.getValue();
			new Trigger(world, name, data, triggers);
		}
		
		Debug.logger.info(rawTriggers.size() + " triggers loaded.");
			
		triggers.setLoaded();
		
		return triggers;
	}
	
}
