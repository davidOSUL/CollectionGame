package thingFramework;

import java.util.Set;
import java.util.Arrays;
import java.util.HashSet;

/**
 * A set of attribute types
 * @author David O'Sullivan
 *
 */
public class AttributeTypeSet {
	private Set<AttributeType> typeMap = new HashSet<AttributeType>();
	public AttributeTypeSet(AttributeType...attributeTypes) {
		typeMap.addAll(Arrays.asList(attributeTypes));
	}
	public boolean containsAttribute(AttributeType at) {
		return typeMap.contains(at);
	}
	
	
}
