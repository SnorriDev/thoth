package snorri.entities;

import snorri.ai.AIAgent;
import snorri.animations.Animation;
import snorri.inventory.Droppable;

public abstract class AIUnit extends Unit implements AIAgent {
	
	private static final long serialVersionUID = 1L;
	
	private Entity target;
	
	protected AIUnit(Entity target, Animation idle, Animation walking) {
		super(idle, walking);
		setTarget(target);
	}
	
	@Override
	public void setTarget(Entity target) {
		this.target = target;
	}
	
	@Override
	public Entity getTarget() {
		return target;
	}

}
