package snorri.inventory;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;

import snorri.entities.Entity;
import snorri.events.SpellEvent;
import snorri.main.GameWindow;
import snorri.main.Main;
import snorri.parser.Node;
import snorri.world.Vector;

public abstract class Item implements Droppable {
	
	private static final long serialVersionUID = 1L;
	
	protected int quantity = 1; // default value
	protected Node spell; // spell/enchantment associated with the item
	protected String nickname; //name which the player gives the item so they know what it does
	protected ItemType type; // what type of item it is; you can get ID, maxQuantity, enchantable from this
	
	private static final int ICON_SIZE = 64;
	
	private static final Image DEFAULT_BORDER = Main.getImageResource("/textures/hud/itemBorder.png");
	private static final Color DEFAULT_COOLDOWN_COLOR = new Color(156, 134, 73, 200);
	
	protected Timer timer;
	
	public enum ItemType {

		EMPTY,
		PAPYRUS(5, Papyrus.class, Main.getImageResource("/textures/items/papyrus.png")),
		HELMET(Armor.class, Main.getImageResource("/textures/items/helmet.png"), 2d),
		SLING(Weapon.class, Main.getImageResource("/textures/items/bow.png"), 34d, 0.3),
		PELLET(5, Orb.class, Main.getImageResource("/textures/items/pellet.png")),
		SLOW_SLING(Weapon.class, Main.getImageResource("/textures/items/bow.png"), 34d, 2d);

		private Class<? extends Item> c;
		private int maxQuantity = 1; //number of inventory slots; use Consumable class with data field for charges
		private boolean enchantable = true;
		private Object[] args;
		private Image texture;
		
		ItemType() { //only use this for empty item
		}
		
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
	
	public void updateCooldown(double deltaTime) {
		
		if (timer == null) {
			return;
		}
		
		timer.update(deltaTime);
	}
	
	public boolean canUse() {
		
		if (timer == null) {
			return true;
		}
		
		return timer.isOffCooldown();
	}
	
	public Timer getTimer() {
		return timer;
	}

	// returns quantity of item
	public int getQuantity() {
		return quantity;
	}

	/**
	 * attempts to change the quantity of the item
	 * @return
	 * 	false iff illegal
	 */
	public boolean setQuantity(int amount) {
		if (amount <= 0) {
			return false;
		}
		quantity = Math.min(type.getMaxQuantity(), amount);
		return true;
	}
	
	public boolean isFullStack() {
		return quantity == type.getMaxQuantity();
	}
	
	/**
	 * attempt to stack an item onto this one
	 * @return
	 * 	whether or not anything was added
	 */
	@Override
	public boolean stack(Droppable other) {
		if (!equals(other) || isFullStack()) {
			return false;
		}
		return setQuantity(quantity + ((Item) other).quantity);
	}

	@Override
	public boolean equals(Object other) {
		//TODO perhaps check if spell and nickname are equal?
		//this will require equality for spells, which could be hard
		if (!(other instanceof Item)) {
			return false;
		}
		return type == ((Item) other).type;
	}
	
	@Override
	public int getMaxQuantity() {
		return type.getMaxQuantity();
	}
	
	/**
	 * changes the spell on the item iff it's enchantable
	 */
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
	
	public Object useSpellOn(Entity subject) {
				
		if (spell == null) {
			return null;
		}
				
		SpellEvent e = new SpellEvent((GameWindow) Main.getWindow(), subject);
		return spell.getMeaning(e);
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
		return type.toString() + "{" + nickname + "}";
	}
	
	public void setNickname(String s) {
		nickname = s;
	}
	
	public String getNickname() {
		return nickname;
	}
	
	public Image getBorder(boolean selected) {
		return DEFAULT_BORDER;
	}
	
	public Color getCooldownColor() {
		return DEFAULT_COOLDOWN_COLOR;
	}
	
	//TODO: use an ImageViewer to scale things
	
	/**
	 * draws a thumbnail and returns its width
	 * @param g
	 * @param pos
	 * @param selected 
	 * @return
	 * 	width of thumbnail drawn
	 */
	public int drawThumbnail(Graphics g, Vector pos, boolean selected) {
		
		Image border = getBorder(selected);
		Image icon = type.getTexture();
		
		Vector iconPos = pos.copy();
		iconPos.add(new Vector((border.getWidth(null) - icon.getWidth(null)) / 2, (border.getHeight(null) - icon.getHeight(null)) / 2));
		
		//TODO: show cooldown
				
		if (selected) {
			g.drawImage(border, pos.getX(), pos.getY(), null);
			g.drawImage(icon, iconPos.getX(), iconPos.getY(), null);
		} else {
			g.drawImage(icon, iconPos.getX(), iconPos.getY(), null);
			g.drawImage(border, pos.getX(), pos.getY(), null);
			if (timer != null) { //TODO: instanceof Cooldownable
				g.setColor(getCooldownColor());
				g.fillArc(iconPos.getX(), iconPos.getY(), ICON_SIZE, ICON_SIZE, 90, this.getTimer().getRatio(360));
				g.setColor(Color.BLACK);
			}
		}
					
		return border.getWidth(null);
		
	}
	
	/**
	 * draws an empty thumbnail and returns its width
	 * @param g
	 * @param pos
	 * @return
	 * 	width of thumbnail drawn
	 */
	public static int drawEmpty(Graphics g, Vector pos) {
		g.drawImage(DEFAULT_BORDER, pos.getX(), pos.getY(), null);
		return getSlotWidth();
	}
	
	public static int getSlotWidth() {
		return DEFAULT_BORDER.getWidth(null);
	}

}
