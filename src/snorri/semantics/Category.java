package snorri.semantics;

import java.util.List;

import snorri.parser.Node;

public class Category {
	
	public final Object catA;
	public final Object catB;
	
	public Category(Object cat1, Object cat2) {
		this.catA = cat1;
		this.catB = cat2;
	}
	
	@Override
	public boolean equals(Object other) {
		if (other instanceof Category) {
			return catA.equals(((Category) other).catA);
		}
		return false;
	}
	
	public Object apply(Object arg) {
		if (fitsArg(arg)) {
			return catB;
		}
		return null;
	}
	
	public boolean fitsArg(Object arg) {
		return isHypernym(catA, arg);
	}

	public static Object getCategory(Object value) {
		if (value instanceof Lambda) {
			return ((Lambda<?, ?>) value).category;
		}
		return value.getClass();
	}
	
	public static Object combine(Object type1, Object type2) {
		Object newType;
		if (type1 instanceof Category) {
			newType = ((Category) type1).apply(type2);
			if (newType != null) {
				return newType;
			}
		}
		if (type2 instanceof Category) {
			newType = ((Category) type2).apply(type1);
			if (newType != null) {
				return newType;
			}
		}
		return null;
	}

	public static Object combine(Object type1, Object type2, Object type3) {
				
		Object result1, result2;
		
		//this should capture both possibilities
		if ((result1 = Category.combine(type1, type2)) != null) {
			if ((result2 = Category.combine(result1, type3)) != null) {
				return result2;
			}
		}
		
		return null;
		
	}
	
	//FIXME category computation breaks for Command, Conditional, Statement
	public static Object combine(List<Node<?>> nodes) {
		
		Object arg1, arg2, arg3;
		switch (nodes.size()) {
		case 1:
			return nodes.get(0).getCategory();
		case 2:
			arg1 = nodes.get(0).getCategory();
			arg2 = nodes.get(1).getCategory();
			return Category.combine(arg1, arg2);
		case 3:
			arg1 = nodes.get(0).getCategory();
			arg2 = nodes.get(1).getCategory();
			arg3 = nodes.get(2).getCategory();
			return Category.combine(arg1, arg2, arg3);
		}
		
		return null;
		
	}
	
	public static boolean isHypernym(Object type1, Object type2) {
		
		if (type1 instanceof Category && type2 instanceof Category) {
			return isHypernym(((Category) type1).catA, ((Category) type2).catA) &&
					isHypernym(((Category) type1).catB, ((Category) type2).catB);
		}
		
		if (type1 instanceof Class && type2 instanceof Class) {
			return ((Class<?>) type1).isAssignableFrom((Class<?>) type2);
		}
		
		return false;
		
	}
	
	@Override
	public String toString() {
		return "<" + catA.toString() + ", " + catB.toString() + ">";
	}
	
}