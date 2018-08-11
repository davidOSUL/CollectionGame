package attributeTypes.regularAttributes;

import attributes.ReadableAttribute;
import interfaces.Copyable;

public class IntegerAttribute extends ReadableAttribute<Integer> implements Copyable<IntegerAttribute> {
	public IntegerAttribute() {}
	private IntegerAttribute(final IntegerAttribute attribute) {
		super(attribute);
	}
	@Override
	public IntegerAttribute makeCopy() {
		return new IntegerAttribute(this);
	}
}
