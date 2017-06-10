package snorri.world;

import java.awt.FileDialog;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import snorri.main.Debug;
import snorri.main.Main;

public interface Savable {

	public void load(File folder) throws FileNotFoundException, IOException;

	default void save(File f) throws IOException {
		save(f, false);
	}
	
	public void save(File f, boolean recomputeGraphs) throws IOException;
	
	default void load(String folderName) throws FileNotFoundException, IOException {
		load(new File(folderName));
	}

	default void save(String folderName) throws IOException {
		save(new File(folderName));
	}
	
	default void wrapSave() {
		
		File f = Main.getFileDialog("Select save destination", FileDialog.SAVE, this instanceof Level);
		
		if (f == null) {
			return;
		}
		
		try {
			save(f, true);
		} catch (IOException er) {
			Debug.error("cannot save world " + f.getName() + "; are all objects serializable?");
			er.printStackTrace(); //see what is non-serializable
		}
	}
	
}
