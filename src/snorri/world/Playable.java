package snorri.world;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import snorri.entities.Player;

public interface Playable {
	
	World getCurrentWorld();
	
	public void update(float deltaTime);

	public Player getFocus();

	public void load(File folder) throws FileNotFoundException, IOException;

	public void save(File folder) throws IOException;
	
	default void load(String folderName) throws FileNotFoundException, IOException {
		load(new File(folderName));
	}

	default void save(String folderName) throws IOException {
		save(new File(folderName));
	}
	
}
