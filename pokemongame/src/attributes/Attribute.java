package attributes;

import java.io.Serializable;

import interfaces.SerializableFunction;
public class Attribute<T> implements Serializable {
	private T value;
	private  T defaultValue;
	private AttributeCharacteristicSet atttributeCharacteristicSet;
	private final ParseType<T> parseType;
	private SerializableFunction<T, Boolean> isPositive;
	
	Attribute(final ParseType<T> parseType) {this.parseType = parseType;}
	protected Attribute(final Attribute<T> at) {
		this(at.parseType);
		setValue(at.value);
		setDefaultValue(at.defaultValue);
		setAttributeTypeSet(at.atttributeCharacteristicSet.makeCopy());
		setIsPositiveFunction(at.isPositive);
	}
	public void setValue(final T value) {
		this.value = value;
	}
	public void setValueParse(final String value) {
		this.value = AttributeValueParser.getInstance().parseValue(value, parseType);
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
	
	Attribute<T> makeCopy() {
		return new Attribute<T>(this);
	}
	
	@Override
	public String toString() {
		return "Attribute Of Parse Type: " + parseType + ". [value: " + value + ", defaultvalue: " + defaultValue + ", attributeCharacteristics: " + atttributeCharacteristicSet + "]";
	}
	public boolean isPositive() {
		if (isPositive == null)
			return false;
		return isPositive.apply(getValue());
	}
	public boolean shouldDisplay() {
		return false;
	}
	void setIsPositiveFunction(final SerializableFunction<T, Boolean> isPositive) {
		this.isPositive = isPositive;
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