package snorri.triggers;

import java.util.Map;

import snorri.entities.Entity;
import snorri.main.GamePanel;
import snorri.main.GameWindow;
import snorri.main.Main;
import snorri.world.Vector;
import snorri.world.World;

public abstract class Action {

	/**
	 * Functional interface for trigger actions
	 */
		
	private enum Actions {
		
		/**
		 * Enumerates the various actions that exist
		 */
		
		BROADCAST(new Action() {
			@Override
			public Runnable build(World world, Map<String, Object> args) {
				return new Runnable() {
					@Override
					public void run() {
						Trigger.TriggerType.BROADCAST.activate(args.get("msg"));
					}
				};
			}
		}),
		
		TELEPORT(new Action() {
			@Override
			public Runnable build(World world, Map<String, Object> args) {
				return new Runnable() {
					@Override
					public void run() {
						Entity ent = Trigger.getByTag((String) args.get("unit"));
						world.getEntityTree().move(ent, (Vector) args.get("pos"));
					}
				};
			}
		}),
		
		DIALOG(new Action() {
			@Override
			public Runnable build(World world, Map<String, Object> args) {
				return new Runnable() {
					@Override
					public void run() {
						//show msg on screen
					}
				};
			}
		}),
		
		SET_OBJECTIVE(new Action() {
			@Override
			public Runnable build(World world, Map<String, Object> args) {
				return new Runnable() {
					@Override
					public void run() {
						GamePanel window = Main.getWindow();
						if (window instanceof GameWindow) {
							((GameWindow) window).setObjective((String) args.get("msg"));
						}
					}
				};
			}
		}),
		
		SPAWN_ENTITY(new Action() {
			@Override
			public Runnable build(World world, Map<String, Object> args) {
				final Class<? extends Entity> type = Entity.getSpawnableByName((String) args.get("type"));
				return new Runnable() {
					@Override
					public void run() {
						if (type == null) {
							Main.error("tried to spawn null entity type in trigger action");
							return;
						}
						Entity e = Entity.spawnNew(world, (Vector) args.get("pos"), type);
						e.setTag((String) args.get("tag"));
					}
				};
			}
		});
		
		private final Action action;
		
		Actions(Action action) {
			this.action = action;
		}
		
		public Runnable build(World world, Map<String, Object> args) {
			return action.build(world, args);
		}
		
	}
	
	public static Runnable getRunnable(String key, World world, Map<String, Object> args) {
		Actions a = Actions.valueOf(key);
		return (a == null) ? null : a.build(world, args);
	}
	
	public abstract Runnable build(World world, Map<String, Object> args);
	
}
