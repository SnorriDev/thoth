package snorri.semantics;

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
		if (catA.equals(arg)) {
			return catB;
		}
		return null;
	}
	
	public boolean fitsArg(Object arg) {
		return catA.equals(arg);
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
		
		if ((result1 = Category.combine(type1, type2)) != null) {
			if ((result2 = Category.combine(result1, type3)) != null) {
				return result2;
			}
		}
		
		if ((result1 = Category.combine(type2, type1)) != null) {
			if ((result2 = Category.combine(result1,  type3)) != null) {
				return result2;
			}
		}
		
		return null;
		
	}
	
}