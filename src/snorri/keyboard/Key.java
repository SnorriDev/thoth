package snorri.keyboard;

import java.awt.event.KeyEvent;

public enum Key {
	
	W(87, 'w'),
	A(65, 'a'),
	S(83, 's'),
	D(68, 'd'),
	
	ONE(49, '1'),
	TWO(50, '2'),
	THREE(51, '3'),
	FOUR(52, '4'),
	FIVE(53, '5'),
	
	SPACE(32, ' '),
	ESC(27, ' '),
	
	E(69, 'e'), //spawn entity
	DELETE(8, ' '), //delete entity
	
	Q(81, 'q'), //UNUSED
	I(73, 'i'); //UNUSED
	
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
	 * do NOT use this from keyTyped because e.getKeyCode will be empty
	 * @param e
	 * 	key event
	 * @return
	 * 	whether this key is pressed in the event
	 */
	public boolean isPressed(KeyEvent e) {
		return id == e.getKeyCode();
	}
	
	//for changing controls?
	public void set(int c, char ch) {
		id = c;
		this.ch = ch;
	}
	
}
