package snorri.main;

import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.util.Collection;
import java.util.Collections;

public class Util {

	public static Integer getInteger(String input) {
		try {
			return Integer.parseInt(input);
		} catch (Exception e) {
			return null;
		}
	}
	
	public static Double getDouble(String input) {
		try {
			return Double.parseDouble(input);
		} catch (Exception e) {
			return null;
		}
	}
	
	public static String removeExtension(String fileName) {
		return fileName.replaceFirst("[.][^.]+$", "");
	}
	
	public static BufferedImage deepCopy(BufferedImage bi) {
		ColorModel cm = bi.getColorModel();
		boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
		WritableRaster raster = bi.copyData(null);
		return new BufferedImage(cm, raster, isAlphaPremultiplied, null);
	}
	
	public static String clean(String constant) {
		return constant.toLowerCase().replace('_', ' ');
	}
	
	public static Collection<Object> safe(Collection<Object> c) {
		return c == null ? Collections.emptyList() : c;
	}
	
}
