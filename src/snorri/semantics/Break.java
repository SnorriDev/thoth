package snorri.semantics;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import snorri.entities.Entity;
import snorri.events.SpellEvent;
import snorri.parser.Node;
import snorri.triggers.Trigger.TriggerType;
import snorri.world.ForegroundElement;
import snorri.world.Level;
import snorri.world.Tile;
import snorri.world.Vector;
import snorri.world.World;

public class Break extends TransVerbDef {
	
	private static final List<Vector> TRIPWIRE_CONNECTIONS = new ArrayList<>();
	private static final HashSet<ForegroundElement> TRIPWIRES = new HashSet<>();
	
	static {
		
		Vector[] unitVectors = new Vector[] {new Vector(0, 1), new Vector(1, 0)};
		for (Vector unitVector : unitVectors) {
			for (int x = -1; x <= 1; x++) {
				TRIPWIRE_CONNECTIONS.add(unitVector.copy().multiply_(x));
			}
		}
		
		TRIPWIRES.add(ForegroundElement.TRIPWIRE);
		TRIPWIRES.add(ForegroundElement.TRIPWIRE_END);
		
	}
	
	/**
	 * Only use this interface with Entities
	 * @author lambdaviking
	 */
	public interface Smashable {
		
		default void smash(World world, double deltaTime) {
			world.delete((Entity) this);
		}
		
	}
	
	public Break() {
		super();
	}

	@Override
	public boolean exec(Node<Object> object, SpellEvent e) {
		
		Object obj = object.getMeaning(e);
		
		if (!(obj instanceof Entity)) {
			return false;
		}
		
		Entity target = (Entity) obj;
		
		if (target instanceof Smashable) {
			((Smashable) target).smash(e.getWorld(), e.getDeltaTime());
			return true;
		}
		
		//TODO make this open doors that are locked
		Vector tilePos = ((Entity) obj).getPos().copy().gridPos_();
		return Break.cutTripwire(e.getWorld(),  tilePos) ||
				Open.openDoor(e.getWorld(), tilePos);
		
	}

	@Override
	public boolean eval(Object subj, Object obj, SpellEvent e) {
		return (Entity) obj instanceof Smashable;
	}

	@Override
	public String toString() {
		return "break";
	}
	
	/**
	 * Recursively cut the tripwire at this grid position.
	 * @param v The grid position at which to cut.
	 */
	public static boolean cutTripwire(World world, Vector v) {
		Level foreground = world.getLevel(ForegroundElement.class);
		if (!isTripwire(foreground.getTileGrid(v))) {
			return false;
		}
		world.wrapGridUpdate(v, new Tile(ForegroundElement.NONE));
		TriggerType.TRIP.activate(v);
		for (Vector trans : TRIPWIRE_CONNECTIONS) {
			cutTripwire(world, v.copy().add_(trans));
		}
		return true;
	}

	public static boolean isTripwire(Tile tileGrid) {
		return TRIPWIRES.contains(tileGrid.getType());
	}

}
