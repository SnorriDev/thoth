package snorri.inventory;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.lang.reflect.InvocationTargetException;
import java.util.logging.Level;

import javax.swing.ImageIcon;

import snorri.animations.Animation;
import snorri.collisions.CircleCollider;
import snorri.events.CastEvent;
import snorri.main.Debug;
import snorri.main.Main;
import snorri.semantics.CommandStatus;
import snorri.util.Util;
import snorri.windows.GamePanel;
import snorri.windows.GameWindow;
import snorri.world.Vector;

public abstract class Item implements Droppable {
	
	private static final long serialVersionUID = 1L;
	
	protected Spell spell; // spell/enchantment associated with the item
	protected String nickname; //name which the player gives the item so they know what it does
	protected ItemType type; // what type of item it is; you can get ID, maxQuantity, enchantable from this
	protected transient BufferedImage texture; // don't save this!
	
	private static final int ARC_SIZE = 32;
	private static final int ENTITY_SIZE = 44;
	private static final int SMALL_ICON_SIZE = 32;
	private static final int SLOT_SPACE = 16;
	//private static final int BIG_SLOT_SIZE = 2;
	private static final double SEPARATION_FACTOR = 1.0/1.8;
	
	private static final Color DEFAULT_COOLDOWN_COLOR = new Color(116, 100, 50, 200);
	
	protected static transient final BufferedImage[] ACTIVE_BORDERS_TOP;
	protected static transient final BufferedImage[] INACTIVE_BORDERS_TOP;
	
	protected static transient final BufferedImage[] ACTIVE_BORDERS_BOTTOM;
	protected static transient final BufferedImage[] INACTIVE_BORDERS_BOTTOM;
	
	static {
		
		ACTIVE_BORDERS_TOP = new BufferedImage[5];
		INACTIVE_BORDERS_TOP = new BufferedImage[5];
		
		for (int i = 0; i < 3; i++) {
			ACTIVE_BORDERS_TOP[i] = Main.getImage("/textures/hud/items/item" + (i + 1) + ".png");
			INACTIVE_BORDERS_TOP[i] = Main.getImage("/textures/hud/items/item" + (i + 1) + "Alt.png");
		}
		
		ACTIVE_BORDERS_BOTTOM = new BufferedImage[] {
			Main.getImage("/textures/hud/items/itemMouse.png"),
			Main.getImage("/textures/hud/items/itemOrb.png")
		};
		
		INACTIVE_BORDERS_BOTTOM = new BufferedImage[] {
			Main.getImage("/textures/hud/items/itemMouseAlt.png"),
			Main.getImage("/textures/hud/items/itemOrbAlt.png")
		};
		
	}
	
	protected Timer timer;
	
	public enum ItemType {

		//TODO(#46): Allow these to be Animations in addition to BufferedImages.
		EMPTY,
		PAPYRUS(Papyrus.class, Main.getImage("/textures/items/papyrus.png")),
		SLING(Weapon.class, Main.getImage("/textures/items/sling.png"), 34d, 0.45, "/sound/arrow.wav"),
		PELLET(5, Orb.class, Main.getImage("/textures/items/pellet.png"), new Animation("/textures/objects/pellet.png")),
		SLOW_SLING(Weapon.class, Main.getImage("/textures/items/sling.png"), 34d, 2d, "/sound/arrow.wav"),
		BOW(Weapon.class, Main.getImage("/textures/items/bow.png"), 75d, 0.6, "/sound/arrow.wav"),
		ARROW(5, Orb.class, Main.getImage("/textures/items/arrow.png"), new Animation("/textures/objects/pellet.png")),
		SNAKE_BITE(Weapon.class, null, 75d, 1d, "/sound/arrow.wav"),
		VENOM(5, Orb.class, null, new Animation("/textures/objects/venom.png")),
		CROCODILE_BITE(MeleeWeapon.class, null, 100d, 1d, "/sound/arrow.wav", 50, new CircleCollider(100)),
		BOLT(5, Orb.class, null, new Animation("/textures/objects/bolt.png"));

		private Class<? extends Item> c;
		private int maxQuantity = 1; //number of inventory slots; use Consumable class with data field for charges
		private boolean enchantable = true;
		private Object[] args;
		private BufferedImage texture;
		//private Image inventoryImage;
		
		ItemType() { //only use this for empty item
		}
		
		ItemType(Class<? extends Item> c, BufferedImage texture, Object...args) {
			this.c = c;
			this.args = args;
			this.texture = Util.resize(texture, ENTITY_SIZE, 0);
		}

		// we need to put maxQuantity as the first argument to avoid ambiguity
		// (lol)
		ItemType(int maxQuantity, Class<? extends Item> c, BufferedImage texture, Object... args) {
			this(c, texture, args);
			this.maxQuantity = maxQuantity;
		}
		
		ItemType(boolean enchantable, Class<? extends Item> c, BufferedImage texture, Object... args) {
			this(c, texture, args);
			this.enchantable = enchantable;
		}
		
		ItemType(int maxQuantity, boolean enchantable, Class<? extends Item> c, BufferedImage texture, Object... args) {
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
		
		public BufferedImage getTexture() {
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
			if (c == null) { // Create the empty item.
				return null;
			}

			try {
				return c.getConstructor(ItemType.class).newInstance(this);
			} catch (Exception e) {
				Debug.logger.log(Level.SEVERE, "Could not create new Item", e);
				return null;
			}
		}
		
		public static ItemType fromString(String raw) {
			try {
				Integer i;
				if ((i = Util.getInteger(raw)) == null) {
						return valueOf(Util.unclean(raw));
				}
				return values()[i];
			} catch (IllegalArgumentException e) {
				return null;
			} catch (IndexOutOfBoundsException e) {
				Debug.logger.info("Invalid item number " + raw + " specified");
				return null;
			}
		}
		
		@Override
		public String toString() {
			return Util.clean(name());
		}

	}
	
	public Item(ItemType t) {
		type = t;
	}

	// returns the item type
	public ItemType getType() {
		return type;
	}
	
	/**
	 * Compute the texture for this item.
	 * @return The BufferedImage representing the texture.
	 */
	protected void computeTexture() {
		texture = type.getTexture();
	}
	
	@Override
	public BufferedImage getTexture() {
		if (texture == null && type.getTexture() != null) {
			computeTexture();
		}
		return texture;
	}
	
	@Override
	public Animation getAnimation() {
		return new Animation(getTexture());
	}
	
	public void updateCooldown(double deltaTime) {
		
		if (timer == null) {
			return;
		}
		
		timer.update(deltaTime);
	}
	
	public boolean canCast() {
		
		if (timer == null) {
			return true;
		}
		
		return timer.isOffCooldown();
	}
	
	public Timer getTimer() {
		return timer;
	}
	
	/**
	 * attempt to stack an item onto this one
	 * this should only really be relevant for consumables
	 * @return
	 * 	whether or not anything was done
	 */
	@Override
	public boolean stack(Droppable other) {
		return false;
	}

	/**
	 * 
	 * @param other
	 * 	The item to compare against
	 * @return <code>true</code> iff the items have the same <code>ItemType</code>
	 */
	@Override
	public boolean equals(Object other) {
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
	 * Changes the spell on the item iff it's enchantable.
	 * @param newSpell The spell to enchant this item with.
	 * @return True if and only if the operation was successful.
	 */
	public boolean setSpell(Spell newSpell) {
		if (! type.isEnchantable()) {
			return false;
		}
		spell = newSpell;
		computeTexture();
		resetTimer();
		return true;
	}

	public Spell getSpell() {
		return spell;
	}
	
	/** Cast this item's spell in the context specified by spellEvent. */
	public CommandStatus wrapCastSpell(CastEvent spellEvent) {
		if (spell == null) {
			return null;
		}
		try {
			return spell.cast(spellEvent);
		} catch (Exception e) {
			// Prevent unintended behavior of spells from crashing the game.
			Debug.logger.log(Level.SEVERE, "Unexpected error during spell execution.", e);
			return CommandStatus.UNKNOWN_ERROR;
		}
	}

	public static Item newItem() {
		return null;
	}

	/** Create a new item by type. */
	public static Item newItem(ItemType type) {
		return type.getNew();
	}

	/** Create an item with the id itemId. */
	public static Item newItem(int itemId) {
		return newItem(ItemType.byId(itemId));
	}
	
	@Override
	public String toString() {
		if (nickname == null) {
			return type.toString();
		}
		return type.toString() + " (" + nickname + ")";
	}
	
	public void setNickname(String s) {
		nickname = s;
	}
	
	public String getNickname() {
		return nickname;
	}
	
	public static BufferedImage getBorder(int i, boolean top, boolean selected) {
		if (!top) {
			return (selected ? ACTIVE_BORDERS_BOTTOM : INACTIVE_BORDERS_BOTTOM)[i];
		}
		return (selected ? ACTIVE_BORDERS_TOP : INACTIVE_BORDERS_TOP)[i];
	}
	
	public Color getArcColor() {
		return DEFAULT_COOLDOWN_COLOR;
	}
	
	public static Vector getPos(int i, boolean top) {
		int y = top ? GamePanel.MARGIN : (((GameWindow) Main.getWindow()).getHeight() - (i == 0 ? Item.getBigSlotWidth() : Item.getSlotWidth()) - GamePanel.MARGIN);
		int width = top ? Item.getSlotWidth() : Item.getBigSlotWidth();
		return new Vector(GamePanel.MARGIN + i * (width + Item.SLOT_SPACE), y);
	}
	
	/**
	 * draws a thumbnail and returns its width
	 * @param g
	 * @param pos
	 * @param selected 
	 * @return
	 * 	width of thumbnail drawn
	 */
	public int drawThumbnail(Graphics g, int i, boolean top, boolean selected) {
		
		BufferedImage border = getBorder(i, top, selected);
		BufferedImage icon = getTexture();
		
		Vector pos = getPos(i, top);
		Vector iconPos = pos.add(new Vector(border.getWidth(null) - icon.getWidth(null), border.getHeight(null) - icon.getHeight(null)).divide_(2));
		Vector arcPos = pos.add(new Vector(border.getWidth(null) - ARC_SIZE, border.getHeight(null) - ARC_SIZE).divide_(2));
		
		if (selected) {
			g.drawImage(border, pos.getX(), pos.getY(), null);
			g.drawImage(icon, iconPos.getX(), iconPos.getY(), null);
		} else {
			g.drawImage(icon, iconPos.getX(), iconPos.getY(), null);
			g.drawImage(border, pos.getX(), pos.getY(), null);
			if (timer != null) {
				g.setColor(getArcColor());
				g.fillArc(arcPos.getX(), arcPos.getY(), ARC_SIZE, ARC_SIZE, 90, this.getTimer().getRatio(360));
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
	public static int drawEmpty(Graphics g, int i, boolean top) {
		Vector pos = getPos(i, top);
		g.drawImage(getBorder(i, top, false), pos.getX(), pos.getY(), null);
		return getSlotWidth();
	}
	
	public static int getSlotWidth() {
		return (int) (getBigSlotWidth() * SEPARATION_FACTOR);
	}
	
	public static int getBigSlotWidth() {
		return ACTIVE_BORDERS_BOTTOM[0].getWidth(null);
	}
	
	public void resetTimer() {
		if (timer != null) {
			timer.activateIfPossible();
		}
	}
	
	@Override
	public Item copy() {
		try {
			Item copy = getClass().getConstructor(ItemType.class).newInstance(type);
			copy.nickname = nickname;
			copy.spell = spell.copy();
			return copy;
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException e) {
			Debug.logger.log(Level.SEVERE, "Could not copy item.", e);
			return null;
		}
	}

	public ImageIcon getIcon() {
		BufferedImage texture = getTexture();
		return new ImageIcon(texture.getScaledInstance(SMALL_ICON_SIZE, (int) (((double) texture.getHeight(null)) / texture.getWidth(null) * SMALL_ICON_SIZE), BufferedImage.SCALE_SMOOTH));
	}

	public static Droppable fromString(String raw) {
		ItemType type = ItemType.fromString(raw);
		if (type != null) {
			return type.getNew();
		}
		return null;
	}

}
