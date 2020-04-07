package snorri.entities;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import snorri.util.Util;

public class SpawnableRegistry {

	/** A list of entities which can be spawned using the word <code>CreateObject</code>. */
	public static final List<Class<? extends Entity>> SPAWNABLE;
	/** A list of entities which can be spawned in the level editor. */
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
		EDIT_SPAWNABLE.add(Ghost.class);
		
		// Alphabetize the list for nice view in the editor.
		Collections.sort(EDIT_SPAWNABLE, new Comparator<Class<? extends Entity>>() {
			@Override
			public int compare(Class<? extends Entity> o1, Class<? extends Entity> o2) {
				return o1.getSimpleName().compareTo(o2.getSimpleName());
			}
		});
	}
	
	public static Class<? extends Entity> getEditSpawnableByName(String name) {
		for (Class<? extends Entity> c : EDIT_SPAWNABLE) {
			if (Util.clean(c.getSimpleName()).equals(name)) {
				return c;
			}
		}
		return null;
	}
	
	public static boolean canSpawn(Class<?> obj) {
		return SPAWNABLE.contains(obj);
	}

}
