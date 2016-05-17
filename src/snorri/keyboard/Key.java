package snorri.keyboard;

public enum Key {
	
	W (87),
	A (65),
	S (83),
	D (68);
	
	private int id;
	
	Key(int c) {
		id = c;
	}
	
	public int getCode() {
		return id;
	}
	
}
