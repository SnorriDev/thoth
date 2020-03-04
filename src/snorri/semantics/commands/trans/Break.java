package snorri.semantics.commands.trans;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import snorri.entities.Entity;
import snorri.grammar.PartOfSpeech;
import snorri.semantics.CommandStatus;
import snorri.semantics.Definition;
import snorri.semantics.Lambda;
import snorri.semantics.commands.Command;
import snorri.semantics.commands.intrans.Open;
import snorri.semantics.nouns.Noun;
import snorri.triggers.TriggerType;
import snorri.world.TileType;
import snorri.world.UnifiedTileType;
import snorri.world.TileLayer;
import snorri.world.Tile;
import snorri.world.Vector;
import snorri.world.World;

public class Break implements Definition<Lambda<Noun, Command>> {
	
	private static final List<Vector> TRIPWIRE_CONNECTIONS = new ArrayList<>();
	private static final HashSet<TileType> TRIPWIRES = new HashSet<>();
	
	static {
		
		Vector[] unitVectors = new Vector[] {new Vector(0, 1), new Vector(1, 0)};
		for (Vector unitVector : unitVectors) {
			for (int x = -1; x <= 1; x++) {
				TRIPWIRE_CONNECTIONS.add(unitVector.copy().multiply_(x));
			}
		}
		
		TRIPWIRES.add(UnifiedTileType.TRIPWIRE);
		TRIPWIRES.add(UnifiedTileType.TRIPWIRE_END);
		
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

	@Override
	public Lambda<Noun, Command> getMeaning() {
		return noun -> {
			return e -> {
				Object obj = noun.apply(e);
				if (!(obj instanceof Entity)) {
					return CommandStatus.FAILED;
				}
				
				Entity target = (Entity) obj;
				
				if (target instanceof Smashable) {
					((Smashable) target).smash(e.getWorld(), e.getDeltaTime());
					return CommandStatus.FAILED;
				}
				
				//TODO make this open doors that are locked
				Vector tilePos = ((Entity) obj).getPos().copy().gridPos_();
				if (Break.tryToCutTripWire(e.getWorld(),  tilePos) ||
						Open.openDoor(e.getWorld(), tilePos)) {
					return CommandStatus.DONE;
				}
				return CommandStatus.FAILED;
			};
		};
	}

	@Override
	public PartOfSpeech getPartOfSpeech() {
		return PartOfSpeech.TRANS_CMD;
	}

	@Override
	public String getEnglish() {
		return "break";
	}

	@Override
	public String getDocumentation() {
		return "Break the object under the cursor.";
	}
	
	/**
	 * Recursively cut the tripwire at this grid position.
	 * @param v The grid position at which to cut.
	 */
	public static boolean tryToCutTripWire(World world, Vector v) {
		TileLayer foreground = world.getTileLayer();
		if (!isTripwire(foreground.getTileGrid(v))) {
			return false;
		}
		world.wrapGridUpdate(v, new Tile(UnifiedTileType.EMPTY));
		TriggerType.TRIP.activate(v);
		for (Vector trans : TRIPWIRE_CONNECTIONS) {
			tryToCutTripWire(world, v.copy().add_(trans));
		}
		return true;
	}

	public static boolean isTripwire(Tile tileGrid) {
		return TRIPWIRES.contains(tileGrid.getType());
	}

}
