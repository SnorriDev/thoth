package snorri.inventory;

import snorri.main.Main;
import snorri.parser.Node;



public abstract class Item {
	
	protected int						quantity = 1; //default value	
	protected Node						spell;																																																																																																																																																																																																																																																																																																						//spell/enchantment associated with the item
	protected boolean					enchantable = true;
	protected ItemType					type; //what type of item it is; you can get the ID and maxQuantity from this
	
	public enum ItemType {
		
		EMPTY(null),
		PAPYRUS(Papyrus.class),
		HELMET(Armor.class, 2),
		SLING(Weapon.class, 34),
		PELLET(Projectile.class);
		
		private Class<? extends Item> c;
		private int maxQuantity = 1;
		private Object[] args;
		
		ItemType(Class<? extends Item> c, Object...args) {
			this.c = c;
			this.args = args;
		}
		
		//we need to put maxQuantity as the first argument to avoid ambiguity (lol)
		ItemType(int maxQuantity, Class<? extends Item> c, Object...args) {
			this(c, args);
			this.maxQuantity = maxQuantity;
		}
		
		public static ItemType byId(int id) {
			return values()[id];
		}
		
		//the id of the item
		public int getId() {
			return ordinal();
		}
		
		public int getMaxQuantity() {
			return maxQuantity;
		}
		
		public Object getProperty(int i) {
			return args[i];
		}
		
		public Item getNew() {
			
			if (c == null) { //the empty item
				return null;
			}
			
			try {
				return c.getConstructor(ItemType.class).newInstance(this); //we always call the constructor which takes a type
			} catch (Exception e) {
				Main.error("invalid parameters specified for " + this.toString());
				return null;
			}
		}
		
	}
	
	public Item(ItemType t) {
		type = t;
	}
	
	//returns the item type
	public ItemType getType() {
		return type;
	}
	
	//returns quantity of item
	public int getQuantity() {
		return quantity;
	}
	
	//attempts to change the quantity of the item
	//returns false iff it is illegal
	public boolean setQuantity(int amount) {
		
		if (amount <= 0) {
			return false;
		}
		
		if (amount <= type.getMaxQuantity()) {
			quantity = amount;
			return true;
		}
		
		return false;
		
	}
	
	//changes the spell on the item
	public void setSpell(Node newSpell) {
		spell = newSpell;
	}
	
	//returns the spell on the item
	public Node getSpell() {
		return spell;
	}
	
	public boolean isEmpty() {
		return type == ItemType.EMPTY;
	}

	public static Item newItem() {
		return null;
	}
	
	//create a new item by type
	public static Item newItem(ItemType type) {
		return type.getNew();
	}
	
	//creates an item with the id: "itemId"
	public static Item newItem(int itemId) {
		return newItem(ItemType.byId(itemId));
	}

}
