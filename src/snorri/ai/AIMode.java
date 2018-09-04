package snorri.ai;

import snorri.world.World;

public enum AIMode {
	
	IDLE(IdleAILogic.class),
	TURRET(TurretAILogic.class),
	CHARGE(ChargeAILogic.class);
	
	Class<? extends AILogic> logic;
	
	AIMode(Class<? extends AILogic> logic) {
		this.logic = logic;
	}
	
	interface AILogic {
		
		void update(World world, double deltaTime);
		
	}

}
