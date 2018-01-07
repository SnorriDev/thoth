package snorri.terrain;

import snorri.entities.Player;
import snorri.world.Vector;
import snorri.world.World;

/**
 * Create a Bermuda-like island by sloping the edges of the height map down.
 * Increasing <code>SINK_FACTOR</code> will make the island's edges sink more.
 * Shoutout to Uzimon.
 * @author lambdaviking
 */

@Deprecated
public class IslandGen extends TerrainGen {

	protected final double sink;
	protected final double sinkFactor;
	
	protected static final double SINK = 1.2d;
	protected static final double SINK_FACTOR = 0.08d;
	
	public IslandGen(int x, int y) {
		this (new Vector(x, y), SINK, SINK_FACTOR);
	}
	
	public IslandGen(Vector v) {
		this(v, SINK, SINK_FACTOR);
	}
	
	public IslandGen(Vector v, double sink, double sinkFactor) {
		super(v);
		this.sink = sink;
		this.sinkFactor = sinkFactor;
	}
	
	@Override
	protected double getHeight(int x, int y, Vector randomTrans, double[] frequencies, double smoothness, double elevation) {
		double orig = super.getHeight(x, y, randomTrans, frequencies, smoothness, elevation);
		double scaledDist = new Vector(x, y).distanceSquared(dim.copy().divide_(2)) / dim.magnitude() / 2;
		return orig - sinkFactor * Math.pow(scaledDist, sink);
	}
	
	@Override
	public World genWorld() {
		World world = new World(genBackground(), genMidground(), genForeground());	
		Player p = new Player(world.getRandomSpawnPos());
		world.add(p);		
		return world;
	}

}
