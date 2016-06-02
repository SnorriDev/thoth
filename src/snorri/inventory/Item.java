package snorri.inventory;

import java.awt.Graphics;
import java.awt.Image;

import snorri.main.Main;
import snorri.parser.Node;
import snorri.world.Vector;

public abstract class Item {
	
	protected int quantity = 1; // default value
	protected Node spell; // spell/enchantment associated with the item
	protected ItemType type; // what type of item it is; you can get ID, maxQuantity, enchantable from this

	public enum ItemType {

		EMPTY(null, null),
		PAPYRUS(Papyrus.class, null),
		HELMET(Armor.class, null, 2),
		SLING(Weapon.class, Main.getImageResource("/textures/items/bow.png"), 34),
		PELLET(Projectile.class, null);

		private Class<? extends Item> c;
		private int maxQuantity = 1;
		private boolean enchantable = true;
		private Object[] args;
		private Image texture;
		
		ItemType(Class<? extends Item> c, Image texture, Object...args) {
			this.c = c;
			this.args = args;
			this.texture = texture;
		}

		// we need to put maxQuantity as the first argument to avoid ambiguity
		// (lol)
		ItemType(int maxQuantity, Class<? extends Item> c, Image texture, Object... args) {
			this(c, texture, args);
			this.maxQuantity = maxQuantity;
		}
		
		ItemType(boolean enchantable, Class<? extends Item> c, Image texture, Object... args) {
			this(c, texture, args);
			this.enchantable = enchantable;
		}
		
		ItemType(int maxQuantity, boolean enchantable, Class<? extends Item> c, Image texture, Object... args) {
			this(c, texture, args);
			this.maxQuantity = maxQuantity;
			this.enchantable = enchantable;
		}

		public static ItemType byId(int id) {
			return values()[id];
		}

		// the id of the item
		public int getId() {
			return ordinal();
		}
		
		public Image getTexture() {
			return texture;
		}
		
		public boolean isEnchantable() {
			return enchantable;
		}

		public int getMaxQuantity() {
			return maxQuantity;
		}

		public Object getProperty(int i) {
			return args[i];
		}

		public Item getNew() {

			if (c == null) { // the empty item
				return null;
			}

			try {
				return c.getConstructor(ItemType.class).newInstance(this);
			} catch (Exception e) {
				Main.error("invalid parameters specified for " + this.toString());
				return null;
			}
		}

	}
	
	public Item(ItemType t) {
		type = t;
	}

	// returns the item type
	public ItemType getType() {
		return type;
	}

	// returns quantity of item
	public int getQuantity() {
		return quantity;
	}

	// attempts to change the quantity of the item
	// returns false iff it is illegal
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

	// changes the spell on the item iff it's enchantable
	public boolean setSpell(Node newSpell) {
		
		if (! type.isEnchantable()) {
			return false;
		}
		
		spell = newSpell;
		return true;
	}

	// returns the spell on the item
	public Node getSpell() {
		return spell;
	}

	public static Item newItem() {
		return null;
	}

	// create a new item by type
	public static Item newItem(ItemType type) {
		return type.getNew();
	}

	// creates an item with the id: "itemId"
	public static Item newItem(int itemId) {
		return newItem(ItemType.byId(itemId));
	}
	
	@Override
	public String toString() {
		return type.toString();
	}
	
	public void render(Graphics g, Vector pos) {
		
		if (type.getTexture() == null) {
			return;
		}
		
		g.drawImage(type.getTexture(), pos.getX(), pos.getY(), null);
	}

	public void renderSmall(Graphics g, Vector pos) {

		if (type.getTexture() == null) {
			return;
		}
		
		g.drawImage(type.getTexture(), pos.getX(), pos.getY(), 32, 32, null);
		
	}

}
