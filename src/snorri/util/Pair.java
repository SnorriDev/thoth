package snorri.util;

public class Pair<A, B> {
	
	private final A first;
	private final B second;
	private int iHashCode;

	public Pair(A first, B second) {
		this.first = first;
		this.second = second;
		iHashCode = 17;
		iHashCode = 31 * iHashCode + first.hashCode();
		iHashCode = 31 * iHashCode + second.hashCode();
	}
	
	public A getFirst() {
		return first;
	}
	
	public B getSecond() {
		return second;
	}
	
	@Override
	public String toString() {
		return "(" + this.first + "," + this.second + ")";
	}
	
	@Override
	public int hashCode() {
		return iHashCode;
	}
	
	@Override
	public boolean equals(Object other) {
		if (other instanceof Pair) {
			Pair<?, ?> otherPair = (Pair<?, ?>) other;
			return first.equals(otherPair.first) && second.equals(otherPair.second); 
		}
		return false;
	}

}
