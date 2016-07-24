package snorri.overlay;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import snorri.main.Main;
import snorri.main.Util;
import snorri.parser.Grammar;

public class Hieroglyphics {

	private static List<String> htmlGlyphs;
	
	static {
		
		Main.log("loading HTML glyphs...");
		htmlGlyphs = new ArrayList<>();
		
		File dir = Main.getPath("/textures/hieroglyphs");
		
		if (dir.exists()) {
			for (File glyph : dir.listFiles()) {
				htmlGlyphs.add(Util.removeExtension(glyph.getName()));
			}
			Collections.sort(htmlGlyphs, new Comparator<String>() {
				@Override
				public int compare(String s1, String s2) {
					return Integer.compare(s2.length(), s1.length()); //longer first
				}
			});
			Main.log(Main.getHTMLGlyph(htmlGlyphs.get(0)));
			Main.log(htmlGlyphs.size() + " HTML glyphs loaded");
		} else {
			Main.error("could not find HTML glyph directory");
		}
			
	}
		
	public static String transliterate(String raw) {
		List<String> out = new ArrayList<>();
		nextWord: for (String word : Grammar.getWords(raw)) {
			for (String glyph : htmlGlyphs) {
				if (word.equals(glyph)) {
					out.add(Main.getHTMLGlyph(glyph));
					continue nextWord;
				}
			}
			out.add(word);
		}
		return "<p>" + String.join(" ", out) + "</p>";
	}
	
}
