package snorri.keyboard;

import snorri.main.Main;
import snorri.world.Vector;

public class KeyStates {

	//TODO switch implementation to KeyBinding from just the event stuff
	//should bind WASD, arrows, and maybe more this way
	//might not be possible with arbitrary keys? not really sure
	
	private boolean[] states;
	private boolean[] mouseStates;
	
	public KeyStates() {
		states = new boolean[256];
		mouseStates = new boolean[100];
	}
	
	public void set(int id, boolean state) {
		Main.log("set " + id + " " + state);
		states[id] = state;
	}
	
	public void setMouseButton(int num, boolean state) {
		mouseStates[num] = state;
	}
	
	public boolean get(final Binding key) {
		if (key instanceof Key) {
			return get(((Key) key).getCode());
		} else {
			return getMouseButton(((MouseButton) key).getNum());
		}
	}
	
	public boolean get(final int keyCode) {
		return states[keyCode];
	}
	
	public boolean getMouseButton(final int num) {
		return mouseStates[num];
	}
	
	private int getInt(final Key key) {
		return get(key) ? 1 : 0;
	}
	
	public Vector getMovementVector() {
		return new Vector(getInt(Key.D) - getInt(Key.A), getInt(Key.S) - getInt(Key.W));
	}
	
	public void purge() {
		for (int i = 0; i < states.length; i ++) {
			states[i] = false;
		}
		for (int i = 0; i < mouseStates.length; i++) {
			mouseStates[i] = false;
		}
	}
	
}
