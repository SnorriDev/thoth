package snorri.keyboard;

public enum MouseButton implements Binding {

	SHOOT(1), // Left mouse button.
	CAST(3); // Right mouse button.
		
	private int num;
	
	MouseButton(int num) {
		this.num = num;
	}
	
	public int getNum() {
		return num;
	}
	
}
