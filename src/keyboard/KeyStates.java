package keyboard;

import snorri.world.Position;

public class KeyStates {

	private boolean[] states;
	
	public KeyStates() {
		states = new boolean[256];
	}
	
	public void set(int id, boolean state) {
		states[id] = state;
	}
	
	public boolean get(final Key key) {
		return states[key.getCode()];
	}
	
	public boolean get(final int keyCode) {
		return states[keyCode];
	}
	
	private int getInt(final Key key) {
		return get(key) ? 1 : 0;
	}
	
	public Position getMovementVector() {
		return new Position(getInt(Key.D) - getInt(Key.A), getInt(Key.S) - getInt(Key.W));
	}
	
}
