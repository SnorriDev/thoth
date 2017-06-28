package snorri.triggers;

import java.util.Map;

import snorri.dialog.Dialog;
import snorri.dialog.Objective;
import snorri.entities.Drop;
import snorri.entities.Entity;
import snorri.entities.NPC;
import snorri.inventory.Droppable;
import snorri.main.Debug;
import snorri.main.FocusedWindow;
import snorri.main.GamePanel;
import snorri.main.GameWindow;
import snorri.main.Main;
import snorri.semantics.Open;
import snorri.world.MidgroundElement;
import snorri.world.Tile;
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
						Vector globalPos = ((Vector) args.get("pos")).copy().toGlobalPos();
						world.getEntityTree().move(ent, globalPos);
					}
				};
			}
		}),
		
		SHOW_DIALOG(new Action() {
			@Override
			public Runnable build(World world, Map<String, Object> args) {
				return new Runnable() {
					@Override
					public void run() {
						GamePanel window = Main.getWindow();
						if (window instanceof FocusedWindow) {
							((FocusedWindow<?>) window).showDialog((Dialog) args.get("dialog"));
						}
					}
				};
			}
		}),
		
		SET_NPC_DIALOG(new Action() {
			@Override
			public Runnable build(World world, Map<String, Object> args) {
				return new Runnable() {
					@Override
					public void run() {
						NPC npc = (NPC) Trigger.getByTag((String) args.get("npc"));
						Dialog dialog = (Dialog) args.get("dialog");
						npc.setDialog(dialog);
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
							((GameWindow) window).setObjective((Objective) args.get("objective"));
						} else {
							Debug.error("Setting objective in non-GameWindow");
						}
					}
				};
			}
		}),
		
		SPAWN_ENTITY(new Action() {
			@Override
			public Runnable build(World world, Map<String, Object> args) {
				return new Runnable() {
					@Override
					public void run() {
						final Class<? extends Entity> type = Entity.getSpawnableByName((String) args.get("type"));
						if (type == null) {
							Debug.error("tried to spawn null entity type in trigger action");
							return;
						}
						Entity e = Entity.spawnNew(world, (Vector) args.get("pos"), type);
						e.setTag((String) args.get("tag"));
					}
				};
			}
		}),
		
		OPEN_DOOR(new Action() {
			@Override
			public Runnable build(World world, Map<String, Object> args) {
				final Vector pos = (Vector) args.get("door");
				return new Runnable() {
					@Override
					public void run() {
						Open.openDoor(world, pos);
					}
				};
			}
		}),
		
		DROP_TREASURE(new Action() {
			@Override
			public Runnable build(World world, Map<String, Object> args) {
				final Vector pos = ((Vector) args.get("pos")).toGlobalPos();
				final Droppable reward = Droppable.fromString((String) args.get("drop"));
				return new Runnable() {
					@Override
					public void run() {
						world.add(new Drop(pos, reward));
					}
				};
			}
		}),
		
		BREAK_WALL(new Action() {
			@Override
			public Runnable build(World world, Map<String, Object> args) {
				final Vector pos1 = ((Vector) args.get("pos1"));
				final Vector pos2 = ((Vector) args.get("pos2"));
				final Vector unit = pos2.copy().sub(pos1).normalize();
				return new Runnable() {
					@Override
					public void run() {
						for (Vector pos = pos1.copy(); pos.distance(pos1) <= pos2.distance(pos1); pos.add(unit)) {
							world.wrapGridUpdate(pos, new Tile(MidgroundElement.NONE));
						}
					}
				};
			}
		});
		
		//TODO action that prints to notification queue?
		
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
