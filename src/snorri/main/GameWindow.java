package snorri.main;

import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.LinkedList;
import java.util.Queue;

import snorri.entities.Detector;
import snorri.entities.Desk;
import snorri.entities.Entity;
import snorri.entities.Player;
import snorri.entities.Unit;
import snorri.keyboard.Key;
import snorri.overlay.DeathOverlay;
import snorri.overlay.InventoryOverlay;
import snorri.overlay.PauseOverlay;
import snorri.world.Playable;
import snorri.world.Vector;
import snorri.world.World;

public class GameWindow extends FocusedWindow {
		
	/**
	 * Main game window
	 */
	private static final long serialVersionUID = 1L;
	
	public static final int MARGIN = 20;
	
	private Playable universe;
	private Player focus;
	private Queue<DialogMessage> dialogQ;
	private boolean paused, editingInventory, hasDied;
	private long lastTime;
		
	public GameWindow(Playable universe, Player focus) {
		super();
		this.universe = universe;
		this.focus = focus;
		dialogQ = new LinkedList<DialogMessage>();
		lastTime = getTimestamp();
		paused = false;
		editingInventory = false;
		hasDied = false;
	}
	
	public GameWindow(Playable universe) {
		this(universe, universe.computeFocus());
	}
	
	@Override
	protected void onFrame() {
				
		long time = getTimestamp();
		double deltaTime = (time - lastTime) / 1000000000d;
		lastTime = time;
		
		if (deltaTime > 0.2) { //this is shitty
			Main.log("high delta time detected (" + deltaTime + " sec)");
		}
		
		if (dialogQ.peek() != null && dialogQ.peek().update(deltaTime)) {
			dialogQ.poll();
		}
				
		if (isPaused() || isEditingInventory()) {
			return;
		}
		
		if (!hasDied && focus != null && focus.isDead()) {
			hasDied = true;
			Main.setOverlay(new DeathOverlay());
		}
		
		if (universe == null || universe.getCurrentWorld() == null) {
			return;
		}
		
		repaint();
		synchronized (this) {
			universe.getCurrentWorld().update(deltaTime);
		}
				
	}
	
	@Override
	public void paintComponent(Graphics g){
				
		if (focus == null) {
			return;
		}
		
		super.paintComponent(g);
		
		synchronized (this) {
			universe.getCurrentWorld().render(this, g, true);
			focus.getInventory().render(this, g);
			focus.renderHealthBar(g);
		}
		
		int i = 0;
		for (DialogMessage msg : dialogQ) {
			msg.render(this, g, i);
			i++;
		}
		
	}
	
	public Player getFocus() {
		return focus;
	}
	
	public void showDialog(String msg) {
		dialogQ.add(new DialogMessage(msg));
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
			pause();
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
					openInventory();
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
	
	public synchronized void pause() {
		paused = true;
		Main.setOverlay(new PauseOverlay(this));
	}
	
	public synchronized void unpause() {
		paused = false;
		Main.setOverlay(null);
	}
	
	public synchronized void openInventory() {
		editingInventory = true;
		Main.setOverlay(new InventoryOverlay(this, focus.getInventory()));
	}
	
	public synchronized void closeInventory() {
		editingInventory = false;
		Main.setOverlay(null);
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
