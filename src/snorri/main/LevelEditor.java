package snorri.main;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;

import snorri.entities.Ballista;
import snorri.entities.Center;
import snorri.entities.Drop;
import snorri.entities.Dummy;
import snorri.entities.Entity;
import snorri.entities.Listener;
import snorri.entities.Player;
import snorri.events.CastEvent.Caster;
import snorri.inventory.Droppable;
import snorri.inventory.Item;
import snorri.keyboard.Key;
import snorri.masking.Mask;
import snorri.world.Editable;
import snorri.world.TileLayer;
import snorri.world.UnifiedTileType;
import snorri.world.Playable;
import snorri.world.Tile;
import snorri.world.Vector;
import snorri.world.World;

public class LevelEditor extends FocusedWindow<Entity> implements ActionListener {

	private static final long serialVersionUID = 1L;

	private static final double SCALE_FACTOR = 1.5d;
	private static final double SPEED_MULTIPLIER = 3.9;
	
	private double speed = SCALE_FACTOR;

	private Editable env;
	private Tile selectedTile;
	private Class<? extends Entity> selectedEntityClass;
	private boolean isClicking = false;
	
	// TODO(#48): Be able to modify this in interface.
	private TileLayer selectedTileLayer; // The selected layer to modify.

	private boolean canGoLeft;
	private boolean canGoRight;
	private boolean canGoUp;
	private boolean canGoDown;

	private boolean canUndo;
	private boolean canRedo;
	
	private boolean extraSpeed = false;
	
	JMenuBar menuBar;
	JMenu menu, subsubmenu;
	JMenuItem menuItem;
	JRadioButtonMenuItem rbMenuItem;
	JCheckBoxMenuItem cbMenuItem;

	public LevelEditor() {
		super(new Entity(new Vector(20, 20)));
		new Tile(UnifiedTileType.EMPTY);
		selectedTile = new Tile(UnifiedTileType.SAND, 0);
		selectedEntityClass = Entity.EDIT_SPAWNABLE.get(0);
		createMenu();
		repaint();
		lastRenderTime = getTimestamp();
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

		menuItem = new JMenuItem("Open", KeyEvent.VK_O);
		menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, ActionEvent.CTRL_MASK));
		menuItem.addActionListener(this);
		menu.add(menuItem);

		menuItem = new JMenuItem("Save", KeyEvent.VK_S);
		menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK));
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

		// Menu to select tiles.
		menu = new JMenu("Select Tile");
		menu.setMnemonic(KeyEvent.VK_T);
		menuBar.add(menu);

		ButtonGroup groupTiles = new ButtonGroup();
		boolean firstTile = true;
		for (Tile t : Tile.getAllTypes()) {
			subsubmenu = new JMenu(t.toStringShort());
			for (Tile s : t.getType().getAllStyles()) {
				BufferedImage baseTexture = s.getBaseTexture();
				ImageIcon icon = (baseTexture == null) ? null : new ImageIcon(baseTexture);
				rbMenuItem = new JRadioButtonMenuItem(s.toString(), icon);
				rbMenuItem.setSelected(firstTile);
				rbMenuItem.setActionCommand("set" + s.toNumericString());
				rbMenuItem.addActionListener(this);
				subsubmenu.add(rbMenuItem);
				groupTiles.add(rbMenuItem);
			}
			menu.add(subsubmenu);
			firstTile = false;
		}

		// Menu to select entities.
		menu = new JMenu("Select Entity");
		menu.setMnemonic(KeyEvent.VK_E);
		menuBar.add(menu);

		List<Class<? extends Entity>> entityClassList = Entity.EDIT_SPAWNABLE;
		ButtonGroup groupEntities = new ButtonGroup();

		boolean firstEntity = true;
		int i = 0;
		for (Class<? extends Entity> c : entityClassList) {
			rbMenuItem = new JRadioButtonMenuItem(c.getSimpleName());
			rbMenuItem.setSelected(firstEntity);
			rbMenuItem.setActionCommand("spawn" + i);
			rbMenuItem.addActionListener(this);
			groupEntities.add(rbMenuItem);
			menu.add(rbMenuItem);

			firstEntity = false;
			i++;
		}
		
		// Menu to create a new edge.
		menu = new JMenu("Edge");
		menu.setMnemonic(KeyEvent.VK_G);
		menuBar.add(menu);
		
		menuItem = new JMenuItem("Top");
		menuItem.addActionListener(this);
		menu.add(menuItem);
		
		menuItem = new JMenuItem("Bottom");
		menuItem.addActionListener(this);
		menu.add(menuItem);
		
		menuItem = new JMenuItem("Left");
		menuItem.addActionListener(this);
		menu.add(menuItem);
		
		menuItem = new JMenuItem("Right");
		menuItem.addActionListener(this);
		menu.add(menuItem);
		

		Main.getFrame().setJMenuBar(menuBar);
	}

	private int[] whDialog() {
		int[] wh = { -1, -1 };
		DialogMap inputs = new DialogMap();
		inputs.put("Width", "" + World.DEFAULT_LEVEL_SIZE.getX());
		inputs.put("Height", "" + World.DEFAULT_LEVEL_SIZE.getY());
		if (dialog("World Dimensions", inputs) == null) {
			return null;
		}

		wh[0] = inputs.getInteger("Width");
		wh[1] = inputs.getInteger("Height");

		return wh;
	}
	
	private String connectionDialog() {
		String w2 = "";
		DialogMap inputs = new DialogMap();
		inputs.put("Connected World Name", "");
		if (dialog("Enter Connected World Name", inputs) == null) {
			return null;
		}

		w2 = inputs.getText("Connected World Name");

		return w2;
	}

	@Override
	public void actionPerformed(ActionEvent e) {

		if (e.getActionCommand().startsWith("set")) {
			//Main.debug(e.getActionCommand());
			if (!selectedTile.equals(new Tile(e.getActionCommand().substring(3)))) {
				selectedTile = new Tile(e.getActionCommand().substring(3));
				return;
			}
		}

		if (e.getActionCommand().startsWith("spawn")) {
			selectedEntityClass = Entity.EDIT_SPAWNABLE.get(Integer.parseInt(e.getActionCommand().substring(5)));
			return;
		}

		String w2;
		switch (e.getActionCommand()) {
		case "New":
			int[] wh = whDialog();
			if (wh == null) {
				return;
			}
			Vector dims = new Vector(wh[0], wh[1]);
			if (wh[0] > 0 && wh[1] > 0 && wh[0] <= TileLayer.MAX_SIZE && wh[1] <= TileLayer.MAX_SIZE) {
				setEditableEnvironment(World.createDefaultWorld(dims));
				centerCamera();
			} else {
				throw new IllegalArgumentException("Invalid dimensions for World: " + dims + ".");
			}
			break;
		case "Open":
			try {
				File worldFile = World.wrapLoad();
				if (worldFile == null) {
					break;
				}
				setEditableEnvironment(new World(worldFile));
				centerCamera();
			} catch (IOException e1) {
				Debug.logger.warning("Failed to open World.");
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
				Debug.logger.warning("Unable to undo.");
				return;
			}
			undo();
			break;
		case "Redo":
			if (env == null || !canRedo) {
				Debug.logger.warning("Unable to redo.");
				return;
			}
			redo();
			break;
		case "Quit":
			Main.getFrame().setJMenuBar(null);
			Main.setWindow(new MainMenu());
			break;
		case "Top":
			if (env == null) {
				Debug.logger.warning("Null editable object.");
				return;
			}
			w2 = connectionDialog();
			if (w2 == null || w2 == "") {
				Debug.logger.warning("Invalid connecting world.");
				return;
			}
			if (env.getWorldGraph() == null) {
				Debug.logger.warning("Could not get world graph.");
				return;
			}
			env.getWorldGraph().createLink(w2, 3);
			break;
		case "Bottom":
			if (env == null) {
				return;
			}
			w2 = connectionDialog();
			if (w2 == null || w2 == "") {
				return;
			}
			if (env.getWorldGraph() == null) {
				return;
			}
			env.getWorldGraph().createLink(w2, 1);
			break;
		case "Left":
			if (env == null) {
				return;
			}
			w2 = connectionDialog();
			if (w2 == null || w2 == "") {
				return;
			}
			if (env.getWorldGraph() == null) {
				return;
			}
			env.getWorldGraph().createLink(w2, 2);
			break;
		case "Right":
			if (env == null) {
				return;
			}
			w2 = connectionDialog();
			if (w2 == null || w2 == "") {
				return;
			}
			if (env.getWorldGraph() == null) {
				return;
			}
			env.getWorldGraph().createLink(w2, 0);
			break;
		}
					

		repaint();

	}
	
	public void centerCamera() {
		if (env != null) {
			Vector middle = env.getDimensions().copy().divide_(2);
			getFocus().setPos(middle);
		}
	}

	@Override
	public void paintComponent(Graphics gr) {
		super.paintComponent(gr);
		if (env == null) {
			return;
		}
		
		long time = getTimestamp();
		double deltaTime = (time - lastRenderTime) / 1000000000d;
		lastRenderTime = time;
		
		env.render(this, (Graphics2D) gr, deltaTime, false);
		renderMousePos(gr);
	}

	private void renderMousePos(Graphics gr) {
		String gridPos = getMousePosAbsolute().gridPos_().toIntString();
		gr.drawString("grid: " + gridPos, 20, 20);
	}

	@Override
	protected void onFrame() {

		if (env != null) {
			synchronized (env) {
				canGoLeft = true;
				canGoRight = true;
				canGoUp = true;
				canGoDown = true;

				if (player.getPos().getX() <= -SCALE_FACTOR) {
					canGoLeft = false;
				}
				if (player.getPos().getX() >= getSelectedTileLayer().getWidth() * Tile.WIDTH + SCALE_FACTOR) {
					canGoRight = false;
				}
				if (player.getPos().getY() <= -SCALE_FACTOR) {
					canGoUp = false;
				}
				if (player.getPos().getY() >= getSelectedTileLayer().getHeight() * Tile.WIDTH + SCALE_FACTOR) {
					canGoDown = false;
				}	
				
				/**
				 * Note: Used to be states.getMomentumVector(), but when we changed the game to be more like a platformer,
				 *       it became impossible to go up and down in the level editor.  Rather than refactor everything and abstract
				 *       the concept of movement, getLevelEditorMomentumVector is the quick and dirty fix to change as little
				 *       as possible about the mechanics of how the movement works.
				 */
				if (states.getLevelEditorMovementVector().getX() < 0 && !canGoLeft) {
					player.getPos().sub_(states.getLevelEditorMovementVector().getProjectionX().scale_(speed));
				}
				if (states.getLevelEditorMovementVector().getX() > 0 && !canGoRight) {
					player.getPos().sub_(states.getLevelEditorMovementVector().getProjectionX().scale_(speed));
				}
				if (states.getLevelEditorMovementVector().getY() < 0 && !canGoUp) {
					player.getPos().sub_(states.getLevelEditorMovementVector().getProjectionY().scale_(speed));
				}
				if (states.getLevelEditorMovementVector().getY() > 0 && !canGoDown) {
					player.getPos().sub_(states.getLevelEditorMovementVector().getProjectionY().scale_(speed));
				}
				player.getPos().add_(states.getLevelEditorMovementVector().scale_(speed));
				
				if (isClicking) {
					Vector location = getMousePosAbsolute().copy();
					int x = location.getX();
					int y = location.getY();
					getSelectedTileLayer().setTile(x, y, new Tile(selectedTile));
				}
			}
		}

		repaint();

	}

	@Override
	public void mouseClicked(MouseEvent e) {
		if (env != null) {
			if (SwingUtilities.isRightMouseButton(e)) {
				fill();
			}
			else if(SwingUtilities.isMiddleMouseButton(e)) {
				pick();
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
			editEntityInfo();
		}
		if (Key.SPACE.isPressed(e)) {
			openEntityInventory();
		}
		if (Key.P.isPressed(e)) {
			pick();
		}
		if (Key.Q.isPressed(e)) {
			changeSpeed();
		}
	}

	private void changeSpeed() {
		if (extraSpeed) {
			speed = SCALE_FACTOR;
			extraSpeed = false;
		}
		else {
			speed = SCALE_FACTOR * SPEED_MULTIPLIER;
			extraSpeed = true;
		}
	}

	public void fill() {
		Vector location = getMousePosAbsolute().copy();
		int x = location.getX() / Tile.WIDTH;
		int y = location.getY() / Tile.WIDTH;
		int w = getSelectedTileLayer().getWidth();
		int h = getSelectedTileLayer().getHeight();

		Tile t = getSelectedTileLayer().getTileGrid(x, y);

		ArrayList<Vector> willFill = computeConnectedSubGraph(new Vector(x, y), new boolean[w][h]);

		if (selectedTile != null && getSelectedTileLayer().getTileGrid(x, y) != null && t != null && !t.equals(selectedTile)) {
			autosaveUndo();
			for (Vector v : willFill) {
				getSelectedTileLayer().setTileGrid(v, new Tile(selectedTile));
			}
		}

	}
	
	public void pick() {
		if (!selectedTile.equals(getSelectedTileLayer().getTileGrid(getMousePosAbsolute().getX() / Tile.WIDTH, getMousePosAbsolute().getY() / Tile.WIDTH))) {
			selectedTile = getSelectedTileLayer().getTileGrid(getMousePosAbsolute().getX() / Tile.WIDTH, getMousePosAbsolute().getY() / Tile.WIDTH);
		}
	}

	@Deprecated
	private ArrayList<Vector> computeConnectedSubGraph(Vector start, boolean[][] visited) {
																							
		final Tile START_TILE = getSelectedTileLayer().getTileGrid(start);

		ArrayList<Vector> graph = new ArrayList<Vector>();
		Queue<Vector> searchQ = new LinkedList<Vector>();
		searchQ.add(start);
		Vector pos;

		while (!searchQ.isEmpty()) {

			pos = searchQ.poll();
			if (getSelectedTileLayer().getTileGrid(pos) == null || !getSelectedTileLayer().getTileGrid(pos).equals(START_TILE) || visited[pos.getX()][pos.getY()]) {
				continue;
			}

			visited[pos.getX()][pos.getY()] = true;
			if (getSelectedTileLayer().getTileGrid(pos).equals(START_TILE))
				graph.add(pos);
			
			for (Vector v : Mask.getNeighbors(pos)) {
				searchQ.add(v);
			}
		}
		return graph;
	}

	private void openEntityInventory() {

		if (!(env instanceof World)) {
			return;
		}
		World world = (World) env;
		Entity ent = world.getEntityTree().getFirstCollision(new Entity(getMousePosAbsolute()), true);

		
		if (ent instanceof Caster) {
			editInventory((Caster) ent);
		}

	}

	private void editEntityInfo() {
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
		if (ent instanceof Drop) {
			inputs.put("Prize", ((Drop) ent).getPrizeString());
			Droppable prize = ((Drop) ent).getPrize();
			inputs.put("Spell", (prize instanceof Item && ((Item) prize).getSpell() != null) ? ((Item) prize).getSpell().getOrthography() : "");
		}
		if (dialog("Edit Entity Info", inputs) == null) {
			return;
		}
		
		String tag = inputs.getText("Tag");
		ent.setTag(tag.isEmpty() ? null : tag);
		if (ent instanceof Drop) {
			world.delete(ent);
			world.add(new Drop(ent.getPos().copy(), inputs.getText("Prize"), inputs.getText("Spell")));
		}
	}

	private void spawnEntity() {

		if (!(env instanceof World)) {
			Debug.logger.warning("Tried to spawn entity in non-world.");
			return;
		}
		World world = (World) env;

		autosaveUndo();

		if (selectedEntityClass.equals(Player.class)) {
			if (world.computeFocus() != null) {
				world.delete(world.computeFocus()); // don't need to check null
			}
		}
		else if (selectedEntityClass.equals(Center.class)) {
			world.delete(world.getEntityTree().getFirst(Center.class));
		}

		Vector spawnPos = getMousePosAbsolute();

		try {

			if (selectedEntityClass.equals(Drop.class)) {
				DialogMap inputs = new DialogMap();
				inputs.put("Prize", "Enter item name/id or vocab word");
				inputs.put("Spell", "");
				if (dialog("Drop Reward", inputs) == null) {
					return;
				}
				world.add(new Drop(spawnPos, inputs.getText("Prize"), inputs.getText("Spell")));
			} else if (selectedEntityClass.equals(Listener.class)) {
				DialogMap inputs = new DialogMap();
				inputs.put("Radius", "40");
				inputs.put("Tag", "Trigger to activate");
				if (dialog("Configure Listener", inputs) == null) {
					return;
				}
				world.add(new Listener(spawnPos, inputs.getInteger("Radius"), inputs.getText("Tag")));
			} else if (selectedEntityClass.equals(Ballista.class)) {
				DialogMap inputs = new DialogMap();
				inputs.put("X", "1");
				inputs.put("Y", "0");
				if (dialog("Ballista Direction", inputs) == null) {
					return;
				}
				Vector dir = new Vector(inputs.getDouble("X"), inputs.getDouble("Y"));
				world.add(new Ballista(spawnPos, dir));
			} else if (selectedEntityClass.equals(Dummy.class)) {
				DialogMap inputs = new DialogMap();
				inputs.put("Path", "/textures/animations/path");
				if (dialog("Dummy Animation", inputs) == null) {
					return;
				}
				String animation = inputs.getText("Path");
				world.add(new Dummy(spawnPos, animation));
			}

			else {
				world.add(selectedEntityClass.getConstructor(Vector.class).newInstance(spawnPos));
			}

		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| SecurityException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			Debug.logger.warning("Cannot spawn entity type " + selectedEntityClass.getSimpleName() + ".");
		}
	}

	private void deleteEntity() {
		if (!(env instanceof World)) {
			Debug.logger.warning("Tried to delete entity in non-world.");
			return;
		}
		World world = (World) env;
		Entity deletableEntity = world.getEntityTree().getFirstCollision(new Entity(getMousePosAbsolute()), true);
		autosaveUndo();
		world.delete(deletableEntity);
	}

	public void resize(int newWidth, int newHeight) {
		autosaveUndo();
		setEditableEnvironment(env.getResized(newWidth, newHeight));
	}

	public void autosaveUndo() {
		try {
			env.save(Main.getFile("/worlds/.undo1"), false);
			canUndo = true;
			canRedo = false;
		} catch (IOException e) {
			Debug.logger.warning("Failed to undo.");
		}
	}

	public void autosaveRedo() {
		try {
			env.save(Main.getFile("/worlds/.redo1"));
			canUndo = false;
			canRedo = true;
		} catch (IOException e) {
			Debug.logger.warning("Failed to redo.");
			canRedo = false;
		}
	}

	public void undo() {
		if (canUndo) {
			try {
				autosaveRedo();
				env.load(Main.getFile("/saves/.undo1"));
				canUndo = false;
				canRedo = true;
				Debug.logger.info("Undone!");
			} catch (IOException e) {
				Debug.logger.warning("Failed to undo.");
			}
		} else {
			Debug.logger.info("Cannot undo right now.");
		}
	}

	public void redo() {
		if (canRedo) {
			try {
				env.load(Main.getFile("/saves/.redo1"));
				Debug.logger.info("Redone!");
				canUndo = true;
				canRedo = false;
			} catch (IOException e) {
				Debug.logger.warning("Failed to redo.");
			}
		} else {
			Debug.logger.info("Cannot redo right now.");
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
	}

	@Override
	protected void onStart() {
	}
	
	@Override
	public Entity getCenterObject() {
		if (getWorld() != null) {
			return getFocus();
		}
		else {
			return null;
		}
	}
	
	private void setEditableEnvironment(Editable env) {
		this.env = env;
		setSelectedTileLayer(env.getTileLayer());
	}
	
	private TileLayer getSelectedTileLayer() {
		return selectedTileLayer;
	}
	
	private void setSelectedTileLayer(TileLayer selectedTileLayer) {
		this.selectedTileLayer = selectedTileLayer;
	}
	
}
