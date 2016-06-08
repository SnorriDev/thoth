package snorri.main;

import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.io.IOException;

import snorri.entities.Entity;
import snorri.world.Tile;
import snorri.world.Vector;
import snorri.world.World;

public class LevelEditor extends FocusedWindow {
	
	private static final long serialVersionUID = 1L;

	private World world;
	private Entity focus;
	private boolean openingFile = false;
		
	public LevelEditor() {
		super();
		createButton("New");
		createButton("Load");
		createButton("Save");
		focus = new Entity(new Vector(50, 50));
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		
		
		switch (e.getActionCommand()) {
		case "New":
			world = new World();
			break;
		case "Load":
			try {
				openingFile = true;
				world = new World(Main.getFileDialog("Select file to load"));
				openingFile = false;
			} catch (IOException ex) {
				 Main.error("error loading file");
			}
			break;
		case "Save":
			
			if (world == null) {
				return;
			}
			
			try {
				openingFile = true;
				world.save(Main.getFileDialog("Select save destination"));
				openingFile = false;
			} catch (IOException ex) {
				Main.error("error saving file");
			}
		}
		
		repaint();
		
	}
	
	@Override
	public void paintComponent(Graphics g) {
		
		super.paintComponent(g);
		
		if (world == null) {
			return;
		}
		
		world.render(this, g, false);
		
	}
	
	@Override
	protected void onFrame() {
				
		if (world != null) {
			focus.getPos().add(states.getMovementVector().scale(10));
		}
		
		repaint();
		if (!openingFile)
			requestFocus();
		
	}

	@Override
	public Entity getFocus() {
		return focus;
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
	public void mousePressed(MouseEvent e) {
		Vector location = getMousePosAbsolute().copy();
		int x = location.getX();
		int y = location.getY();
		Main.log(x + "\t" + y);
		Main.log(world.getLevel().getTile(x, y).getType() + "\t" + world.getLevel().getTile(x, y).getStyle());
		world.getLevel().setTile(x,y,new Tile(1,1));
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyTyped(KeyEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	
	//TOBY do stuff here!
}
