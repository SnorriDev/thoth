package snorri.world;

import java.awt.FileDialog;
import java.io.File;
import java.io.IOException;

import snorri.main.Debug;
import snorri.main.Main;

/**
 * An interface for objects (including layers) that can be saved.
 * @author snorri
 *
 */
public interface Savable {
	
	public void save(File f, boolean recomputeGraphs) throws IOException;
	
	default void save(File f) throws IOException {
		save(f, false);
	}

	default void save(String folderName) throws IOException {
		save(new File(folderName));
	}
	
	default void wrapSave() {
		File f = Main.getFileDialog("Select save destination", FileDialog.SAVE, this instanceof TileLayer);
		if (f == null) {
			return;
		}
		try {
			save(f, true);
		} catch (IOException er) {
			Debug.logger.log(java.util.logging.Level.SEVERE, "Are all object serializable?", er);
			er.printStackTrace();
		}
	}
	
}
