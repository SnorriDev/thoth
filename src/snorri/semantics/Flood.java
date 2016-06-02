package snorri.semantics;

import snorri.events.SpellEvent;
import snorri.nonterminals.Noun;
import snorri.world.Tile;
import snorri.world.Tile.TileType;

public class Flood extends Definition {

	public Flood() {
		super(Noun.class);
	}

	private static final TileType[] LIQUIDS = new TileType[] {TileType.WATER, TileType.LAVA};
	
	@Override
	public Object getMeaning(SpellEvent e) {
		
		if (e.getInstrument() == null) {
			return new Tile(TileType.WATER);
		}
		
		if (e.getInstrument() instanceof TileType && isLiquid((TileType) e.getInstrument())) {
			return new Tile((TileType) e.getInstrument());
		}
		
		return null;
	}
	
	public static boolean isLiquid(TileType type) {
		
		for (int i = 0; i < LIQUIDS.length; i++) {
			if (type == LIQUIDS[i]) {
				return true;
			}
		}
		
		return false;
		
	}

}
