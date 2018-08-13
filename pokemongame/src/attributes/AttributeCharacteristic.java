package attributes;

/**
 * Represents the different types of possible attributes
 * 
 * @author David O'Sullivan
 */
public enum AttributeCharacteristic {
	/**
	 * To be used by pokemon only
	 */
	POKEONLY,
	/**
	 * To be used by items only
	 */
	ITEMONLY,
	/**
	 * Modifies a board statistic
	 */
	STATMOD;
}