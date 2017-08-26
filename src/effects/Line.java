package effects;

import java.awt.Graphics2D;

import snorri.world.Vector;

/**
 * A line class useful for building lasers and lightning bolts.
 * @author snorri
 * @see https://gamedevelopment.tutsplus.com/tutorials/how-to-generate-shockingly-good-2d-lightning-effects--gamedev-2681
 *
 */

public class Line {

	private Vector a, b;
	private float thickness;
	
	public Line(Vector a, Vector b, float thickness) {
		this.a = a;
		this.b = b;
		this.thickness = thickness;
	}
	
	public void render(Graphics2D g) {
		
		Vector tangent = b.copy().sub(a);
		float rotation = (float) Math.atan2(tangent.getY(), tangent.getX());
	}
	
	
}
