package snorri.main;

import java.awt.FileDialog;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.io.IOException;

import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.KeyStroke;

import snorri.entities.Entity;
import snorri.world.Tile;
import snorri.world.Vector;
import snorri.world.World;

public class LevelEditor extends FocusedWindow implements ActionListener {

	private static final long serialVersionUID = 1L;

	private World world;
	private Entity focus;
	private boolean openingFile = false;
	
	private Tile selectedTile;
	private boolean isClicking = false;

	JMenuBar menuBar;
	JMenu menu, submenu;
	JMenuItem menuItem;
	JRadioButtonMenuItem rbMenuItem;
	JCheckBoxMenuItem cbMenuItem;

	public LevelEditor() {
		super();

		selectedTile = new Tile(0,0);
		createMenu();

		repaint();

		focus = new Entity(new Vector(50, 50));
	}

	private void createMenu() {
		// Create the menu bar.
		menuBar = new JMenuBar();

		// Build the first menu.
		menu = new JMenu("File");
		menu.setMnemonic(KeyEvent.VK_F);
		menuBar.add(menu);

		// a group of JMenuItems
		menuItem = new JMenuItem("New", KeyEvent.VK_N);
		menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, ActionEvent.CTRL_MASK));
		menuItem.addActionListener(this);
		menu.add(menuItem);
		
		menuItem = new JMenuItem("Open", KeyEvent.VK_O);
		menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, ActionEvent.CTRL_MASK));
		menuItem.addActionListener(this);
		menu.add(menuItem);
		
		menuItem = new JMenuItem("Save", KeyEvent.VK_S);
		menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK));
		menuItem.addActionListener(this);
		menu.add(menuItem);
		
		//Tile Selection Menu
		menu = new JMenu("Select Tile");
		menu.setMnemonic(KeyEvent.VK_T);
		menuBar.add(menu);
		
		ButtonGroup group = new ButtonGroup();

		boolean first = true;
		for(Tile t : Tile.getAll()) {
			
			if (t == null || t.getTexture() == null) {
				continue;
			}
			
			rbMenuItem = new JRadioButtonMenuItem(t.toString(), new ImageIcon(t.getTexture()));
			rbMenuItem.setSelected(first);
			rbMenuItem.setActionCommand("set" + t.toNumericString());
			rbMenuItem.addActionListener(this);
			group.add(rbMenuItem);
			menu.add(rbMenuItem);
			
			first = false;

		}

		Main.getFrame().setJMenuBar(menuBar);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		
		if (e.getActionCommand().startsWith("set")) {
			selectedTile = new Tile(e.getActionCommand().substring(3));			
			return;
		}
		
		switch (e.getActionCommand()) {
		case "New":
			world = new World();
			break;
		case "Open":
			openingFile = true;
			File file = Main.getFileDialog("Select file to load", FileDialog.LOAD);
			try {
				if (file != null)
					world = new World(file);
			} catch (IOException er) {
				Main.error("error opening world " + file.getName());
			}
			openingFile = false;
			break;
		case "Save":

			if (world == null) {
				return;
			}

			openingFile = true;
			File f = Main.getFileDialog("Select save destination", FileDialog.SAVE);
			try {
				if (f != null)
					world.save(f);
			} catch (IOException er) {
				Main.error("error saving world " + f.getName());
				Main.error("make sure all objects being saved are serializable");
				er.printStackTrace(); // keep this so we can easily tell what
										// non-serializable object is causing
										// the issue
			}
			openingFile = false;
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
			
			if (isClicking) {
				Vector location = getMousePosAbsolute().copy();
				int x = location.getX();
				int y = location.getY();
				
				world.getLevel().setTile(x, y, new Tile(selectedTile));
			}
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
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		
	}

	@Override
	public void mousePressed(MouseEvent e) {
		isClicking = true;
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		isClicking = false;
	}

	@Override
	public void keyTyped(KeyEvent arg0) {
		// TODO Auto-generated method stub

	}
	// TOBY do stuff here!
}
