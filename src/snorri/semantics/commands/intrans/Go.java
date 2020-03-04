package snorri.semantics.commands.intrans;

import snorri.grammar.PartOfSpeech;
import snorri.semantics.CommandStatus;
import snorri.semantics.Definition;
import snorri.semantics.commands.Command;
import snorri.world.Vector;
import snorri.world.World;

public class Go implements Definition<Command> {
	
	/**This verb can be used to guide projectiles.*/
	
	private static final double SPEED = 700;
	private static final double DELETE_MARGIN = 5;
	
	public interface Movable {
		
		/**
		 * translates (moves) the movable object through the world along the delta vector
		 * @param world word to move object through
		 * @param delta change in position (velocity*time)
		 */
		public void translate(World world, Vector delta);
		
		/**
		 * @return whether the object should be falling
		 */
		public boolean isFalling();
		
		/**
		 * translates (moves) the movable object through the world along the dir vector
		 * @param world word to move object through
		 * @param dir change in position (velocity)
		 * @param deltaTime change in time since last frame
		 */
		default void translate(World world, Vector dir, double deltaTime) {
			translate(world, dir.multiply(deltaTime));
		}
		
		/**
		 * translates (moves) the movable object through the world along the dir vector at a normalized speed
		 * @param world word to move object through
		 * @param dir change in position (velocity vector)
		 * @param deltaTime change in time since last frame
		 */
		default void translateNormalized(World world, Vector dir, double deltaTime) {
			translate(world, dir.normalize(), deltaTime);
		}
		
	}

	@Override
	public PartOfSpeech getPartOfSpeech() {
		return PartOfSpeech.INTRANS_CMD;
	}

	@Override
	public Command getMeaning() {
		return e -> {
			if (e.getSecondPerson() instanceof Movable && e.getDestination() != null) {
				Vector trans = e.getDestination().copy().sub_(e.getSecondPerson().getPos());
				if (trans.magnitude() < DELETE_MARGIN) {
					e.getWorld().delete(e.getSecondPerson());
					return CommandStatus.FAILED;
				}
				((Movable) e.getSecondPerson()).translateNormalized(e.getWorld(), trans, SPEED * e.getDeltaTime());
				return CommandStatus.DONE;
			}
			return CommandStatus.FAILED;
		};
	}

	@Override
	public String getEnglish() {
		return "go";
	}

	@Override
	public String getDocumentation() {
		return "Guide a projectile to the target location.";
	}
	
}
