/**
 * 
 */
package gameutils;

import java.io.Serializable;
import java.util.Arrays;
import java.util.EnumSet;

/**
 * A bare-minimum wrapping of an Enum Set. Should be extended whenever a Set of Enums of a certain type is wanted.
 * @author David O'Sullivan
 * @param <T> the type of the enum
 *
 */
public abstract class EnumSetHolder<T extends Enum<T>> implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private EnumSet<T> typeMap;
	/**
	 * Creates a new EnumSetHolder of the correct type and with no elements
	 */
	public EnumSetHolder() {
		typeMap = EnumSet.noneOf(getEnumClass());
	}
	/**
	 * Creates a new EnumSetHolder of the correct type and with the passed in elements
	 * @param enums the enums to initalize this with
	 */
	public EnumSetHolder(final T[] enums) {
		if (enums.length == 0)
			typeMap = EnumSet.noneOf(getEnumClass());
		else
			typeMap = EnumSet.copyOf(Arrays.asList(enums));
	}
	
	/**
	 * Creates a new EnumSetHolder by copying over from an old EnumSetHolder
	 * @param enumSetHolder the old EnumSetHolder to copy from
	 */
	protected EnumSetHolder(final EnumSetHolder<T> enumSetHolder) {
		typeMap = EnumSet.copyOf(enumSetHolder.typeMap);
	}
	/**
	 * Checks if the EnumSetHolder contains the specified value 
	 * @param value the enum to check if contains
	 * @return true if contains the value, false if not
	 */
	public boolean containsValue(final T value) {
		return typeMap.contains(value);
	}
	/**
	 * Checks if the EnumSetHolder contains the value represented by the passed in string
	 * @param value the string representation of the value to check
	 * @return true if contains the value, false if not
	 */
	public boolean containsValueParse(final String value) {
		return containsValue(parseValue(value));
	}
	/**
	 * Parses the value to an enum of the correct type. 
	 * Generally, implementors of this function should just make use of the Enum.valueOf(...) method
	 * @param value the string represention of a value
	 * @return the parsed value
	 * 
	 */
	protected abstract T parseValue(final String value);
	/**
	 * Adds the enum to the backing EnumSet
	 * @param value the value to add
	 * @return true if this collection changed as a result of the call
	 */
	public boolean addValue(final T value) {
		return typeMap.add(value);
	}
	/**
	 * Creates a copy of this EnumSetHolder
	 * @return a new instance of this EnumSetHolder, with all the values in it copied over
	 */
	public abstract EnumSetHolder<T> makeCopy();
	/**
	 * Gets the class of the enum that this EnumSetHolder uses
	 * @return the class of the enum that this EnumSetHolder implementation uses.
	 * Should be implemented as:
	 * return myEnum.class
	 */
	protected abstract Class<T> getEnumClass();
	
	/** 
	 * @see java.lang.Object#toString()
	 * @return the string representation of this EnumSetHolder
	 */
	@Override
	public String toString() {
		return typeMap.toString();
	}
}
