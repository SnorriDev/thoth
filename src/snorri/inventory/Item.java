package snorri.inventory;

import snorri.parser.Node;



public abstract class Item {
	protected int						id;																																																																																																																																																																																																																																																																																																																				//item id number, fastest way to get all item properties at once
	protected int						quantity;																																																																																																																																																																																																																																																																																								//number of that item stacked together
	protected Node						spell;																																																																																																																																																																																																																																																																																																						//spell/enchantment associated with the item
	protected static final String[]		ITEMNAME	= { "Empty", "Papyrus", "Helmet", "Sling", "Staff", "Pellet" };																																 //name of items where ITEMNAME[id] gives you the name of the item with that id number
	protected static final String[]		ITEMTYPE	= { "Empty", "Papyrus", "Armor", "Weapon", "Weapon", "Projectile" }; //type of items where ITEMTYPE[id] gives you the type of the item with that id number
	protected static final int[]		MAXQUANTITY	= { 1, 1, 1, 1, 1, 1 };																																																																																																																																																																																																 //maximum quantity of items where MAXQUANTITY[id] gives you the maximum quantity of the item with that id number
	protected static final boolean[]	ENCHANTABLE	= { false, true, true, true, true, true };																																																																																																																 //ENCHANTABLE[id] tells you whether the item with that id number is enchantable
													
	//returns id number of item
	public int getID() {
		return id;
	}
	
	//returns quantity of item
	public int getQuantity() {
		return quantity;
	}
	
	//changes the quantity of the item
	public void setQuantity(int amount) {
		if (amount <= MAXQUANTITY[id] && amount > 0) {
			quantity = amount;
		}
		else if (amount <= 0) { //this is supposed to set the item to an empty inventory slot but I am not sure if it works -Toby
			id = 0;
			quantity = 1;
			spell = null;
		}
		else {
			quantity = MAXQUANTITY[id];
		}
	}
	
	//returns the maximum quantity that the item can be stacked to
	public int getMaxQuantity() {
		return MAXQUANTITY[id];
	}
	
	//returns name of item
	public String getITEMNAME() {
		return ITEMNAME[id];
	}
	
	//returns type of item
	public String getITEMTYPE() {
		return ITEMTYPE[id];
	}
	
	//changes the spell on the item
	public void setSpell(Node newSpell) {
		spell = newSpell;
	}
	
	//returns the spell on the item
	public Node getSpell() {
		return spell;
	}
	
	//creates an empty inventory slot item
	public static Item newItem() {
		return new EmptyInvSlot();
	}
	
	//creates an item with the id: "itemID"
	public static Item newItem(int itemID) {
		if (itemID >= ITEMNAME.length || itemID < 0) {
			System.out.println("item with that ID does not exist, setting to EmptyInvSlot");
			return new EmptyInvSlot();
		}
		if (ITEMTYPE[itemID] == "Empty")
			return new EmptyInvSlot(itemID);
		else if (ITEMTYPE[itemID] == "Papyrus")
			return new Papyrus(itemID);
		else if (ITEMTYPE[itemID] == "Armor")
			return new Armor(itemID);
		else if (ITEMTYPE[itemID] == "Weapon")
			return new Weapon(itemID);
		else if (ITEMTYPE[itemID] == "Projectile")
			return new Projectile(itemID);
		else
			return new EmptyInvSlot();
	}
	
	//creates an item with the id: "itemID" and the quantity: "amount"
	public static Item newItem(int itemID, int amount) {
		if (amount >= MAXQUANTITY[itemID]) {
			System.out.println("amount greater than maxquantity, setting amount to max quantity");
			amount = MAXQUANTITY[itemID];
		}
		if (amount <= 0) {
			System.out.println("amount less than or equal to 0, setting to EmptyInvSlot");
			itemID = 0;
			amount = 1;
			return new EmptyInvSlot();
		}
		if (itemID >= ITEMNAME.length || itemID < 0) {
			System.out.println("item with that ID does not exist, setting to EmptyInvSlot");
			return new EmptyInvSlot();
		}
		if (ITEMTYPE[itemID] == "Empty")
			return new EmptyInvSlot(itemID, amount);
		else if (ITEMTYPE[itemID] == "Papyrus")
			return new Papyrus(itemID, amount);
		else if (ITEMTYPE[itemID] == "Armor")
			return new Armor(itemID, amount);
		else if (ITEMTYPE[itemID] == "Weapon")
			return new Weapon(itemID, amount);
		else if (ITEMTYPE[itemID] == "Projectile")
			return new Projectile(itemID, amount);
		else
			return new EmptyInvSlot();
	}
	
	//creates an item with the id: "itemID" and the spell: "enchantment"
	public static Item newItem(int itemID, Node enchantment) {
		if (ENCHANTABLE[itemID] == false && enchantment != null) {
			System.out.println("item not encantable, setting enchantment to null");
			enchantment = null;
		}
		if (itemID >= ITEMNAME.length || itemID < 0) {
			System.out.println("item with that ID does not exist, setting to EmptyInvSlot");
			return new EmptyInvSlot();
		}
		if (ITEMTYPE[itemID] == "Empty")
			return new EmptyInvSlot(itemID, enchantment);
		else if (ITEMTYPE[itemID] == "Papyrus")
			return new Papyrus(itemID, enchantment);
		else if (ITEMTYPE[itemID] == "Armor")
			return new Armor(itemID, enchantment);
		else if (ITEMTYPE[itemID] == "Weapon")
			return new Weapon(itemID, enchantment);
		else if (ITEMTYPE[itemID] == "Projectile")
			return new Projectile(itemID, enchantment);
		else
			return new EmptyInvSlot();
	}
	
	//creates an item with the id: "itemID", the quantity: "amount", and the spell: "enchantment"
	public static Item newItem(int itemID, int amount, Node enchantment) {
		if (ENCHANTABLE[itemID] == false && enchantment != null) {
			System.out.println("item not encantable, setting enchantment to null");
			enchantment = null;
		}
		if (amount >= MAXQUANTITY[itemID]) {
			System.out.println("amount greater than maxquantity, setting amount to max quantity");
			amount = MAXQUANTITY[itemID];
		}
		if (amount <= 0) {
			System.out.println("amount less than or equal to 0, setting to EmptyInvSlot");
			itemID = 0;
			amount = 1;
			enchantment = null;
		}
		if (itemID >= ITEMNAME.length || itemID < 0) {
			System.out.println("item with that ID does not exist, setting to EmptyInvSlot");
			return new EmptyInvSlot();
		}
		if (ITEMTYPE[itemID] == "Empty")
			return new EmptyInvSlot(itemID, amount, enchantment);
		else if (ITEMTYPE[itemID] == "Papyrus")
			return new Papyrus(itemID, amount, enchantment);
		else if (ITEMTYPE[itemID] == "Armor")
			return new Armor(itemID, amount, enchantment);
		else if (ITEMTYPE[itemID] == "Weapon")
			return new Weapon(itemID, amount, enchantment);
		else if (ITEMTYPE[itemID] == "Projectile")
			return new Projectile(itemID, amount, enchantment);
		else
			return new EmptyInvSlot();
	}
}
