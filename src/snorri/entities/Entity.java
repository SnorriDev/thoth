package snorri.entities;

import java.awt.Graphics;
import java.awt.Shape;
import java.awt.image.BufferedImage;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Level;

import snorri.animations.Animation;
import snorri.collisions.CircleCollider;
import snorri.collisions.Collider;
import snorri.entities.Player.Interactor;
import snorri.events.CollisionEvent;
import snorri.events.CastEvent;
import snorri.main.Debug;
import snorri.main.FocusedWindow;
import snorri.main.GameWindow;
import snorri.main.LevelEditor;
import snorri.main.Util;
import snorri.semantics.Nominal;
import snorri.triggers.Trigger;
import snorri.triggers.Trigger.TriggerType;
import snorri.world.Tile;
import snorri.world.Vector;
import snorri.world.World;

public class Entity implements Nominal, Serializable, Comparable<Entity>, Cloneable {

	private static final long serialVersionUID = 1L;
	
	/** a list of entities which can be spawned using the word <code>CreateObject</code> */
	public static final List<Class<? extends Entity>> SPAWNABLE;
	/** a list of entities which can be spawned in the level editor */
	public static final List<Class<? extends Entity>> EDIT_SPAWNABLE;
	
	static {
		SPAWNABLE = new ArrayList<>();
		SPAWNABLE.add(Urn.class);
		SPAWNABLE.add(Spike.class);
		SPAWNABLE.add(Vortex.class);
		SPAWNABLE.add(Bomb.class);
		
		EDIT_SPAWNABLE = new ArrayList<>(SPAWNABLE);
		EDIT_SPAWNABLE.add(Desk.class);
		EDIT_SPAWNABLE.add(Drop.class);
		EDIT_SPAWNABLE.add(Mummy.class);
		EDIT_SPAWNABLE.add(Explosion.class);
		EDIT_SPAWNABLE.add(Flower.class);
		EDIT_SPAWNABLE.add(Player.class);
		EDIT_SPAWNABLE.add(Unit.class);
		EDIT_SPAWNABLE.add(Sarcophagus.class);
		EDIT_SPAWNABLE.add(Listener.class);
		EDIT_SPAWNABLE.add(Crocodile.class);
		EDIT_SPAWNABLE.add(Cobra.class);
		EDIT_SPAWNABLE.add(Glyph.class);
		EDIT_SPAWNABLE.add(Thoth.class);
		EDIT_SPAWNABLE.add(Fountain.class);
		EDIT_SPAWNABLE.add(NPC.class);
		EDIT_SPAWNABLE.add(Ballista.class);
		EDIT_SPAWNABLE.add(Spawn.class);
		EDIT_SPAWNABLE.add(Dummy.class);
		EDIT_SPAWNABLE.add(Center.class);
		EDIT_SPAWNABLE.add(Statue.class);
		
		// Alphabetize the list for nice view in the editor.
		Collections.sort(EDIT_SPAWNABLE, new Comparator<Class<? extends Entity>>() {
			@Override
			public int compare(Class<? extends Entity> o1, Class<? extends Entity> o2) {
				return o1.getSimpleName().compareTo(o2.getSimpleName());
			}
		});
	}
	
	/** A layer above the player for particle effects */
	protected static final int PARTICLE_LAYER = 15;
	/** The default layer for objects (below the player) */
	protected static final int DEFAULT_LAYER = 4;
	/** The default layer for units */
	protected static final int UNIT_LAYER = 0;
	/** The layer for the player */
	protected static final int PLAYER_LAYER = 5;
	
	protected Collider collider;
	protected Vector pos;
	protected Animation animation;
	protected boolean ignoreCollisions = false, staticObject = false;
	/** used to determine which entities should be rendered over others **/
	protected int z;
	protected String tag;
	protected Vector dir;
		
	private boolean deleted = false;
	private boolean hasCycled = false;

	/**
	 * This method will automatically set the collider focus to the entity
	 */
	public Entity(Vector pos, Collider collider) {
		this.pos = (pos == null) ? null : pos.copy();
		if (collider == null) {
			Debug.logger.warning("Entity " + this + " has null collider.");
		} else {
			this.collider = collider.cloneOnto(this);
		}
		z = DEFAULT_LAYER;
		refreshStats();
	}
	
	public Entity(Entity e) {
		this(e.pos, e.collider);
	}
	
	public Entity(Vector pos, int r) {
		this(pos, new CircleCollider(r));
	}
		
	public Entity(Vector pos) {
		this(pos, 1);
	}
	
	public static Class<? extends Entity> getSpawnableByName(String name) {
		for (Class<? extends Entity> c : Entity.EDIT_SPAWNABLE) {
			if (Util.clean(c.getSimpleName()).equals(name)) {
				return c;
			}
		}
		return null;
	}
	
	public static boolean canSpawn(Class<?> obj) {
		return SPAWNABLE.contains(obj);
	}
	
	public static Entity spawnNew(World world, Vector pos, Class<? extends Entity> c) {
		return spawnNew(world, pos, c, true);
	}
	
	public static Entity spawnNew(World world, Vector pos, Class<? extends Entity> c, boolean checkCollisions) {
		try {
			Entity e = c.getConstructor(Vector.class).newInstance(pos);
			if (e instanceof Despawner) {
				((Despawner) e).setDespawnable(true);
			}
			if (checkCollisions && !e.shouldIgnoreCollisions() && world.getEntityTree().getFirstCollision(e) != null) {
				return null;
			}
			if (!world.add(e)) {
				return null;
			}
			return e;
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException e) {
			e.printStackTrace();
			return null;
		}
	}

	public Vector getPos() {
		return pos;
	}
	
	public Animation getAnimation() {
		return animation;
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
		for (int i = (pos.getX() - collider.getMaxRadius()) / Tile.WIDTH - 1; i <= (pos.getX() + collider.getMaxRadius()) / Tile.WIDTH; i++) {
			for (int j = (pos.getY() - collider.getMaxRadius()) / Tile.WIDTH - 1; j <= (pos.getY() + collider.getMaxRadius()) / Tile.WIDTH; j++) {
				if (!intersects(Tile.getRectangle(i, j))) {
					continue;
				}
				if (!world.isPathable(i, j)) {
					return true;
				}
			}
		}
		return false;	
	}
	
	public boolean contains(Entity e) {
		if (e == null) {
			return false;
		}
		return collider != null && collider.contains(e.collider);
	}
	
	protected void traverse(int depth) {
		String indent = "";
		for (int i = 0; i < depth; i++) {
			indent += "  ";
		}
		Debug.logger.info(indent + toString());
	}
	
	public void traverse() {
		traverse(0);
	}
	
	@Override
	public String toString() {
		String tag = getTag();
		String name = tag == null ? Util.clean(this.getClass().getSimpleName()) : tag;
		return name.equals("entity") ? null : name;
	}
	
	public String toStringDebug() {
		return toString() + "{pos: " + pos + ", col: " + collider + "}";
	}
	
	public void update(World world, double d) {
		if (!hasCycled && (animation == null || animation.hasCycled())) {
			onCycleComplete(world);
			hasCycled = true;
		}
	}
	
	public void renderAround(FocusedWindow<?> g, Graphics gr, double timeDelta) {
		
		if (Debug.collidersRendered() || (animation == null && g instanceof LevelEditor)  || inInteractRange(g)) {
			collider.render(g, gr);
		}
		
		if (animation == null) {
			return;
		}
				
		BufferedImage sprite = animation.getSprite(timeDelta);
		if (sprite == null) {
			return;
		}
		
		Vector rel = pos.copy().sub_(g.getCenterObject().getPos());
		gr.drawImage(sprite, rel.getX() + (g.getBounds().width - sprite.getWidth()) / 2, rel.getY() + (g.getBounds().height - sprite.getHeight()) / 2, sprite.getWidth(null), sprite.getHeight(null), null);
		
	}
	
	private boolean inInteractRange(FocusedWindow<?> g) {
		return this instanceof Interactor && g instanceof GameWindow &&
				((Interactor) this).inRange(((GameWindow) g).getFocus());
	}

	@Override
	public Nominal get(AbstractSemantics attr, CastEvent e) {
		
		if (attr == AbstractSemantics.POSITION) {
			return pos;
		}
		if (attr == AbstractSemantics.TILE) {
			return e.getWorld().getTileLayer().getTile(pos);
		}
		
		return Nominal.super.get(attr, e);
	}
	
	/**
	 * if dir is zero, then this function will always return false
	 * @return whether moving in direction dir would bring entity into wall
	 */
	private boolean wouldIntersectSomething(World world, Vector dir) {				
		return wouldIntersectSomethingAt(world, pos.add(dir));	
	}
	
	public boolean wouldIntersectSomethingAt(World world, Vector pos) {
		Entity newEnt = new Entity(pos, collider);
		return newEnt.intersectsWall(world) || world.getEntityTree().getFirstCollisionOtherThan(newEnt, this) != null;
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
	public boolean moveNicely(World world, Vector dir) {
		if (dir.equals(Vector.ZERO) || wouldIntersectSomething(world, dir)) {
			return false;
		}
		world.getEntityTree().move(this, pos.add(dir));
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

	@Override
	public int compareTo(Entity other) {
		return Integer.compare(z, other.z);
	}
	
	@Override @Deprecated
	public Entity clone() {		
		try {
			Entity copy = (Entity) super.clone();
			copy.pos = copy.pos.copy();
			copy.collider = copy.collider.cloneOnto(this);
			return copy;
		} catch (CloneNotSupportedException e) {
			Debug.log("issue cloning entity " + this);
			e.printStackTrace();
			return null;
		}
	}
	
	public Entity copy() {
		try {
			Entity newEnt = getClass().getConstructor(Vector.class).newInstance(pos.copy());
			newEnt.collider = collider.cloneOnto(newEnt);
			newEnt.animation = new Animation(animation);
			return newEnt;
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | SecurityException | InvocationTargetException | NoSuchMethodException e) {
			Debug.logger.log(Level.SEVERE, "Could not copy Entity.", e);
			return null;
		}
	}

	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
		Trigger.setTag(tag, this);
	}

	public boolean isStaticObject() {
		return false;
	}
	
	/**
	 * Refresh the core attributes of an object that has possibly been saved from old versions to be up to date.
	 * 
	 * Inventory and other dynamic fields must not be modified here. This will actually lead to critical failures.
	 * 
	 * This method is not fully supported; use at your own risk. However, in some cases it can prove to be useful
	 * for making old maps forward-compatible.
	 */
	public void refreshStats() {
	}
	
	public Vector getGridBounds() {
		return new Vector(collider.getRadiusX(), collider.getRadiusY()).multiply_(2).gridPosRounded_();
	}
	
	/**
	 * To prevent infinite loops, this method calls another method, which is the one that implementations should extend.
	 * @return Whether the deletion was successful.
	 */
	public final boolean onDelete(World world) {
		if (!deleted) {
			deleted = true;
			onSafeDelete(world);
			TriggerType.DESTROY.activate(tag);
			return true;
		}
		return false;
	}
	
	protected void onSafeDelete(World world) {
	}

	public void onExplosion(CollisionEvent e) {
	}
	
	public void setDirection(Vector dir) {
		this.dir = dir.copy();
		if (animation != null) {
			setAnimation(animation);
		}
	}
	
	/**
	 * Set the animation of this entity to a copy of the specified animation with
	 * the correct rotation.
	 * @param animation
	 * 	The animation to copy.
	 */
	public void setAnimation(Animation animation) {
		hasCycled = false;
		if (dir == null) {
			this.animation = new Animation(animation);
			return;
		}
		this.animation = new Animation(animation).getRotated(dir);
	}
	
	/**
	 * This event fires after the entity's animation completes a cycle.
	 * Note that this is called by <code>update</code>, so it won't get called in the LevelEditor view.
	 * @param world
	 * The world in which the cycle was completed.
	 */
	protected void onCycleComplete(World world) {
	}
	
	/** Destruct this entity and create an explosion.
	 * 
	 * @param world The world to explode in.
	 * @param damage The damage the explosion should yield.
	 */
	public void explode(World world, double damage) {
		world.delete(this);
		world.add(new Explosion(getPos(), damage));
	}

}
