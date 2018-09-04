package thingFramework;

import gameutils.EnumSetHolder;

/**
 * An EnumSetHolder of type CreatureType. Used for the "types" attribute
 * @author David O'Sullivan
 *
 */
public class CreatureTypeSet extends EnumSetHolder<CreatureType>{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * Construct a new, empty CreatureTypeSet
	 */
	public CreatureTypeSet() {};
	/**
	 * Construct a CreatureTypeSet with the provided types in it
	 * @param types the String representations of the enums to initially load into this CreatureTypeSet
	 */
	public CreatureTypeSet(final String...types) {
		for (final String type : types) {
			if(type.isEmpty())
				continue;
			addValue(CreatureType.valueOf(type.trim().toUpperCase()));
		}
	}
	private CreatureTypeSet(final CreatureTypeSet set) {
		super(set);
	}
	/** 
	 * @see gameutils.EnumSetHolder#makeCopy()
	 */
	@Override
	public CreatureTypeSet makeCopy() {
		return new CreatureTypeSet(this);
	}

	/** 
	 * @see gameutils.EnumSetHolder#getEnumClass()
	 */
	@Override
	protected Class<CreatureType> getEnumClass() {
		return CreatureType.class;
	}
	/** 
	 * @see gameutils.EnumSetHolder#parseValue(java.lang.String)
	 */
	@Override
	protected CreatureType parseValue(final String value) {
		return CreatureType.valueOf(value.toUpperCase().trim());
	}



	
}

