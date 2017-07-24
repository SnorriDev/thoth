package snorri.inventory;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.lang.reflect.InvocationTargetException;

import javax.swing.ImageIcon;

import snorri.animations.Animation;
import snorri.collisions.CircleCollider;
import snorri.entities.Entity;
import snorri.events.SpellEvent;
import snorri.events.SpellEvent.Caster;
import snorri.main.Debug;
import snorri.main.GamePanel;
import snorri.main.GameWindow;
import snorri.main.Main;
import snorri.main.Util;
import snorri.nonterminals.Sentence;
import snorri.parser.Node;
import snorri.world.Vector;
import snorri.world.World;

public abstract class Item implements Droppable, ImageObserver {
	
	private static final long serialVersionUID = 1L;
	
	protected Node<Boolean> spell; // spell/enchantment associated with the item
	protected String nickname; //name which the player gives the item so they know what it does
	protected ItemType type; // what type of item it is; you can get ID, maxQuantity, enchantable from this
	protected transient BufferedImage texture;
	
	private static final int ARC_SIZE = 32;
	private static final int ENTITY_SIZE = 44;
	private static final int SMALL_ICON_SIZE = 32;
	private static final int SLOT_SPACE = 16;
	//private static final int BIG_SLOT_SIZE = 2;
	private static final double SEPARATION_FACTOR = 1.0/1.8;
	
	private static final Color DEFAULT_COOLDOWN_COLOR = new Color(116, 100, 50, 200);
	
	protected static final BufferedImage[] ACTIVE_BORDERS_TOP;
	protected static final BufferedImage[] INACTIVE_BORDERS_TOP;
	
	protected static final BufferedImage[] ACTIVE_BORDERS_BOTTOM;
	protected static final BufferedImage[] INACTIVE_BORDERS_BOTTOM;
	
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

		//TODO convert these to Animations instead of BufferedImages?
		
		EMPTY,
		PAPYRUS(5, Papyrus.class, Main.getImage("/textures/items/papyrus.png")),
		HELMET(Armor.class, Main.getImage("/textures/items/helmet.png"), 2d),
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

			if (c == null) { // the empty item
				return null;
			}

			try {
				return c.getConstructor(ItemType.class).newInstance(this);
			} catch (Exception e) {
				Debug.error(e);
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
				Debug.log("invalid item number " + raw + " specified");
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
	
	//TODO require this method in Droppable?
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
	
	public boolean canUse() {
		
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
	 * changes the spell on the item iff it's enchantable
	 * @param newSpell
	 * 	The spell to enchant this item with.
	 */
	public boolean setSpell(Sentence newSpell) {
		
		if (! type.isEnchantable()) {
			return false;
		}
		
		spell = newSpell;
		computeTexture();
		resetTimer();
		return true;
	}

	// returns the spell on the item
	public Node<Boolean> getSpell() {
		return spell;
	}
	
	/**
	 * Use this item's spell on <code>subject</code> from <code>caster</code>'s perspective
	 * @param caster
	 * 	The caster of the spell
	 * @param subject
	 * 	The target of the spell
	 * @return
	 */
	public Object useSpellOn(World world, Caster caster, Entity subject, double modifier) {
				
		if (spell == null) {
			return null;
		}
						
		SpellEvent e = new SpellEvent(world, caster, subject, modifier);
		return spell.getMeaning(e);
		
	}
	
	public Object useSpell(World world, Caster caster, Entity subject) {
		return useSpellOn(world, caster, subject, 1);	
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
	
	//TODO: use an ImageViewer to scale things
	
	public static Vector getPos(int i, boolean top) {
		int y = top ? GamePanel.MARGIN : (((GameWindow) Main.getWindow()).getDimensions().getY() - (i == 0 ? Item.getBigSlotWidth() : Item.getSlotWidth()) - GamePanel.MARGIN);
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
		Vector iconPos = pos.copy().add(new Vector(border.getWidth(null) - icon.getWidth(null), border.getHeight(null) - icon.getHeight(null)).divide(2));
		Vector arcPos = pos.copy().add(new Vector(border.getWidth(null) - ARC_SIZE, border.getHeight(null) - ARC_SIZE).divide(2));
		
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
	
	@Override
	public int compareIn(Droppable o, Inventory inv) {
		int cmp = Droppable.super.compareIn(o, inv);
		if (o instanceof Item && cmp == 0) {
			int cmp2 = Integer.compare(inv.getIndex(this), inv.getIndex((Item) o));
			if (cmp2 == 0) { //same-named items can
				return toUniqueString().compareTo(o.toUniqueString());
			}
			return cmp2;
		}
		return cmp;
	}
	
	public void resetTimer() {
		if (timer != null) {
			timer.activate();
		}
	}
	
	@Override
	public Item copy() {
		//TODO copy spell/name?
		try {
			Item copy = getClass().getConstructor(ItemType.class).newInstance(type);
			copy.nickname = nickname;
			copy.spell = spell.copy();
			return copy;
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException e) {
			Debug.error(e);
			return null;
		}
	}
	
	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
		in.defaultReadObject();
	}
	
	public boolean imageUpdate(Image img, int infoflags, int x, int y, int width, int height) {
		return true;
	}

	public ImageIcon getIcon() {
		BufferedImage texture = getTexture();
		return new ImageIcon(texture.getScaledInstance(SMALL_ICON_SIZE, (int) (((double) texture.getHeight(null)) / texture.getWidth(null) * SMALL_ICON_SIZE), BufferedImage.SCALE_SMOOTH));
	}

}
