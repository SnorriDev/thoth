package snorri.semantics;

import snorri.world.Tile;

public class CreateObject extends VerbDef {

	public CreateObject() {
		super(true);
	}

	@Override
	public boolean exec(Object obj) {
		
		if (obj instanceof Tile) {
			
			Tile tile = e.getWorld().getLevel().getTile(e.getLocative());
			if (tile == null || !tile.getType().isChangable()) {
				return false;
			}
			
			e.getWorld().getLevel().wrapUpdate(e.getLocative(), (Tile) obj);
			return true;
		}
		
		//create code?
		
		return false;
		
	}

	@Override
	public boolean eval(Object subj, Object obj) {
		return false;
	}

	@Override
	public String getShortDesc() {
		return "create object";
	}

}
