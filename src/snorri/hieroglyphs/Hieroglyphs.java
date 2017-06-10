package snorri.hieroglyphs;

import java.awt.Image;
import java.io.File;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import javax.swing.ImageIcon;

import snorri.main.Debug;
import snorri.main.Main;
import snorri.main.Util;
import snorri.parser.Grammar;

public class Hieroglyphs {

	private static final HashMap<String, Image> glyphIcons;
	
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
			Debug.error("could not find HTML glyph directory");
		}
			
	}
	
	public static                                                                              Set<String> getGlyphs() {
		return glyphIcons.keySet();
	}
	
	public static void load() {
		Debug.log(glyphIcons.size() + " HTML glyphs loaded");
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
			Debug.error("bad URL for file " + raw);
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
	
	public static Image getImage(String raw) {
		return glyphIcons.get(raw);
	}
	
	private static Image loadImage(String raw) {
		return Main.getImage(getPath(raw));
	}
	
}
