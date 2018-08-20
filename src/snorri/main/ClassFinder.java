package snorri.main;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import snorri.entities.Entity;

public class ClassFinder {

	private static final char PKG_SEPARATOR = '.';
	private static final char DIR_SEPARATOR = '/';
	private static final String CLASS_FILE_SUFFIX = ".class";

	
	public static File getPackageFolder(String scannedPackage) {
		String scannedPath = "/" + scannedPackage.replace(PKG_SEPARATOR, DIR_SEPARATOR);
		String scannedDir = ClassFinder.class.getResource(scannedPath).toString().replace("file:", "");
		try {
			return new File(URLDecoder.decode(scannedDir, "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			Debug.logger.log(Level.SEVERE, "Could not access resource", e);
			return null;
		}
	}
	
	public static List<Class<? extends Entity>> find(String scannedPackage) {
		File dir = getPackageFolder(scannedPackage); // allows directories with spaces
		List<Class<? extends Entity>> classes = new ArrayList<Class<? extends Entity>>();
		if (!dir.isDirectory()) {
			Debug.logger.warning("could not find class " + scannedPackage);
			return null;
		}
		for (File file : dir.listFiles()) {
			classes.addAll(find(file, scannedPackage));
		}
		return classes;
	}

	@SuppressWarnings("unchecked")
	private static List<Class<? extends Entity>> find(File file, String scannedPackage) {
		List<Class<? extends Entity>> classes = new ArrayList<Class<? extends Entity>>();
		String resource = scannedPackage + PKG_SEPARATOR + file.getName();
		if (file.isDirectory()) {
			for (File child : file.listFiles()) {
				classes.addAll(find(child, resource));
			}
		} else if (resource.endsWith(CLASS_FILE_SUFFIX)) {
			int endIndex = resource.length() - CLASS_FILE_SUFFIX.length();
			String className = resource.substring(0, endIndex);
			try {
				classes.add((Class<? extends Entity>) Class.forName(className));
			} catch (ClassNotFoundException ignore) {
			}
		}
		return classes;
	}

}