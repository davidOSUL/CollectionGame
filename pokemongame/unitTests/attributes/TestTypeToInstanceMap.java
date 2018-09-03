package attributes;

import java.util.HashMap;
import java.util.Map;

public class TestTypeToInstanceMap {
	Map<ParseType<?>, Map<String, Attribute<?>>> map = new HashMap<ParseType<?>, Map<String, Attribute<?>>>();

	public static void main(final String...strings) {
		final Attribute<Integer> intAttr = new Attribute<Integer>(ParseType.INTEGER);
		intAttr.setValue(25);
		final Attribute<Double> doubAttr = new Attribute<Double>(ParseType.DOUBLE);
		doubAttr.setValue(123.123);
		final Attribute<String> stringAttr = new Attribute<String>(ParseType.STRING);
		stringAttr.setValue("hello");
	}
	<T> void addAttribute(final ParseType<T> parseType, final Attribute<T> attribute) {
		
	}
	<T> Attribute<T> getAttribute(final ParseType<T> parseType, final String name) {
		
	}
}
