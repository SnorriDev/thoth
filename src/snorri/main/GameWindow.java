package snorri.main;

import java.awt.Graphics;
import java.awt.MouseInfo;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import snorri.entities.Collider;
import snorri.entities.Desk;
import snorri.entities.Entity;
import snorri.entities.Player;
import snorri.keyboard.Key;
import snorri.keyboard.KeyStates;
import snorri.world.Vector;
import snorri.world.World;

public class GameWindow extends FocusedWindow implements KeyListener, MouseListener {
	
	/**
	 * Main game window
	 */
	private static final long serialVersionUID = 1L;
	
	public static final int MARGIN = 20;
	
	private KeyStates states;
	
	private World world;
	private Player focus;
	private long lastTime;
	
	public GameWindow(World world, Player focus) {
		this.world = world;
		this.focus = focus;
		states = new KeyStates();
		addMouseListener(this);
		addKeyListener(this);
		setFocusable(true);
		lastTime = getTimestamp();
		startAnimation();
	}
	
	public Vector getMovementVector() {
		return states.getMovementVector();
	}
	
	@Override
	protected void onFrame() {
		
		if (! focus.isDead()) {
			
			//if (! focus.wouldHitSomething(states.getMovementVector(), world.getEntityTree())) this is buggy
			focus.walk(states.getMovementVector(), world.getEntityTree()); //TODO: move to update method of Player?
			
		}
				
		long time = getTimestamp();
		world.update((time - lastTime) / 1000000000f);
		lastTime = time;
		repaint();
				
	}
	
	@Override
	public void paintComponent(Graphics g){
		super.paintComponent(g);
		world.render(this, g, true);
		focus.getInventory().render(this, g);
		focus.renderHealthBar(g);
	}
	
	public Player getFocus() {
		return focus;
	}
	
	public World getWorld() {
		return world;
	}
	
	/**
	 * @return mouse position relative to the player
	 */
	public Vector getMousePosRelative() {
		Vector origin = new Vector(getLocationOnScreen());
		origin.add(getDimensions().divide(2));		
		return (new Vector(MouseInfo.getPointerInfo().getLocation())).sub(origin);
	}
	
	/**
	 * @return absolute mouse position
	 */
	public Vector getMousePosAbsolute() {
		return getMousePosRelative().add(getFocus().getPos());
	}

	public void keyPressed(KeyEvent e) {
		states.set(e.getKeyCode(), true);
	}

	public void keyReleased(KeyEvent e) {
		states.set(e.getKeyCode(), false);
	}
	
	public void keyTyped(KeyEvent e) {
		
		if (e.getKeyChar() == Key.SPACE.getChar()) {
			Collider interactRegion = new Collider(getFocus().getPos(), getFocus().getRadius() + Desk.INTERACT_RANGE);
			//could make this more efficient potentially by making a new method
			//also move to its own thing for organization?
			for (Entity entity : world.getEntityTree().getAllCollisions(interactRegion)) {
				if (entity instanceof Desk) {
					Main.log("interacting with a desk");
					return;
				}
			}
		}
		
		if (e.getKeyChar() == Key.ONE.getChar()) {
			focus.getInventory().selectProjectile(0);
		}
		
		if (e.getKeyChar() == Key.TWO.getChar()) {
			focus.getInventory().selectProjectile(1);
		}
		
		if (e.getKeyChar() == Key.THREE.getChar()) {
			focus.getInventory().usePapyrus(0);
		}
		
		if (e.getKeyChar() == Key.FOUR.getChar()) {
			focus.getInventory().usePapyrus(1);
		}
		
		if (e.getKeyChar() == Key.FIVE.getChar()) {
			focus.getInventory().usePapyrus(2);
		}
		
//		if (e.getKeyChar() == Key.Q.getChar()) {
//			//Cast a spell with space bar for debugging purposes	
//			Spell.castWTFMode("bm m=f", new SpellEvent(this, getFocus()));	
//		}
		
	}

	public void mouseEntered(MouseEvent e) {
	}

	public void mouseExited(MouseEvent e) {
	}

	public void mousePressed(MouseEvent e) {
		
		if (e.getButton() == 1) {
			//TODO: put this stuff in a shoot function, shoot with mouse
			
			Vector dir = getMousePosRelative().copy().normalize();
			
			if (dir.notInPlane()) {
				return;
			}
			
			focus.getInventory().tryToShoot(world, focus, states.getMovementVector(), dir);
			
		}
		
	}

	public void mouseReleased(MouseEvent e) {		
	}

	public void mouseClicked(MouseEvent e) {
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	
}
