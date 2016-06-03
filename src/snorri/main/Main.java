package snorri.main;

import java.awt.BorderLayout;
import java.awt.FileDialog;
import java.awt.Image;
import java.awt.Rectangle;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JFrame;

import snorri.entities.Desk;
import snorri.entities.Entity;
import snorri.entities.Player;
import snorri.entities.Unit;
import snorri.parser.Lexicon;
import snorri.world.Vector;
import snorri.world.World;



public class Main {
	
	private static GamePanel	window;
	private static JFrame		frame;
								
	public static void main(String[] args) {
		
		Lexicon.init();
		
//		World w = new World();
//		w.add(new Entity(new Vector(40, 40)));
//		try {
//			w.save("C:/Users/vikin_000/Desktop/world_test");
//		} catch (IOException e) {
//			Main.error("boom");
//		}
//		
//		try {
//			World w2 = new World("C:/Users/vikin_000/Desktop/world_test");
//			Main.log(w2.getEntityTree().getAllEntities());
//		} catch (IOException e) {
//			Main.error("boom2");
//		}
		
		frame = new JFrame("Spoken Word");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(1800, 900);
		frame.setVisible(true);
		
		//FOR FULL SCREEN: frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
		
		window = new MainMenu();
		frame.getContentPane().add(window, BorderLayout.CENTER);
		frame.getContentPane().validate();
	}
	
	public static Rectangle getBounds() {
		return frame.getBounds();
	}
	
	public static void error(Object o) {
		System.out.println("[ERROR] " + o);
	}
	
	public static void log(String s) {
		System.out.println("[LOG] " + s);
		//System.out.println("[" + Colors.LOG + "LOG" + Colors.RESET + "] " + s);
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
	
	public static String getFileDialog(String msg) {
		FileDialog fd = new FileDialog(frame, msg);
		fd.setVisible(true);
		return fd.getFile();
	}
	
	public static void launchGame() {
		
		frame.getContentPane().remove(window);
		
		World world = new World();
		
		world.add(new Entity(new Vector(105, 130)));
		world.add(new Entity(new Vector(100, 100)));
		world.add(new Entity(new Vector(143, 133)));
		world.add(new Entity(new Vector(100, 124)));
		world.add(new Entity(new Vector(115, 100)));
		world.add(new Entity(new Vector(111, 130)));
		world.add(new Desk(new Vector(200, 200)));
		world.add(new Unit(new Vector(20, 20)));
		
		window = new GameWindow(world, new Player(new Vector(50, 50)));
		world.add(((GameWindow) window).getFocus()); //the player
		frame.getContentPane().add(window, BorderLayout.CENTER);
		frame.getContentPane().revalidate();
		window.requestFocus();
		
	}
	
	public static void launchEditor() {
		
		frame.getContentPane().remove(window);
		window = new LevelEditor();
		frame.getContentPane().add(window, BorderLayout.CENTER);
		frame.getContentPane().revalidate();
		window.requestFocus();
		
	}
	
}
