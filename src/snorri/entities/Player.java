package snorri.entities;

import java.awt.Graphics;
import java.awt.Image;

import javax.swing.SwingUtilities;

import snorri.animations.Animation;
import snorri.events.InteractEvent;
import snorri.events.CastEvent;
import snorri.events.CastEvent.Caster;
import snorri.inventory.Droppable;
import snorri.inventory.Item;
import snorri.inventory.Item.ItemType;
import snorri.keyboard.Key;
import snorri.inventory.Orb;
import snorri.inventory.Papyrus;
import snorri.inventory.Stats;
import snorri.inventory.Weapon;
import snorri.main.FocusedWindow;
import snorri.main.GamePanel;
import snorri.main.GameWindow;
import snorri.main.Main;
import snorri.parser.Lexicon;
import snorri.semantics.Nominal;
import snorri.triggers.Trigger.TriggerType;
import snorri.world.Vector;
import snorri.world.World;

public class Player extends Unit implements Caster {

	private static final Animation IDLE = new Animation("/textures/animations/setna/idle");
	private static final Animation WALKING = new Animation("/textures/animations/setna/walking");

	private static final Image HEART = Main.getImage("/textures/hud/heart.png");
	private static final Image HALF_HEART = Main.getImage("/textures/hud/halfHeart.png");
	private static final Image GREY_HEART = Main.getImage("/textures/hud/greyHeart.png");

	private static final int HEALTH_RES = 20;
	protected static final int PLAYER_BASE_SPEED = 190;
	private static final long serialVersionUID = 1L;

	private static final String[] SPEECH_SOUNDS = {"/sound/arrow.wav"};
	private static final String[] DAMAGE_SOUNDS = {"/sound/arrow.wav"};
	private static final String[] DEATH_SOUNDS = {"/sound/arrow.wav"};

	private Stats stats;
	private Lexicon lexicon;

	protected double mana;

	public interface Interactor {

		public static final int INTERACT_RANGE = 100;
		public static final int WIDE_INTERACT_RANGE = INTERACT_RANGE + Math.min(Unit.RADIUS_X, Unit.RADIUS_Y);

		public void onInteract(InteractEvent e);

		public Vector getPos();

		/**
		 * This function sometimes returns false negative results. But it holds generally that 
		 * <code>inRange(p)</code> => the interactor is in range to interact with <code>p</code>.
		 * @param p
		 * @return
		 */
		default boolean inRange(Player p) {
			return getPos().distance(p.pos) < Interactor.WIDE_INTERACT_RANGE;
		}

	}

	public Player(Vector pos) {
		super(pos, IDLE, WALKING);
		stats = new Stats(this);
		lexicon = new Lexicon();

		mana = stats.getMaxMana();
		z = PLAYER_LAYER;

		speechSounds = SPEECH_SOUNDS;
		damageSounds = DAMAGE_SOUNDS;
		deathSounds = DEATH_SOUNDS;

		// TODO(#43): Stuff below here should become a static factory.

		// Add the default weapons.
		Weapon sling = (Weapon) Item.newItem(ItemType.SLING);
		Orb o1 = (Orb) Item.newItem(ItemType.PELLET);
		Orb o2 = (Orb) Item.newItem(ItemType.PELLET);
		Papyrus p1 = (Papyrus) Item.newItem(ItemType.PAPYRUS);
		Papyrus p2 = (Papyrus) Item.newItem(ItemType.PAPYRUS);
		Papyrus p3 = (Papyrus) Item.newItem(ItemType.PAPYRUS);

		// Equip items in inventory.
		getInventory().add(sling);
		getInventory().add(o1);
		getInventory().add(o2);
		getInventory().add(p1);
		getInventory().add(p2);
		getInventory().add(p3);

		tag = "player";
	}

	@Override
	public void update(World world, double deltaTime) {
		super.update(world, deltaTime);

		mana = Math.min(mana + stats.getManaRegen() * deltaTime, stats.getMaxMana());

		FocusedWindow<?> window = (FocusedWindow<?>) Main.getWindow();
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				translateNormalized(world, window.getMomentumVector(), deltaTime);
			}
		});

		// Only the player's inventory responds to keyboard input.
		getInventory().checkKeys();

		//TODO(#36): Move this to an Event.
		// We construct a new entity because positions can be assigned/pointers fucked up.
		Entity checker = new Entity(pos, Interactor.INTERACT_RANGE);
		Interactor selected = world.getEntityTree().getFirstCollision(checker, Interactor.class);
		if (selected != null && Key.SPACE.isPressed()) {
			((Interactor) selected).onInteract(new InteractEvent(world, this));
		}
	}

	@Override
	public Nominal get(AbstractSemantics attr, CastEvent e) {

		if (attr == AbstractSemantics.WEAPON) {
			return getInventory().getWeapon();
		}

		return super.get(attr, e);

	}

	@Override
	public Lexicon getLexicon() {
		return lexicon;
	}

	@Override
	public double getMana() {
		return mana;
	}

	public double getHearts() {
		return getHealth() / stats.getMaxHealth() * HEALTH_RES;
	}

	public Vector getHealthBarPos() {
		return new Vector(((GameWindow) Main.getWindow()).getDimensions().getX() - GamePanel.MARGIN - HEALTH_RES * HEART.getWidth(null), GamePanel.MARGIN);
	}

	public void renderHealthBar(Graphics g) {
		Vector pos = getHealthBarPos();
		for (int i = 0; i < HEALTH_RES; i++) {
			//TODO(#44): Show total health, not percent health.
			if (getHearts() >= i) {
				g.drawImage(HEART, pos.getX(), pos.getY(), null);
			} else if (getHearts() < i - 1 || getHearts() - (int) getHearts() < 0.5d) {
				g.drawImage(GREY_HEART, pos.getX(), pos.getY(), null);
			} else {
				g.drawImage(HALF_HEART, pos.getX(), pos.getY(), null);
			}
			pos.add_(HEART.getWidth(null), 0);
		}
	}

	@Override
	public int getBaseSpeed() {
		return PLAYER_BASE_SPEED;
	}

	@Override
	public Vector getAimPosition() {
		return ((FocusedWindow<?>) Main.getWindow()).getMousePosAbsolute();
	}

	@Override
	public boolean add(Droppable d) {
		if (Caster.super.add(d) || lexicon.add(d)) {
			TriggerType.ACQUIRE.activate(d.toString());
			if (Main.getWindow() instanceof GameWindow) {
				((GameWindow) Main.getWindow()).showMessage(d);
			}
			return true;
		}
		return false;
	}

}
