package snorri.keyboard;

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
	
	Q(81, 'q');
	
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
	
	//for changing controls?
	public void set(int c, char ch) {
		id = c;
		this.ch = ch;
	}
	
}
