package snorri.main;

import java.awt.MouseInfo;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseListener;

import snorri.entities.Entity;
import snorri.keyboard.KeyStates;
import snorri.world.Vector;
import snorri.world.World;

public abstract class FocusedWindow extends GamePanel implements MouseListener, KeyListener {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	protected KeyStates states = new KeyStates();

	public FocusedWindow() {
		addMouseListener(this);
		addKeyListener(this);
		setFocusable(true);
		startAnimation();
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
}
