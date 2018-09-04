package attributes;

import java.io.Serializable;

import interfaces.SerializableFunction;
/**
 * @author DOSullivan
 * A parameterized value used by things to store different characteristics. 
 * Essentially name value pair with some other pieces of logic/information.
 * @param <T> the type of the value stored by the attribute
 */
public class Attribute<T> implements Serializable, DisplayMethods {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private T value;
	private  T defaultValue;
	private AttributeCharacteristicSet atttributeCharacteristicSet;
	private transient ParseType<T> parseType;
	private SerializableFunction<T, Boolean> isPositive;
	private String name;
	/**
	 * An extra string that can be added on at the end of this toString's method
	 */
	private String extraDescription = "";
	/**
	 * Creates a new attribute, and sets the parse type of the attribute to the passed in parse type
	 * @param parseType the ParseType<T> corresponding the the type T of this attribute
	 */
	Attribute(final ParseType<T> parseType) {this.parseType = parseType;}
	/**
	 * Creates a new attribute by copying over from another older attribute
	 * @param oldAttribute the old attribute to copy from
	 */
	protected Attribute(final Attribute<T> oldAttribute) {
		this(oldAttribute.parseType);
		setValue(oldAttribute.value);
		setDefaultValue(oldAttribute.defaultValue);
		setAttributeTypeSet(oldAttribute.atttributeCharacteristicSet.makeCopy());
		setIsPositiveFunction(oldAttribute.isPositive);
		setName(oldAttribute.name);
		setExtraDescription(oldAttribute.getExtraDescription());
	}
	/**
	 * Set the value associated with this attribute
	 * @param value the value to set
	 */
	public void setValue(final T value) {
		this.value = value;
	}
	/**
	 * Set the value associated with this attribute by parsing a string
	 * @param value the string representation of the value to set
	 */
	public void setValueParse(final String value) {
		this.value = AttributeValueParser.getInstance().parseValue(value, parseType);
	}
	/**
	 * get the value associated with this attribute
	 * @return the value associated with this attribute
	 */
	public T getValue() {
		return value;
	}
	/**
	 * set the default value of this attribute. This is the value that the attribute want to default to
	 * if its value has not yet been set
	 * @param defaultValue the default Value
	 */
	void setDefaultValue(final T defaultValue) {
		this.defaultValue = defaultValue;
	}

	/**
	 * Set the attribute type set for this attribute. These represent pieces of meta-data about the attribute used 
	 * by other objects
	 * @param atTypes
	 */
	void setAttributeTypeSet(final AttributeCharacteristicSet atTypes) {
		this.atttributeCharacteristicSet = atTypes;
	}
	
	/**
	 * makes a copy of the attribute
	 * @return the new attribute
	 */
	Attribute<T> makeCopy() {
		return new Attribute<T>(this);
	}
	/**
	 * get the name of this attribute
	 * @return the attribute's name
	 */
	public String getName() {
		return name;
	}
	/**
	 * Set the name of this attribute
	 * @param name the new name for the attribute
	 */
	void setName(final String name) {
		this.name = name;
	}
	/** 
	 * @see java.lang.Object#toString()
	 * @return this attribute as a string
	 */
	@Override
	public String toString() {
		return "Attribute Of Parse Type: " + parseType + ". [value: " + value + ", defaultvalue: " + defaultValue + ", attributeCharacteristics: " + atttributeCharacteristicSet + " extra description: " + extraDescription + "]";
	}
	/**
	 * if this attribute has a defined way of determining if it is positive, will return true if it is positive.
	 * If it is not positive, or there is no way to determine if it is positive, will return false
	 * @return whether or not the attribute is positive
	 */
	public boolean isPositive() {
		if (isPositive == null)
			return false;
		return isPositive.apply(getValue());
	}
	
	/**
	 * Checks if this attribute contains the specified AttributeCharacteristic
	 * @param characteristic the AttributeCharacteristic to check
	 * @return true if it contains the AttributeCharacteristic
	 */
	public boolean hasCharacteristic(final AttributeCharacteristic characteristic) {
		return atttributeCharacteristicSet.containsValue(characteristic);
	}
	/**
	 * set the function that determines whether or not this attribute's value is positive
	 * @param isPositive function that takes in the present attribute's value and return true if it is positive, false otherwise
	 */
	void setIsPositiveFunction(final SerializableFunction<T, Boolean> isPositive) {
		this.isPositive = isPositive;
	}
	/**
	 * Returns the extraDescription associated with this attribute
	 * @return this attribute's extra description
	 */
	public String getExtraDescription() {
		return extraDescription;
	}
	/**
	 * Sets the extra description of this attribute. 
	 * @param extraDescription this attribute's extra description
	 */
	public void setExtraDescription(final String extraDescription) {
		this.extraDescription = extraDescription;
	}
	/**
	 * Returns true if this attributes value equals the parsed input
	 * @param value the String representation of the value to compare
	 * @return true if this.value == parse(value)
	 */
	boolean valEqualsParse(final String value) {
		return AttributeValueParser.getInstance().parseValue(value, parseType).equals(getValue());
	}
	/**
	 * Sets the value of the attribute to its default value
	 */
	public void setValueToDefault() {
		setValue(defaultValue);
	}
	/**
	 * set the parse type of this attribute. As ParseType<T> is effectively an enum, this method should never be called more than 
	 * once for a given attribute.
	 * @param parseType the parse type to set
	 * @throws UnsupportedOperationException if the parseType associated with this Attribute is not null
	 */
	void setParseType(final ParseType<T> parseType) {
		if (this.parseType != null)
			throw new UnsupportedOperationException("Attribute already has an associated Parse Type: " + parseType);
		else
			this.parseType = parseType;
	}
}