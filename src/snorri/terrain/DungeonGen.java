package snorri.terrain;

import java.awt.Rectangle;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import net.sourceforge.yamlbeans.YamlException;
import net.sourceforge.yamlbeans.YamlReader;
import snorri.entities.Enemy;
import snorri.entities.Player;
import snorri.main.Main;
import snorri.world.Editable;
import snorri.world.Level;
import snorri.world.Tile;
import snorri.world.Vector;
import snorri.world.World;
import snorri.world.Tile.TileType;

@SuppressWarnings("unchecked")
public class DungeonGen extends TerrainGen {
	
	public static final int DOOR_WIDTH = 7;
	
	protected static HashMap<String, Structure> structures;
	protected Vector start;
	
	static {
		
		structures = new HashMap<>();

		try {
			Main.log("loading structures...");
			YamlReader reader = new YamlReader(new FileReader(Main.getPath("/worlds/structures/doors.yaml")));
			reader.getConfig().setClassTag("door", Vector.class);
			reader.getConfig().setClassTag("spawn", Vector.class);
			reader.getConfig().setClassTag("struct", Structure.class);
			structures = (HashMap<String, Structure>) reader.read();
			for (String key : structures.keySet()) {
				Editable l;
				File levelFile = Main.getPath("/worlds/structures/" + key + ".dat");
				if (levelFile.exists()) {
					l = new Level(levelFile);
				} else {
					l = new World(Main.getPath("/worlds/structures/" + key));
				}
				structures.get(key).setTemplate(l);
			}
			Main.log("structures loaded successfully!");
		} catch (IOException e) {
			Main.error("could not locate file while loading structures");
			e.printStackTrace();
		} catch (YamlException e) {
			Main.error("could not parse YAML in doors file");
			e.printStackTrace();
		}
		
	}
	
	public static Structure get(String key) {
		return structures.get(key);
	}
	
	public DungeonGen(int x, int y) {
		super(x, y);
	}

	@Override
	public Level genLevel() {
		
		Level l = new Level(dim, TileType.VOID);
		
		List<Rectangle> filledRegions = new ArrayList<Rectangle>();
		Queue<Vector> doorQ = new LinkedList<Vector>();
		List<Structure> children = new ArrayList<Structure>() {

			private static final long serialVersionUID = 1L;

			@Override
			public boolean add(Structure s) {
				Structure r = s.getXReflected();
				return super.add(s) && super.add(r) &&
						super.add(r.getTransposed()) && super.add(s.getTransposed());
			}
			
			//TODO flip as well as transpose
			
		};
		
		start = new Vector(1, dim.getY() / 2);
		if (Math.random() > 0.5) {
			Main.log("inverting start");
			start.invert();
		}
		doorQ.add(start);
		children.add(structures.get("spawn"));
		
		boolean startSet = false;
		nextDoor: while (!doorQ.isEmpty()) {
			Collections.shuffle(children);
			Vector pos = doorQ.poll();
			//TODO match with door pos on other thing
			for (Structure s : children) {
				for (Vector drawPos : s.getStarts(pos)) {
					if (s.drawAt(l, filledRegions, drawPos)) {
						if (!startSet) {
							start = drawPos.copy().add(s.start);
							startSet = true;
						}
						doorQ.addAll(s.getExits(drawPos, pos));
						children.clear();
						s.addChildren(children);
						continue nextDoor;
					}
				}
			}
			l.fillDoor(pos, new Tile(TileType.WALL, 4), TileType.VOID);
		}
		
		return l;
		
	}
	
	@Override
	public World genWorld() {
		World world = new World(genLevel());
		Player p = new Player(start.copy().toGlobalPos());
		world.addHard(p);
		for (int i = 0; i < 20; i++) {
			Vector pos = world.getRandomSpawnPos();
			if (pos != null) {
				world.addHard(new Enemy(pos, p));
			}
		}
		return world;
	}
	
}
