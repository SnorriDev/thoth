package snorri.triggers;

import snorri.entities.Entity;
import snorri.world.Campaign;
import snorri.world.Vector;
import snorri.world.World;

public class Actions {

	/**
	 * Class which statically builds
	 * runnable actions for triggers.
	 */
	
	public static Runnable broadcast(String msg) {
		return new Runnable() {
			@Override
			public void run() {
				Trigger.TriggerType.BROADCAST.activate(msg);
			}
		};
	}
	
	public static Runnable teleport(Campaign campaign, Entity focus, World world, Vector newPos) {
		return new Runnable() {
			@Override
			public void run() {
				campaign.teleport(focus, world, newPos);
			}
		};
	}
	
	public static Runnable dialog(String msg) {
		return new Runnable() {
			@Override
			public void run() {
				//show msg on screen
			}
		};
	}
	
}
