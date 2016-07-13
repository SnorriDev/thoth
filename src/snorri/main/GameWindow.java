package snorri.main;

import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

import snorri.entities.Detector;
import snorri.entities.Desk;
import snorri.entities.Entity;
import snorri.entities.Player;
import snorri.entities.Unit;
import snorri.keyboard.Key;
import snorri.overlay.PauseOverlay;
import snorri.world.Playable;
import snorri.world.Vector;
import snorri.world.World;

public class GameWindow extends FocusedWindow {
	
	//https://docs.oracle.com/javase/7/docs/api/javax/swing/JLayeredPane.html
	//http://stackoverflow.com/questions/8776540/painting-over-the-top-of-components-in-swing
	//TODO perhaps restructure all this stuff
	//TODO rather than having a render method for pause HUD, override paintComponent and paint other things (buttons)
	
	/**
	 * Main game window
	 */
	private static final long serialVersionUID = 1L;
	
	public static final int MARGIN = 20;
	
	private final PauseOverlay pauseOverlay;
	private Playable universe;
	private Player focus;
	private boolean paused, editingInventory;
	private long lastTime;
		
	public GameWindow(Playable universe, Player focus) {
		super();
		this.universe = universe;
		this.focus = focus;
		lastTime = getTimestamp();
		paused = false;
		editingInventory = false;
		pauseOverlay = new PauseOverlay(this);
	}
	
	public GameWindow(Playable universe) {
		this(universe, universe.computeFocus());
	}
	
	@Override
	protected void onFrame() {
				
		if (universe.getCurrentWorld() == null) {
			return;
		}
				
		long time = getTimestamp();
		double deltaTime = (time - lastTime) / 1000000000d;
		lastTime = time;
		
		if (deltaTime > 0.2) { //this is shitty
			Main.log("high delta time detected (" + deltaTime + " sec)");
		}
		
		if (isPaused() || isEditingInventory()) {
			//TODO draw menu when paused or something
			return;
		}
		
		universe.getCurrentWorld().update(deltaTime);
		repaint();
				
	}
	
	@Override
	public void paintComponent(Graphics g){
		
		if (focus == null) {
			return;
		}
		super.paintComponent(g);
		universe.getCurrentWorld().render(this, g, true);
		focus.getInventory().render(this, g);
		focus.renderHealthBar(g);
		
		//draw inventory HUD over game background
		if (isEditingInventory()) {
			//TODO: figure out how to do this
			focus.getFullInventory().paint(null);
		}
		
		//draw pause HUD over inventory HUD
		if (isPaused()) {
			//TODO draw pause HUD
		}
		
	}
	
	public Player getFocus() {
		return focus;
	}
	
	@Override
	public World getWorld() {
		return universe.getCurrentWorld();
	}
	
	@Override
	public Playable getUniverse() {
		return universe;
	}
	
	@Override
	public void keyPressed(KeyEvent e) {
		
		super.keyPressed(e);
		if (Key.ESC.isPressed(e)) {
			togglePause();
		}
		
		if (focus == null || focus.isDead()) {
			return;
		}
		
		if (Key.SPACE.isPressed(e)) {
			Detector interactRegion = new Detector(getFocus().getPos(), Unit.RADIUS + Desk.INTERACT_RANGE);
			//could make this more efficient potentially by making a new method
			//also move to its own thing for organization?
			for (Entity entity : universe.getCurrentWorld().getEntityTree().getAllCollisions(interactRegion)) {
				if (entity instanceof Desk) {
					Main.log("interacting with a desk");
					return;
				}
			}
		}
				
		if (Key.ONE.isPressed(e)) {
			focus.getInventory().selectOrb(0);
		}
		if (Key.TWO.isPressed(e)) {
			focus.getInventory().selectOrb(1);
		}
		if (Key.THREE.isPressed(e)) {
			focus.getInventory().usePapyrus(0);
		}
		if (Key.FOUR.isPressed(e)) {
			focus.getInventory().usePapyrus(1);
		}
		if (Key.FIVE.isPressed(e)) {
			focus.getInventory().usePapyrus(2);
		}
		
	}
	
	public void togglePause() {
		if (paused) {
			Main.setOverlay(null);
		} else {
			Main.setOverlay(pauseOverlay);
		}
		paused = !paused;
	}
	
	public boolean isPaused() {
		return paused;
	}
	
	public boolean isEditingInventory() {
		return editingInventory;
	}

	public void mousePressed(MouseEvent e) {
		
		if (focus == null) {
			return;
		}
		
		if (e.getButton() == 1) {
			//TODO: put this stuff in a shoot function, shoot with mouse
			
			Vector dir = getMousePosRelative().copy().normalize();
			
			if (dir.notInPlane()) {
				return;
			}
			
			focus.getInventory().tryToShoot(universe.getCurrentWorld(), focus, states.getMovementVector(), dir);
			
		}
		
	}

	@Override
	public void mouseClicked(MouseEvent e) {
	}

	@Override
	public void mouseEntered(MouseEvent e) {
	}

	@Override
	public void mouseExited(MouseEvent e) {
	}

	@Override
	public void mouseReleased(MouseEvent e) {
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
	}

	@Override
	public void keyTyped(KeyEvent e) {
	}
	
}
