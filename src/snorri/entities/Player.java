package snorri.entities;

import java.awt.Graphics;
import java.awt.Image;

import snorri.animations.Animation;
import snorri.inventory.FullInventory;
import snorri.inventory.Inventory;
import snorri.inventory.Item;
import snorri.inventory.Item.ItemType;
import snorri.inventory.Orb;
import snorri.inventory.Papyrus;
import snorri.inventory.Weapon;
import snorri.main.Debug;
import snorri.main.GameWindow;
import snorri.main.Main;
import snorri.parser.Grammar;
import snorri.world.Vector;
import snorri.world.World;

public class Player extends Unit {

	private static final Image HEART = Main.getImageResource("/textures/hud/heart.png");
	private static final Image HALF_HEART = Main.getImageResource("/textures/hud/halfHeart.png");
	private static final Image GREY_HEART = Main.getImageResource("/textures/hud/greyHeart.png");
	private static final int HEALTH_RES = 20;
	
	private static final long serialVersionUID = 1L;
	
	private Inventory inventory;
	
	public Player(Vector pos) {
		
		super(pos);
		inventory = new Inventory(this);
		animation = new Animation(Animation.UNIT_IDLE);
		
		//test inventory
		inventory.setWeapon((Weapon) Item.newItem(ItemType.SLING));
		inventory.getWeapon().setSpell(Grammar.parseString("nD wi"));
		
		inventory.addPapyrus((Papyrus) Item.newItem(ItemType.PAPYRUS));
		inventory.addPapyrus((Papyrus) Item.newItem(ItemType.PAPYRUS));
		inventory.getPapyrus(0).setSpell(Grammar.parseString("pA wrt aAw"));
		inventory.getPapyrus(1).setSpell(Grammar.parseString("nD wi"));
		
		inventory.addOrb((Orb) Item.newItem(ItemType.PELLET));
		inventory.getOrb(0).setSpell(Grammar.parseString("bm m=k"));
		
	}
	
	@Override
	public void update(World world, double deltaTime) {
		
		if (Debug.LOG_FOCUS) {
			Main.log("main player updated");
		}
		
		super.update(world, deltaTime);
		inventory.update(deltaTime);
		walk(world, ((GameWindow) Main.getWindow()).getMovementVector(), deltaTime);
	}
	
	@Override
	public Object get(World world, AbstractSemantics attr) {

		if (attr == AbstractSemantics.WEAPON) {
			return inventory.getWeapon();
		}
		
		return super.get(world, attr);
		
	}
	
	public Inventory getInventory() {
		return inventory;
	}
	
	public double getHearts() {
		return getHealth() / MAX_HEALTH * HEALTH_RES;
	}
	
	public void renderHealthBar(Graphics g) {
		
		Vector pos = new Vector(((GameWindow) Main.getWindow()).getDimensions().getX() - GameWindow.MARGIN - HEALTH_RES * HEART.getWidth(null), GameWindow.MARGIN);
		
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

	public FullInventory getFullInventory() {
		return inventory.getFullInventory();
	}

}
