package snorri.terrain;

import java.util.HashSet;
import java.util.Set;

import snorri.entities.Center;
import snorri.main.Util;
import snorri.world.BackgroundElement;
import snorri.world.ForegroundElement;
import snorri.world.MidgroundElement;
import snorri.world.Tile;
import snorri.world.Vector;
import snorri.world.World;

public class RoomGen implements Generator {

	public static final int DOOR_WIDTH = 3; // this has no effect within the class

	private static final double CRACK_CHANCE = 0.1;
	private static final Set<Tile> BG_OPTIONS;

	static {
		BG_OPTIONS = new HashSet<>();
		BG_OPTIONS.add(new Tile(BackgroundElement.SAND, 0));
//		for (int i = 0; i < 1; i++) {
//			BG_OPTIONS.add(new Tile(BackgroundElement.SAND, i));
//		}
	}

	private final Vector dim;
	private final int range;

	private final Set<Tile> bgOptions;
	private final double crackChance;

	public RoomGen(int width, int height) {
		this(new Vector(width, height), 0);
	}
	
	public RoomGen(Vector dim, int range) {
		this(dim, range, BG_OPTIONS, CRACK_CHANCE);
	}

	public RoomGen(Vector dim, int range, Set<Tile> bgOptions, double crackChance) {
		this.dim = dim;
		this.range = range;
		this.bgOptions = bgOptions;
		this.crackChance = crackChance;
	}

	@Override
	public World genWorld() {

		// make a blank world
		Tile bg = Util.random(bgOptions);
		int xRange = Util.randintSigned(range), yRange = Util.randintSigned(range);
		World world = new World(dim.getX() + xRange, dim.getY() + yRange);

		// place floor tiles
		for (int x = 0; x < dim.getX(); x++) {
			for (int y = 0; y < dim.getY(); y++) {
				world.getLevel().setTileGrid(x, y, new Tile(bg));
			}
		}

		// place walls along the top and bottom sides
		for (int x = 1; x < dim.getX() - 1; x++) {
			world.wrapGridUpdate(x, 0, new Tile(getRandomWallType(), 0));
			world.wrapGridUpdate(x, dim.getY() - 1, new Tile(getRandomWallType(), 2));
		}

		// place walls along the left and right sides
		for (int y = 1; y < dim.getY() - 1; y++) {
			world.wrapGridUpdate(0, y, new Tile(getRandomWallType(), 3));
			world.wrapGridUpdate(dim.getX() - 1, y, new Tile(getRandomWallType(), 1));
		}

		// place wall corners
		world.wrapGridUpdate(0, 0, new Tile(MidgroundElement.WALL_CONCAVE, 0));
		world.wrapGridUpdate(dim.getX() - 1, 0, new Tile(MidgroundElement.WALL_CONCAVE, 1));
		world.wrapGridUpdate(dim.getX() - 1, dim.getY() - 1, new Tile(MidgroundElement.WALL_CONCAVE, 2));
		world.wrapGridUpdate(0, dim.getY() - 1, new Tile(MidgroundElement.WALL_CONCAVE, 3));

		// TODO should not always make all 4 doors (parameter)
		// TODO places the arches
		
		// place doors along the left and right sides
		Vector center = dim.copy().divide_(2);
		Vector axis = Vector.RIGHT;
		if (center.getX() == Math.floor(center.getX())) {			
			placeFourDoor(world, center.getX(), 0, true, axis);
			placeFourDoor(world, center.getX(), dim.getY() - 1, false, axis);
		} else {
			placeFiveDoor(world, center.getX(), 0, true, axis);
			placeFiveDoor(world, center.getX(), dim.getY() - 1, false, axis);
		}
		
		// place doors along the top and bottom sides
		axis = Vector.DOWN;
		if (center.y == center.getY()) {			
			placeFourDoor(world, 0, center.getY(), false, axis);
			placeFourDoor(world, dim.getX() - 1, center.getY(), true, axis);
		} else {
			placeFiveDoor(world, 0, center.getY(), false, axis);
			placeFiveDoor(world, dim.getX() - 1, center.getY(), true, axis);
		}
		
		// create the center object
		world.add(new Center(dim.copy().globalPos_().divide_(2)));
		return world;
	}

	private void placeFiveDoor(World world, int x, int y, boolean orientation, Vector axis) {
		int style = getStyle(axis, orientation);
		int c = (int) Math.ceil(x);
		Vector center = new Vector(c, y);
		placeGate(world, center.copy().add_(axis.copy().multiply_(-2)), style, true);
		placeDoor(world, center.copy().add_(axis.copy().multiply_(-1)), style);
		placeDoor(world, center, style);
		placeDoor(world, center.copy().add_(axis), style);
		placeGate(world, center.copy().add_(axis.copy().multiply_(2)), style, false); // TODO gate placements need to be fixed
	}
	
	private void placeFourDoor(World world, int x, int y, boolean orientation, Vector axis) {
		int style = getStyle(axis, orientation);
		Vector center = new Vector(x, y);
		placeGate(world, center.copy().add_(axis.copy().multiply_(-2)), style, orientation);
		placeDoor(world, center.copy().add_(axis.copy().multiply_(-1)), style);
		placeDoor(world, center, style);
		placeGate(world, center.copy().add_(axis), style, !orientation);
	}
	
	private int getStyle(Vector axis, boolean orientation) {
		return axis.equals(Vector.RIGHT) ? (orientation ? 0 : 2) : (orientation ? 1 : 3);
	}
	
	private void placeDoor(World world, Vector v, int style) {
		world.wrapGridUpdate(v, new Tile(MidgroundElement.NONE));
		world.wrapGridUpdate(v, new Tile(ForegroundElement.GATE, style));
	}
	
	private void placeGate(World world, Vector v, int style, boolean left) {
		world.wrapGridUpdate(v, new Tile(MidgroundElement.WALL_STUB, style));
		ForegroundElement type = left ? ForegroundElement.GATE_LEFT_OPEN : ForegroundElement.GATE_RIGHT_OPEN;
		world.wrapGridUpdate(v, new Tile(type, style + 4));
	}

	private MidgroundElement getRandomWallType() {
		return Util.flip(crackChance) ? MidgroundElement.BROKEN_WALL : MidgroundElement.WALL;
	}

}
