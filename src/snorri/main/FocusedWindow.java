package snorri.main;

import java.awt.MouseInfo;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import snorri.entities.Entity;
import snorri.inventory.Inventory;
import snorri.keyboard.Key;
import snorri.keyboard.KeyStates;
import snorri.keyboard.MouseButton;
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
		states.purge();
		paused = true;
	}
	
	public void unpause() {
		Main.setOverlay(null);
		paused = false;
	}
	
	public void openInventory(Inventory inv) {
		Main.setOverlay(new InventoryOverlay(this, inv));
		states.purge();
		paused = true;
	}
	
	public void editInventory(Inventory inv) {
		Main.setOverlay(new InventoryOverlay(this, inv, true));
		states.purge();
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
	
	@Override
	public void keyPressed(KeyEvent e) {
		states.set(e.getKeyCode(), true);
	}

	@Override
	public void keyReleased(KeyEvent e) {
		states.set(e.getKeyCode(), false);
	}
	
	@Override
	public void mousePressed(MouseEvent e) {
		states.setMouseButton(e.getButton(), true);
	}
	
	@Override
	public void mouseReleased(MouseEvent e) {
		states.setMouseButton(e.getButton(), false);
	}
	
	public Vector getMovementVector() {
		return states.getMovementVector();
	}
	
	public Vector getShotDirection() {
		
		if (states.get(MouseButton.SHOOT)) {
			return getMousePosRelative().copy().normalize();
		}
		
		if (states.get(Key.SHOOT_LEFT)) {
			return new Vector(-1, 0);
		}
		
		if (states.get(Key.SHOOT_RIGHT)) {
			return new Vector(1, 0);
		}
		
		if (states.get(Key.SHOOT_DOWN)) {
			return new Vector(0, 1);
		}
		
		if (states.get(Key.SHOOT_UP)) {
			return new Vector(0, -1);
		}
		
		return null;
		
	}

	public abstract World getWorld();

	public abstract Playable getUniverse();
		
}
