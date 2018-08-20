package snorri.keyboard;

import java.util.LinkedList;
import java.util.Queue;

import snorri.world.Vector;

public class KeyStates {
	
	private final boolean[] states;
	private final boolean[] mouseStates;
	private final Queue<Binding> actionQ;
	
	public KeyStates() {
		states = new boolean[256];
		mouseStates = new boolean[100];
		actionQ = new LinkedList<>();
	}
	
	public void set(int id, boolean state) {
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
	
	public Vector getMomentumVector() {
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
	
	public void registerAction(Binding b) {
		actionQ.add(b);
	}
	
	public Binding popAction() {
		return actionQ.poll();
	}
	
}
