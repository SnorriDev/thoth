package snorri.main;

import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.io.IOException;

import snorri.entities.Entity;
import snorri.world.Vector;
import snorri.world.World;

public class LevelEditor extends FocusedWindow {
	
	private static final long serialVersionUID = 1L;

	private World world;
	private Entity focus;
	
	public LevelEditor() {
		createButton("New");
		createButton("Load");
		createButton("Save");
		focus = new Entity(new Vector(50, 50));
		startAnimation();
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		
		switch (e.getActionCommand()) {
		case "New":
			world = new World();
			break;
		case "Load":
			try {
				world = new World(Main.getFileDialog("Select file to load"));
			} catch (IOException ex) {
				 Main.error("error loading file");
			}
			break;
		case "Save":
			
			if (world == null) {
				return;
			}
			
			try {
				world.save(Main.getFileDialog("Select save destination"));
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
	public Entity getFocus() {
		return focus;
	}
	
	//TOBY do stuff here!
}
