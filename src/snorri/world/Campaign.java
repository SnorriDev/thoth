package snorri.world;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import snorri.entities.Player;

public class Campaign implements Playable {

	// TODO: store a bunch of worlds

	@Override
	public World getCurrentWorld() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void update(double deltaTime) {
		// TODO Auto-generated method stub

	}

	@Override
	public Player getFocus() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void load(File folder) throws FileNotFoundException, IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void save(File folder) throws IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void save(File f, boolean recomputeGraphs) throws IOException {
		// TODO Auto-generated method stub
		
	}

}
