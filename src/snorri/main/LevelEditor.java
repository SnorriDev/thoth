package snorri.main;

import java.awt.Graphics;
import java.awt.KeyboardFocusManager;
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
//import java.util.Vector as javaVector;

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

public class LevelEditor extends FocusedWindow<Entity> implements ActionListener {

	private static final long serialVersionUID = 1L;

	private static final double SCALE_FACTOR = 1.5d;
	private static final double SPEED_MULTIPLIER = 3.9;
	
	private double speed = SCALE_FACTOR;

	private Editable env;
	private Tile selectedTile;
	private Tile previousTile;
	private Class<? extends Entity> selectedEntityClass;
	private boolean isClicking = false;

	private boolean canGoLeft;
	private boolean canGoRight;
	private boolean canGoUp;
	private boolean canGoDown;

	private boolean canUndo;
	private boolean canRedo;
	
	private boolean extraSpeed = false;
	private boolean wallMode = false;
	
	private java.util.Vector<Vector> wallPoints = new java.util.Vector<Vector>();
	private boolean alternateWallOrientation = false;
	
	private static final Tile N0 = new Tile(1, 5, 3);
	private static final Tile S0 = new Tile(1, 5, 1);
	private static final Tile E0 = new Tile(1, 5, 0);
	private static final Tile W0 = new Tile(1, 5, 2);
	
	private static final Tile NEc0 = new Tile(1, 6, 0);
	private static final Tile NWc0 = new Tile(1, 7, 2);
	private static final Tile SEc0 = new Tile(1, 7, 0);
	private static final Tile SWc0 = new Tile(1, 6, 2);
	private static final Tile ENc0 = new Tile(1, 7, 3);
	private static final Tile ESc0 = new Tile(1, 6, 1);
	private static final Tile WNc0 = new Tile(1, 6, 3);
	private static final Tile WSc0 = new Tile(1, 7, 1);
	
	private static final Tile NFront0 = new Tile(1, 8, 3);
	private static final Tile SFront0 = new Tile(1, 8, 1);
	private static final Tile EFront0 = new Tile(1, 8, 0);
	private static final Tile WFront0 = new Tile(1, 8, 2);
	
	private static final Tile NEnd0 = new Tile(1, 9, 3);
	private static final Tile SEnd0 = new Tile(1, 9, 1);
	private static final Tile EEnd0 = new Tile(1, 9, 0);
	private static final Tile WEnd0 = new Tile(1, 9, 2);
	
	private static final Tile N1 = S0;
	private static final Tile S1 = N0;
	private static final Tile E1 = W0;
	private static final Tile W1 = E0;
	
	private static final Tile NEc1 = WSc0;
	private static final Tile NWc1 = ESc0;
	private static final Tile SEc1 = WNc0;
	private static final Tile SWc1 = ENc0;
	private static final Tile ENc1 = SWc0;
	private static final Tile ESc1 = NWc0;
	private static final Tile WNc1 = SEc0;
	private static final Tile WSc1 = NEc0;
	
	private static final Tile NFront1 = SEnd0;
	private static final Tile SFront1 = NEnd0;
	private static final Tile EFront1 = WEnd0;
	private static final Tile WFront1 = EEnd0;
	
	private static final Tile NEnd1 = SFront0;
	private static final Tile SEnd1 = NFront0;
	private static final Tile EEnd1 = WFront0;
	private static final Tile WEnd1 = EFront0;

	JMenuBar menuBar;
	JMenu menu, submenu0, submenu1, submenu2, subsubmenu;
	JMenuItem menuItem;
	JRadioButtonMenuItem rbMenuItem;
	JCheckBoxMenuItem cbMenuItem;

	public LevelEditor() {
		super(new Entity(new Vector(20, 20)));

		previousTile = new Tile(BackgroundElement.class, 0, 0);
		selectedTile = new Tile(BackgroundElement.class, 0, 0);
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
				if (t.getType() == MidgroundElement.NONE) {
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
			if (!selectedTile.equals(new Tile(e.getActionCommand().substring(3)))) {
				previousTile = selectedTile;
				selectedTile = new Tile(e.getActionCommand().substring(3));
				return;
			}
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
			centerCamera();
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
					Debug.error(gen.getSimpleName() + " is not a terrain generator");

				}
				Debug.log("generating " + gen.getSimpleName() + " world...");
				env = ((TerrainGen) g).genWorld();
				Debug.log("world successfully generated");
			} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
					| InvocationTargetException | NoSuchMethodException | SecurityException e1) {
				Debug.error("generator " + gen + " has no vector size constructor");
			}
			centerCamera();
			break;
		case "Open":
			World w1 = World.wrapLoad();
			if (w1 != null) {
				env = w1;
			}
			centerCamera();
			break;
		case "Open Level":
			Level l1 = Level.wrapLoad();
			if (l1 != null) {
				env = l1;
			}
			centerCamera();
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
				((World) env).getPathfinding().compute();
			}
			break;
		case "Undo":
			if (env == null || !canUndo) {
				Debug.error("unable to undo");
				return;
			}
			undo();
			break;
		case "Redo":
			if (env == null || !canRedo) {
				Debug.error("unable to redo");
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
	
	//FIXME why isn't this working? debugging this would be nice
	public void centerCamera() {
//		focus.getPos().add(env.getDimensions().copy().divide(2));
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
		
		if (Debug.LOG_FOCUS) {
			Debug.log("Focused component: " + KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner());
		}

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
					int xGrid = x / Tile.WIDTH;
					int yGrid = y / Tile.WIDTH;
					if (!wallMode) {	
						env.getLevel(selectedTile.getType().getLayer()).setTile(x, y, new Tile(selectedTile));
					}
					else { //WALL MODE
						if (!wallPoints.isEmpty()) {
							if (!wallPoints.lastElement().equals(new Vector(xGrid,yGrid)) && wallPoints.lastElement().isNormalTo(xGrid,yGrid) && !wallPoints.lastElement().isToCloseTo(xGrid,yGrid)) {
								env.getLevel(selectedTile.getType().getLayer()).setTile(x, y, new Tile(selectedTile));
								if (xGrid == wallPoints.lastElement().getX()) {
									if (yGrid > wallPoints.lastElement().getY()) {
										for (int i = yGrid - 1; i > wallPoints.lastElement().getY(); --i) {
											env.getLevel(selectedTile.getType().getLayer()).setTileGrid(xGrid, i, new Tile(selectedTile));
										}
									}
									else {
										for (int i = yGrid + 1; i < wallPoints.lastElement().getY(); ++i) {
											env.getLevel(selectedTile.getType().getLayer()).setTileGrid(xGrid, i, new Tile(selectedTile));
										}
									}
								}
								else {
									if (xGrid > wallPoints.lastElement().getX()) {
										for (int i = xGrid - 1; i > wallPoints.lastElement().getX(); --i) {
											env.getLevel(selectedTile.getType().getLayer()).setTileGrid(i, yGrid, new Tile(selectedTile));
										}
									}
									else {
										for (int i = xGrid + 1; i < wallPoints.lastElement().getX(); ++i) {
											env.getLevel(selectedTile.getType().getLayer()).setTileGrid(i, yGrid, new Tile(selectedTile));
										}
									}
								}
							
								Debug.log("Added Tile (" + xGrid + ", " + yGrid + ") to wall");
								wallPoints.add(new Vector(xGrid,yGrid));
								
								if (wallPoints.firstElement().equals(wallPoints.lastElement())) {
									makeWall();
									deactivateWallMode();
								}
							}
							else {
								Debug.error("Cannot add Tile (" + xGrid + ", " + yGrid + ") to wall, in wall mode, tiles must be properly spaced and normal to each other");
							}
						}
						else {
							env.getLevel(selectedTile.getType().getLayer()).setTileGrid(xGrid, yGrid, new Tile(selectedTile));
							Debug.log("Added Tile (" + xGrid + ", " + yGrid + ") to wall");
							wallPoints.add(new Vector(xGrid,yGrid));
						}
					}
				}
			}

		}

		repaint();

	}

	private void makeWall() { //FIXME: make me better
		if (!alternateWallOrientation) {
			if (wallPoints.firstElement().equals(wallPoints.lastElement())) {
				for (int i = 0; i < wallPoints.size(); i++) {
					int cornerType = getCornerType(wallPoints.elementAt((i + 0) % (wallPoints.size() - 1)), wallPoints.elementAt((i + 1) % (wallPoints.size() - 1)), wallPoints.elementAt((i + 2) % (wallPoints.size() - 1)));
					switch(cornerType) {
						case 0: //NN
							for (int j = wallPoints.elementAt((i + 0) % (wallPoints.size() - 1)).getY() - 1; j > wallPoints.elementAt((i + 2) % (wallPoints.size() - 1)).getY(); j--) {
								env.getLevel(1).setTileGrid(wallPoints.elementAt((i + 1) % (wallPoints.size() - 1)).getX(), j, N0);
							}
							break;
							
						case 1: //NS
							for (int j = wallPoints.elementAt((i + 0)  % (wallPoints.size() - 1)).getY() - 1; j >= wallPoints.elementAt((i + 1) % (wallPoints.size() - 1)).getY(); j--) {
								env.getLevel(1).setTileGrid(wallPoints.elementAt((i + 1) % (wallPoints.size() - 1)).getX(), j, S0);
							}
							break;
							
						case 2: //NE
//							for (int j = wallPoints.elementAt(i + 0).getY() - 1; j > wallPoints.elementAt(i + 1).getY(); j--) {
//								env.getLevel(1).setTileGrid(wallPoints.elementAt(i + 1).getX(), j, N0);
//							}
							env.getLevel(1).setTileGrid(wallPoints.elementAt((i + 1) % (wallPoints.size() - 1)).getX(), wallPoints.elementAt((i + 1) % (wallPoints.size() - 1)).getY(), NEc0);
							for (int j = wallPoints.elementAt((i + 1) % (wallPoints.size() - 1)).getX() + 1; j < wallPoints.elementAt((i + 2) % (wallPoints.size() - 1)).getX(); j++) {
								env.getLevel(1).setTileGrid(j, wallPoints.elementAt((i + 1) % (wallPoints.size() - 1)).getY(), E0);
							}
							break;
							
						case 3: //NW
//							for (int j = wallPoints.elementAt(i + 0).getY() - 1; j > wallPoints.elementAt(i + 1).getY(); j--) {
//								env.getLevel(1).setTileGrid(wallPoints.elementAt(i + 1).getX(), j, N0);
//							}
							env.getLevel(1).setTileGrid(wallPoints.elementAt((i + 1) % (wallPoints.size() - 1)).getX(), wallPoints.elementAt((i + 1) % (wallPoints.size() - 1)).getY(), NWc0);
							for (int j = wallPoints.elementAt((i + 1) % (wallPoints.size() - 1)).getX() - 1; j > wallPoints.elementAt((i + 2) % (wallPoints.size() - 1)).getX(); j--) {
								env.getLevel(1).setTileGrid(j, wallPoints.elementAt((i + 1) % (wallPoints.size() - 1)).getY(), W0);
							}
							break;
							
							
						case 4: //SN
							for (int j = wallPoints.elementAt((i + 0) % (wallPoints.size() - 1)).getY() + 1; j < wallPoints.elementAt((i + 1) % (wallPoints.size() - 1)).getY(); j++) {
								env.getLevel(1).setTileGrid(wallPoints.elementAt((i + 1) % (wallPoints.size() - 1)).getX(), j, N0);
							}
							break;
							
						case 5: //SS
							for (int j = wallPoints.elementAt((i + 0) % (wallPoints.size() - 1)).getY() + 1; j < wallPoints.elementAt((i + 2)  % (wallPoints.size() - 1)).getY(); j++) {
								env.getLevel(1).setTileGrid(wallPoints.elementAt((i + 1) % (wallPoints.size() - 1)).getX(), j, S0);
							}
							break;
							
						case 6: //SE
//							for (int j = wallPoints.elementAt(i + 0).getY() + 1; j < wallPoints.elementAt(i + 1).getY(); j++) {
//								env.getLevel(1).setTileGrid(wallPoints.elementAt(i + 1).getX(), j, S0);
//							}
							env.getLevel(1).setTileGrid(wallPoints.elementAt((i + 1) % (wallPoints.size() - 1)).getX(), wallPoints.elementAt((i + 1) % (wallPoints.size() - 1)).getY(), SEc0);
							for (int j = wallPoints.elementAt((i + 1) % (wallPoints.size() - 1)).getX() + 1; j < wallPoints.elementAt((i + 2) % (wallPoints.size() - 1)).getX(); j++) {
								env.getLevel(1).setTileGrid(j, wallPoints.elementAt((i + 1) % (wallPoints.size() - 1)).getY(), E0);
							}
							break;
							
						case 7: //SW
//							for (int j = wallPoints.elementAt(i + 0).getY() + 1; j < wallPoints.elementAt(i + 1).getY(); j++) {
//								env.getLevel(1).setTileGrid(wallPoints.elementAt(i + 1).getX(), j, S0);
//							}
							env.getLevel(1).setTileGrid(wallPoints.elementAt((i + 1) % (wallPoints.size() - 1)).getX(), wallPoints.elementAt((i + 1) % (wallPoints.size() - 1)).getY(), SWc0);
							for (int j = wallPoints.elementAt((i + 1) % (wallPoints.size() - 1)).getX() - 1; j > wallPoints.elementAt((i + 2) % (wallPoints.size() - 1)).getX(); j--) {
								env.getLevel(1).setTileGrid(j, wallPoints.elementAt((i + 1) % (wallPoints.size() - 1)).getY(), W0);
							}
							break;
							
							
						case 8: //EN
//							for (int j = wallPoints.elementAt(i + 0).getX() + 1; j < wallPoints.elementAt(i + 1).getX(); j++) {
//								env.getLevel(1).setTileGrid(j, wallPoints.elementAt(i + 1).getY(), E0);
//							}
							env.getLevel(1).setTileGrid(wallPoints.elementAt((i + 1) % (wallPoints.size() - 1)).getX(), wallPoints.elementAt((i + 1) % (wallPoints.size() - 1)).getY(), ENc0);
							for (int j = wallPoints.elementAt((i + 1) % (wallPoints.size() - 1)).getY() - 1; j > wallPoints.elementAt((i + 2) % (wallPoints.size() - 1)).getY(); j--) {
								env.getLevel(1).setTileGrid(wallPoints.elementAt((i + 1) % (wallPoints.size() - 1)).getX(), j, N0);
							}
							break;
							
						case 9: //ES
//							for (int j = wallPoints.elementAt(i + 0).getX() + 1; j < wallPoints.elementAt(i + 1).getX(); j++) {
//								env.getLevel(1).setTileGrid(j, wallPoints.elementAt(i + 1).getY(), E0);
//							}
							env.getLevel(1).setTileGrid(wallPoints.elementAt((i + 1) % (wallPoints.size() - 1)).getX(), wallPoints.elementAt((i + 1) % (wallPoints.size() - 1)).getY(), ESc0);
							for (int j = wallPoints.elementAt((i + 1) % (wallPoints.size() - 1)).getY() + 1; j < wallPoints.elementAt((i + 2) % (wallPoints.size() - 1)).getY(); j++) {
								env.getLevel(1).setTileGrid(wallPoints.elementAt((i + 1) % (wallPoints.size() - 1)).getX(), j, S0);
							}
							break;
							
						case 10: //EE
							for (int j = wallPoints.elementAt((i + 0) % (wallPoints.size() - 1)).getX() + 1; j < wallPoints.elementAt((i + 2) % (wallPoints.size() - 1)).getX(); j++) {
								env.getLevel(1).setTileGrid(j, wallPoints.elementAt((i + 1) % (wallPoints.size() - 1)).getY(), E0);
							}
							break;
							
						case 11: //EW
							for (int j = wallPoints.elementAt((i + 0) % (wallPoints.size() - 1)).getX() + 1; j < wallPoints.elementAt((i + 1) % (wallPoints.size() - 1)).getX(); j++) {
								env.getLevel(1).setTileGrid(j, wallPoints.elementAt((i + 1) % (wallPoints.size() - 1)).getY(), W0);
							}
							break;
							
							
						case 12: //WN
//							for (int j = wallPoints.elementAt(i + 0).getX() - 1; j > wallPoints.elementAt(i + 1).getX(); j--) {
//								env.getLevel(1).setTileGrid(j, wallPoints.elementAt(i + 1).getY(), W0);
//							}
							env.getLevel(1).setTileGrid(wallPoints.elementAt((i + 1) % (wallPoints.size() - 1)).getX(), wallPoints.elementAt((i + 1) % (wallPoints.size() - 1)).getY(), WNc0);
							for (int j = wallPoints.elementAt((i + 1) % (wallPoints.size() - 1)).getY() - 1; j > wallPoints.elementAt((i + 2) % (wallPoints.size() - 1)).getY(); j--) {
								env.getLevel(1).setTileGrid(wallPoints.elementAt((i + 1) % (wallPoints.size() - 1)).getX(), j, N0);
							}
							break;
							
						case 13: //WS
//							for (int j = wallPoints.elementAt(i + 0).getX() - 1; j > wallPoints.elementAt(i + 1).getX(); j--) {
//								env.getLevel(1).setTileGrid(j, wallPoints.elementAt(i + 1).getY(), W0);
//							}
							env.getLevel(1).setTileGrid(wallPoints.elementAt((i + 1) % (wallPoints.size() - 1)).getX(), wallPoints.elementAt((i + 1) % (wallPoints.size() - 1)).getY(), WSc0);
							for (int j = wallPoints.elementAt((i + 1) % (wallPoints.size() - 1)).getY() + 1; j < wallPoints.elementAt((i + 2) % (wallPoints.size() - 1)).getY(); j++) {
								env.getLevel(1).setTileGrid(wallPoints.elementAt((i + 1) % (wallPoints.size() - 1)).getX(), j, S0);
							}
							break;
							
						case 14: //WE
							for (int j = wallPoints.elementAt((i + 0) % (wallPoints.size() - 1)).getX() - 1; j > wallPoints.elementAt((i + 1) % (wallPoints.size() - 1)).getX(); j--) {
								env.getLevel(1).setTileGrid(j, wallPoints.elementAt((i + 1) % (wallPoints.size() - 1)).getY(), E0);
							}
							break;
							
						case 15: //WW
							for (int j = wallPoints.elementAt((i + 0) % (wallPoints.size() - 1)).getX() - 1; j > wallPoints.elementAt((i + 2) % (wallPoints.size() - 1)).getX(); j--) {
								env.getLevel(1).setTileGrid(j, wallPoints.elementAt((i + 1) % (wallPoints.size() - 1)).getY(), W0);
							}
							break;
					}
				}
			}
			else {
				int startType = getDirection(wallPoints.elementAt(0), wallPoints.elementAt(1));
				switch(startType) {
					case 0:
						env.getLevel(1).setTileGrid(wallPoints.elementAt(0).getX(), wallPoints.elementAt(0).getY(), NFront0);
						for (int j = wallPoints.elementAt(0).getY() - 1; j > wallPoints.elementAt(1).getY(); j--) {
							env.getLevel(1).setTileGrid(wallPoints.elementAt(0).getX(), j, N0);
						}
						break;
					
					case 1:
						env.getLevel(1).setTileGrid(wallPoints.elementAt(0).getX(), wallPoints.elementAt(0).getY(), SFront0);
						for (int j = wallPoints.elementAt(0).getY() + 1; j < wallPoints.elementAt(1).getY(); j++) {
							env.getLevel(1).setTileGrid(wallPoints.elementAt(0).getX(), j, S0);
						}
						break;
					
					case 2:
						env.getLevel(1).setTileGrid(wallPoints.elementAt(0).getX(), wallPoints.elementAt(0).getY(), EFront0);
						for (int j = wallPoints.elementAt(0).getX() + 1; j < wallPoints.elementAt(1).getX(); j++) {
							env.getLevel(1).setTileGrid(j, wallPoints.elementAt(0).getY(), E0);
						}
						break;
					
					case 3:
						env.getLevel(1).setTileGrid(wallPoints.elementAt(0).getX(), wallPoints.elementAt(0).getY(), WFront0);
						for (int j = wallPoints.elementAt(0).getX() - 1; j > wallPoints.elementAt(1).getX(); j--) {
							env.getLevel(1).setTileGrid(j, wallPoints.elementAt(0).getY(), W0);
						}
						break;
				}
				for (int i = 0; i < wallPoints.size() - 2; i++) {
					int cornerType = getCornerType(wallPoints.elementAt(i + 0), wallPoints.elementAt(i + 1), wallPoints.elementAt(i + 2));
					switch(cornerType) {
						case 0: //NN
							for (int j = wallPoints.elementAt(i + 0).getY() - 1; j > wallPoints.elementAt(i + 2).getY(); j--) {
								env.getLevel(1).setTileGrid(wallPoints.elementAt(i + 1).getX(), j, N0);
							}
							break;
							
						case 1: //NS
							for (int j = wallPoints.elementAt(i + 0).getY() - 1; j >= wallPoints.elementAt(i + 1).getY(); j--) {
								env.getLevel(1).setTileGrid(wallPoints.elementAt(i + 1).getX(), j, S0);
							}
							break;
							
						case 2: //NE
	//							for (int j = wallPoints.elementAt(i + 0).getY() - 1; j > wallPoints.elementAt(i + 1).getY(); j--) {
	//								env.getLevel(1).setTileGrid(wallPoints.elementAt(i + 1).getX(), j, N0);
	//							}
							env.getLevel(1).setTileGrid(wallPoints.elementAt(i + 1).getX(), wallPoints.elementAt(i + 1).getY(), NEc0);
							for (int j = wallPoints.elementAt(i + 1).getX() + 1; j < wallPoints.elementAt(i + 2).getX(); j++) {
								env.getLevel(1).setTileGrid(j, wallPoints.elementAt(i + 1).getY(), E0);
							}
							break;
							
						case 3: //NW
	//							for (int j = wallPoints.elementAt(i + 0).getY() - 1; j > wallPoints.elementAt(i + 1).getY(); j--) {
	//								env.getLevel(1).setTileGrid(wallPoints.elementAt(i + 1).getX(), j, N0);
	//							}
							env.getLevel(1).setTileGrid(wallPoints.elementAt(i + 1).getX(), wallPoints.elementAt(i + 1).getY(), NWc0);
							for (int j = wallPoints.elementAt(i + 1).getX() - 1; j > wallPoints.elementAt(i + 2).getX(); j--) {
								env.getLevel(1).setTileGrid(j, wallPoints.elementAt(i + 1).getY(), W0);
							}
							break;
							
							
						case 4: //SN
							for (int j = wallPoints.elementAt(i + 0).getY() + 1; j < wallPoints.elementAt(i + 1).getY(); j++) {
								env.getLevel(1).setTileGrid(wallPoints.elementAt(i + 1).getX(), j, N0);
							}
							break;
							
						case 5: //SS
							for (int j = wallPoints.elementAt(i + 0).getY() + 1; j < wallPoints.elementAt(i + 2).getY(); j++) {
								env.getLevel(1).setTileGrid(wallPoints.elementAt(i + 1).getX(), j, S0);
							}
							break;
							
						case 6: //SE
	//							for (int j = wallPoints.elementAt(i + 0).getY() + 1; j < wallPoints.elementAt(i + 1).getY(); j++) {
	//								env.getLevel(1).setTileGrid(wallPoints.elementAt(i + 1).getX(), j, S0);
	//							}
							env.getLevel(1).setTileGrid(wallPoints.elementAt(i + 1).getX(), wallPoints.elementAt(i + 1).getY(), SEc0);
							for (int j = wallPoints.elementAt(i + 1).getX() + 1; j < wallPoints.elementAt(i + 2).getX(); j++) {
								env.getLevel(1).setTileGrid(j, wallPoints.elementAt(i + 1).getY(), E0);
							}
							break;
							
						case 7: //SW
	//							for (int j = wallPoints.elementAt(i + 0).getY() + 1; j < wallPoints.elementAt(i + 1).getY(); j++) {
	//								env.getLevel(1).setTileGrid(wallPoints.elementAt(i + 1).getX(), j, S0);
	//							}
							env.getLevel(1).setTileGrid(wallPoints.elementAt(i + 1).getX(), wallPoints.elementAt(i + 1).getY(), SWc0);
							for (int j = wallPoints.elementAt(i + 1).getX() - 1; j > wallPoints.elementAt(i + 2).getX(); j--) {
								env.getLevel(1).setTileGrid(j, wallPoints.elementAt(i + 1).getY(), W0);
							}
							break;
							
							
						case 8: //EN
	//							for (int j = wallPoints.elementAt(i + 0).getX() + 1; j < wallPoints.elementAt(i + 1).getX(); j++) {
	//								env.getLevel(1).setTileGrid(j, wallPoints.elementAt(i + 1).getY(), E0);
	//							}
							env.getLevel(1).setTileGrid(wallPoints.elementAt(i + 1).getX(), wallPoints.elementAt(i + 1).getY(), ENc0);
							for (int j = wallPoints.elementAt(i + 1).getY() - 1; j > wallPoints.elementAt(i + 2).getY(); j--) {
								env.getLevel(1).setTileGrid(wallPoints.elementAt(i + 1).getX(), j, N0);
							}
							break;
							
						case 9: //ES
	//							for (int j = wallPoints.elementAt(i + 0).getX() + 1; j < wallPoints.elementAt(i + 1).getX(); j++) {
	//								env.getLevel(1).setTileGrid(j, wallPoints.elementAt(i + 1).getY(), E0);
	//							}
							env.getLevel(1).setTileGrid(wallPoints.elementAt(i + 1).getX(), wallPoints.elementAt(i + 1).getY(), ESc0);
							for (int j = wallPoints.elementAt(i + 1).getY() + 1; j < wallPoints.elementAt(i + 2).getY(); j++) {
								env.getLevel(1).setTileGrid(wallPoints.elementAt(i + 1).getX(), j, S0);
							}
							break;
							
						case 10: //EE
							for (int j = wallPoints.elementAt(i + 0).getX() + 1; j < wallPoints.elementAt(i + 2).getX(); j++) {
								env.getLevel(1).setTileGrid(j, wallPoints.elementAt(i + 1).getY(), E0);
							}
							break;
							
						case 11: //EW
							for (int j = wallPoints.elementAt(i + 0).getX() + 1; j < wallPoints.elementAt(i + 1).getX(); j++) {
								env.getLevel(1).setTileGrid(j, wallPoints.elementAt(i + 1).getY(), W0);
							}
							break;
							
							
						case 12: //WN
	//							for (int j = wallPoints.elementAt(i + 0).getX() - 1; j > wallPoints.elementAt(i + 1).getX(); j--) {
	//								env.getLevel(1).setTileGrid(j, wallPoints.elementAt(i + 1).getY(), W0);
	//							}
							env.getLevel(1).setTileGrid(wallPoints.elementAt(i + 1).getX(), wallPoints.elementAt(i + 1).getY(), WNc0);
							for (int j = wallPoints.elementAt(i + 1).getY() - 1; j > wallPoints.elementAt(i + 2).getY(); j--) {
								env.getLevel(1).setTileGrid(wallPoints.elementAt(i + 1).getX(), j, N0);
							}
							break;
							
						case 13: //WS
	//							for (int j = wallPoints.elementAt(i + 0).getX() - 1; j > wallPoints.elementAt(i + 1).getX(); j--) {
	//								env.getLevel(1).setTileGrid(j, wallPoints.elementAt(i + 1).getY(), W0);
	//							}
							env.getLevel(1).setTileGrid(wallPoints.elementAt(i + 1).getX(), wallPoints.elementAt(i + 1).getY(), WSc0);
							for (int j = wallPoints.elementAt(i + 1).getY() + 1; j < wallPoints.elementAt(i + 2).getY(); j++) {
								env.getLevel(1).setTileGrid(wallPoints.elementAt(i + 1).getX(), j, S0);
							}
							break;
							
						case 14: //WE
							for (int j = wallPoints.elementAt(i + 0).getX() - 1; j > wallPoints.elementAt(i + 1).getX(); j--) {
								env.getLevel(1).setTileGrid(j, wallPoints.elementAt(i + 1).getY(), E0);
							}
							break;
							
						case 15: //WW
							for (int j = wallPoints.elementAt(i + 0).getX() - 1; j > wallPoints.elementAt(i + 2).getX(); j--) {
								env.getLevel(1).setTileGrid(j, wallPoints.elementAt(i + 1).getY(), W0);
							}
							break;
					}
				}
				int endType = getDirection(wallPoints.elementAt(wallPoints.size() - 2), wallPoints.lastElement());
				switch(endType) {
					case 0:
						env.getLevel(1).setTileGrid(wallPoints.lastElement().getX(), wallPoints.lastElement().getY(), NEnd0);
						break;
					
					case 1:
						env.getLevel(1).setTileGrid(wallPoints.lastElement().getX(), wallPoints.lastElement().getY(), SEnd0);
						break;
					
					case 2:
						env.getLevel(1).setTileGrid(wallPoints.lastElement().getX(), wallPoints.lastElement().getY(), EEnd0);
						break;
					
					case 3:
						env.getLevel(1).setTileGrid(wallPoints.lastElement().getX(), wallPoints.lastElement().getY(), WEnd0);
						break;
				}
			}
		}
		else {
			if (wallPoints.firstElement().equals(wallPoints.lastElement())) {
				for (int i = 0; i < wallPoints.size(); i++) {
					int cornerType = getCornerType(wallPoints.elementAt((i + 0) % (wallPoints.size() - 1)), wallPoints.elementAt((i + 1) % (wallPoints.size() - 1)), wallPoints.elementAt((i + 2) % (wallPoints.size() - 1)));
					switch(cornerType) {
						case 0: //NN
							for (int j = wallPoints.elementAt((i + 0) % (wallPoints.size() - 1)).getY() - 1; j > wallPoints.elementAt((i + 2)  % (wallPoints.size() - 1)).getY(); j--) {
								env.getLevel(1).setTileGrid(wallPoints.elementAt((i + 1) % (wallPoints.size() - 1)).getX(), j, N1);
							}
							break;
							
						case 1: //NS
							for (int j = wallPoints.elementAt((i + 0)  % (wallPoints.size() - 1)).getY() - 1; j >= wallPoints.elementAt((i + 1) % (wallPoints.size() - 1)).getY(); j--) {
								env.getLevel(1).setTileGrid(wallPoints.elementAt((i + 1) % (wallPoints.size() - 1)).getX(), j, S1);
							}
							break;
							
						case 2: //NE
//							for (int j = wallPoints.elementAt(i + 0).getY() - 1; j > wallPoints.elementAt(i + 1).getY(); j--) {
//								env.getLevel(1).setTileGrid(wallPoints.elementAt(i + 1).getX(), j, N0);
//							}
							env.getLevel(1).setTileGrid(wallPoints.elementAt((i + 1) % (wallPoints.size() - 1)).getX(), wallPoints.elementAt((i + 1) % (wallPoints.size() - 1)).getY(), NEc1);
							for (int j = wallPoints.elementAt((i + 1) % (wallPoints.size() - 1)).getX() + 1; j < wallPoints.elementAt((i + 2) % (wallPoints.size() - 1)).getX(); j++) {
								env.getLevel(1).setTileGrid(j, wallPoints.elementAt((i + 1) % (wallPoints.size() - 1)).getY(), E1);
							}
							break;
							
						case 3: //NW
//							for (int j = wallPoints.elementAt(i + 0).getY() - 1; j > wallPoints.elementAt(i + 1).getY(); j--) {
//								env.getLevel(1).setTileGrid(wallPoints.elementAt(i + 1).getX(), j, N0);
//							}
							env.getLevel(1).setTileGrid(wallPoints.elementAt((i + 1) % (wallPoints.size() - 1)).getX(), wallPoints.elementAt((i + 1) % (wallPoints.size() - 1)).getY(), NWc1);
							for (int j = wallPoints.elementAt((i + 1) % (wallPoints.size() - 1)).getX() - 1; j > wallPoints.elementAt((i + 2) % (wallPoints.size() - 1)).getX(); j--) {
								env.getLevel(1).setTileGrid(j, wallPoints.elementAt((i + 1) % (wallPoints.size() - 1)).getY(), W1);
							}
							break;
							
							
						case 4: //SN
							for (int j = wallPoints.elementAt((i + 0) % (wallPoints.size() - 1)).getY() + 1; j < wallPoints.elementAt((i + 1) % (wallPoints.size() - 1)).getY(); j++) {
								env.getLevel(1).setTileGrid(wallPoints.elementAt((i + 1) % (wallPoints.size() - 1)).getX(), j, N1);
							}
							break;
							
						case 5: //SS
							for (int j = wallPoints.elementAt((i + 0) % (wallPoints.size() - 1)).getY() + 1; j < wallPoints.elementAt((i + 2)  % (wallPoints.size() - 1)).getY(); j++) {
								env.getLevel(1).setTileGrid(wallPoints.elementAt((i + 1) % (wallPoints.size() - 1)).getX(), j, S1);
							}
							break;
							
						case 6: //SE
//							for (int j = wallPoints.elementAt(i + 0).getY() + 1; j < wallPoints.elementAt(i + 1).getY(); j++) {
//								env.getLevel(1).setTileGrid(wallPoints.elementAt(i + 1).getX(), j, S0);
//							}
							env.getLevel(1).setTileGrid(wallPoints.elementAt((i + 1) % (wallPoints.size() - 1)).getX(), wallPoints.elementAt((i + 1) % (wallPoints.size() - 1)).getY(), SEc1);
							for (int j = wallPoints.elementAt((i + 1) % (wallPoints.size() - 1)).getX() + 1; j < wallPoints.elementAt((i + 2) % (wallPoints.size() - 1)).getX(); j++) {
								env.getLevel(1).setTileGrid(j, wallPoints.elementAt((i + 1) % (wallPoints.size() - 1)).getY(), E1);
							}
							break;
							
						case 7: //SW
//							for (int j = wallPoints.elementAt(i + 0).getY() + 1; j < wallPoints.elementAt(i + 1).getY(); j++) {
//								env.getLevel(1).setTileGrid(wallPoints.elementAt(i + 1).getX(), j, S0);
//							}
							env.getLevel(1).setTileGrid(wallPoints.elementAt((i + 1) % (wallPoints.size() - 1)).getX(), wallPoints.elementAt((i + 1) % (wallPoints.size() - 1)).getY(), SWc1);
							for (int j = wallPoints.elementAt((i + 1) % (wallPoints.size() - 1)).getX() - 1; j > wallPoints.elementAt((i + 2) % (wallPoints.size() - 1)).getX(); j--) {
								env.getLevel(1).setTileGrid(j, wallPoints.elementAt((i + 1) % (wallPoints.size() - 1)).getY(), W1);
							}
							break;
							
							
						case 8: //EN
//							for (int j = wallPoints.elementAt(i + 0).getX() + 1; j < wallPoints.elementAt(i + 1).getX(); j++) {
//								env.getLevel(1).setTileGrid(j, wallPoints.elementAt(i + 1).getY(), E0);
//							}
							env.getLevel(1).setTileGrid(wallPoints.elementAt((i + 1) % (wallPoints.size() - 1)).getX(), wallPoints.elementAt((i + 1) % (wallPoints.size() - 1)).getY(), ENc1);
							for (int j = wallPoints.elementAt((i + 1) % (wallPoints.size() - 1)).getY() - 1; j > wallPoints.elementAt((i + 2) % (wallPoints.size() - 1)).getY(); j--) {
								env.getLevel(1).setTileGrid(wallPoints.elementAt((i + 1) % (wallPoints.size() - 1)).getX(), j, N1);
							}
							break;
							
						case 9: //ES
//							for (int j = wallPoints.elementAt(i + 0).getX() + 1; j < wallPoints.elementAt(i + 1).getX(); j++) {
//								env.getLevel(1).setTileGrid(j, wallPoints.elementAt(i + 1).getY(), E0);
//							}
							env.getLevel(1).setTileGrid(wallPoints.elementAt((i + 1) % (wallPoints.size() - 1)).getX(), wallPoints.elementAt((i + 1) % (wallPoints.size() - 1)).getY(), ESc1);
							for (int j = wallPoints.elementAt((i + 1) % (wallPoints.size() - 1)).getY() + 1; j < wallPoints.elementAt((i + 2) % (wallPoints.size() - 1)).getY(); j++) {
								env.getLevel(1).setTileGrid(wallPoints.elementAt((i + 1) % (wallPoints.size() - 1)).getX(), j, S1);
							}
							break;
							
						case 10: //EE
							for (int j = wallPoints.elementAt((i + 0) % (wallPoints.size() - 1)).getX() + 1; j < wallPoints.elementAt((i + 2) % (wallPoints.size() - 1)).getX(); j++) {
								env.getLevel(1).setTileGrid(j, wallPoints.elementAt((i + 1) % (wallPoints.size() - 1)).getY(), E1);
							}
							break;
							
						case 11: //EW
							for (int j = wallPoints.elementAt((i + 0) % (wallPoints.size() - 1)).getX() + 1; j < wallPoints.elementAt((i + 1) % (wallPoints.size() - 1)).getX(); j++) {
								env.getLevel(1).setTileGrid(j, wallPoints.elementAt((i + 1) % (wallPoints.size() - 1)).getY(), W1);
							}
							break;
							
							
						case 12: //WN
//							for (int j = wallPoints.elementAt(i + 0).getX() - 1; j > wallPoints.elementAt(i + 1).getX(); j--) {
//								env.getLevel(1).setTileGrid(j, wallPoints.elementAt(i + 1).getY(), W0);
//							}
							env.getLevel(1).setTileGrid(wallPoints.elementAt((i + 1) % (wallPoints.size() - 1)).getX(), wallPoints.elementAt((i + 1) % (wallPoints.size() - 1)).getY(), WNc1);
							for (int j = wallPoints.elementAt((i + 1) % (wallPoints.size() - 1)).getY() - 1; j > wallPoints.elementAt((i + 2) % (wallPoints.size() - 1)).getY(); j--) {
								env.getLevel(1).setTileGrid(wallPoints.elementAt((i + 1) % (wallPoints.size() - 1)).getX(), j, N1);
							}
							break;
							
						case 13: //WS
//							for (int j = wallPoints.elementAt(i + 0).getX() - 1; j > wallPoints.elementAt(i + 1).getX(); j--) {
//								env.getLevel(1).setTileGrid(j, wallPoints.elementAt(i + 1).getY(), W0);
//							}
							env.getLevel(1).setTileGrid(wallPoints.elementAt((i + 1) % (wallPoints.size() - 1)).getX(), wallPoints.elementAt((i + 1) % (wallPoints.size() - 1)).getY(), WSc1);
							for (int j = wallPoints.elementAt((i + 1) % (wallPoints.size() - 1)).getY() + 1; j < wallPoints.elementAt((i + 2) % (wallPoints.size() - 1)).getY(); j++) {
								env.getLevel(1).setTileGrid(wallPoints.elementAt((i + 1) % (wallPoints.size() - 1)).getX(), j, S1);
							}
							break;
							
						case 14: //WE
							for (int j = wallPoints.elementAt((i + 0) % (wallPoints.size() - 1)).getX() - 1; j > wallPoints.elementAt((i + 1) % (wallPoints.size() - 1)).getX(); j--) {
								env.getLevel(1).setTileGrid(j, wallPoints.elementAt((i + 1) % (wallPoints.size() - 1)).getY(), E1);
							}
							break;
							
						case 15: //WW
							for (int j = wallPoints.elementAt((i + 0) % (wallPoints.size() - 1)).getX() - 1; j > wallPoints.elementAt((i + 2) % (wallPoints.size() - 1)).getX(); j--) {
								env.getLevel(1).setTileGrid(j, wallPoints.elementAt((i + 1) % (wallPoints.size() - 1)).getY(), W1);
							}
							break;
					}
				}
			}
			else {
				int startType = getDirection(wallPoints.elementAt(0), wallPoints.elementAt(1));
				switch(startType) {
					case 0:
						env.getLevel(1).setTileGrid(wallPoints.elementAt(0).getX(), wallPoints.elementAt(0).getY(), NFront1);
						for (int j = wallPoints.elementAt(0).getY() - 1; j > wallPoints.elementAt(1).getY(); j--) {
							env.getLevel(1).setTileGrid(wallPoints.elementAt(0).getX(), j, N1);
						}
						break;
					
					case 1:
						env.getLevel(1).setTileGrid(wallPoints.elementAt(0).getX(), wallPoints.elementAt(0).getY(), SFront1);
						for (int j = wallPoints.elementAt(0).getY() + 1; j < wallPoints.elementAt(1).getY(); j++) {
							env.getLevel(1).setTileGrid(wallPoints.elementAt(0).getX(), j, S1);
						}
						break;
					
					case 2:
						env.getLevel(1).setTileGrid(wallPoints.elementAt(0).getX(), wallPoints.elementAt(0).getY(), EFront1);
						for (int j = wallPoints.elementAt(0).getX() + 1; j < wallPoints.elementAt(1).getX(); j++) {
							env.getLevel(1).setTileGrid(j, wallPoints.elementAt(0).getY(), E1);
						}
						break;
					
					case 3:
						env.getLevel(1).setTileGrid(wallPoints.elementAt(0).getX(), wallPoints.elementAt(0).getY(), WFront1);
						for (int j = wallPoints.elementAt(0).getX() - 1; j > wallPoints.elementAt(1).getX(); j--) {
							env.getLevel(1).setTileGrid(j, wallPoints.elementAt(0).getY(), W1);
						}
						break;
				}
				for (int i = 0; i < wallPoints.size() - 2; i++) {
					int cornerType = getCornerType(wallPoints.elementAt(i + 0), wallPoints.elementAt(i + 1), wallPoints.elementAt(i + 2));
					switch(cornerType) {
						case 0: //NN
							for (int j = wallPoints.elementAt(i + 0).getY() - 1; j > wallPoints.elementAt(i + 2).getY(); j--) {
								env.getLevel(1).setTileGrid(wallPoints.elementAt(i + 1).getX(), j, N1);
							}
							break;
							
						case 1: //NS
							for (int j = wallPoints.elementAt(i + 0).getY() - 1; j >= wallPoints.elementAt(i + 1).getY(); j--) {
								env.getLevel(1).setTileGrid(wallPoints.elementAt(i + 1).getX(), j, S1);
							}
							break;
							
						case 2: //NE
	//							for (int j = wallPoints.elementAt(i + 0).getY() - 1; j > wallPoints.elementAt(i + 1).getY(); j--) {
	//								env.getLevel(1).setTileGrid(wallPoints.elementAt(i + 1).getX(), j, N0);
	//							}
							env.getLevel(1).setTileGrid(wallPoints.elementAt(i + 1).getX(), wallPoints.elementAt(i + 1).getY(), NEc1);
							for (int j = wallPoints.elementAt(i + 1).getX() + 1; j < wallPoints.elementAt(i + 2).getX(); j++) {
								env.getLevel(1).setTileGrid(j, wallPoints.elementAt(i + 1).getY(), E1);
							}
							break;
							
						case 3: //NW
	//							for (int j = wallPoints.elementAt(i + 0).getY() - 1; j > wallPoints.elementAt(i + 1).getY(); j--) {
	//								env.getLevel(1).setTileGrid(wallPoints.elementAt(i + 1).getX(), j, N0);
	//							}
							env.getLevel(1).setTileGrid(wallPoints.elementAt(i + 1).getX(), wallPoints.elementAt(i + 1).getY(), NWc1);
							for (int j = wallPoints.elementAt(i + 1).getX() - 1; j > wallPoints.elementAt(i + 2).getX(); j--) {
								env.getLevel(1).setTileGrid(j, wallPoints.elementAt(i + 1).getY(), W1);
							}
							break;
							
							
						case 4: //SN
							for (int j = wallPoints.elementAt(i + 0).getY() + 1; j < wallPoints.elementAt(i + 1).getY(); j++) {
								env.getLevel(1).setTileGrid(wallPoints.elementAt(i + 1).getX(), j, N1);
							}
							break;
							
						case 5: //SS
							for (int j = wallPoints.elementAt(i + 0).getY() + 1; j < wallPoints.elementAt(i + 2).getY(); j++) {
								env.getLevel(1).setTileGrid(wallPoints.elementAt(i + 1).getX(), j, S1);
							}
							break;
							
						case 6: //SE
	//							for (int j = wallPoints.elementAt(i + 0).getY() + 1; j < wallPoints.elementAt(i + 1).getY(); j++) {
	//								env.getLevel(1).setTileGrid(wallPoints.elementAt(i + 1).getX(), j, S0);
	//							}
							env.getLevel(1).setTileGrid(wallPoints.elementAt(i + 1).getX(), wallPoints.elementAt(i + 1).getY(), SEc1);
							for (int j = wallPoints.elementAt(i + 1).getX() + 1; j < wallPoints.elementAt(i + 2).getX(); j++) {
								env.getLevel(1).setTileGrid(j, wallPoints.elementAt(i + 1).getY(), E1);
							}
							break;
							
						case 7: //SW
	//							for (int j = wallPoints.elementAt(i + 0).getY() + 1; j < wallPoints.elementAt(i + 1).getY(); j++) {
	//								env.getLevel(1).setTileGrid(wallPoints.elementAt(i + 1).getX(), j, S0);
	//							}
							env.getLevel(1).setTileGrid(wallPoints.elementAt(i + 1).getX(), wallPoints.elementAt(i + 1).getY(), SWc1);
							for (int j = wallPoints.elementAt(i + 1).getX() - 1; j > wallPoints.elementAt(i + 2).getX(); j--) {
								env.getLevel(1).setTileGrid(j, wallPoints.elementAt(i + 1).getY(), W1);
							}
							break;
							
							
						case 8: //EN
	//							for (int j = wallPoints.elementAt(i + 0).getX() + 1; j < wallPoints.elementAt(i + 1).getX(); j++) {
	//								env.getLevel(1).setTileGrid(j, wallPoints.elementAt(i + 1).getY(), E0);
	//							}
							env.getLevel(1).setTileGrid(wallPoints.elementAt(i + 1).getX(), wallPoints.elementAt(i + 1).getY(), ENc1);
							for (int j = wallPoints.elementAt(i + 1).getY() - 1; j > wallPoints.elementAt(i + 2).getY(); j--) {
								env.getLevel(1).setTileGrid(wallPoints.elementAt(i + 1).getX(), j, N1);
							}
							break;
							
						case 9: //ES
	//							for (int j = wallPoints.elementAt(i + 0).getX() + 1; j < wallPoints.elementAt(i + 1).getX(); j++) {
	//								env.getLevel(1).setTileGrid(j, wallPoints.elementAt(i + 1).getY(), E0);
	//							}
							env.getLevel(1).setTileGrid(wallPoints.elementAt(i + 1).getX(), wallPoints.elementAt(i + 1).getY(), ESc1);
							for (int j = wallPoints.elementAt(i + 1).getY() + 1; j < wallPoints.elementAt(i + 2).getY(); j++) {
								env.getLevel(1).setTileGrid(wallPoints.elementAt(i + 1).getX(), j, S1);
							}
							break;
							
						case 10: //EE
							for (int j = wallPoints.elementAt(i + 0).getX() + 1; j < wallPoints.elementAt(i + 2).getX(); j++) {
								env.getLevel(1).setTileGrid(j, wallPoints.elementAt(i + 1).getY(), E1);
							}
							break;
							
						case 11: //EW
							for (int j = wallPoints.elementAt(i + 0).getX() + 1; j < wallPoints.elementAt(i + 1).getX(); j++) {
								env.getLevel(1).setTileGrid(j, wallPoints.elementAt(i + 1).getY(), W1);
							}
							break;
							
							
						case 12: //WN
	//							for (int j = wallPoints.elementAt(i + 0).getX() - 1; j > wallPoints.elementAt(i + 1).getX(); j--) {
	//								env.getLevel(1).setTileGrid(j, wallPoints.elementAt(i + 1).getY(), W0);
	//							}
							env.getLevel(1).setTileGrid(wallPoints.elementAt(i + 1).getX(), wallPoints.elementAt(i + 1).getY(), WNc1);
							for (int j = wallPoints.elementAt(i + 1).getY() - 1; j > wallPoints.elementAt(i + 2).getY(); j--) {
								env.getLevel(1).setTileGrid(wallPoints.elementAt(i + 1).getX(), j, N1);
							}
							break;
							
						case 13: //WS
	//							for (int j = wallPoints.elementAt(i + 0).getX() - 1; j > wallPoints.elementAt(i + 1).getX(); j--) {
	//								env.getLevel(1).setTileGrid(j, wallPoints.elementAt(i + 1).getY(), W0);
	//							}
							env.getLevel(1).setTileGrid(wallPoints.elementAt(i + 1).getX(), wallPoints.elementAt(i + 1).getY(), WSc1);
							for (int j = wallPoints.elementAt(i + 1).getY() + 1; j < wallPoints.elementAt(i + 2).getY(); j++) {
								env.getLevel(1).setTileGrid(wallPoints.elementAt(i + 1).getX(), j, S1);
							}
							break;
							
						case 14: //WE
							for (int j = wallPoints.elementAt(i + 0).getX() - 1; j > wallPoints.elementAt(i + 1).getX(); j--) {
								env.getLevel(1).setTileGrid(j, wallPoints.elementAt(i + 1).getY(), E1);
							}
							break;
							
						case 15: //WW
							for (int j = wallPoints.elementAt(i + 0).getX() - 1; j > wallPoints.elementAt(i + 2).getX(); j--) {
								env.getLevel(1).setTileGrid(j, wallPoints.elementAt(i + 1).getY(), W1);
							}
							break;
					}
				}
				int endType = getDirection(wallPoints.elementAt(wallPoints.size() - 2), wallPoints.lastElement());
				switch(endType) {
					case 0:
						env.getLevel(1).setTileGrid(wallPoints.lastElement().getX(), wallPoints.lastElement().getY(), NEnd1);
						break;
					
					case 1:
						env.getLevel(1).setTileGrid(wallPoints.lastElement().getX(), wallPoints.lastElement().getY(), SEnd1);
						break;
					
					case 2:
						env.getLevel(1).setTileGrid(wallPoints.lastElement().getX(), wallPoints.lastElement().getY(), EEnd1);
						break;
					
					case 3:
						env.getLevel(1).setTileGrid(wallPoints.lastElement().getX(), wallPoints.lastElement().getY(), WEnd1);
						break;
				}
			}
		}
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
		if (Key.M.isPressed(e)) {
			if (!wallMode) {
				activateWallMode();
			}
			else {
				makeWall();
				deactivateWallMode();
			}
		}
		if (Key.N.isPressed(e)) {
			alternateWallOrientation = !alternateWallOrientation;
			makeWall();
			deactivateWallMode();
		}
		if (Key.ONE.isPressed(e)) {
			if (!selectedTile.equals(new Tile(BackgroundElement.class, 0, 0))) {
				previousTile = selectedTile;
				selectedTile = new Tile(BackgroundElement.class, 0, 0);
			}
		}
		if (Key.TWO.isPressed(e)) {
			if (!selectedTile.equals(new Tile(MidgroundElement.class, 0, 0))) {
				previousTile = selectedTile;
				selectedTile = new Tile(MidgroundElement.class, 0, 0);
			}
		}
		if (Key.THREE.isPressed(e)) {
			if (!selectedTile.equals(new Tile(ForegroundElement.class, 0, 0))) {
				previousTile = selectedTile;
				selectedTile = new Tile(ForegroundElement.class, 0, 0);
			}
		}
		
		//TODO add a key to register a team, and function to look up by name?

	}
	
	private void activateWallMode() {
		autosaveUndo();
		wallPoints.clear();
		wallMode = true;
		if (!selectedTile.equals(new Tile(MidgroundElement.class, 10, 0))) {
			previousTile = selectedTile;
			selectedTile = new Tile(MidgroundElement.class, 10, 0);
		}
	}

	private void deactivateWallMode() {
		wallMode = false;
		selectedTile = previousTile;
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
		if (!selectedTile.equals(env.getLevel(selectedTile.getType().getLayer()).getTileGrid(getMousePosAbsolute().getX() / Tile.WIDTH, getMousePosAbsolute().getY() / Tile.WIDTH))) {
			previousTile = selectedTile;
			selectedTile = env.getLevel(selectedTile.getType().getLayer()).getTileGrid(getMousePosAbsolute().getX() / Tile.WIDTH, getMousePosAbsolute().getY() / Tile.WIDTH);
		}
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
			Debug.error("tried to spawn entity in non-world");
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
			} else if (selectedEntityClass.equals(Ballista.class)) {
				DialogMap inputs = new DialogMap();
				inputs.put("X", "1");
				inputs.put("Y", "0");
				if (dialog("Ballista Direction", inputs) == null) {
					return;
				}
				Vector dir = new Vector(inputs.getDouble("X"), inputs.getDouble("Y"));
				world.add(new Ballista(spawnPos, dir));
			}

			else {
				world.add(selectedEntityClass.getConstructor(Vector.class).newInstance(spawnPos));
			}

		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| SecurityException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			Debug.error("cannot spawn entity type " + selectedEntityClass.getSimpleName());
		}
	}

	private void deleteEntity() {

		if (!(env instanceof World)) {
			Debug.error("tried to delete entity in non-world");
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
			Debug.error("unable to autosave for undo");
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
			Debug.error("unable to autosave for redo");
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
				Debug.log("undone!");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				Debug.error("cannot undo, IOException");
				e.printStackTrace();
			}
		} else {
			Debug.error("cannot undo right now");
		}
	}

	public void redo() {
		if (canRedo) {
			try {
				env.load(Main.getFile("/saves/.redo1"));
				Debug.log("redo!");
				canUndo = true;
				canRedo = false;
			} catch (IOException e) {
				Debug.error("cannot redo, IOException");
				e.printStackTrace();
			}
		} else {
			Debug.error("cannot redo right now");
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

	//0 = GOING NORTH
	//1 = GOING SOUTH
	//2 = GOING EAST
	//3 = GOING WEST
	private int getDirection(Vector t0, Vector t1) {
		if (t0.getX() == t1.getX()) {
			if (t0.getY() > t1.getY()) {
				return 0;
			}
			else {
				return 1;
			}
		}
		else {
			if (t0.getX() < t1.getX()) {
				return 2;
			}
			else {
				return 3;
			}
		}
	}
	
	//0 = NORTHBOUND WALL
	//1 = NORTH->SOUTH
	//2 = NORTH->EAST 
	//3 = NORTH->WEST
	//4 = SOUTH->NORTH
	//5 = SOUTHBOUND WALL
	//6 = SOUTH->EAST
	//7 = SOUTH->WEST
	//8 = EAST->NORTH
	//9 = EAST->SOUTH
	//10= EASTBOUND WALL
	//11= EAST->WEST
	//12= WEST->NORTH
	//13= WEST->SOUTH
	//14= WEST->EAST
	//15= WESTBOUND WALL
	private int getCornerType(Vector t0, Vector t1, Vector t2) {
		int x = 0;
		
		if (getDirection(t0,t1) == 0) {
			x += 0;
		}
		else if (getDirection(t0,t1) == 1) {
			x += 4;
		}
		else if (getDirection(t0,t1) == 2) {
			x += 8;
		}
		else {
			x += 12;
		}
		
		if (getDirection(t1,t2) == 0) {
			x += 0;
		}
		else if (getDirection(t1,t2) == 1) {
			x += 1;
		}
		else if (getDirection(t1,t2) == 2) {
			x += 2;
		}
		else {
			x += 3;
		}
		
		return x;
	}

	@Override
	protected void onStart() {
	}
}
