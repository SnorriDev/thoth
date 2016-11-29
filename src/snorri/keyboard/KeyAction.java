package snorri.keyboard;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

public class KeyAction extends AbstractAction {
	
	private static final long serialVersionUID = 1L;
	private final int i;
	private final KeyStates states;
	private final boolean pressed;

	public KeyAction(int i, KeyStates states, boolean pressed) {
		this.i = i;
		this.states = states;
		this.pressed = pressed;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		states.set(i, pressed);
	}

}
