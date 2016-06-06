package snorri.inventory;



public class Weapon extends Item {

	public Weapon(ItemType t) {
		super(t);
		timer = new Timer(0.3f);
	}
	
	//retrieve the sharpness for this weapon from the ItemType
	public int getSharpness() {
		return (int) type.getProperty(0);
	}

	public boolean altersMovement() {
		
		if (spell == null) {
			return false;
		}
		
		return spell.altersMovement();
	}

}
