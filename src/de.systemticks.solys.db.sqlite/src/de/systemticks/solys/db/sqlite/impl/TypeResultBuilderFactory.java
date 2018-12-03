package de.systemticks.solys.db.sqlite.impl;

public class TypeResultBuilderFactory {

	static DoubleResultBuilder doubleBuilder = new DoubleResultBuilder();
	static IntegerResultBuilder integerBuilder = new IntegerResultBuilder();
	
	public static <T> TypedResultBuilder<T> create(Class<T> clazz) {
		
		if(clazz.getName().equals("java.lang.Double")) {
			return (TypedResultBuilder<T>) doubleBuilder;			
		}
		else if(clazz.getName().equals("java.lang.Integer")) {
			return (TypedResultBuilder<T>) integerBuilder;			
		}
		
		return null;
	}
	
}
