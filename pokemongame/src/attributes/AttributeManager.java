package attributes;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Consumer;

import attributes.AttributeFactories.AttributeFactory;
import gameutils.GameUtils;
import interfaces.SerializablePredicate;

public class AttributeManager implements Serializable {
	private SerializablePredicate<Attribute<?>> validate = new SerializablePredicate<Attribute<?>>() {
		@Override
		public boolean test(final Attribute<?> t) {
			return true;
		}	
	};
	private String currentDescription = "";
	public AttributeManager() {
		//performOnAllFactories(f -> f.addNewManager(this));
		performOnAllFactories(f -> f.addNewManager2(this));
	}
	public void copyOverFromOldManager(final AttributeManager old) {
		performOnAllFactories(f -> f.copyManagerToNewManager(old, this));
		this.validate = old.validate;
		this.currentDescription = old.currentDescription;
	}
	public void setAttributeValidation(final SerializablePredicate<Attribute<?>> validate) {
		this.validate = validate;
	}
	public <T> void addWatcher(final AttributeManagerWatcher<T> watcher, final ParseType<T> type) {
		//type.getAssociatedFactory().addNewWatcherForManager(this, watcher);
		getHelper(type).addNewWatcher(watcher);
	}
	public void generateAttribute(final String attributeName) {
		/*final AttributeFactory<?> creator = getCreator(attributeName);
		if (containsAttribute(attributeName))
			throw new IllegalArgumentException("Attribute " + attributeName + "already exists");
		if (validate.test(creator.getAttributeTemplate(attributeName))) {
			AttributeFactories.getInstance().getCreatorFactory(attributeName).generateAttributeForManager(this, attributeName);
		}
		else
			throw new IllegalArgumentException("Attribute " + attributeName + " failed attribute validation");
		updateDescription();*/
		if (containsAttribute(attributeName))
			throw new IllegalArgumentException("Attribute " + attributeName + "already exists");
		if (validate.test(getHelper(attributeName).getAttributeTemplate(attributeName))) {
			getHelper(attributeName).generateAttribute(attributeName);
		}
		else
			throw new IllegalArgumentException("Attribute " + attributeName + " failed attribute validation");
		updateDescription();

	}
	public void removeAttribute(final String attributeName) {
		/*final AttributeFactory<?> creator = getCreator(attributeName);
		if (!containsAttribute(attributeName))
			throw new IllegalArgumentException("Attribute " + attributeName + " not present");
		creator.removeAttributeForManager(this, attributeName);
		updateDescription();*/
		if (!containsAttribute(attributeName))
			throw new IllegalArgumentException("Attribute " + attributeName + " not present");
		getHelper(attributeName).removeAttribute(attributeName);
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
		//return type.getAssociatedFactory().getAttributeForManager(this, attributeName);
		return getHelper(type).getAttribute(attributeName);
	}
	public String getAttributeAsString(final String attributeName) {
		//return getCreator(attributeName).getAttributeForManager(this, attributeName).toString();
		return getHelper(attributeName).getAttribute(attributeName).toString();
	}
	public <T> T getAttributeValue(final String attributeName, final ParseType<T> type) {
		return getAttribute(attributeName, type).getValue();
	}
	public <T> void setAttributeValue(final String attributeName, final T value, final ParseType<T> type) {
		//type.getAssociatedFactory().setAttributeValueForManager(this, attributeName, value);
		getHelper(type).setAttributeValue(attributeName, value);
		updateDescription();
	}
    public void setAttributeValue(final String attributeName, final String value) {
    		//getCreator(attributeName).setAttributeValueForManager(this, attributeName, value);
    		getHelper(attributeName).setAttributeValue(attributeName, value);
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
		/*type.getAssociatedFactory().getAllAttributesForManager(this).forEach(at -> {
			if (at.hasCharacteristic(characteristic))
				validAttributes.add(at);
		});*/
		getHelper(type).getAllAttributes().forEach(at -> {
			if (at.hasCharacteristic(characteristic))
				validAttributes.add(at);
		});
		return validAttributes;
	}
	public Set<Attribute<?>> getAttributesOfCharacteristic(final AttributeCharacteristic characteristic) {
		final Set<Attribute<?>> validAttributes = new HashSet<Attribute<?>>();
		performOnAllFactories(factory -> {
			getHelper(factory).getAllAttributes().forEach(at -> {
				if (at.hasCharacteristic(characteristic))
					validAttributes.add(at);
			});
		});
		return validAttributes;
	}
	public boolean containsAttribute(final String attributeName) {
		//return getCreator(attributeName).containsAttributeForManager(this, attributeName);
		return getHelper(attributeName).containsAttribute(attributeName);
	}
	/*private AttributeFactory<?> getCreator(final String attributeName) {
		final AttributeFactory<?> creator = AttributeFactories.getInstance().getCreatorFactory(attributeName);
		if (creator == null)
			throw new IllegalArgumentException("Invalid attribute name: " + attributeName);
		return creator;
	}*/
	private Set<Attribute<?>> getAllAttributesInOrder() {
		final Set<Attribute<?>> allAttributes = new TreeSet<Attribute<?>>((a1, a2) -> {
			if (a1.getDisplayRank() == a2.getDisplayRank())
				return 1;
			else
				return Integer.compare(a1.getDisplayRank(), a2.getDisplayRank());
		});
		performOnAllFactories(factory -> allAttributes.addAll(getHelper(factory).getAllAttributes()));
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
		//getCreator(attributeName).getAttributeForManager(this, attributeName).setExtraDescription(extraDescription);
		getAttribute(attributeName).setExtraDescription(extraDescription);
		updateDescription();
	}
	public boolean attributeValueEqualsParse(final String attributeName, final String value) {
		//return getCreator(attributeName).getAttributeForManager(this, attributeName).valEqualsParse(value);
		return getAttribute(attributeName).valEqualsParse(value); 
	}
	public static <T> String displayAttribute(final String attributeName, final T attributeValue, final ParseType<T> type,  final DisplayStringSetting... displayStringSettings)  {
		final AttributeManager manager = new AttributeManager();
		manager.generateAttribute(attributeName, attributeValue, type);
		final String result = manager.getAttribute(attributeName, type).getDisplayString(GameUtils.arrayToEnumSet(displayStringSettings, DisplayStringSetting.class));
		type.getAssociatedFactory().removeManager(manager);
		return result;
	}
	private void writeObject(final ObjectOutputStream oos) throws IOException {
		oos.defaultWriteObject();
		performOnAllFactories(f -> {
			try {
				getHelper(f).writeObject(oos);
			} catch (final IOException e) {
				e.printStackTrace();
			}
		});
	}

	private void readObject(final ObjectInputStream ois) throws ClassNotFoundException, IOException {
		ois.defaultReadObject(); 
		performOnAllFactories(f -> {
			f.addNewManager2(this);
			try {
				getHelper(f).readObject(ois);
			} catch (ClassNotFoundException | IOException e) {
				e.printStackTrace();
			}
		});
		

	}
	private void performOnAllFactories(final Consumer<AttributeFactory<?>> consumer) {
		for (final AttributeFactory<?> factory: AttributeFactories.getInstance().getFactoryList())
			consumer.accept(factory);
	}
	private <T> AttributeManagerHelperInterface<T> getHelper(final ParseType<T> type) {
		return type.getAssociatedFactory().getHelper(this);
	}
	private AttributeManagerHelperInterface<?> getHelper(final String attributeName) {
		return AttributeFactories.getInstance().getCreatorFactory(attributeName).getHelper(this);
	}
	private <T> AttributeManagerHelperInterface<T> getHelper(final AttributeFactory<T> factory) {
		return factory.getHelper(this);
	}
	private Attribute<?> getAttribute(final String attributeName) {
		return getHelper(attributeName).getAttribute(attributeName);
	}
	
}