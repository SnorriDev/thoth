package snorri.triggers;

import java.util.Map;

import snorri.ai.AIMode;
import snorri.dialog.Dialog;
import snorri.entities.AIUnit;
import snorri.entities.Ballista;
import snorri.entities.Drop;
import snorri.entities.Entity;
import snorri.entities.NPC;
import snorri.entities.SpawnableRegistry;
import snorri.inventory.Droppable;
import snorri.main.Debug;
import snorri.main.Main;
import snorri.semantics.commands.intrans.Open;
import snorri.windows.CutScene;
import snorri.windows.FocusedWindow;
import snorri.windows.GamePanel;
import snorri.windows.GameWindow;
import snorri.world.Tile;
import snorri.world.UnifiedTileType;
import snorri.world.Vector;
import snorri.world.World;

public abstract class Action {

	/**
	 * Functional interface for trigger actions
	 */

	public enum Actions {

		/**
		 * Enumerates the various actions that exist
		 */

		BROADCAST(new Action() {
			@Override
			public Runnable build(World world, Map<String, Object> args) {
				return new Runnable() {
					@Override
					public void run() {
						TriggerType.BROADCAST.activate(args.get("msg"));
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
						Vector globalPos = ((Vector) args.get("pos")).copy().globalPos_();
						world.getEntityTree().move(ent, globalPos);
					}
				};
			}
		}),

		SET_DIALOG(new Action() {
			@Override
			public Runnable build(World world, Map<String, Object> args) {
				return new Runnable() {
					@Override
					public void run() {
						GamePanel window = Main.getWindow();
						if (window instanceof FocusedWindow) {
							((FocusedWindow<?>) window).setDialog((Dialog) args.get("dialog"));
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

		SPAWN_ENTITY(new Action() {
			@Override
			public Runnable build(World world, Map<String, Object> args) {
				return new Runnable() {
					@Override
					public void run() {
						final Class<? extends Entity> type = SpawnableRegistry.getEditSpawnableByName((String) args.get("type"));
						if (type == null) {
							Debug.logger.warning("Tried to spawn null entity type in trigger action.");
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
				final Vector pos = ((Vector) args.get("pos")).globalPos_();
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
				final Vector unit = pos2.sub(pos1).normalize_();
				return new Runnable() {
					@Override
					public void run() {
						for (Vector pos = pos1.copy(); pos.distance(pos1) <= pos2.distance(pos1); pos.add_(unit)) {
							world.wrapGridUpdate(pos, new Tile(UnifiedTileType.EMPTY));
						}
					}
				};
			}
		}),

		DELETE_ENTITY(new Action() {
			@Override
			public Runnable build(World world, Map<String, Object> args) {
				return new Runnable() {
					@Override
					public void run() {
						Entity e = Trigger.getByTag((String) args.get("entity"));
						world.delete(e);
					}
				};
			}
		}),

		FIRE_BALLISTA(new Action() {
			@Override
			public Runnable build(World world, Map<String, Object> args) {
				return new Runnable() {
					@Override
					public void run() {
						Entity e = Trigger.getByTag((String) args.get("ballista"));
						if (e instanceof Ballista) {
							((Ballista) e).shoot(world);
						}
					}
				};
			}
		}),

		ENTER_WORLD(new Action() {
			@Override
			public Runnable build(World world, Map<String, Object> args) {
				return new Runnable() {
					final String path = (String) args.get("path");
					@Override
					public void run() {
						GamePanel window = Main.getWindow();
						if (window instanceof GameWindow) {
							((GameWindow) window).stopBackgroundThread();
							Main.launchGame(path, ((GameWindow) window).getFocus());
						} else {
							throw new RuntimeException("trying to enter other world in non-GameWindow");
						}
					}
				};
			}
		}),
		
		SHOW_CUTSCENE(new Action() {
			@Override
			public Runnable build(World world, Map<String, Object> args) {
				return new Runnable() {
					final String nextWorld = (String) args.get("nextPath");
					@Override
					public void run() {
						GamePanel window = Main.getWindow();
						if (window instanceof GameWindow) {
							((GameWindow) window).stopBackgroundThread();
							Main.setWindow(new CutScene(nextWorld, ((GameWindow) window).getFocus()));
						} else {
							throw new RuntimeException("trying to show cutscene in non-GameWindow");
						}
					}
				};
			}
		}),
		
		SET_AI_MODE(new Action() {
			@Override
			public Runnable build(World world, Map<String, Object> args) {
				return () -> {
					Entity entity = Trigger.getByTag((String) args.get("entity"));
					AIMode mode = AIMode.valueOf((String) args.get("mode"));
					if (entity instanceof AIUnit) {
						AIUnit agent = (AIUnit) entity;
						agent.setMode(mode);
					} else {
						throw new RuntimeException("Attempting to update AI mode on non AIUnit.");
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
