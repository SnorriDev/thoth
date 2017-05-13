package snorri.entities;

import java.awt.Graphics;
import java.awt.Image;

import javax.swing.SwingUtilities;

import snorri.animations.Animation;
import snorri.events.SpellEvent.Caster;
import snorri.inventory.Carrier;
import snorri.inventory.Inventory;
import snorri.inventory.Item;
import snorri.inventory.Item.ItemType;
import snorri.keyboard.Key;
import snorri.inventory.Orb;
import snorri.inventory.Papyrus;
import snorri.inventory.Weapon;
import snorri.main.Debug;
import snorri.main.FocusedWindow;
import snorri.main.GamePanel;
import snorri.main.GameWindow;
import snorri.main.Main;
import snorri.world.Vector;
import snorri.world.World;

public class Player extends Unit implements Carrier, Caster {

	private static final Animation IDLE = new Animation("/textures/animations/unit/idle");
	private static final Animation WALKING = new Animation("/textures/animations/unit/walking");
	
	private static final Image HEART = Main.getImage("/textures/hud/heart.png");
	private static final Image HALF_HEART = Main.getImage("/textures/hud/halfHeart.png");
	private static final Image GREY_HEART = Main.getImage("/textures/hud/greyHeart.png");

	private static final int HEALTH_RES = 20;
	protected static final int PLAYER_BASE_SPEED = 190;
	
	private static final long serialVersionUID = 1L;
	
	private static final String[] SPEECH_SOUNDS = {"/sound/arrow.wav"};
	private static final String[] DAMAGE_SOUNDS = {"/sound/arrow.wav"};
	private static final String[] DEATH_SOUNDS = {"/sound/arrow.wav"};
	
	private Inventory inventory;
	
	public Player(Vector pos) {
		
		super(pos, IDLE, WALKING);
		inventory = new Inventory(this);
		z = PLAYER_LAYER;
		
		speechSounds = SPEECH_SOUNDS;
		damageSounds = DAMAGE_SOUNDS;
		deathSounds = DEATH_SOUNDS;
		
		//default weapons
		Weapon sling = (Weapon) Item.newItem(ItemType.SLING);
		Orb o1 = (Orb) Item.newItem(ItemType.PELLET);
		Orb o2 = (Orb) Item.newItem(ItemType.PELLET);
		Papyrus p1 = (Papyrus) Item.newItem(ItemType.PAPYRUS);
		Papyrus p2 = (Papyrus) Item.newItem(ItemType.PAPYRUS);
		Papyrus p3 = (Papyrus) Item.newItem(ItemType.PAPYRUS);
		
		//equip items in inventory
		inventory.add(sling);
		inventory.add(o1);
		inventory.add(o2);
		inventory.add(p1);
		inventory.add(p2);
		inventory.add(p3);
		
		tag = "player";
		
	}
	
	@Override
	public void update(World world, double deltaTime) {
				
		if (Debug.LOG_FOCUS) {
			Main.log("main player updated");
		}
		
		FocusedWindow window = (FocusedWindow) Main.getWindow();
		super.update(world, deltaTime);
		inventory.update(deltaTime);
				
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				walkNormalized(world, window.getMovementVector(), deltaTime);
			}
			
		});
		
		//check if we're hitting a desk
		if (Key.SPACE.isPressed()) {
			Entity interactRegion = new Entity(pos, Unit.RADIUS + Desk.INTERACT_RANGE);
			for (Entity entity : world.getEntityTree().getAllCollisions(interactRegion)) {
				if (entity instanceof Desk && Main.getWindow() instanceof GameWindow) {
					((GameWindow) Main.getWindow()).openInventory();
					return;
				}
			}
		}
		
		inventory.checkKeys();
		
		Vector movement = window.getMovementVector();
		Vector dir = window.getShotDirection();
		inventory.attack(world, this, movement, dir);
				
	}
	
	@Override
	public Object get(World world, AbstractSemantics attr) {

		if (attr == AbstractSemantics.WEAPON) {
			return inventory.getWeapon();
		}
		
		return super.get(world, attr);
		
	}
	
	@Override
	public Inventory getInventory() {
		return inventory;
	}
	
	public double getHearts() {
		return getHealth() / MAX_HEALTH * HEALTH_RES;
	}
	
	public Vector getHealthBarPos() {
		return new Vector(((GameWindow) Main.getWindow()).getDimensions().getX() - GamePanel.MARGIN - HEALTH_RES * HEART.getWidth(null), GamePanel.MARGIN);
	}
	
	public void renderHealthBar(Graphics g) {
		
		Vector pos = getHealthBarPos();
		
		for (int i = 0; i < HEALTH_RES; i++) {
			
			//figure out a better way to do this
						
			if (getHearts() >= i) {
				g.drawImage(HEART, pos.getX(), pos.getY(), null);
			} else if (getHearts() < i - 1 || getHearts() - (int) getHearts() < 0.5d) {
				g.drawImage(GREY_HEART, pos.getX(), pos.getY(), null);
			} else {
				g.drawImage(HALF_HEART, pos.getX(), pos.getY(), null);
			}

			pos.add(HEART.getWidth(null), 0);
		}
		
	}
	
	@Override
	public int getBaseSpeed() {
		return PLAYER_BASE_SPEED;
	}

	@Override
	public Vector getAimPosition() {
		return ((FocusedWindow) Main.getWindow()).getMousePosAbsolute();
	}

}
