package snorri.hieroglyphs;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;

import javax.swing.ImageIcon;

import snorri.main.Debug;
import snorri.main.Main;
import snorri.main.Util;
import snorri.parser.Grammar;

public class Hieroglyphs {

	private static final HashMap<String, BufferedImage> glyphIcons;
	
	private static final int SMALL_GLYPH_HEIGHT = 20;
	
	static {
		
		glyphIcons = new HashMap<>();
		
		File dir = Main.getFile("/textures/hieroglyphs");
		
		if (dir.exists()) {
			for (File glyph : dir.listFiles()) {
				
				if (!glyph.getName().endsWith("png")) {
					continue;
				}
				
				String name = Util.removeExtension(glyph.getName());
				glyphIcons.put(name, loadImage(name));
			}
		} else {
			Debug.logger.severe("Could not find HTML glyph directory.");
		}
			
	}
	
	public static Set<String> getGlyphs() {
		return glyphIcons.keySet();
	}
	
	public static void load() {
		Debug.logger.info(glyphIcons.size() + " HTML glyphs loaded.");
	}
		
	public static String transliterate(String raw) {
		List<String> out = new ArrayList<>();
		nextWord: for (String word : Grammar.getWords(raw)) {
			for (String glyph : getGlyphs()) {
				if (word.equals(glyph)) {
					out.add(getHTMLGlyph(glyph));
					continue nextWord;
				}
			}
			out.add(word);
		}
		return String.join("", out);
	}
	
	static String getPath(String raw) {
		return "/textures/hieroglyphs/" + raw + ".png";
	}
	
	public static String getHTMLGlyph(String raw) {
		File f = Main.getFile(getPath(raw));
		if (!f.exists()) {
			return null;
		}
		try {
			return "<img class='hiero' src=\'" + f.toURI().toURL() + "'/>";
		} catch (MalformedURLException e) {
			Debug.logger.log(Level.SEVERE, "Bad URL for HTML glyph.", e);
			return null;
		}
	}
		
	public static ImageIcon getIcon(String raw) {
		Image image = getImage(raw);
		if (image == null) {
			return null;
		}
		return new ImageIcon(image);
	}
	
	public static ImageIcon getSmallIcon(String raw) {
		Image image = getImage(raw);
		if (image == null) {
			return null;
		}
		return new ImageIcon(Util.resize(image, 0, SMALL_GLYPH_HEIGHT));
	}
	
	public static BufferedImage getImage(String raw) {
		return glyphIcons.get(raw);
	}
	
	//is it necessary that things are buffered images? idk
	private static BufferedImage loadImage(String raw) {
		return Util.getBufferedImage(Main.getImage(getPath(raw)));
	}
	
}
