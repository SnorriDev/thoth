package snorri.entities;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;
import java.io.Serializable;

import snorri.animations.Animation;
import snorri.inventory.Timer;
import snorri.main.FocusedWindow;
import snorri.main.Main;
import snorri.semantics.Nominal;
import snorri.world.Level;
import snorri.world.Tile;
import snorri.world.Vector;
import snorri.world.World;

public class Entity implements Nominal, Serializable {

	private static final long serialVersionUID = 1L;
	protected Vector pos;
	protected int r;
	protected Animation animation;
	
	//TODO: entities that ignore collisions (with a boolean?)
	
	private Timer burnTimer = new Timer(5);
	
	public Entity(Entity e) {
		this.pos = e.pos.copy();
		this.r = e.r;
	}
	
	public Entity(Vector pos, int r) {
		this.pos = pos;
		this.r = r;
	}
	
	public Entity(Vector pos) {
		this(pos, 2);
	}

	public Vector getPos() {
		return pos;
	}
	
	public int getRadius() {
		return r;
	}
	
	public Animation getAnimation() {
		return animation;
	}
	
	public void burn() {
		burnTimer.hardReset();
	}
	
	public boolean isBurning() {
		return ! burnTimer.isOffCooldown();
	}

	public boolean intersects(Vector pos1) {
		return pos.distance(pos1) <= r;
	}
	
	public boolean intersects(Entity e) {
		return e.pos.distance(pos) <= r + e.r;
	}
	
	public boolean intersects(Entity e, int rad) {
		return e.pos.distance(pos) <= r + e.r + rad;
	}
	
	public boolean intersects(Rectangle rect) {
		
		Vector circleDistance = new Vector(rect.getX(), rect.getY()).add(new Vector(rect).divide(2)).sub(pos).abs();
		
		if (circleDistance.getX() > rect.getWidth() / 2 + r) {
			return false;
		}
		
		if (circleDistance.getY() > rect.getHeight() / 2 + r) {
			return false;
		}
		
		if (circleDistance.getX() <= rect.getWidth() / 2) {
			return true;
		}
		
		if (circleDistance.getY() <= rect.getHeight() / 2) {
			return true;
		}
		
		double cornerDistance = new Vector(rect).divide(2).distance(circleDistance);
		return cornerDistance <= r;
		
	}
	
	public boolean intersectsWall(World world) {
		
		for (int i = (pos.getX() - r) / Tile.WIDTH; i <= (pos.getX() + r) / Tile.WIDTH; i++) {
			for (int j = (pos.getY() - r) / Tile.WIDTH; j <= (pos.getY() + r) / Tile.WIDTH; j++) {
				
				if (! intersects(Level.getRectange(i, j))) {
					continue;
				}
				
				Tile t = world.getLevel().getTileRaw(i, j);
				if (t == null || !t.isPathable()) {
					return true;
				}
								
			}
		}
		
		return false;
		
	}
	
	public boolean contains(Entity e) {
		return e.pos.distance(pos) + e.r <= r;
	}
	
	public boolean contains(Entity e, int rad) {
		return e.pos.distance(pos) + e.r <= r + rad;
	}
	
	protected void traverse(int depth) {
		String indent = "";
		for (int i = 0; i < depth; i++) {
			indent += "  ";
		}
		Main.log(indent + this.toString());
	}
	
	public void traverse() {
		traverse(0);
	}
	
	public String toString() {
		return this.getClass().getSimpleName() + "{pos: " + pos.toString() + ", r: " + r + "}";
	}
	
	//TODO: make this into a boolean so we can know whether or not to recalculate collision bubbles
	public void update(World world, float f) {
		burnTimer.update(f);
	}
	
	public void renderHitbox(FocusedWindow g, Graphics gr) {
		
		if (pos == null) {
			return;
		}
		
		Vector rel = pos.copy();
		rel.sub(g.getFocus().pos);
		gr.drawOval(rel.getX() - r + g.getBounds().width / 2, rel.getY() - r + g.getBounds().height / 2, 2 * r, 2 * r);
	}
	
	//returns true if the two entities are spatially equivalent
	public boolean equals(Entity e) {
		return e.pos.equals(pos) && e.r == r;
	}
	
	public void renderAround(FocusedWindow g, Graphics gr) {
		
		renderHitbox(g, gr);
		
		if (animation == null) {
			return;
		}
		
		Image sprite = animation.getSprite();
		if (sprite == null) {
			return;
		}
		
		gr.drawImage(animation.getSprite(), pos.getX() - sprite.getWidth(null) + g.getBounds().width / 2, pos.getY() - sprite.getHeight(null) + g.getBounds().height / 2, null);
		
	}

	@Override
	public Object get(World world, AbstractSemantics attr) {
		
		if (attr == AbstractSemantics.POSITION) {
			return pos;
		}
		if (attr == AbstractSemantics.TILE) {
			return world.getLevel().getTile(pos);
		}
		if (attr == AbstractSemantics.NAME) {
			return toString();
		}
		
		return null;
	}
	
	public void move(World world, Vector direction, double speed) {
		
		Vector dir = direction.copy().multiply(speed);
		
		if (dir.equals(Vector.ZERO)) {
			return;
		}
		
		if (new Entity(pos.copy().add(dir), r).intersectsWall(world)) {
			return;
		}
		
		world.getEntityTree().move(this, dir);
	}

}
