package snorri.world;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;

import snorri.entities.Entity;
import snorri.entities.Player;
import snorri.main.Main;
import snorri.main.Util;

public class Campaign implements Playable {

	//TODO general multiworld class for non-campaigns
	
	private int currentWorld = 0;
	private World[] worlds;
	
	public enum WorldId {
		SPAWN_TOWN,
		VALLEY_OF_KINGS,
		PYRAMIDS,
		NILE,
		UNDERWORLD;

		//read a string representation as a number or an enum name
		public static WorldId fromString(String world) {
			Integer parse = Util.getInteger(world);
			if (parse == null) {
				return valueOf(world);
			}
			return values()[parse];
		}
		
	}
		
	public Campaign(File f) throws FileNotFoundException, IOException  {
		load(f);
	}
	
	public Campaign() {
		worlds = new World[] {
				new World(), // spawn town
				new World(), // Valley of Kings
				new World(), // pyramids
				new World(), // Nile
				new World() // underworld
		};
	}
	
	public World getWorld(int i) {
		return worlds[i];
	}
	
	public World getWorld(WorldId id) {
		return getWorld(id.ordinal());
	}
	
	public void teleport(Entity e, int i, Vector newPos) {
		getCurrentWorld().delete(e);
		e.setPos(newPos);
		currentWorld = i;
		getCurrentWorld().add(e);
	}
	
	public void teleport(Entity e, World world, Vector newPos) {
		int i = Arrays.asList(worlds).indexOf(world);
		if (i != -1) {
			teleport(e, i, newPos);
		}
	}
	
	public void teleport(Entity e, WorldId i, Vector newPos) {
		teleport(e, i.ordinal(), newPos);
	}
	
	@Override
	public World getCurrentWorld() {
		return worlds[currentWorld];
	}

	@Override
	public void update(Entity focus, double deltaTime) {
		getCurrentWorld().update(focus, deltaTime);
	}

	@Override
	public Player computeFocus() {
		for (World world : worlds) {
			Player focus = world.computeFocus();
			if (focus != null) {
				return focus;
			}
		}
		return null;
	}

	@Override
	public void load(File folder) throws FileNotFoundException, IOException {
		
		if (!folder.isDirectory()) {
			Main.error("path for campaign is not a folder");
			throw new IOException();
		}
		
		worlds = new World[WorldId.values().length];
		for (File f : folder.listFiles()) {
			WorldId id = WorldId.valueOf(f.getName());
			if (id != null) {
				worlds[id.ordinal()] = new World(f);
			}
		}
		
	}

	@Override
	public void save(File f, boolean recomputeGraphs) throws IOException {
		
		if (!f.isDirectory()) {
			f.mkdir();
		}
		
		for (int i = 0; i < worlds.length; i++) {
			worlds[i].save(new File(f, WorldId.values()[i].name()));
		}
		
	}

}
