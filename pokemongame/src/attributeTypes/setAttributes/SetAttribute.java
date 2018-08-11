package attributeTypes.setAttributes;

import java.util.Set;

import attributes.ReadableAttribute;
import interfaces.Copyable;

abstract class SetAttribute<Q, T extends Set<Q>> extends ReadableAttribute<Set<Q>> implements Copyable<SetAttribute<Q, T>> {
	public SetAttribute() {}
	protected SetAttribute(final SetAttribute<Q, T> attribute) {
		super(attribute);
	}

	public boolean containsValue(final Q value) {
		return getValue().contains(value);
	}
	
}
