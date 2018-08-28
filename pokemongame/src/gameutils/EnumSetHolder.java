/**
 * 
 */
package gameutils;

import java.io.Serializable;
import java.util.Arrays;
import java.util.EnumSet;

/**
 * @author DOSullivan
 *
 */
public abstract class EnumSetHolder<T extends Enum<T>> implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private EnumSet<T> typeMap;
	public EnumSetHolder() {
		typeMap = EnumSet.noneOf(getEnumClass());
	}
	public EnumSetHolder(final T...enums) {
		if (enums.length == 0)
			typeMap = EnumSet.noneOf(getEnumClass());
		else
			typeMap = EnumSet.copyOf(Arrays.asList(enums));
	}
	private EnumSetHolder(final EnumSet<T> typeMap) {
		this.typeMap = typeMap;
	}
	protected EnumSetHolder(final EnumSetHolder<T> enumSetHolder) {
		typeMap = EnumSet.copyOf(enumSetHolder.typeMap);
	}
	public boolean containsValue(final T value) {
		return typeMap.contains(value);
	}
	public boolean containsValueParse(final String value) {
		return containsValue(parseValue(value));
	}
	protected abstract T parseValue(final String value);
	public boolean addValue(final T value) {
		return typeMap.add(value);
	}
	public abstract EnumSetHolder<T> makeCopy();
	protected abstract Class<T> getEnumClass();
	
	@Override
	public String toString() {
		return typeMap.toString();
	}
}
