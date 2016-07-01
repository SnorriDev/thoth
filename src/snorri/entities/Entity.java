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
	private boolean flying;

	public Entity(Entity e) {
		if (e.pos == null && !(this instanceof EntityGroup)) {
			Main.error("spawned non-EntityGroup at null using other entity: " + this.getClass().getSimpleName());
			return;
		}
		
		this.pos = e.pos.copy();
		this.r = e.r;
		
	}
	
	public Entity(Vector pos, int r) {
		
		if (pos == null && !(this instanceof EntityGroup)) {
			Main.error("spawned non-EntityGroup at null: " + this.getClass().getSimpleName());
		}
		
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
		if (pos == null || e.pos == null) {
			return true;
		}
		return e.pos.distance(pos) <= r + e.r + rad;
	}
	
	public boolean intersects(Rectangle rect) {
		
		if (pos == null) {
			return true;
		}
		
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
		return intersectsWall(world.getLevel());
	}
	
	public boolean intersectsWall(Level level) {

		for (int i = (pos.getX() - r) / Tile.WIDTH - 1; i <= (pos.getX() + r) / Tile.WIDTH; i++) {
			for (int j = (pos.getY() - r) / Tile.WIDTH - 1; j <= (pos.getY() + r) / Tile.WIDTH; j++) {
				
				if (! intersects(Level.getRectange(i, j))) {
					continue;
				}
				
				Tile t = level.getTileGrid(i, j);
				if (t == null || !t.isPathable()) {
					return true;
				}
								
			}
		}
		
		return false;
		
	}
	
	public boolean contains(Entity e) {
		if (pos == null || e.pos == null) {
			return true;
		}
		return e.pos.distance(pos) + e.r <= r;
	}
	
	public boolean contains(Entity e, int rad) {
		if (pos == null || e.pos == null) {
			return true;
		}
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
	public void update(World world, double d) {
		burnTimer.update(d);
	}
	
	public void renderHitbox(FocusedWindow g, Graphics gr) {
		//for some reason this is causing concurrent modification issues
		if (pos == null || g.getFocus().getPos() == null) {
			return;
		}
		Vector rel = pos.copy().sub(g.getFocus().getPos());
		//the issue is in this draw oval line! wtf (concurrent modification of graphics?)
		gr.drawOval(rel.getX() - r + g.getBounds().width / 2, rel.getY() - r + g.getBounds().height / 2, 2 * r, 2 * r);
	}
	
	/**
	 * @param e
	 * 	other
	 * @return
	 * 	whether the two entities are spatially equivalent (same radius and position)
	 */
	public boolean spatialEquals(Entity e) {
		if (e.pos == null) {
			return pos == null;
		}
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
		
		Vector rel = pos.copy().sub(g.getFocus().getPos());
		gr.drawImage(sprite, rel.getX() + (g.getBounds().width - sprite.getWidth(null)) / 2, rel.getY() + (g.getBounds().height - sprite.getHeight(null)) / 2, sprite.getWidth(null), sprite.getHeight(null), null);

		renderHitbox(g, gr);
		
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
	
	/**
	 * if dir is zero, then this function will always return false
	 * @return whether moving in direction dir would bring entity into wall
	 */
	public boolean wouldIntersectWall(World world, Vector dir) {
		//TODO: perhaps this is using lots of memory
		
		if (dir.equals(Vector.ZERO)) {
			return true;
		}
		
		return new Entity(pos.copy().add(dir), r).intersectsWall(world);
		
	}
	
	public void startFlying() {
		flying = true;
	}
	
	public void stopFlying() {
		flying = false;
	}
	
	public boolean isFlying() {
		return flying;
	}
	
	/**
	 * moves entity WITHOUT recalculating entity tree radii
	 * @param world
	 * 	world we're moving in
	 * @param direction
	 * 	direction to move (magnitude is irrelevant)
	 * @param speed
	 * 	speed to move at
	 * @return
	 * 	whether or not we were able to move
	 */
	public boolean moveHard(World world, Vector direction, double speed) {
		
		Vector dir = direction.copy().scale(speed);
		
		if (dir.equals(Vector.ZERO)) {
			return false;
		}
				
		if (!flying && wouldIntersectWall(world, dir)) {
			
			//see if we're hitting only one wall
			if (! wouldIntersectWall(world, dir.getProjectionX())) {
				dir = dir.getProjectionX();
			}
			else if (! wouldIntersectWall(world, dir.getProjectionY())) {
				dir = dir.getProjectionY();
			}
			
			//see if we're hitting a corner
			else if (! wouldIntersectWall(world, dir.getProjection(Vector.DOWN_LEFT))) {
				dir = dir.getProjection(Vector.DOWN_LEFT);
			}
			else if (! wouldIntersectWall(world, dir.getProjection(Vector.DOWN_RIGHT))) {
				dir = dir.getProjection(Vector.DOWN_RIGHT);
			}
			
			//give up; TODO more stuff?
			else {
				return false;
			}
			
		}
		
		this.pos.add(dir);
		return true;
		
	}

}
