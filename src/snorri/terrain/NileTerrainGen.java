package snorri.terrain;

import snorri.world.Vector;

public class NileTerrainGen extends TerrainGenerator {

	private int centerX;
	
	public NileTerrainGen(double height) {
		super(new Vector(100, height));
		centerX = dim.getX() / 2;
	}

	@Override
	protected double getHeight(int x, int y, Vector randomTrans, double[] frequencies, double smoothness, double elevation) {
		Vector n = getScaledPos(x, y);
		double e = elevation;
		for (int i = 0; i < frequencies.length; i++) {
			e += 1 / frequencies[i] * noise(n.copy().multiply(frequencies[i]));
		}
		if (e > 0) {
			return Math.pow(e, smoothness);
		} else {
			return e;
		}
	}
	
	//TODO: play around with this
	protected double distanceToShore(int x) {
		return (centerX - Math.abs(x - centerX)) / centerX;
	}

}
