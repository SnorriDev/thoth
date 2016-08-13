package snorri.triggers;

import java.util.ArrayList;
import java.util.HashMap;

import snorri.triggers.Trigger.TriggerType;

public class TriggerMap extends HashMap<Trigger.TriggerType, ArrayList<Trigger>> {

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
	
}
