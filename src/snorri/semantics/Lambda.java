package snorri.semantics;

import snorri.parser.Node;

/**
 * Represents Lambda expressions. By convention, 
 * @author snorri
 *
 * @param <A>
 * @param <B>
 */
public abstract class Lambda<A, B> {

	protected final Class<A> classA;
	
	public Lambda(Class<A> classA) {
		this.classA = classA;
	}
	
	public B eval(A arg) {
		return null;
	}
	
	public B exec(Node<A> arg) {
		return null;
	}
	
	public boolean fitsArg(Object arg) {
		return classA.isInstance(arg);
	}
	
}
