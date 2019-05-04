package snorri.keyboard;

import snorri.world.Vector;

public class KeyStates {
	
	private static final int NUM_KEYS = 256;
	private static final int NUM_MOUSE_BUTTONS = 50;
	
	private final boolean[] states;
	private final boolean[] mouseStates;
	/** Horizontal movement input for the player. */
	private int momentum;
	
	public KeyStates() {
		states = new boolean[NUM_KEYS];
		mouseStates = new boolean[NUM_MOUSE_BUTTONS];
		momentum = 0;
	}
	
	public void set(int id, boolean state) {
		states[id] = state;
		setMomentum(id, state);
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
	
	private int getInt(final int keyCode) {
		return get(keyCode) ? 1 : 0;
	}
	
	/** Slightly complex logic to set the momentum vector.
	 * 
	 * Basically, the idea is that pressing D while holding A should reset momentum to D rather than cancelling it out.
	 * 
	 * @param keyId Key that has been changed.
	 * @param keyState Whether this key has been pressed or released.
	 */
	private void setMomentum(final int keyId, final boolean keyState) {
		if (keyId == Key.D.getCode()) {
			if (keyState) {
				momentum = 1;
			} else if (momentum > 0) {
				momentum = getInt(Key.A.getCode());
			}
		} else if (keyId == Key.A.getCode()) {
			if (keyState) {
				momentum = -1;
			} else if (momentum < 0) {
				momentum = getInt(Key.D.getCode());
			}
		}
	}
	
	public Vector getMomentumVector() {
		return new Vector(momentum, 0);
	}
	
	public void purge() {
		for (int i = 0; i < states.length; i ++) {
			states[i] = false;
		}
		for (int i = 0; i < mouseStates.length; i++) {
			mouseStates[i] = false;
		}
		momentum = 0;
	}
	
}
