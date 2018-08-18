package attributes;

import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

import attributes.AttributeFactories.AttributeFactory;
import interfaces.SerializablePredicate;

public final class AttributeManager {
	private SerializablePredicate<Attribute<?>> validate = x -> true;
	private String currentDescription = "";
	public AttributeManager() {
		for (final AttributeFactory<?> factory: AttributeFactories.getInstance().getFactoryList())
			factory.addNewManager(this);
	}
	public void copyOverFromOldManager(final AttributeManager old) {
		for (final AttributeFactory<?> factory: AttributeFactories.getInstance().getFactoryList())
			factory.copyManagerToNewManager(old, this);
		this.validate = old.validate;
		this.currentDescription = old.currentDescription;
	}
	public void setAttributeValidation(final SerializablePredicate<Attribute<?>> validate) {
		this.validate = validate;
	}
	public <T> void addWatcher(final AttributeManagerWatcher<T> watcher, final ParseType<T> type) {
		type.getAssociatedFactory().addNewWatcherForManager(this, watcher);
	}
	public void generateAttribute(final String attributeName) {
		final AttributeFactory<?> creator = getCreator(attributeName);
		if (containsAttribute(attributeName))
			throw new IllegalArgumentException("Attribute " + attributeName + "already exists");
		if (validate.test(creator.getAttributeTemplate(attributeName))) {
			final Attribute<?> generatedAttribute = AttributeFactories.getInstance().getCreatorFactory(attributeName).generateAttributeForManager(this, attributeName);
		}
		else
			throw new IllegalArgumentException("Attribute " + attributeName + " failed attribute validation");
		updateDescription();

	}
	public void removeAttribute(final String attributeName) {
		final AttributeFactory<?> creator = getCreator(attributeName);
		if (!containsAttribute(attributeName))
			throw new IllegalArgumentException("Attribute " + attributeName + " not present");
		creator.removeAttributeForManager(this, attributeName);
		updateDescription();


	}
	public <T> void generateAttribute(final String attributeName, final T value, final ParseType<T> type) {
		generateAttribute(attributeName);
		setAttributeValue(attributeName, value, type);
		
	}
	public <T> void generateAttribute(final String attributeName, final String value) {
		generateAttribute(attributeName);
		setAttributeValue(attributeName, value);
	}
	private <T> Attribute<T> getAttribute(final String attributeName, final ParseType<T> type) {
		return type.getAssociatedFactory().getAttributeForManager(this, attributeName);
	}
	public String getAttributeAsString(final String attributeName) {
		return AttributeFactories.getInstance().getCreatorFactory(attributeName).getAttributeForManager(this, attributeName).toString();
	}
	public <T> T getAttributeValue(final String attributeName, final ParseType<T> type) {
		return getAttribute(attributeName, type).getValue();
	}
	public <T> void setAttributeValue(final String attributeName, final T value, final ParseType<T> type) {
		type.getAssociatedFactory().setAttributeValueForManager(this, attributeName, value);
		updateDescription();
	}
    public void setAttributeValue(final String attributeName, final String value) {
    		getCreator(attributeName).setAttributeValueForManager(this, attributeName, value);
    		updateDescription();
	}
	public void generateAttributes(final String[] names, final String[] values) {
		if (names.length != values.length)
			throw new IllegalArgumentException("names and values must have same length");
		final Attribute[] attributes = new Attribute[names.length];
		for (int i = 0; i < names.length; i++) {
			generateAttribute(names[i], values[i]);
		}
	}
	public <T> void generateAttributes(final String[] names, final T[] values, final ParseType<T> type) {
		if (names.length != values.length)
			throw new IllegalArgumentException("names and values must have same length");
		final Attribute[] attributes = new Attribute[names.length];
		for (int i = 0; i < names.length; i++) {
			generateAttribute(names[i], values[i], type);
		}
	}
	public <T> Set<Attribute<T>> getAttributesOfCharacteristic(final AttributeCharacteristic characteristic, final ParseType<T> type) {
		final Set<Attribute<T>> validAttributes = new HashSet<Attribute<T>>();
		type.getAssociatedFactory().getAllAttributesForManager(this).forEach(at -> {
			if (at.hasCharacteristic(characteristic))
				validAttributes.add(at);
		});
		return validAttributes;
	}
	public Set<Attribute<?>> getAttributesOfCharacteristic(final AttributeCharacteristic characteristic) {
		final Set<Attribute<?>> validAttributes = new HashSet<Attribute<?>>();
		AttributeFactories.getInstance().getFactoryList().forEach(factory -> {
			factory.getAllAttributesForManager(this).forEach(at -> {
				if (at.hasCharacteristic(characteristic))
					validAttributes.add(at);
			});
		});
		return validAttributes;
	}
	public boolean containsAttribute(final String attributeName) {
		return getCreator(attributeName).containsAttributeForManager(this, attributeName);
	}
	private AttributeFactory<?> getCreator(final String attributeName) {
		final AttributeFactory<?> creator = AttributeFactories.getInstance().getCreatorFactory(attributeName);
		if (creator == null)
			throw new IllegalArgumentException("Invalid attribute name: " + attributeName);
		return creator;
	}
	private Set<Attribute<?>> getAllAttributesInOrder() {
		final Set<Attribute<?>> allAttributes = new TreeSet<Attribute<?>>((a1, a2) -> {
			if (a1.getDisplayRank() == a2.getDisplayRank())
				return 1;
			else
				return Integer.compare(a1.getDisplayRank(), a2.getDisplayRank());
		});
		for (final AttributeFactory<?> factory: AttributeFactories.getInstance().getFactoryList())
			allAttributes.addAll(factory.getAllAttributesForManager(this));
		return allAttributes;
	}
	@Override
	public String toString() {
		return currentDescription;
	}
	private void updateDescription() {
		final StringBuilder result = new StringBuilder();
		String newline = "";  
		for (final Attribute<?> at: getAllAttributesInOrder()) {
			if (at.shouldDisplay()) {
				result.append(newline).append(at.toString());
			    newline = "\n";
			}
		}
		currentDescription =  result.toString();
	}
	public void setAttributeExtraDescription(final String attributeName, final String extraDescription) {
		getCreator(attributeName).getAttributeForManager(this, attributeName).setExtraDescription(extraDescription);
		updateDescription();
	}
	public boolean attributeValueEqualsParse(final String attributeName, final String value) {
		return getCreator(attributeName).getAttributeForManager(this, attributeName).valEqualsParse(value);
	}
}