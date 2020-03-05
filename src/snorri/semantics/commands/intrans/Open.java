package snorri.semantics.commands.intrans;

import java.util.HashSet;
import java.util.Set;

import snorri.audio.Audio;
import snorri.audio.ClipWrapper;
import snorri.entities.Entity;
import snorri.entities.Sarcophagus;
import snorri.grammar.PartOfSpeech;
import snorri.semantics.CommandStatus;
import snorri.semantics.Definition;
import snorri.semantics.commands.Command;
import snorri.triggers.TriggerType;
import snorri.world.Vector;
import snorri.world.World;
import snorri.world.Tile;
import snorri.world.TileType;
import snorri.world.UnifiedTileType;

public class Open implements Definition<Command> {
	
	private static final ClipWrapper OPEN_DOOR_SOUND = new ClipWrapper("sound/door.wav");
	
	private static final Set<TileType> DOOR_TYPES = new HashSet<>();
	
	static {
		DOOR_TYPES.add(UnifiedTileType.DOOR);
	}

	public static boolean openDoor(World w, Vector pos) {
		Tile tile = w.getTileLayer().getTileGrid(pos);
		if (!isDoor(tile)) {
			return false;
		}
		
		// Do the door replacement at this tile.
		Tile replacementTile = tile.getType().newReplacementTile(tile);
		if (replacementTile == null) {
			throw new IllegalArgumentException("The replacement type for a door must be defined.");
		}
		w.wrapGridUpdate(pos, new Tile(replacementTile));
		TriggerType.DOOR_OPEN.activate(pos);
		
		// Recurse on neighbors of this tile.
		w.getTileLayer().forEachNeighborOf(pos, neighborPos -> {
			openDoor(w, neighborPos);
		});
		return true;
	}
	
	public static boolean isDoor(Tile tile) {
		if (tile == null) {
			return false;
		}
		return DOOR_TYPES.contains(tile.getType());
	}

	@Override
	public PartOfSpeech getPartOfSpeech() {
		return PartOfSpeech.INTRANS_CMD;
	}

	@Override
	public Command getMeaning() {
		return e -> {
			Entity checker = e.getThirdPerson();
			if (checker == null) {
				return CommandStatus.FAILED;
			}
			
			if (checker instanceof Sarcophagus) {
				e.getWorld().delete(checker);
				return CommandStatus.DONE;
			}

			Vector tilePos = checker.getPos().gridPos();
			if (openDoor(e.getWorld(), tilePos)) {
				Audio.playClip(OPEN_DOOR_SOUND);
				return CommandStatus.DONE;
			} else {
				return CommandStatus.FAILED;
			}
		};
	}

	@Override
	public String getEnglish() {
		return "open";
	}

	@Override
	public String getDocumentation() {
		return "Open a door at the cast position.";
	}

}
