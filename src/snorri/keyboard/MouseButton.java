package snorri.keyboard;

public enum MouseButton implements Binding {

	SHOOT(1);
		
	private int num;
	
	MouseButton(int num) {
		this.num = num;
	}
	
	public int getNum() {
		return num;
	}
	
}
