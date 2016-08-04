package snorri.main;

import java.awt.MouseInfo;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseListener;

import snorri.entities.Entity;
import snorri.inventory.Inventory;
import snorri.keyboard.KeyStates;
import snorri.overlay.InventoryOverlay;
import snorri.overlay.PauseOverlay;
import snorri.world.Playable;
import snorri.world.Vector;
import snorri.world.World;

public abstract class FocusedWindow extends GamePanel implements MouseListener, KeyListener {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	protected KeyStates states = new KeyStates();
	private boolean paused = false;

	public FocusedWindow() {
		addMouseListener(this);
		addKeyListener(this);
		setFocusable(true);
		startAnimation();
	}
	
	public void pause() {
		Main.setOverlay(new PauseOverlay(this));
		paused = true;
	}
	
	public void unpause() {
		Main.setOverlay(null);
		paused = false;
	}
	
	public void openInventory(Inventory inv) {
		Main.setOverlay(new InventoryOverlay(this, inv));
		paused = true;
	}
	
	public boolean isPaused() {
		return paused;
	}
	
	public abstract Entity getFocus();
	
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
	
	public Vector getMovementVector() {
		return states.getMovementVector();
	}

	public abstract World getWorld();

	public abstract Playable getUniverse();
		
}
