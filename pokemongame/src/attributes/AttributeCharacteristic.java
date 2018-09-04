package attributes;

/**
 * Different characteristics that Attributes could potentially have
 * 
 * @author David O'Sullivan
 */
public enum AttributeCharacteristic {
	/**
	 * To be used by creatures only
	 */
	CREATUREONLY,
	/**
	 * To be used by items only
	 */
	ITEMONLY,
	/**
	 * Modifies a board statistic
	 */
	STATMOD;
}