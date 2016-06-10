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
import javax.swing.JOptionPane;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;

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

		selectedTile = new Tile(0, 0);
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

		// Tile Selection Menu
		menu = new JMenu("Select Tile");
		menu.setMnemonic(KeyEvent.VK_T);
		menuBar.add(menu);

		ButtonGroup group = new ButtonGroup();

		boolean first = true;
		for (Tile t : Tile.getAll()) {

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
			String widthString = (String) JOptionPane.showInputDialog(this, "What would you like the map width to be?",
					"Width", JOptionPane.PLAIN_MESSAGE);
			String heightString = (String) JOptionPane.showInputDialog(this,
					"What would you like the map height to be?", "Height", JOptionPane.PLAIN_MESSAGE);

			int width = Integer.parseInt(widthString);
			int height = Integer.parseInt(heightString);

			int maxSize = 1024; // TODO: we might want to put this somewhere
								// else
			if (width > 0 && height > 0 && width <= maxSize && height <= maxSize) {
				world = new World(width, height);
			}
			else {
				world = new World();
			}
			break;
		case "Open":
			openingFile = true;
			World w1 = World.wrapLoad();
			if (w1 != null) {
				world = w1;
			}
			openingFile = false;
			break;
		case "Save":

			if (world == null) {
				return;
			}

			openingFile = true;
			world.wrapSave();
			openingFile = false;
		}

		repaint();

	}

	@Override
	public void paintComponent(Graphics gr) {

		super.paintComponent(gr);

		if (world == null) {
			return;
		}

		world.render(this, gr, false);
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
		if (SwingUtilities.isRightMouseButton(e)) {
			fill();
		}
	}

	@Override
	public void mouseEntered(MouseEvent e) {

	}

	@Override
	public void mouseExited(MouseEvent e) {

	}

	@Override
	public void mousePressed(MouseEvent e) {
		if (SwingUtilities.isLeftMouseButton(e)) {
			isClicking = true;
		}
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

	public void fill() {
		Vector location = getMousePosAbsolute().copy();
		int x = location.getX() / Tile.WIDTH;
		int y = location.getY() / Tile.WIDTH;

		Tile t = world.getLevel().getNewTileGrid(x, y);
		if (selectedTile != null && world.getLevel().getNewTileGrid(x, y) != null && t != null
				&& !t.equals(selectedTile)) {
			world.getLevel().setTileGrid(x, y, selectedTile);
			fill_helper(x + 1, y, t);
			fill_helper(x - 1, y, t);
			fill_helper(x, y + 1, t);
			fill_helper(x, y - 1, t);
		}
	}

	public void fill_helper(int x, int y, Tile t) {
		if (selectedTile != null && world.getLevel().getNewTileGrid(x, y) != null && t != null
				&& world.getLevel().getNewTileGrid(x, y).equals(t)) {
			world.getLevel().setTileGrid(x, y, selectedTile);
			fill_helper(x + 1, y, t);
			fill_helper(x - 1, y, t);
			fill_helper(x, y + 1, t);
			fill_helper(x, y - 1, t);
		}
	}
}
