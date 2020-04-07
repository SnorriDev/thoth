package snorri.semantics.commands.trans;

import snorri.entities.Entity;
import snorri.entities.SpawnableRegistry;
import snorri.entities.Spike;
import snorri.grammar.PartOfSpeech;
import snorri.semantics.ClassWrapper;
import snorri.semantics.CommandStatus;
import snorri.semantics.Definition;
import snorri.semantics.Lambda;
import snorri.semantics.commands.Command;
import snorri.semantics.nouns.Noun;
import snorri.world.Tile;
import snorri.world.Vector;

public class CreateObject implements Definition<Lambda<Noun, Command>> {

	@Override
	public PartOfSpeech getPartOfSpeech() {
		return PartOfSpeech.TRANS_CMD;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Lambda<Noun, Command> getMeaning() {
		return noun -> {
			return e -> {
				Object obj = noun.apply(e);

				if (obj instanceof Tile) {
					
					Tile tile = e.getWorld().getTileLayer().getTile(e.getLocative());
					if (tile == null || !tile.getType().isChangable()) {
						return CommandStatus.FAILED;
					}
					
					//check if there is an entity in the way
					if (((Tile) obj).isOccupied() && !tile.isOccupied()) {
						Vector pos = e.getLocative().copy().gridPos_();
						if (e.getWorld().tileHasEntity(pos)) { //could move this out, but want to allow pathable tiles to be placed
							return CommandStatus.FAILED;
						}
					}
					
					e.getWorld().wrapUpdate(e.getLocative(), (Tile) obj);
					return CommandStatus.DONE;
				}
						
				if (obj instanceof ClassWrapper) {
					Class<? extends Entity> c = (Class<? extends Entity>) ((ClassWrapper) obj).getValue();
					if (!SpawnableRegistry.canSpawn(c)) {
						return CommandStatus.FAILED;
					}
					boolean checkCollisions = c != Spike.class;
					Entity spawned = Entity.spawnNew(e.getWorld(), e.getLocative(), c, checkCollisions);
					if (spawned == null) {
						return CommandStatus.FAILED;
					}
					return CommandStatus.DONE;
				}
				
				return CommandStatus.FAILED;
			};
		};
	}

	@Override
	public String getEnglish() {
		return "build";
	}

	@Override
	public String getDocumentation() {
		return "Construct an object.";
	}

}
