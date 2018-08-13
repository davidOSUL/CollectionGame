package attributes;

import attributes.AttributeFactories.AttributeFactory;

public final class AttributeManager {
	public AttributeManager() {
		for (final AttributeFactory<?> factory: AttributeFactories.getInstance().getFactoryList())
			factory.addNewManager(this);
	}
	public void generateAttribute(final String attributeName) {
		AttributeFactories.getInstance().getCreatorFactory(attributeName).generateAttributeForManager(this, attributeName);
	}
	public <T> void generateAttribute(final String attributeName, final T value, final ParseType<T> type) {
		generateAttribute(attributeName);
		setAttributeValue(attributeName, value, type);
	}
	public <T> void generateAttribute(final String attributeName, final String value) {
		generateAttribute(attributeName);
		setAttributeValue(attributeName, value);
	}
	public <T> Attribute<T> getAttribute(final String attributeName, final ParseType<T> type) {
		return type.getAssociatedFactory().getAttributeForManager(this, attributeName);
	}
	public String getAttributeAsString(final String attributeName) {
		return AttributeFactories.getInstance().getCreatorFactory(attributeName).getAttributeForManager(this, attributeName).toString();
	}
	public <T> T getAttributeValue(final String attributeName, final ParseType<T> type) {
		return getAttribute(attributeName, type).getValue();
	}
	public <T> void setAttributeValue(final String attributeName, final T value, final ParseType<T> type) {
		type.getAssociatedFactory().getAttributeForManager(this, attributeName).setValue(value);
	}
    public void setAttributeValue(final String attributeName, final String value) {
		AttributeFactories.getInstance().getCreatorFactory(attributeName).getAttributeForManager(this, attributeName).setValueParse(value);
		
	}
}