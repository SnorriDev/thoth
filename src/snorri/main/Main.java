package snorri.main;

import java.awt.FileDialog;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLayeredPane;
import javax.swing.UIManager;

import net.sourceforge.yamlbeans.YamlReader;
import snorri.dialog.Dialog;
import snorri.dialog.Objective;
import snorri.dialog.Portraits;
import snorri.hieroglyphs.Hieroglyphs;
import snorri.inventory.RandomDrop;
import snorri.parser.Lexicon;
import snorri.terrain.Structure;
import snorri.terrain.TerrainGen;
import snorri.world.Vector;
import snorri.world.World;

public class Main {
	
	private static GamePanel window;
	private static GamePanel outerOverlay;

	private static JFrame frame;
	private static JLayeredPane pane;

	private static Font customFont;
	
	public static final BufferedImage DEFAULT_TEXTURE = getImage("/textures/tiles/default00.png");

	public static class ResizeListener implements ComponentListener {

		public ResizeListener() {
		}
		
		@Override
		public void componentResized(ComponentEvent e) {
			resize();
		}

		@Override
		public void componentMoved(ComponentEvent e) {
			resize();
		}

		@Override
		public void componentShown(ComponentEvent e) {
		}

		@Override
		public void componentHidden(ComponentEvent e) {
		}
		
		private void resize() {
			resize(outerOverlay);
			resize(window);
		}
		
		public static void resize(JComponent component) {
			if (component != null) {
				component.setBounds(frame.getContentPane().getBounds());
			}
		}
		
	}
	
	public static void main(String[] args) {
		
		try {
			UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		Lexicon.load();
		Hieroglyphs.load();
		RandomDrop.load();
		Portraits.load();
		setupFont();
								
		frame = new JFrame("The Book of Thoth");
		frame.setSize(1800, 900);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);

		pane = new JLayeredPane();
		getLayeredPane().setOpaque(true);
		getLayeredPane().addComponentListener(new Main.ResizeListener());

		frame.getContentPane().add(getLayeredPane());
		// FOR FULL SCREEN: frame.setExtendedState(JFrame.MAXIMIZED_BOTH);

		launchMenu();

	}

	public static Rectangle getBounds() {
		return frame.getBounds();
	}

	public static GamePanel getWindow() {
		return window;
	}

	public static File getDir() {
		return new File(".");
	}

	public static File getFile(String path) {
		return new File(getDir(), path);
	}

	public static void setupFont() {
		customFont = loadFont("/fonts/avenir.otf");		
		UIManager.put("Button.font", getCustomFont(20));
		UIManager.put("Label.font", getCustomFont(13));
		Debug.log("default font loaded");
	}

	public static Font getCustomFont(float size) {
		return customFont.deriveFont(Font.PLAIN, size);
	}

	public static Font loadFont(String path) {
		try {
			GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
			File fontFile = getFile(path);
			Font f = Font.createFont(Font.TRUETYPE_FONT, fontFile);
			ge.registerFont(f);
			return f;
		} catch (IOException | FontFormatException e) {
			Debug.error("font not found at " + path);
			return null;
		}
	}

	public static BufferedImage getImage(File file) {
		try {
			return ImageIO.read(file);
		} catch (IllegalArgumentException e) {
			Debug.error("unable to find image " + file.getName());
			return null;
		} catch (IOException e) {
			Debug.error("issue loading image " + file.getName());
			return null;
		}
	}
	
	public static BufferedImage getImage(String path) {
		return getImage(getFile(path));
	}
	
	public static YamlReader getYamlReader(File f) throws FileNotFoundException {
		YamlReader reader = new YamlReader(new FileReader(f));
		reader.getConfig().setClassTag("door", Vector.class);
		reader.getConfig().setClassTag("spawn", Vector.class);
		reader.getConfig().setClassTag("vector", Vector.class);
		reader.getConfig().setClassTag("pos", Vector.class);
		reader.getConfig().setClassTag("struct", Structure.class);
		reader.getConfig().setClassTag("objective", Objective.class);
		reader.getConfig().setClassTag("dialog", Dialog.class);
		return reader;
	}
	
	public static YamlReader getYamlReader(String path) throws FileNotFoundException {
		return getYamlReader(getFile(path));
	}

	public static File getFileDialog(String msg, int flag) {
		return getFileDialog(msg, flag, false);
	}

	public static File getFileDialog(String msg, int flag, boolean isLevel) {
		FileDialog fd = new FileDialog(frame, msg, flag);
		fd.setVisible(true);

		if (fd.getFile() == null) {
			return null;
		}

		File f = new File(fd.getDirectory(), fd.getFile());

		// if they select an image (or we are editing a level, not a world),
		// don't grab the folder
		if (fd.getFile().endsWith("png") || fd.getFile().endsWith("wav") || isLevel) {
			return f;
		}

		// otherwise return that directory
		if (!f.isDirectory() && !f.mkdir()) {
			return new File(fd.getDirectory());
		}

		// if the file is a directory or doesn't exist, return it
		return f;
	}

	/**
	 * display the window in the main JFrame
	 * 
	 * @param newWindow
	 *            the new window to display
	 */
	public static final void setWindow(GamePanel newWindow) {
		if (window != null) {
			window.stopBackgroundThread();
			getLayeredPane().remove(window);
		}
		
		window = newWindow;
		
		getLayeredPane().add(window, JLayeredPane.DEFAULT_LAYER);
		
		window.grabFocus();
		ResizeListener.resize(window);
		
		frame.revalidate();
		frame.repaint();
		
	}

	/**
	 * set the outer HUD which should appear over the game screen
	 * 
	 * @param newOverlay
	 *            the new HUD overlay
	 */
	public static final void setOverlay(GamePanel newOverlay) {
		if (outerOverlay != null) {
			outerOverlay.stopBackgroundThread();
			getLayeredPane().remove(outerOverlay);
		}
		outerOverlay = newOverlay;
		if (newOverlay != null) {
			
			getLayeredPane().add(outerOverlay, JLayeredPane.PALETTE_LAYER);
			
			outerOverlay.grabFocus();
			ResizeListener.resize(outerOverlay);
						
		}
		frame.revalidate();
		frame.repaint();
	}

	public static JLayeredPane getLayeredPane() {
		return pane;
	}
	
	private static void launchWorld(World world) {
		if (world == null) {
			return;
		}
		setWindow(new GameWindow(world));
	}

	public static void launchMenu() {
		setOverlay(null);
		setWindow(new MainMenu());
	}

	public static void launchGame(WorldSelection worldSelect) {
		loadInto(new Runnable() {
			@Override
			public void run() {
				World w = worldSelect.loadWorld();
				if (w != null) {
					launchWorld(worldSelect.loadWorld());
				} else {
					setWindow(new MainMenu());
				}
			}
		});
	}
	
	public static void launchGame(TerrainGen gen) {
		loadInto(new Runnable() {
			@Override
			public void run() {
				launchWorld(gen.genWorld());
			}
		});
	}
	
	public static void launchGame(World world) {
		loadInto(new Runnable() {
			@Override
			public void run() {
				launchWorld(world);
			}
		});
	}
	
	//TODO set up loading screens well

	public static void launchGame() {
		launchGame(new TerrainGen(200, 200));
	}

	public static void launchEditor() {
		setWindow(new LevelEditor());
	}

	/**
	 * show a loading screen while a Runnable runs
	 * 
	 * @param proc
	 *            a Runnable whose <code>run()</code> method will be invoked.
	 *            This Runnable should change the screen to the target window
	 *            when the loading is done
	 */
	public static void loadInto(Runnable proc) {
		setWindow(new LoadingScreen());
		new Thread(proc).start();
	}

	public static JFrame getFrame() {
		return frame;
	}

}
