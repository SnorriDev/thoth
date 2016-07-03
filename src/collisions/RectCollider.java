package collisions;

public class RectCollider extends Collider {

	@Override
	protected boolean intersects(CircleCollider other) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected boolean intersects(RectCollider other) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected boolean contains(CircleCollider other) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected boolean contains(RectCollider other) {
		// TODO Auto-generated method stub
		return false;
	}

}
