package attributeTypes.regularAttributes;

import attribues.ReadableAttribute;
import interfaces.Copyable;

public class BooleanAttribute extends ReadableAttribute<Boolean> implements Copyable<BooleanAttribute> {
	public BooleanAttribute() {}
	private BooleanAttribute(final BooleanAttribute attribute) {
		super(attribute);
	}
	@Override
	public BooleanAttribute makeCopy() {
		return new BooleanAttribute(this);
	}
}
