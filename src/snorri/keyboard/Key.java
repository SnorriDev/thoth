package snorri.keyboard;

public enum Key {
	
	W(87, 'w'),
	A(65, 'a'),
	S(83, 's'),
	D(68, 'd'),
	Q(81, 'q'),
	SPACE(32, ' ');
	
	private int id;
	private char ch;
	
	Key(int c, char ch) {
		id = c;
		this.ch = ch;
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
