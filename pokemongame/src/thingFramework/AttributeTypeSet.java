package thingFramework;

import java.io.Serializable;
import java.util.Arrays;
import java.util.EnumSet;

/**
 * A set of attribute types
 * @author David O'Sullivan
 *
 */
public class AttributeTypeSet implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private EnumSet<AttributeType> typeMap;
	public AttributeTypeSet(final AttributeType...attributeTypes) {
		if (attributeTypes.length == 0)
			typeMap = EnumSet.noneOf(AttributeType.class);
		else
			typeMap = EnumSet.copyOf(Arrays.asList(attributeTypes));
	}
	private AttributeTypeSet(final EnumSet<AttributeType> typeMap) {
		this.typeMap = typeMap;
	}
	public boolean containsAttributeType(final AttributeType at) {
		return typeMap.contains(at);
	}
	public boolean addAttributeType(final AttributeType at) {
		return typeMap.add(at);
	}
	public AttributeTypeSet makeCopy() {
		return new AttributeTypeSet(EnumSet.copyOf(typeMap));
	}

	
	
}
