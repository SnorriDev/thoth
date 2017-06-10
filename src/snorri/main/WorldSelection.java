package snorri.main;

import java.io.IOException;

import snorri.world.World;

public class WorldSelection {

	private final String name;
	
	public WorldSelection(String name) {
		this.name = name;
	}
	
	public World loadWorld() {
		
		try {
			return new World(Main.getFile(name));
		} catch (IOException e1) {
			Debug.error("could not load " + name);
			return null;
		}
		
	}
	
}
