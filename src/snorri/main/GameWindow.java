package snorri.main;

import java.awt.Graphics;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.ArrayDeque;

import snorri.entities.Collider;
import snorri.entities.Desk;
import snorri.entities.Entity;
import snorri.entities.Player;
import snorri.keyboard.Key;
import snorri.pathfinding.PathNode;
import snorri.pathfinding.Pathfinder;
import snorri.pathfinding.Pathfinding;
import snorri.world.Playable;
import snorri.world.Vector;
import snorri.world.World;

public class GameWindow extends FocusedWindow implements Pathfinder {
	
	/**
	 * Main game window
	 */
	private static final long serialVersionUID = 1L;
	
	public static final int MARGIN = 20;
	
	private Playable universe;
	private Player focus;
	private long lastTime;
	
	//temporary for testing pathfinding
	private ArrayDeque<PathNode> path;
	
	public GameWindow(Playable universe, Player focus) {
		super();
		this.universe = universe;
		this.focus = focus;
		lastTime = getTimestamp();
	}
	
	public GameWindow(Playable universe) {
		this(universe, universe.getFocus());
	}
	
	@Override
	protected void onFrame() {
		
		if (! focus.isDead()) {
			
			//if (! focus.wouldHitSomething(states.getMovementVector(), world.getEntityTree())) this is buggy
			focus.walk(universe.getCurrentWorld(), states.getMovementVector()); //TODO: move to update method of Player?
			
		}
				
		long time = getTimestamp();
		universe.getCurrentWorld().update((time - lastTime) / 1000000000f);
		lastTime = time;
		repaint();
				
	}
	
	@Override
	public void paintComponent(Graphics g){
		super.paintComponent(g);
		universe.getCurrentWorld().render(this, g, true);
		focus.getInventory().render(this, g);
		focus.renderHealthBar(g);
		
		//temp for debugging pathfinding
		if (path == null) {
			return;
		}
		for (PathNode node : path) {
			node.render(g, this);
		}
		
	}
	
	public Player getFocus() {
		return focus;
	}
	
	public World getWorld() {
		return universe.getCurrentWorld();
	}
	
	public void keyTyped(KeyEvent e) {
		
		if (e.getKeyChar() == Key.Q.getChar()) {
			//TODO: it takes a while to DECIDE that there is no path somewhere.
			//perhaps circumvent this by storing a variable reachableFromSpawn in each Tile
			//we could also terminate the search after a certain amount of time
			//alternatively, if it's running in another thread, it doesn't really matter that much
			Pathfinding.setWorld(getWorld());
			Vector target = getMousePosAbsolute().toGridPos();
			if (getWorld().getLevel().getTileGrid(target) != null) {
				Pathfinding.setPathAsync(focus.getPos().copy().toGridPos(), target, this);
			}
		}
		
		if (e.getKeyChar() == Key.SPACE.getChar()) {
			Collider interactRegion = new Collider(getFocus().getPos(), getFocus().getRadius() + Desk.INTERACT_RANGE);
			//could make this more efficient potentially by making a new method
			//also move to its own thing for organization?
			for (Entity entity : universe.getCurrentWorld().getEntityTree().getAllCollisions(interactRegion)) {
				if (entity instanceof Desk) {
					Main.log("interacting with a desk");
					return;
				}
			}
		}
		
		if (e.getKeyChar() == Key.ONE.getChar()) {
			focus.getInventory().selectOrb(0);
		}
		
		if (e.getKeyChar() == Key.TWO.getChar()) {
			focus.getInventory().selectOrb(1);
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

	public void mousePressed(MouseEvent e) {
		
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
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void setPath(ArrayDeque<PathNode> stack) {
		path = stack;
	}
	
}
