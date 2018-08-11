package attribues;

import java.io.Serializable;

import thingFramework.AttributeCharacteristicSet;
/*
 * Load attributes from file:
 * gph IntegerType extraDescription:"", etc.
 * Have AttributeFactory Class, which has generateAttribute <T> Method
 * AttributeFactory can have List<Attribute<Integer>>, etc.
 */
public abstract class Attribute<T> implements Serializable {
	private T value;
	private T defaultValue;
	private AttributeCharacteristicSet atttributeCharacteristicSet;
	private AttributeFactory parseType;
	/**
	 * An extra string that can be added on at the end of this toString's method
	 */
	private final String extraDescription = "";
	/**
	 * The value at which if the input value to generateAttribute has this value, the attribute class reccomends you ignore the value
	 * (Will not stop you from setting it however) (e.g. -1 for an attribute that should always be >=0)
	 */
	private  T objectToIgnoreValueAt = null;
	Attribute() {}
	protected Attribute(final Attribute<T> at) {
		setValue(at.value);
		setDefaultValue(at.defaultValue);
		setAttributeTypeSet(at.atttributeCharacteristicSet.makeCopy());
	}
	public void setValue(final T value) {
		this.value = value;
	}
	public T getValue() {
		return value;
	}
	void setDefaultValue(final T defaultValue) {
		this.defaultValue = defaultValue;
	}

	void setAttributeTypeSet(final AttributeCharacteristicSet atTypes) {
		this.atttributeCharacteristicSet = atTypes;
	}
	void setIgnoreValue(final T ignoreValue) {
		this.objectToIgnoreValueAt = ignoreValue;
	}
	public boolean valEquals(final T value) {
		return value.equals(this.value);
	}
	public void setParseType(final AttributeFactory parseType) {
		this.parseType = parseType;
	}
	public boolean valEqualsParse(final String value) {
		return AttributeValueParser.getInstance().<T>parseValue(value, parseType).equals(this.value);
	}

}
//private enum ValidAttributes {
//
//
//	/**
//	 * Nothing to do with actual attributes, just a general overview of this Thing
//	 */
//	FLAVOR_DESCRIPTION,
//	/**
//	 * Increase in Gold Per Hour
//	 */
//	GPH,
//	/**
//	 * Increase in Gold Per Minute
//	 */
//	 GPM,
//	/**
//	 *Increase in Popularity 
//	 */
//POPULARITY_BOOST,
//	/**
//	 * Electric, etc.
//	 */
//	TYPES,
//	/**
//	 * Current Happiness of a pokemon (/10)
//	 */
//	 HAPPINESS,
//	/**
//	 * Current Level of a pokemon
//	 */
//	 LEVEL,
//	/**
//	 * The rarity of a pokemon, on scale of 1-10, derived from catchrate. 
//	 * higher is more rare. This version of rarity is used for display purposes only
//	 */
//	 RARITY_OUT_OF_10,
//	/**
//	 * Whether or not this pokemon is legendary
//	 */
//	 IS_LEGENDARY,
//	/**
//	 * A verbal description of the events asssociated with this thing
//	 */
//	 EVENT_DESCRIPTION,
//	/**
//	 * Any other extra description to put after every other displaytype
//	 */
//	 ADDITIONAL_DESCRIPTION,
//	/**
//	 * If this thing only exists for a certain amount of time, how much time it exists for
//	 */
//	 TIME_LEFT,
//	/**
//	 * The catch rate of a pokemon on a scale from 3-255
//	 */
//	 CATCH_RATE,
//	/**
//	 * The rarity of a pokemon, on scale of 1-99, derived from catchrate. 
//	 * higher is more rare
//	 */
//	 RARITY,
//	/**
//	 * The description of an item. Note it doesn't have an orderdisplayvalue because the description is just a combination of all those elements that do
//	 * (and some other text potentially)
//	 */
//	 DESCRIPTION,
//	/**
//	 * The experience group (fast, slow, erratic, etc.) to which this pokemon belongs to.
//	 */
//	 EXPERIENCE_GROUP,
//	/**
//	 * Whether or not this pokemon has both A. an evolution and B. evolves via levels
//	 */
//	 HAS_EVOLUTION,
//	/**
//	 * the next evolution for this pokemon
//	 */
//	 NEXT_EVOLUTIONS,
//	/**
//	 * the level this pokemon evolves at
//	 */
//	 LEVEL_OF_EVOLUTION,
//	/**
//	 * Whether or not this thing can be removed from the board
//	 */
//	 REMOVABLE;
//	private ParseType type;
//	
//}