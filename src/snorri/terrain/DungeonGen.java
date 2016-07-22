package snorri.terrain;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import net.sourceforge.yamlbeans.YamlException;
import net.sourceforge.yamlbeans.YamlReader;
import snorri.main.Main;
import snorri.world.Level;
import snorri.world.Vector;
import snorri.world.Tile.TileType;

@SuppressWarnings("unchecked")
public class DungeonGen extends TerrainGenerator {
	
	public static final HashMap<String, Structure> structures;
	
	static {
		
		structures = new HashMap<>();
		
		try {
			YamlReader reader = new YamlReader(new FileReader(Main.getPath("/worlds/structures/doors.yaml")));
			reader.getConfig().setClassTag("door", Vector.class);
			HashMap<String, List<Vector>> yaml = (HashMap<String, List<Vector>>) reader.read();
			for (Entry<String, List<Vector>> entry : yaml.entrySet()) {
				try {
					File f = Main.getPath("/worlds/structures/" + entry.getKey() + ".dat");
					Structure struct = new Structure(new Level(f), entry.getValue());
					structures.put(entry.getKey(), struct);
				} catch (IOException e) {
					Main.error("could not load level file for structure " + entry.getKey());
				}
			}
		} catch (FileNotFoundException e) {
			Main.error("could not locate doors file");
		} catch (YamlException e) {
			Main.error("could not parse YAML in doors file");
			e.printStackTrace();
		}
		
	}
		
	public DungeonGen(Vector dim) {
		super(dim);
	}

	@Override
	public Level genLevel() {
		
		Level l = new Level(dim, TileType.VOID);
		return l;
		
	}
	
}
