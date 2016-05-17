package snorri.main;

import java.awt.Color;
import java.awt.Graphics;

import javax.swing.JPanel;

import snorri.entities.Entity;
import snorri.entities.EntityGroup;

public class GameWindow extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private EntityGroup col;
	private Entity focus;
	
	public GameWindow(EntityGroup col, Entity focus) {
		this.col = col;
		this.focus = focus;
	}
	
	@Override
	public void paintComponent(Graphics g){
		super.paintComponent(g);
		setBackground(Color.WHITE);
		col.renderHitbox(g);
		//TODO: function which renders everything intersecting a circle inscribing the screen
	}
	
}
