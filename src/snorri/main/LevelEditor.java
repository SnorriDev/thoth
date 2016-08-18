package snorri.main;

import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;

import snorri.entities.Drop;
import snorri.entities.Entity;
import snorri.entities.Listener;
import snorri.entities.Player;
import snorri.entities.Portal;
import snorri.inventory.Carrier;
import snorri.keyboard.Key;
import snorri.world.Campaign.WorldId;
import snorri.world.Editable;
import snorri.world.Level;
import snorri.world.Playable;
import snorri.world.Tile;
import snorri.world.Vector;
import snorri.world.World;

//TODO: add image to world feature
//TODO: figure out why fill overflows

public class LevelEditor extends FocusedWindow implements ActionListener {

	private static final long serialVersionUID = 1L;

	private static final int SCALE_FACTOR = 10;

	private Editable env;
	private Entity focus;
	private Tile selectedTile;
	private Class<? extends Entity> selectedEntityClass;
	private boolean isClicking = false;

	private boolean canGoLeft;
	private boolean canGoRight;
	private boolean canGoUp;
	private boolean canGoDown;

	private boolean canUndo;
	private boolean canRedo;

	JMenuBar menuBar;
	JMenu menu, submenu;
	JMenuItem menuItem;
	JRadioButtonMenuItem rbMenuItem;
	JCheckBoxMenuItem cbMenuItem;

	public LevelEditor() {
		super();

		selectedTile = new Tile(0, 0);
		selectedEntityClass = Entity.EDIT_SPAWNABLE.get(0);
		createMenu();

		repaint();

		focus = new Entity(new Vector(50, 50));

		canUndo = false;
		canRedo = false;
	}

	private void createMenu() {

		menuBar = new JMenuBar();

		menu = new JMenu("File");
		menu.setMnemonic(KeyEvent.VK_F);
		menuBar.add(menu);

		menuItem = new JMenuItem("New", KeyEvent.VK_N);
		menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, ActionEvent.CTRL_MASK));
		menuItem.addActionListener(this);
		menu.add(menuItem);

//		menuItem = new JMenuItem("Generate", KeyEvent.VK_G);
//		menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_G, ActionEvent.CTRL_MASK));
//		menuItem.addActionListener(this);
//		menu.add(menuItem);
		
		//TODO select a generator from the generate list to make a new world
		
		menuItem = new JMenuItem("Open", KeyEvent.VK_O);
		menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, ActionEvent.CTRL_MASK));
		menuItem.addActionListener(this);
		menu.add(menuItem);
		
		menuItem = new JMenuItem("Open Level", KeyEvent.VK_L);
		menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_L, ActionEvent.CTRL_MASK));
		menuItem.addActionListener(this);
		menu.add(menuItem);

		menuItem = new JMenuItem("Save", KeyEvent.VK_S);
		menuItem.addActionListener(this);
		menu.add(menuItem);

		menuItem = new JMenuItem("Resize", KeyEvent.VK_R);
		menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R, ActionEvent.CTRL_MASK));
		menuItem.addActionListener(this);
		menu.add(menuItem);

		menuItem = new JMenuItem("Undo", KeyEvent.VK_Z);
		menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z, ActionEvent.CTRL_MASK));
		menuItem.addActionListener(this);
		menu.add(menuItem);

		menuItem = new JMenuItem("Redo", KeyEvent.VK_Y);
		menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Y, ActionEvent.CTRL_MASK));
		menuItem.addActionListener(this);
		menu.add(menuItem);
		
		menuItem = new JMenuItem("Quit", KeyEvent.VK_Q);
		menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, ActionEvent.CTRL_MASK));
		menuItem.addActionListener(this);
		menu.add(menuItem);

		// Tile Selection Menu
		menu = new JMenu("Select Tile");
		menu.setMnemonic(KeyEvent.VK_T);
		menuBar.add(menu);

		ButtonGroup groupTiles = new ButtonGroup();

		boolean firstTile = true;
		for (Tile t : Tile.getAll()) {

			if (t == null || t.getTexture() == null) {
				continue;
			}

			rbMenuItem = new JRadioButtonMenuItem(t.toString(), new ImageIcon(t.getTexture()));
			rbMenuItem.setSelected(firstTile);
			rbMenuItem.setActionCommand("set" + t.toNumericString());
			rbMenuItem.addActionListener(this);
			groupTiles.add(rbMenuItem);
			menu.add(rbMenuItem);

			firstTile = false;

		}

		menu = new JMenu("Select Entity");
		menu.setMnemonic(KeyEvent.VK_E);
		menuBar.add(menu);

		List<Class<? extends Entity>> entityClassList = Entity.EDIT_SPAWNABLE;
		ButtonGroup groupEntities = new ButtonGroup();

		boolean firstEntity = true;
		int i = 0;
		for (Class<? extends Entity> c : entityClassList) {

			// TODO: give entities image icons
			rbMenuItem = new JRadioButtonMenuItem(c.getSimpleName());
			rbMenuItem.setSelected(firstEntity);
			rbMenuItem.setActionCommand("spawn" + i);
			rbMenuItem.addActionListener(this);
			groupEntities.add(rbMenuItem);
			menu.add(rbMenuItem);
			
			firstEntity = false;
			i++;
		}

		Main.getFrame().setJMenuBar(menuBar);
	}

	private int[] whDialog() {
		int[] wh = { -1, -1 };
		DialogMap inputs = new DialogMap();
		inputs.put("Width", "150");
		inputs.put("Height", "150");
		if (dialog("World Dimensions", inputs) == null) {
			return null;
		}

		wh[0] = inputs.getInteger("Width");
		wh[1] = inputs.getInteger("Height");

		return wh;
	}

	@Override
	public void actionPerformed(ActionEvent e) {

		if (e.getActionCommand().startsWith("set")) {
			selectedTile = new Tile(e.getActionCommand().substring(3));
			return;
		}

		if (e.getActionCommand().startsWith("spawn")) {
			selectedEntityClass = Entity.EDIT_SPAWNABLE
					.get(Integer.parseInt(e.getActionCommand().substring(5)));
			return;
		}

		switch (e.getActionCommand()) {
		case "New":
			int[] wh = whDialog();

			if (wh == null) {
				return;
			}

			if (wh != null && wh[0] > 0 && wh[1] > 0 && wh[0] <= Level.MAX_SIZE && wh[1] <= Level.MAX_SIZE) {
				env = new World(wh[0], wh[1]);
			} else {
				env = new World();
			}
			break;
		case "Open":
			World w1 = World.wrapLoad();
			if (w1 != null) {
				env = w1;
			}
			break;
		case "Open Level":
			Level l1 = Level.wrapLoad();
			if (l1 != null) {
				env = l1;
			}
			break;
		case "Save":

			if (env == null) {
				return;
			}

			env.wrapSave();
			break;
		case "Resize":
			if (env == null) {
				return;
			}

			int[] whNew = whDialog();

			if (whNew == null) {
				return;
			}

			resize(whNew[0], whNew[1]);
			break;
		case "Undo":
			if (env == null || !canUndo) {
				Main.error("unable to undo");
				return;
			}
			undo();
			break;
		case "Redo":
			if (env == null || !canRedo) {
				Main.error("unable to redo");
				return;
			}
			redo();
			break;
		case "Quit":
			Main.getFrame().setJMenuBar(null);
			Main.setWindow(new MainMenu());
		}

		repaint();

	}

	@Override
	public void paintComponent(Graphics gr) {

		super.paintComponent(gr);

		if (env == null) {
			return;
		}
		
		env.render(this, gr, false);
		renderMousePos(gr);
		
	}
	
	private void renderMousePos(Graphics gr) {
		String gridPos = getMousePosAbsolute().toGridPos().toIntString();
		gr.drawString("grid: " + gridPos, 20, 20);
	}

	@Override
	protected void onFrame() {

		synchronized (env) {
		
			if (env != null) {
				canGoLeft = true;
				canGoRight = true;
				canGoUp = true;
				canGoDown = true;
	
				if (focus.getPos().getX() <= -SCALE_FACTOR) {
					canGoLeft = false;
				}
				if (focus.getPos().getX() >= env.getLevel().getDimensions().getX() * Tile.WIDTH + SCALE_FACTOR) {
					canGoRight = false;
				}
				if (focus.getPos().getY() <= -SCALE_FACTOR) {
					canGoUp = false;
				}
				if (focus.getPos().getY() >= env.getLevel().getDimensions().getY() * Tile.WIDTH + SCALE_FACTOR) {
					canGoDown = false;
				}
	
				if (states.getMovementVector().getX() < 0 && !canGoLeft) {
					focus.getPos().sub(states.getMovementVector().getProjectionX().scale(SCALE_FACTOR));
				}
				if (states.getMovementVector().getX() > 0 && !canGoRight) {
					focus.getPos().sub(states.getMovementVector().getProjectionX().scale(SCALE_FACTOR));
				}
				if (states.getMovementVector().getY() < 0 && !canGoUp) {
					focus.getPos().sub(states.getMovementVector().getProjectionY().scale(SCALE_FACTOR));
				}
				if (states.getMovementVector().getY() > 0 && !canGoDown) {
					focus.getPos().sub(states.getMovementVector().getProjectionY().scale(SCALE_FACTOR));
				}
				focus.getPos().add(states.getMovementVector().scale(SCALE_FACTOR));
	
				if (isClicking) {
					Vector location = getMousePosAbsolute().copy();
					int x = location.getX();
					int y = location.getY();
	
					env.getLevel().setTile(x, y, new Tile(selectedTile));
				}
			}
		
		}

		repaint();

	}

	@Override
	public Entity getFocus() {
		return focus;
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		if (env != null) {
			if (SwingUtilities.isRightMouseButton(e)) {
				fill();
			}
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
		if (env != null) {
			if (SwingUtilities.isLeftMouseButton(e)) {
				isClicking = true;
			}
		}
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		isClicking = false;
	}

	@Override
	public void keyPressed(KeyEvent e) {
		
		super.keyPressed(e);
		
		if (Key.E.isPressed(e)) {
			spawnEntity();
		}
		if (Key.DELETE.isPressed(e)) {
			deleteEntity();
		}
		if (Key.T.isPressed(e)) {
			editEntityTag();
		}
		if (Key.SPACE.isPressed(e)) {
			openEntityInventory();
		}

	}

	public void fill() {
		Vector location = getMousePosAbsolute().copy();
		int x = location.getX() / Tile.WIDTH;
		int y = location.getY() / Tile.WIDTH;

		Tile t = env.getLevel().getNewTileGrid(x, y);
		if (selectedTile != null && env.getLevel().getNewTileGrid(x, y) != null && t != null
				&& !t.equals(selectedTile)) {
			autosaveUndo();
			env.getLevel().setTileGrid(x, y, selectedTile);
			fill_helper(x + 1, y, t);
			fill_helper(x - 1, y, t);
			fill_helper(x, y + 1, t);
			fill_helper(x, y - 1, t);
		}
	}

	public void fill_helper(int x, int y, Tile t) {
		if (selectedTile != null && env.getLevel().getNewTileGrid(x, y) != null && t != null
				&& env.getLevel().getNewTileGrid(x, y).equals(t)) {
			env.getLevel().setTileGrid(x, y, selectedTile);
			fill_helper(x + 1, y, t);
			fill_helper(x - 1, y, t);
			fill_helper(x, y + 1, t);
			fill_helper(x, y - 1, t);
		}
	}

	private void openEntityInventory() {
		
		if (!(env instanceof World)) {
			return;
		}
		World world = (World) env;
		Entity ent = world.getEntityTree().getFirstCollision(new Entity(getMousePosAbsolute()), true);
		
		if (ent instanceof Carrier) {
			editInventory(((Carrier) ent).getInventory());
		}
		
	}
	
	private void editEntityTag() {
		
		if (!(env instanceof World)) {
			return;
		}
		World world = (World) env;
		Entity ent = world.getEntityTree().getFirstCollision(new Entity(getMousePosAbsolute()), true);
		
		if (ent == null) {
			return;
		}
		
		DialogMap inputs = new DialogMap();
		inputs.put("Tag", ent.getTag());
		dialog("Edit Entity Tag", inputs);
		String tag = inputs.getText("Tag");
		ent.setTag(tag.isEmpty() ? null : tag);
		
	}
	
	private void spawnEntity() {
		
		if (!(env instanceof World)) {
			Main.error("tried to spawn entity in non-world");
			return;
		}
		World world = (World) env;
		
		autosaveUndo();

		if (selectedEntityClass.equals(Player.class)) {
			world.deleteHard(world.computeFocus()); // don't need to check null
		}
		
		//TODO auto-detect options for constructor; have method that gives them?
		
		Vector spawnPos = getMousePosAbsolute();
		
		try {
			
			if (selectedEntityClass.equals(Portal.class)) {
				DialogMap inputs = new DialogMap();
				inputs.put("World", WorldId.SPAWN_TOWN.name());
				inputs.put("X", "" + focus.getPos().getX());
				inputs.put("Y", "" + focus.getPos().getY());
				dialog("Portal Destination", inputs);
				Vector dest = new Vector(inputs.getDouble("X"), inputs.getDouble("Y"));
				world.addHard(selectedEntityClass.getConstructor(Vector.class, String.class, Vector.class).newInstance(spawnPos, inputs.getText("World"), dest));
			} else if (selectedEntityClass.equals(Drop.class)) {
				DialogMap inputs = new DialogMap();
				inputs.put("Prize", "Enter item name/id or vocab word");
				inputs.put("Spell", "");
				dialog("Drop Reward", inputs);
				world.addHard(selectedEntityClass.getConstructor(Vector.class, String.class, String.class).newInstance(spawnPos, inputs.getText("Prize"), inputs.getText("Spell")));
			} else if (selectedEntityClass.equals(Listener.class)) {
				DialogMap inputs = new DialogMap();
				inputs.put("Radius", "40");
				inputs.put("Tag", "Trigger to activate");
				dialog("Configure Detector", inputs);
				world.addHard(selectedEntityClass.getConstructor(Vector.class, int.class, String.class).newInstance(spawnPos, inputs.getInteger("Radius"), inputs.getText("Tag")));
			}
			
			else {
				world.addHard(selectedEntityClass.getConstructor(Vector.class).newInstance(spawnPos));
			}
			
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| SecurityException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			Main.error("cannot spawn entity type " + selectedEntityClass.getSimpleName());
		}
	}

	private void deleteEntity() {
		
		if (!(env instanceof World)) {
			Main.error("tried to delete entity in non-world");
			return;
		}
		World world = (World) env;
		
		Entity deletableEntity = world.getEntityTree().getFirstCollision(new Entity(getMousePosAbsolute()), true);
		
		autosaveUndo();
		world.deleteHard(deletableEntity);

	}

	public void resize(int newWidth, int newHeight) {
		autosaveUndo();
		env.resize(newWidth, newHeight);
	}

	public void autosaveUndo() {
		try {
			env.save(new File("./worlds/.undo1"), false);
			Main.log("autosaved for undo");
			canUndo = true;
			canRedo = false;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			Main.error("unable to autosave for undo");
			e.printStackTrace();
		}
	}

	public void autosaveRedo() {
		try {
			env.save(new File("./worlds/.redo1"));
			Main.log("autosaved for redo");
			canUndo = false;
			canRedo = true;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			Main.error("unable to autosave for redo");
			canRedo = false;
			e.printStackTrace();
		}
	}

	public void undo() {
		if (canUndo) {
			try {
				autosaveRedo();
				env.load(new File("./worlds/.undo1"));
				canUndo = false;
				canRedo = true;
				Main.log("undo!");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				Main.error("cannot undo, IOException");
				e.printStackTrace();
			}
		} else {
			Main.error("cannot undo right now");
		}
	}

	public void redo() {
		if (canRedo) {
			try {
				env.load(new File("./worlds/.redo1"));
				Main.log("redo!");
				canUndo = true;
				canRedo = false;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				Main.error("cannot redo, IOException");
				e.printStackTrace();
			}
		} else {
			Main.error("cannot redo right now");
		}
	}

	@Override
	public World getWorld() {
		if (env instanceof World) {
			return (World) env;
		}
		return null;
	}

	@Override
	public Playable getUniverse() {
		if (env instanceof Playable) {
			return (Playable) env;
		}
		return null;
	}

	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}
	
}
