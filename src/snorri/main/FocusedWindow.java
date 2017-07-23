package snorri.main;

import java.awt.MouseInfo;
import java.awt.event.FocusEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.lang.Thread.UncaughtExceptionHandler;

import javax.swing.SwingUtilities;

import snorri.dialog.Dialog;
import snorri.entities.Entity;
import snorri.inventory.Inventory;
import snorri.keyboard.Key;
import snorri.keyboard.KeyStates;
import snorri.keyboard.MouseButton;
import snorri.main.Main.ResizeListener;
import snorri.overlay.DialogOverlay;
import snorri.overlay.InventoryOverlay;
import snorri.overlay.PauseOverlay;
import snorri.world.Playable;
import snorri.world.Vector;
import snorri.world.World;

public abstract class FocusedWindow<F extends Entity> extends GamePanel implements MouseListener, KeyListener {
	
	private static final long serialVersionUID = 1L;
	private static final int FRAME_DELTA = 15; // 33 -> 30 FPS (20 -> 50 FPS
		
	protected final KeyStates states = new KeyStates();
	protected final F focus;
	
	protected long lastRenderTime;
	private boolean paused = false, stopped = false;
	
	public FocusedWindow(F focus) {
		super();
		setFocusable(true);
		addMouseListener(this);
		addKeyListener(this);
		this.focus = focus;
		lastRenderTime = getTimestamp();
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
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				Main.setOverlay(new DialogOverlay(FocusedWindow.this, dialog));
				paused = true;
			}
		});
	}
	
	public synchronized void openInventory(Inventory inv) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				Main.setOverlay(new InventoryOverlay(FocusedWindow.this, inv));
				paused = true;
			}
		});
	}
	
	public synchronized void editInventory(Inventory inv) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				Main.setOverlay(new InventoryOverlay(FocusedWindow.this, inv, true));
				paused = true;
			}
		});
	}
	
	public boolean isPaused() {
		return paused;
	}
	
	public F getFocus() {
		return focus;
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
	
	public static double getBaseDelta() {
		return FRAME_DELTA / 1000d;
	}
	
	@Override
	public void startBackgroundThread() {

		Thread thread = new Thread(new Runnable() {

			@Override @SuppressWarnings("unused")
			public void run() {
				onStart();
				try {
					while (!stopped) {
						if (Debug.LOG_PAUSES && isPaused()) {
							Debug.log("paused");
						}
						onFrame();
						Thread.sleep(FRAME_DELTA);
					}
				} catch (InterruptedException e) {
					Debug.error(e);
				}
			}
		});
		
		thread.setUncaughtExceptionHandler(new UncaughtExceptionHandler() {
			@Override
			public void uncaughtException(Thread t, Throwable e) {
				Debug.error(e);
			}
		});
		thread.start();

	}
	
	@Override
	public void stopBackgroundThread() {
		stopped = true;
	}
	
	protected abstract void onStart();

	protected abstract void onFrame();

	public abstract World getWorld();

	public abstract Playable getUniverse();
	
	public KeyStates getKeyStates() {
		return states;
	}
	
	@Override
	public void focusGained(FocusEvent e) {
		ResizeListener.resize(this);
		startBackgroundThread();
	}
		
}
