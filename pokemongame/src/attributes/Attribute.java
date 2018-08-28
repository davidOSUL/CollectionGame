package attributes;

import java.io.Serializable;

import interfaces.SerializableFunction;
public class Attribute<T> implements Serializable, DisplayMethods {
	private T value;
	private  T defaultValue;
	private AttributeCharacteristicSet atttributeCharacteristicSet;
	private final ParseType<T> parseType;
	private SerializableFunction<T, Boolean> isPositive;
	private String name;
	/**
	 * An extra string that can be added on at the end of this toString's method
	 */
	private String extraDescription = "";
	Attribute(final ParseType<T> parseType) {this.parseType = parseType;}
	protected Attribute(final Attribute<T> at) {
		this(at.parseType);
		setValue(at.value);
		setDefaultValue(at.defaultValue);
		setAttributeTypeSet(at.atttributeCharacteristicSet.makeCopy());
		setIsPositiveFunction(at.isPositive);
		setName(at.name);
		setExtraDescription(at.getExtraDescription());
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
	public String getName() {
		return name;
	}
	void setName(final String name) {
		this.name = name;
	}
	@Override
	public String toString() {
		return "Attribute Of Parse Type: " + parseType + ". [value: " + value + ", defaultvalue: " + defaultValue + ", attributeCharacteristics: " + atttributeCharacteristicSet + " extra description: " + extraDescription + "]";
	}
	public boolean isPositive() {
		if (isPositive == null)
			return false;
		return isPositive.apply(getValue());
	}
	
	public boolean hasCharacteristic(final AttributeCharacteristic characteristic) {
		return atttributeCharacteristicSet.containsValue(characteristic);
	}
	void setIsPositiveFunction(final SerializableFunction<T, Boolean> isPositive) {
		this.isPositive = isPositive;
	}
	public String getExtraDescription() {
		return extraDescription;
	}
	public void setExtraDescription(final String extraDescription) {
		this.extraDescription = extraDescription;
	}
	boolean valEqualsParse(final String value) {
		return AttributeValueParser.getInstance().parseValue(value, parseType).equals(getValue());
	}
	/**
	 * Sets the value of the attribute to its default value
	 */
	public void setValueToDefault() {
		setValue(defaultValue);
	}
}