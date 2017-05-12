package snorri.main;

import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
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

import snorri.entities.Drop;
import snorri.entities.Entity;
import snorri.entities.Listener;
import snorri.entities.Player;
import snorri.entities.Portal;
import snorri.entities.Unit;
import snorri.inventory.Carrier;
import snorri.inventory.Droppable;
import snorri.inventory.Item;
import snorri.keyboard.Key;
import snorri.masking.Mask;
import snorri.pathfinding.Team;
import snorri.terrain.TerrainGen;
import snorri.world.Campaign.WorldId;
import snorri.world.Editable;
import snorri.world.ForegroundElement;
import snorri.world.MidgroundElement;
import snorri.world.Level;
import snorri.world.Playable;
import snorri.world.Tile;
import snorri.world.Vector;
import snorri.world.World;
import snorri.world.BackgroundElement;

//TODO: add image to world feature
//TODO: fix overflow with a 2d boolean array (look at methods in Level which compute pathfinding graphs)

public class LevelEditor extends FocusedWindow implements ActionListener {

	private static final long serialVersionUID = 1L;

	private static final double SCALE_FACTOR = 15.9;
	private static final double SPEED_MULTIPLIER = 3.9;
	
	private double speed = SCALE_FACTOR;

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
	
	private boolean extraSpeed = false;

	JMenuBar menuBar;
	JMenu menu, submenu0, submenu1, submenu2, subsubmenu;
	JMenuItem menuItem;
	JRadioButtonMenuItem rbMenuItem;
	JCheckBoxMenuItem cbMenuItem;

	public LevelEditor() {
		super();

		selectedTile = new Tile(BackgroundElement.class, 0, 0);
		selectedEntityClass = Entity.EDIT_SPAWNABLE.get(0);
		createMenu();

		repaint();

		focus = new Entity(new Vector(8, 8));

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

		menuItem = new JMenuItem("Generate", KeyEvent.VK_G);
		menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_G, ActionEvent.CTRL_MASK));
		menuItem.addActionListener(this);
		menu.add(menuItem);

		menuItem = new JMenuItem("Open", KeyEvent.VK_O);
		menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, ActionEvent.CTRL_MASK));
		menuItem.addActionListener(this);
		menu.add(menuItem);

		menuItem = new JMenuItem("Open Level", KeyEvent.VK_L);
		menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_L, ActionEvent.CTRL_MASK));
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

		menuItem = new JMenuItem("Compute Pathing", KeyEvent.VK_P);
		menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_P, ActionEvent.CTRL_MASK));
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
		
		submenu0 = new JMenu("Backgrounds");
		submenu1 = new JMenu("Midgrounds");
		submenu2 = new JMenu("Foregrounds");

		boolean firstTile = true;
		for (Tile t : Tile.getAllTypes(BackgroundElement.class)) {

			if (t == null || t.getTexture() == null) {
				continue;
			}

			subsubmenu = new JMenu(t.toStringShort());
			for (Tile s : t.getType().getSubTypes()) {
				rbMenuItem = new JRadioButtonMenuItem(s.toString(), new ImageIcon(s.getTexture()));
				rbMenuItem.setSelected(firstTile);
				rbMenuItem.setActionCommand("set" + s.toNumericString());
				rbMenuItem.addActionListener(this);
				subsubmenu.add(rbMenuItem);
				groupTiles.add(rbMenuItem);
			}
			submenu0.add(subsubmenu);

			firstTile = false;

		}
		for (Tile t : Tile.getAllTypes(MidgroundElement.class)) {

			if (t == null || t.getTexture() == null) {
				if (t.getType().ordinal() == 0) {
					subsubmenu = new JMenu(t.toStringShort());
					for (Tile s : t.getType().getSubTypes()) {
						rbMenuItem = new JRadioButtonMenuItem(s.toString(), new ImageIcon(Tile.BLANK_TEXTURE));
						rbMenuItem.setSelected(firstTile);
						rbMenuItem.setActionCommand("set" + s.toNumericString());
						rbMenuItem.addActionListener(this);
						subsubmenu.add(rbMenuItem);
						groupTiles.add(rbMenuItem);
					}
					submenu1.add(subsubmenu);
				}
				continue;
			}

			subsubmenu = new JMenu(t.toStringShort());
			for (Tile s : t.getType().getSubTypes()) {
				rbMenuItem = new JRadioButtonMenuItem(s.toString(), new ImageIcon(s.getTexture()));
				rbMenuItem.setSelected(firstTile);
				rbMenuItem.setActionCommand("set" + s.toNumericString());
				rbMenuItem.addActionListener(this);
				subsubmenu.add(rbMenuItem);
				groupTiles.add(rbMenuItem);
			}
			submenu1.add(subsubmenu);
		}
		for (Tile t : Tile.getAllTypes(ForegroundElement.class)) {

			if (t == null || t.getTexture() == null) {
				if (t.getType().ordinal() == 0) {
					subsubmenu = new JMenu(t.toStringShort());
					for (Tile s : t.getType().getSubTypes()) {
						rbMenuItem = new JRadioButtonMenuItem(s.toString(), new ImageIcon(Tile.BLANK_TEXTURE));
						rbMenuItem.setSelected(firstTile);
						rbMenuItem.setActionCommand("set" + s.toNumericString());
						rbMenuItem.addActionListener(this);
						subsubmenu.add(rbMenuItem);
						groupTiles.add(rbMenuItem);
					}
					submenu2.add(subsubmenu);
				}
				continue;
			}

			subsubmenu = new JMenu(t.toStringShort());
			for (Tile s : t.getType().getSubTypes()) {
				rbMenuItem = new JRadioButtonMenuItem(s.toString(), new ImageIcon(s.getTexture()));
				rbMenuItem.setSelected(firstTile);
				rbMenuItem.setActionCommand("set" + s.toNumericString());
				rbMenuItem.addActionListener(this);
				subsubmenu.add(rbMenuItem);
				groupTiles.add(rbMenuItem);
			}
			submenu2.add(subsubmenu);
		}
		
		menu.add(submenu0);
		menu.add(submenu1);
		menu.add(submenu2);

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
		inputs.put("Width", "" + World.DEFAULT_LEVEL_SIZE);
		inputs.put("Height", "" + World.DEFAULT_LEVEL_SIZE);
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
			//Main.debug(e.getActionCommand());
			selectedTile = new Tile(e.getActionCommand().substring(3));
			return;
		}

		if (e.getActionCommand().startsWith("spawn")) {
			selectedEntityClass = Entity.EDIT_SPAWNABLE.get(Integer.parseInt(e.getActionCommand().substring(5)));
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
		case "Generate":
			DialogMap inputs = new DialogMap();
			inputs.put("Class", "snorri.terrain.TerrainGen");
			inputs.put("Width", "150");
			inputs.put("Height", "150");
			if (dialog("Generator Options", inputs) == null) {
				return;
			}
			Class<?> gen = inputs.getClass("Class");
			try {
				Object g = gen.getConstructor(int.class, int.class).newInstance(inputs.getInteger("Width"),
						inputs.getInteger("Height"));
				if (!(g instanceof TerrainGen)) {
					Main.error(gen.getSimpleName() + " is not a terrain generator");

				}
				Main.log("generating " + gen.getSimpleName() + " world...");
				env = ((TerrainGen) g).genWorld();
				Main.log("world successfully generated");
			} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
					| InvocationTargetException | NoSuchMethodException | SecurityException e1) {
				Main.error("generator " + gen + " has no vector size constructor");
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
		case "Compute Pathing":
			if (env instanceof World) {
				((World) env).getGraph().computePathfinding();
			}
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
		
		long time = getTimestamp();
		double deltaTime = (time - lastRenderTime) / 1000000000d;
		lastRenderTime = time;

		env.render(this, gr, deltaTime, false);
		renderMousePos(gr);

	}

	private void renderMousePos(Graphics gr) {
		String gridPos = getMousePosAbsolute().toGridPos().toIntString();
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

				if (focus.getPos().getX() <= -SCALE_FACTOR) {
					canGoLeft = false;
				}
				if (focus.getPos().getX() >= env.getLevel(selectedTile.getType().getLayer()).getDimensions().getX() * Tile.WIDTH + SCALE_FACTOR) {
					canGoRight = false;
				}
				if (focus.getPos().getY() <= -SCALE_FACTOR) {
					canGoUp = false;
				}
				if (focus.getPos().getY() >= env.getLevel(selectedTile.getType().getLayer()).getDimensions().getY() * Tile.WIDTH + SCALE_FACTOR) {
					canGoDown = false;
				}
				
				
				if (states.getMovementVector().getX() < 0 && !canGoLeft) {
					focus.getPos().sub(states.getMovementVector().getProjectionX().scale(speed));
				}
				if (states.getMovementVector().getX() > 0 && !canGoRight) {
					focus.getPos().sub(states.getMovementVector().getProjectionX().scale(speed));
				}
				if (states.getMovementVector().getY() < 0 && !canGoUp) {
					focus.getPos().sub(states.getMovementVector().getProjectionY().scale(speed));
				}
				if (states.getMovementVector().getY() > 0 && !canGoDown) {
					focus.getPos().sub(states.getMovementVector().getProjectionY().scale(speed));
				}
				focus.getPos().add(states.getMovementVector().scale(speed));
				

				if (isClicking) {
					Vector location = getMousePosAbsolute().copy();
					int x = location.getX();
					int y = location.getY();

					env.getLevel(selectedTile.getType().getLayer()).setTile(x, y, new Tile(selectedTile));
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
		if (Key.ONE.isPressed(e)) {
			selectedTile = new Tile(BackgroundElement.class, 0, 0);
		}
		if (Key.TWO.isPressed(e)) {
			selectedTile = new Tile(MidgroundElement.class, 0, 0);
		}
		if (Key.THREE.isPressed(e)) {
			selectedTile = new Tile(ForegroundElement.class, 0, 0);
		}
		
		//TODO add a key to register a team, and function to look up by name?

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
		//Main.debug(selectedTile.toString());
		//Main.debug(selectedTile.getType().getLayer());
		int w = env.getLevel(selectedTile.getType().getLayer()).getWidth();
		int h = env.getLevel(selectedTile.getType().getLayer()).getHeight();

		Tile t = env.getLevel(selectedTile.getType().getLayer()).getTileGrid(x, y);

		ArrayList<Vector> willFill = computeConnectedSubGraph(new Vector(x, y), new boolean[w][h]);

		if (selectedTile != null && env.getLevel(selectedTile.getType().getLayer()).getTileGrid(x,y) != null && t != null && !t.equals(selectedTile)) {
			autosaveUndo();
			for (Vector v : willFill) {
				env.getLevel(selectedTile.getType().getLayer()).setTileGrid(v, new Tile(selectedTile));
			}
		}

	}
	
	public void pick() {
		selectedTile = env.getLevel(selectedTile.getType().getLayer()).getTileGrid(getMousePosAbsolute().getX() / Tile.WIDTH, getMousePosAbsolute().getY() / Tile.WIDTH);
	}

	private ArrayList<Vector> computeConnectedSubGraph(Vector start, boolean[][] visited) {
																							
		final Tile START_TILE = env.getLevel(selectedTile.getType().getLayer()).getTileGrid(start);

		ArrayList<Vector> graph = new ArrayList<Vector>();
		Queue<Vector> searchQ = new LinkedList<Vector>();
		searchQ.add(start);
		Vector pos;

		while (!searchQ.isEmpty()) {

			pos = searchQ.poll();
			if (env.getLevel(selectedTile.getType().getLayer()).getTileGrid(pos) == null || !env.getLevel(selectedTile.getType().getLayer()).getTileGrid(pos).equals(START_TILE) || visited[pos.getX()][pos.getY()]) {
				continue;
			}

			visited[pos.getX()][pos.getY()] = true;
			if (env.getLevel(selectedTile.getType().getLayer()).getTileGrid(pos).equals(START_TILE))
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

		if (ent instanceof Carrier) {
			editInventory(((Carrier) ent).getInventory());
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
		if (ent instanceof Unit) {
			inputs.put("Team", ((Unit) ent).getTeam().toString());
		}
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
		if (ent instanceof Unit) {
			Team team = inputs.getTeam("Team");
			((Unit) ent).setTeam(team);
		}
		if (ent instanceof Drop) {
			world.delete(ent);
			world.add(new Drop(ent.getPos().copy(), inputs.getText("Prize"), inputs.getText("Spell")));
		}

	}

	private void spawnEntity() {

		if (!(env instanceof World)) {
			Main.error("tried to spawn entity in non-world");
			return;
		}
		World world = (World) env;

		autosaveUndo();

		if (selectedEntityClass.equals(Player.class)) {
			world.delete(world.computeFocus()); // don't need to check null
		}

		// TODO auto-detect options for constructor; have method that gives
		// them?

		Vector spawnPos = getMousePosAbsolute();

		try {

			if (selectedEntityClass.equals(Portal.class)) {
				DialogMap inputs = new DialogMap();
				inputs.put("World", WorldId.SPAWN_TOWN.name());
				inputs.put("X", "" + focus.getPos().getX());
				inputs.put("Y", "" + focus.getPos().getY());
				if (dialog("Portal Destination", inputs) == null) {
					return;
				}
				Vector dest = new Vector(inputs.getDouble("X"), inputs.getDouble("Y"));
				world.add(new Portal(spawnPos, inputs.getText("World"), dest));
			} else if (selectedEntityClass.equals(Drop.class)) {
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
			}

			else {
				world.add(selectedEntityClass.getConstructor(Vector.class).newInstance(spawnPos));
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
		world.delete(deletableEntity);

	}

	public void resize(int newWidth, int newHeight) {
		autosaveUndo();
		env.resize(newWidth, newHeight);
	}

	public void autosaveUndo() {
		try {
			env.save(Main.getFile("/saves/.undo1"), false);
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
			env.save(Main.getFile("/saves/.redo1"));
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
				env.load(Main.getFile("/saves/.undo1"));
				canUndo = false;
				canRedo = true;
				Main.log("undone!");
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
				env.load(Main.getFile("/saves/.redo1"));
				Main.log("redo!");
				canUndo = true;
				canRedo = false;
			} catch (IOException e) {
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
	}

}
