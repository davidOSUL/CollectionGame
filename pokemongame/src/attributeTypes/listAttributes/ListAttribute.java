package attributeTypes.listAttributes;

import java.util.List;

import attribues.ReadableAttribute;
import interfaces.Copyable;

abstract class ListAttribute<T> extends ReadableAttribute<List<T>> implements Copyable<ListAttribute<T>> {
	public ListAttribute() {}
	protected ListAttribute(final ListAttribute<T> attribute) {
		super(attribute);
	}


}
