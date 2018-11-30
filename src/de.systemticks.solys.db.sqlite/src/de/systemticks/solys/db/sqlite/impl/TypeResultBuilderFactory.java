package de.systemticks.solys.db.sqlite.impl;

public class TypeResultBuilderFactory {

	static DoubleResultBuilder doubleBuilder = new DoubleResultBuilder();
	
	public static <T> TypedResultBuilder<T> create(Class<T> clazz) {
		
		if(clazz.getName().equals("java.lang.Double")) {
			return (TypedResultBuilder<T>) doubleBuilder;			
		}
		
		return null;
	}
	
}
