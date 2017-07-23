package snorri.pathfinding;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

import snorri.entities.Unit;
import snorri.main.Debug;
import snorri.world.World;

/**
 * A basic type for storing lists of players on the same team.
 * @author Will Merrill
 */

public class Team extends ArrayList<Unit> {

	private static final long serialVersionUID = 1L;

	private String name;
	
	@SuppressWarnings({"unchecked", "resource"})
	public static List<Team> load(File teamsFile) throws FileNotFoundException, IOException {
		try {
			ObjectInputStream stream = new ObjectInputStream(new FileInputStream(teamsFile));
			List<Team> teams = (List<Team>) stream.readObject();
			if (teams == null) {
				Debug.log("loaded null list of teams");
			} else {
				Debug.log("loaded " + teams.size() + " teams");
			}
			return teams;
		} catch (ClassNotFoundException e) {
			Debug.error("corrupted team data", e);
			return null;
		}
	}
	
	@SuppressWarnings("resource")
	public static void save(File teamsFile, List<Team> teams) throws FileNotFoundException, IOException {
		ObjectOutputStream stream = new ObjectOutputStream(new FileOutputStream(teamsFile));
		stream.writeObject(teams);
		if (teams != null) {
			Debug.log("saved " + teams.size() + " teams");
		}
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String toString() {
		return name;
	}

	/**
	 * Lookup a team by its name
	 * @param name
	 * 	The name of the team to find
	 * @param world
	 * 	The world within which to find the team
	 * @return the team object
	 */
	public static Team getByName(String name, World world) {
		if (world.getTeams() == null) {
			return null;
		}
		for (Team t : world.getTeams()) {
			if (t.toString().equals(name)) {
				return t;
			}
		}
		return null;
	}
	
}
