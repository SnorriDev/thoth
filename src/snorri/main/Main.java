package snorri.main;

import java.awt.FileDialog;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Rectangle;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLayeredPane;

import snorri.parser.Lexicon;
import snorri.terrain.TerrainGenerator;
import snorri.world.World;

public class Main {
			
	private static GamePanel window;
	private static JComponent outerOverlay;
	
	private static JFrame frame;
	private static JLayeredPane pane;

	public static void main(String[] args) {

		Lexicon.init();

		System.setProperty("apple.awt.fileDialogForDirectories", "true");
		System.setProperty("windows.awt.fileDialogForDirectories", "true");

		frame = new JFrame("Spoken Word");
		frame.setSize(1800, 900);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
		
		pane = new JLayeredPane();
		getLayeredPane().setOpaque(true);
		
		frame.getContentPane().setLayout(new GridLayout(0,1));  
		frame.getContentPane().add(getLayeredPane());
		//FOR FULL SCREEN: frame.setExtendedState(JFrame.MAXIMIZED_BOTH);

		launchMenu();

	}

	public static Rectangle getBounds() {
		return frame.getBounds();
	}

	public static void error(Object o) {
		System.out.println("[ERROR] " + o);
	}

	public static void log(String s) {
		System.out.println("[LOG] " + s);
	}

	public static void log(Object o) {
		System.out.println("[RAW] " + o);
	}

	public static GamePanel getWindow() {
		return window;
	}

	public static Image getImageResource(String path) {
		try {
			return ImageIO.read(Main.class.getResource(path));
		} catch (IllegalArgumentException e) {
			Main.error("unable to find image " + path);
			return null;
		} catch (IOException e) {
			return null;
		}
	}

	public static File getFileDialog(String msg, int flag) {
		FileDialog fd = new FileDialog(frame, msg, flag);
		fd.setVisible(true);

		if (fd.getFile() == null) {
			return null;
		}

		File f = new File(fd.getDirectory(), fd.getFile());

		// if they select an image, return that
		if (fd.getFile().endsWith("png")) {
			return f;
		}

		// if they select a file that's not an image, return that directory
		if (f.exists() && !f.isDirectory()) {
			return new File(fd.getDirectory());
		}

		// if the file is a directory or doesn't exist, return it
		return f;
	}

	/**
	 * display the window in the main JFrame
	 * @param newWindow
	 * the new window to display
	 */
	public static final void setWindow(GamePanel newWindow) {
		if (window != null) {
			getLayeredPane().remove(window);
			getLayeredPane().revalidate();
		}
		window = newWindow;
		window.setVisible(true);
		window.setBounds(frame.getBounds());
		getLayeredPane().add(window, JLayeredPane.DEFAULT_LAYER);
		getLayeredPane().revalidate();
		getLayeredPane().repaint();
		window.requestFocusInWindow();
	}
	
	/**
	 * set the outer HUD which should appear over the game screen
	 * @param newOverlay
	 * the new HUD overlay
	 */
	public static final void setOverlay(JComponent newOverlay) {
		if (outerOverlay != null) {
			getLayeredPane().remove(outerOverlay);
			getLayeredPane().revalidate();
		}
		outerOverlay = newOverlay;
		if (newOverlay != null) {
			outerOverlay.setVisible(true);
			outerOverlay.setBounds(frame.getBounds());
			getLayeredPane().add(outerOverlay, JLayeredPane.PALETTE_LAYER);
			getLayeredPane().revalidate();
			getLayeredPane().repaint();
			outerOverlay.requestFocusInWindow();
		}
	}
	
	public static JLayeredPane getLayeredPane() {
		return pane;
	}

	public static void launchMenu() {
		setOverlay(null);
		setWindow(new MainMenu());
	}
	
	public static void launchGame(World world) {
		setWindow(new GameWindow(world));
	}

	public static void launchGame() {
		loadInto(new Runnable() {
			@Override
			public void run() {
				TerrainGenerator ter = new TerrainGenerator(200, 200);
				World world = ter.genWorld();
				launchGame(world);
				Main.log("playing world " + world);
			}
		});

	}

	public static void launchEditor() {
		setWindow(new LevelEditor());
	}

	/**
	 * show a loading screen while the thread runs
	 * 
	 * @param proc
	 *            a Runnable whose run() method will be invoked. run() should
	 *            change the screen to something cooler when it's done
	 */
	public static void loadInto(Runnable proc) {
		setWindow(new LoadingScreen());
		new Thread(proc).start();
	}

	public static JFrame getFrame() {
		return frame;
	}

}
