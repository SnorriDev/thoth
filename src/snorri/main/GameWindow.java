package snorri.main;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JPanel;
import javax.swing.SwingWorker;

import snorri.entities.Entity;
import snorri.entities.EntityGroup;
import snorri.keyboard.KeyStates;

public class GameWindow extends JPanel implements KeyListener {

	
	/**
	 * Main game window
	 */
	private static final long serialVersionUID = 1L;
	private static final int FRAME_DELTA = 50;
	
	private KeyStates states;
	
	private EntityGroup col;
	private Entity focus;
	
	public GameWindow(EntityGroup col, Entity focus) {
		this.col = col;
		this.focus = focus;
		states = new KeyStates();
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

		sw.execute();
	}
	
	private void onFrame() {
		focus.walk(states.getMovementVector(), col);
		repaint();
	}
	
	@Override
	public void paintComponent(Graphics g){
		super.paintComponent(g);
		setBackground(Color.WHITE);
		col.renderHitbox(this, g);
		//TODO: function which renders everything intersecting a circle inscribing the screen
	}
	
	public Entity getFocus() {
		return focus;
	}

	@Override
	public void keyPressed(KeyEvent e) {
		states.set(e.getKeyCode(), true);
		//Main.log(e.getKeyCode() + ": " + states.get(e.getKeyCode()));
	}

	@Override
	public void keyReleased(KeyEvent e) {
		states.set(e.getKeyCode(), false);
		//Main.log(e.getKeyCode() + ": " + states.get(e.getKeyCode()));
	}

	@Override
	public void keyTyped(KeyEvent e) {
		//required method in interface
	}
	
}
