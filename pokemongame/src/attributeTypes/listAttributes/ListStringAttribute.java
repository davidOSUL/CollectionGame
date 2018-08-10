package attributeTypes.listAttributes;

public class ListStringAttribute extends ListAttribute<String>{
	public ListStringAttribute() {}
	private ListStringAttribute(final ListStringAttribute attribute) {
		super(attribute);
	}
	@Override
	public ListStringAttribute makeCopy() {
		return new ListStringAttribute(this);
	}

}
