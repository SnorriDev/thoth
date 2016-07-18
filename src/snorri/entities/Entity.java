package snorri.entities;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.Shape;
import java.io.Serializable;

import snorri.animations.Animation;
import snorri.collisions.CircleCollider;
import snorri.collisions.Collider;
import snorri.inventory.Timer;
import snorri.main.Debug;
import snorri.main.FocusedWindow;
import snorri.main.Main;
import snorri.semantics.Nominal;
import snorri.world.Level;
import snorri.world.Tile;
import snorri.world.Vector;
import snorri.world.World;

public class Entity implements Nominal, Serializable {

	private static final long serialVersionUID = 1L;
	protected Collider collider;
	protected Vector pos;
	protected Animation animation;
	protected boolean ignoreCollisions = false;
	
	private Timer burnTimer = new Timer(5);
	private boolean flying;

	public Entity(Vector pos, Collider collider) {
		this.pos = (pos == null) ? null : pos.copy();
		this.collider = collider.cloneOnto(this); //so we don't get weird entangled positions
	}
	
	public Entity(Entity e) {
		this(e.pos, e.collider); //TODO change so that the copying happens here
	}
	
	public Entity(Vector pos, int r) {
		this(pos, new CircleCollider(pos, r));
	}
	
	public Entity(Vector pos) {
		this(pos, 2);
	}

	public Vector getPos() {
		return pos;
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
		return collider.intersects(pos1);
	}
	
	public boolean intersects(Entity e) {
		return intersects(e.collider);
	}
	
	public boolean intersects(Collider c) {
		return intersects(c.getShape());
	}
	
	public boolean intersects(Shape shape) {
		return collider.intersects(shape);
	}
	
	public boolean intersectsWall(World world) {
		return intersectsWall(world.getLevel());
	}
	
	public boolean intersectsWall(Level level) {

		for (int i = (pos.getX() - collider.getMaxRadius()) / Tile.WIDTH - 1; i <= (pos.getX() + collider.getMaxRadius()) / Tile.WIDTH; i++) {
			for (int j = (pos.getY() - collider.getMaxRadius()) / Tile.WIDTH - 1; j <= (pos.getY() + collider.getMaxRadius()) / Tile.WIDTH; j++) {
				
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
		return collider.contains(e.collider);
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
		return this.getClass().getSimpleName() + "{pos: " + pos + ", col: " + collider + "}";
	}
	
	//TODO: make this into a boolean so we can know whether or not to recalculate collision bubbles
	public void update(World world, double d) {
		burnTimer.update(d);
	}
	
	public void renderAround(FocusedWindow g, Graphics gr) {
		
		if (Debug.SHOW_COLLIDERS) {
			collider.render(g, gr);
		}
		
		if (animation == null) {
			return;
		}
				
		Image sprite = animation.getSprite();
		if (sprite == null) {
			return;
		}
		
		Vector rel = pos.copy().sub(g.getFocus().getPos());
		gr.drawImage(sprite, rel.getX() + (g.getBounds().width - sprite.getWidth(null)) / 2, rel.getY() + (g.getBounds().height - sprite.getHeight(null)) / 2, sprite.getWidth(null), sprite.getHeight(null), null);
		
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
		
		return new Entity(pos.copy().add(dir), collider).intersectsWall(world);
		
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
	
	public boolean shouldIgnoreCollisions() {
		return ignoreCollisions;
	}
	
	public void setPos(Vector pos) {
		this.pos = pos.copy();
	}
	
	public Collider getCollider() {
		return collider;
	}

}
