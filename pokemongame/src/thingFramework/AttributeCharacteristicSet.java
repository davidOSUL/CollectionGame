package thingFramework;

import java.io.Serializable;
import java.util.Arrays;
import java.util.EnumSet;

/**
 * A set of attribute types
 * @author David O'Sullivan
 *
 */
public class AttributeCharacteristicSet implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private EnumSet<AttributeCharacteristic> typeMap;
	public AttributeCharacteristicSet(final AttributeCharacteristic...attributeTypes) {
		if (attributeTypes.length == 0)
			typeMap = EnumSet.noneOf(AttributeCharacteristic.class);
		else
			typeMap = EnumSet.copyOf(Arrays.asList(attributeTypes));
	}
	private AttributeCharacteristicSet(final EnumSet<AttributeCharacteristic> typeMap) {
		this.typeMap = typeMap;
	}
	public boolean containsAttributeType(final AttributeCharacteristic at) {
		return typeMap.contains(at);
	}
	public boolean addAttributeType(final AttributeCharacteristic at) {
		return typeMap.add(at);
	}
	public AttributeCharacteristicSet makeCopy() {
		return new AttributeCharacteristicSet(EnumSet.copyOf(typeMap));
	}

	
	
}
