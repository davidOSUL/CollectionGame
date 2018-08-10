package attribues;

import java.util.EnumSet;

import interfaces.Copyable;

class EnumSetAttribute<T extends Enum<T>> extends ReadableAttribute<EnumSet<T>> implements Copyable<EnumSetAttribute<T>> {
	public EnumSetAttribute() {}
	private EnumSetAttribute(final EnumSetAttribute<T> attribute) {
		super(attribute);
	}
	@Override
	public EnumSetAttribute<T> makeCopy() {
		return new EnumSetAttribute<T>(this);
	}
	public boolean containsEnum(final Enum value) {
		return this.getValue().contains(value);
	}
}
