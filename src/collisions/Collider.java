package collisions;

public abstract class Collider {

	/**
	 * @return whether this collider is intersecting another one
	 */
	public boolean intersects(Collider other) {
		if (other instanceof CircleCollider) {
			return intersects((CircleCollider) other);
		}
		if (other instanceof RectCollider) {
			return intersects((RectCollider) other);
		}
		return false;
	}
	
	/**
	 * @return whether other is contained in this collider
	 */
	public boolean contains(Collider other) {
		if (other instanceof CircleCollider) {
			return contains((CircleCollider) other);
		}
		if (other instanceof RectCollider) {
			return contains((RectCollider) other);
		}
		return false;
	}
	
	protected abstract boolean intersects(CircleCollider other);
	
	protected abstract boolean intersects(RectCollider other);
	
	protected abstract boolean contains(CircleCollider other);
	
	protected abstract boolean contains(RectCollider other);
	
}
