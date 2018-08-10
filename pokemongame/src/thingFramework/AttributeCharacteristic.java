package thingFramework;

/**
 * Represents the different types of possible attributes
 * 
 * @author David O'Sullivan
 */
public enum AttributeCharacteristic {
	/**
	 * To be used by pokemon only
	 */
	POKEONLY, //keep
	/**
	 * To be used by items only
	 */
	ITEMONLY, //keep
	/**
	 * Modifies a board statistic
	 */
	STATMOD, //keep
	/**
	 * An intrinsic characterstic of a thing (a pokemon type, or a color for example)
	 */
	CHARACTERISTIC,
	/**
	 * A variable characteristic of a thing(happiness level for example)
	 */
	CHANGINGVAL,
	GOLDMOD,
	POPMOD,
	/**
	 * An attribute that should be displayed in the pokemon's description. Note that this is part of the description
	 * and hence the description attribute itself does not have one, as it is just made up of these guys.
	 */
	DISPLAYTYPE,
	OUTOFTEN,
	/**
	 * An attribute that should have a "+" sign in front of it when positive
	 */
	PLUS_FOR_POSITIVE,
	/**
	 * Only for number attributes, signifies that should have different colors based on it's value.
	 */
	COLOR_BASED_ON_SIGN,
	/**
	 * if the attribute should be displayed with italics
	 */
	ITALICS;
	
	
}
/*private String name;
private static Map<String, AttributeType> mapVals = new HashMap<String, AttributeType>();
private AttributeType(String name) {
	this.name = name;
	mapVals.put(name, this);
}
public static AttributeType getAttributeType(String name) {
	return mapVals.get(name);
}*/
