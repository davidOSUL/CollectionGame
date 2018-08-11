package attributeTypes.regularAttributes;

import attributes.ReadableAttribute;
import interfaces.Copyable;

public class StringAttribute extends ReadableAttribute<String> implements Copyable<StringAttribute>{
	public StringAttribute() {}
	private StringAttribute(final StringAttribute attribute) {
		super(attribute);
	}
	@Override
	public StringAttribute makeCopy() {
		return new StringAttribute(this);
	}
}
