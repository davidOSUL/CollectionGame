package attributes;

import gameutils.EnumSetHolder;

/**
 * A set of AttributeCharacteristic enums
 * @author David O'Sullivan
 *
 */
public class AttributeCharacteristicSet extends EnumSetHolder<AttributeCharacteristic> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * Creates a new empty AttributeCharacteristicSet
	 */
	public AttributeCharacteristicSet() {}
	private AttributeCharacteristicSet(final AttributeCharacteristicSet set) {
		super(set);
	}
	/** 
	 * @see gameutils.EnumSetHolder#makeCopy()
	 */
	@Override
	public AttributeCharacteristicSet makeCopy() {
		return new AttributeCharacteristicSet(this);
	}
	/** 
	 * @see gameutils.EnumSetHolder#getEnumClass()
	 */
	@Override
	public Class<AttributeCharacteristic> getEnumClass() {
		return AttributeCharacteristic.class;
	}
	/** 
	 * @see gameutils.EnumSetHolder#containsValueParse(java.lang.String)
	 */
	@Override
	public boolean containsValueParse(final String value) {
		return containsValue(AttributeCharacteristic.valueOf(value));
	}
	/** 
	 * @see gameutils.EnumSetHolder#parseValue(java.lang.String)
	 */
	@Override
	protected AttributeCharacteristic parseValue(final String value) {
		return AttributeCharacteristic.valueOf(value.toUpperCase().trim());
	}

	
	
}
