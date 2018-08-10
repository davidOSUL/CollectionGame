package attribues;

import interfaces.Copyable;

public class DoubleAttribute extends ReadableAttribute<Double> implements Copyable<DoubleAttribute> {
	public DoubleAttribute() {}
	private DoubleAttribute(final DoubleAttribute attribute) {
		super(attribute);
	}
	@Override
	public DoubleAttribute makeCopy() {
		return new DoubleAttribute(this);
	}
}
