package snorri.main;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.MouseInfo;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Date;

import javax.swing.SwingWorker;

import snorri.entities.Entity;
import snorri.entities.Projectile;
import snorri.entities.Unit;
import snorri.events.SpellEvent;
import snorri.keyboard.Key;
import snorri.keyboard.KeyStates;
import snorri.parser.Spell;
import snorri.world.Vector;
import snorri.world.World;

public class GameWindow extends GamePanel implements KeyListener, MouseListener {

	
	/**
	 * Main game window
	 */
	private static final long serialVersionUID = 1L;
	private static final int FRAME_DELTA = 30;
	
	private KeyStates states;
	
	private World world;
	private Unit focus;
	private long lastTime;
	
	public GameWindow(World world, Unit focus) {
		this.world = world;
		this.focus = focus;
		states = new KeyStates();
		addMouseListener(this);
		addKeyListener(this);
		setFocusable(true);
		startAnimation();
	}
	
	public void startAnimation() {
		
		SwingWorker<Object, Object> sw = new SwingWorker<Object, Object>() {
			@Override
			protected Object doInBackground() throws Exception {
				while (true) {
					onFrame();
					Thread.sleep(FRAME_DELTA);
				}
			}
		};

		lastTime = getTimestamp();
		sw.execute();
	}
	
	//TODO: pass a timeDelta argument to update()
	private void onFrame() {
		focus.walk(states.getMovementVector(), world.getEntityTree()); //TODO: move to update method of Player?
		long time = getTimestamp();
		world.update((time - lastTime) / 1000f);
		lastTime = time;
		repaint();
	}
	
	private long getTimestamp() {
		Date date = new Date();
		return date.getTime();
	}
	
	@Override
	public void paintComponent(Graphics g){
		super.paintComponent(g);
		setBackground(Color.WHITE);
		world.render(this, g);
		//TODO: function which renders everything intersecting a circle inscribing the screen
	}
	
	public Entity getFocus() {
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
		origin.add((new Vector(getBounds())).divide(2));		
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
			//Cast a spell with space bar for debugging purposes	
			Spell.castWTFMode("iry=i", new SpellEvent(this, getFocus()));	
		}
		
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
			
			world.add(new Projectile(focus, states.getMovementVector(), dir));
		}
		
	}

	public void mouseReleased(MouseEvent e) {		
	}

	public void mouseClicked(MouseEvent e) {
	}
	
}
