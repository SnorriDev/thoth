package snorri.semantics.predicates.trans;

import snorri.entities.Entity;
import snorri.grammar.PartOfSpeech;
import snorri.semantics.ClassWrapper;
import snorri.semantics.CommandStatus;
import snorri.semantics.Definition;
import snorri.semantics.Lambda;
import snorri.semantics.nouns.Noun;
import snorri.semantics.predicates.Predicate;
import snorri.world.TileLayer;
import snorri.world.Tile;
import snorri.world.TileType;

public class Cross implements Definition<Lambda<Noun, Lambda<Noun, Predicate>>> {

	@Override
	public String toString() {
		return "touch";
	}

	@Override
	public PartOfSpeech getPartOfSpeech() {
		return PartOfSpeech.TRANS_PRED;
	}

	@Override
	public Lambda<Noun, Lambda<Noun, Predicate>> getMeaning() {
		return objNoun -> {
			return subjNoun -> {
				return event -> {
					Object obj = objNoun.apply(event);
					Object subj = subjNoun.apply(event);
					
					//if the subject isn't an entity, then return false
					if (!(subj instanceof Entity)) {
						return CommandStatus.FALSE;
					}
					Entity ent = (Entity) subj;
					
					if (obj instanceof Entity) {
						if (ent.intersects((Entity) obj)) {
							return CommandStatus.TRUE;
						}
						return CommandStatus.FALSE;
					}
					
					if (obj instanceof TileType) {
						TileLayer level = event.getWorld().getTileLayer();
						Tile tile = level.getTile(ent.getPos());
						if (tile != null && obj == tile.getType()) {
							return CommandStatus.TRUE;
						}
						return CommandStatus.FALSE;
						
					}
						
					if (obj instanceof ClassWrapper) {
					
						Class<?> c = ((ClassWrapper) obj).getValue();
						Object other = event.getWorld().getEntityTree().getFirstCollision(ent, true, c);
						if (other == null) {
							return CommandStatus.FALSE;
						}
						return CommandStatus.TRUE;
					
					}
					
					return CommandStatus.FALSE;
				};
			};
		};
	}

	@Override
	public String getEnglish() {
		return "touches";
	}

	@Override
	public String getDocumentation() {
		return "Check whether two objects are touching.";
	}

}
