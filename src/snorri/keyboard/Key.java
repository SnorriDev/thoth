package snorri.keyboard;

import java.awt.event.KeyEvent;

import snorri.main.FocusedWindow;
import snorri.main.Main;

public enum Key implements Binding {
	
	W(87, 'w'),
	A(65, 'a'),
	S(83, 's'),
	D(68, 'd'),
	
	SHOOT_LEFT(37, ' '),
	SHOOT_UP(38, ' '),
	SHOOT_RIGHT(39, ' '),
	SHOOT_DOWN(40, ' '),
	
	ONE(49, '1'),
	TWO(50, '2'),
	THREE(51, '3'),
	FOUR(52, '4'),
	FIVE(53, '5'),
	
	SPACE(32, ' '),
	ESC(27, ' '),
	ENTER(10, ' '),
	
	E(69, 'e'), //spawn entity
	DELETE(8, ' '), //delete entity
	T(84, 't'), //edit entity tag
	
	Q(81, 'q'), //speed key
	I(73, 'i'), //UNUSED
	P(80, 'p'), //pick key
	M(77, 'm'), //WALL MODE
	N(78, 'n'); //CREATE WALL
	
	private int id;
	private char ch;
	
	Key(int c, char ch) {
		id = c;
		this.ch = ch;
		//can maybe pull char from keycode enum
	}
	
	public int getCode() {
		return id;
	}
	
	public char getChar() {
		return ch;
	}
	
	/**
	 * Returns whether a key is pressed in a given <code>KeyEvent</code>
	 * Do <b>not</b> use this from <code>keyTyped</code> because <code>e.getKeyCode</code> will be empty.
	 * @param e
	 * 	key event
	 * @return
	 * 	Whether this key is pressed with respect to the <code>KeyEvent</code>
	 */
	public boolean isPressed(KeyEvent e) {
		return id == e.getKeyCode();
	}
	
	public boolean isPressed(KeyStates states) {
		return states.get(this);
	}
	
	/**
	 * @return
	 * 	Whether this key is pressed with respect to the current window
	 */
	public boolean isPressed() {
		if (!(Main.getWindow() instanceof FocusedWindow)) {
			return false;
		}
		return isPressed(((FocusedWindow<?>) Main.getWindow()).getKeyStates());
	}
	
	//for changing controls?
	public void set(int c, char ch) {
		id = c;
		this.ch = ch;
	}
		
}
