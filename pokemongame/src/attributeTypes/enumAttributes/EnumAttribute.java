package attributeTypes.enumAttributes;


import attributes.ReadableAttribute;
import interfaces.Copyable;


abstract class EnumAttribute<T extends Enum<T>>  extends ReadableAttribute< Enum<T>, EnumAttribute<T>> implements Copyable<EnumAttribute<T>> {
	public EnumAttribute() {}
	protected EnumAttribute(final EnumAttribute<T> attribute) {
		super(attribute);
	}
	
}
