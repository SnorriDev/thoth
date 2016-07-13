package snorri.main;

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
	
}
