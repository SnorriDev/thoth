package snorri.semantics;

import snorri.parser.Node;

/**
 * Representation of Lambda expressions. 
 * @author snorri
 *
 * @param <A> The input type of the function or procedure
 * @param <B> The output type of the function or procedure
 */
public abstract class Lambda<A, B> {

	protected final Category category;
	
	public Lambda(Category category) {
		this.category = category;
	}
	
	public B eval(A arg) {
		return null;
	}
	
	public B exec(Node<A> arg) {
		return null;
	}
	
	//TODO might want to store classB for this
	@SuppressWarnings("unchecked")
	public <S> S eval(Object term1, Object term2, Class<S> c) {
		Lambda<Object, Object> lambda;
		if (term1 instanceof Lambda && (lambda = (Lambda<Object, Object>) term1).fitsArg(term2)) {
			return (S) lambda.eval(term2);
		}
		if (term2 instanceof Lambda && (lambda = (Lambda<Object, Object>) term2).fitsArg(term1)) {
			return (S) lambda.eval(term1);
		}
		return null;
	}
	
	//TODO do something equivalent with exec
	
	public boolean fitsArg(Object arg) {
		return category.fitsArg(arg);
	}
	
	public Category getCategory() {
		return category;
	}
			
}
