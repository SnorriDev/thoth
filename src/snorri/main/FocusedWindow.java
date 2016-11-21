package snorri.main;

import java.awt.MouseInfo;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import snorri.dialog.Dialog;
import snorri.entities.Entity;
import snorri.inventory.Inventory;
import snorri.keyboard.Key;
import snorri.keyboard.KeyStates;
import snorri.keyboard.MouseButton;
import snorri.overlay.DialogOverlay;
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
	protected long lastRenderTime;
	private boolean paused = false;
	
	public FocusedWindow() {
		setFocusable(true);
		addMouseListener(this);
		addKeyListener(this);
		lastRenderTime = getTimestamp();
		startAnimation();
	}
	
	public synchronized void pause() {
		Main.setOverlay(new PauseOverlay(this));
		paused = true;
	}
	
	public synchronized void unpause() {
		Main.setOverlay(null);
		states.purge();
		lastRenderTime = getTimestamp();
		paused = false;
	}
	
	public synchronized void showDialog(Dialog dialog) {
		Main.setOverlay(new DialogOverlay(this, dialog));
		paused = true;
	}
	
	public synchronized void openInventory(Inventory inv) {
		Main.setOverlay(new InventoryOverlay(this, inv));
		paused = true;
	}
	
	public synchronized void editInventory(Inventory inv) {
		Main.setOverlay(new InventoryOverlay(this, inv, true));
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
	
	//TODO we can port all input to use KeyBindings instead of KeyListeners
	@Override
	public void setKeyBindings() {
//		Main.log("key bindings set");
//		for (Key k : Key.values()) {
//			getInputMap().put(KeyStroke.getKeyStroke(k.getCode(), 0), "press" + k.ordinal());
//			getInputMap().put(KeyStroke.getKeyStroke(k.getCode(), 0, true), "release" + k.ordinal());
//			getActionMap().put("press" + k.ordinal(), new KeyAction(k.getCode(), states, true));
//			getActionMap().put("release" + k.ordinal(), new KeyAction(k.getCode(), states, false));
//		}
	}
	
	public KeyStates getKeyStates() {
		return states;
	}
		
}
