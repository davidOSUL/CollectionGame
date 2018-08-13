package attributes;

import gameutils.EnumSetHolder;

/**
 * A set of attribute types
 * @author David O'Sullivan
 *
 */
public class AttributeCharacteristicSet extends EnumSetHolder<AttributeCharacteristic> {
	public AttributeCharacteristicSet() {}
	private AttributeCharacteristicSet(final AttributeCharacteristicSet set) {
		super(set);
	}
	@Override
	public AttributeCharacteristicSet makeCopy() {
		return new AttributeCharacteristicSet(this);
	}
	@Override
	public Class<AttributeCharacteristic> getEnumClass() {
		return AttributeCharacteristic.class;
	}

	
	
}
