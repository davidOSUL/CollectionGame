package attribues;

import interfaces.Copyable;

public class EnumAttribute<T extends Enum<T>>  extends ReadableAttribute<Enum<T>> implements Copyable<EnumAttribute<T>> {
	public EnumAttribute() {}
	private EnumAttribute(final EnumAttribute<T> attribute) {
		super(attribute);
	}
	@Override
	public EnumAttribute<T> makeCopy() {
		return new EnumAttribute<T>(this);
	}
}
