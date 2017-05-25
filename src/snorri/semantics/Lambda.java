package snorri.semantics;

import java.util.List;

import snorri.parser.Node;

/**
 * Representation of Lambda expressions. 
 * @author snorri
 *
 * @param <A> The input type of the function or procedure
 * @param <B> The output type of the function or procedure
 */
public abstract class Lambda<A, B> {

	public static class Category {
		
		public final Class<?> c;
		public final Category catA, catB;
		
		public Category(Class<?> c) {
			this.c = c;
			this.catA = null;
			this.catB = null;
		}
		
		public Category(Category cat1, Category cat2) {
			this.c = null;
			this.catA = cat1;
			this.catB = cat2;
		}
		
		public Category(Class<?> c1, Class<?> c2) {
			this(new Category(c1), new Category(c2));
		}
		
		public Category(Class<?> c1, Category cat2) {
			this(new Category(c1), cat2);
		}
		
		public Category(Category cat1, Class<?> c2) {
			this(cat1, new Category(c2));
		}
		
		public boolean isLambda() {
			return c == null;
		}
		
		@Override
		public boolean equals(Object other) {
			if (other instanceof Category) {
				
				if (isLambda() && ((Category) other).isLambda()) {
					return catA.equals(((Category) other).catA);
				}
				
				if (!isLambda() && !((Category) other).isLambda()) {
					return c.equals(((Category) other).c);
				}
				
			}
			return false;
		}
		
		public Category apply(Category argCat) {
			if (isLambda() && catA.equals(argCat)) {
				return catB;
			}
			return null;
		}
		
		public boolean fitsArg(Object arg) {
			if (isLambda()) {
				return catA.fitsArg(arg);
			}
			return c.isInstance(arg);
		}

		//TODO this is pretty gross
		public static Category getCategory(Object value) {
			if (value instanceof Lambda) {
				return ((Lambda<?, ?>) value).category;
			}
			return new Category(value.getClass());
		}

		public static Category fromChildren(List<Node<?>> children) {
			
			Category cat1, cat2, cat3, try1;
			switch(children.size()) {
			case 1:
				return children.get(0).getCategory();
			case 2:
				cat1 = children.get(0).getCategory();
				cat2 = children.get(1).getCategory();
				try1 = cat1.apply(cat2);
				return (try1 != null) ? try1 : cat2.apply(cat1);
			case 3:
				cat1 = children.get(0).getCategory();
				cat2 = children.get(1).getCategory();
				cat3 = children.get(2).getCategory();
				try1 = cat1.apply(cat3).apply(cat2); //verb ordering
				return (try1 != null) ? try1 : cat2.apply(cat1).apply(cat3);
			}
			
			return null;
			
		}
		
	}
	
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
