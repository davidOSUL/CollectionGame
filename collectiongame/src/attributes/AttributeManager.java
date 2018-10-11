package attributes;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Consumer;

import gameutils.GameUtils;
import interfaces.SerializablePredicate;

/**
 * Manages all the Attributes for a Thing. This is the one object that should be used outside of the attributes package
 * to create, change, store, etc. multiple atttributes.
 * 
 * <p>As a whole, the goal of this class and the other classes in this package to achieve typesafe attribute management
 * of multiple different types, with no need for casting.
 * 
 * <p> The way the AttributeManager works, is that the AttributeFactories class contains a ManagerMapCreator for each
 * Attribute type (these types are enumerated in the ParseType class). When a AttributeManager is created, it goes through each ManagerMapCreator, and adds itself to each of their
 * AttributeManagerMaps. This in turn creates an AttributeMap of each Attribute Type specifically for this AttributeManager.
 * When the AttributeManager wants to access an attribute. It first gets the appropriate AttributeManagerMap through the use of a
 * ParseType, and then gets the AttributeMap stored for it in that AttributeManagerMap. 
 * @see AttributeFactories
 * @see Attribute
 * @see AttributeManagerMap
 * @see AttributeMap
 * @see ParseType
 * @see ManagerMapCreator
 * @author David O'Sullivan
 *
 */
public class AttributeManager implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private SerializablePredicate<Attribute<?>> validate;
	private String currentDescription = "";
	/**
	 * Creates a new, empty AttributeManager
	 */
	public AttributeManager() {
		performOnAllMapCreators(mc -> mc.getManagerMap().addNewManager(this));
	}
	/**
	 * copies all the attributes managed by the old AttributeManager to this AttributeManager
	 * @param old the old AttributeManager
	 */
	public void copyOverFromOldManager(final AttributeManager old) {
		performOnAllMapCreators(mc -> mc.getManagerMap().copyManagerToNewManager(old, this));
		setAttributeValidation(old.validate);
		this.currentDescription = old.currentDescription;
	}
	/**
	 * Sets the validation predicate that should be used by this AttributeManager. When an attribute is generated,
	 * if the validation fails, an error will be thrown
	 * @param validate the Predicate to validate an attribute
	 */
	public void setAttributeValidation(final SerializablePredicate<Attribute<?>> validate) {
		this.validate = validate;
		performOnAllMapCreators(mc -> getAttributeMap(mc).setDoOnGeneration(at -> {
			if (!this.validate.test(at)) {
				throw new IllegalArgumentException("Attribute " + at.getName() + " failed attribute validation");
			}	
		}));
	}
	/**
	 * Adds a new AttributeManagerObserver for this AttributeManager
	 * @param <T> the type of Attribute that is being observed
	 * @param observer the observer
	 * @param type the associated ParseType
	 */
	public <T> void addObserver(final AttributeManagerObserver<T> observer, final ParseType<T> type) {
		getAttributeMap(type).addNewObserver(observer);
	}
	/**
	 * Adds a new attribute with the specified name for this AttributeManager
	 * @param attributeName the name of the attribute
	 * @throws IllegalArgumentException if the name is invalid or if the attribute validation predicate for this 
	 * Attribute fails
	 */
	public void generateAttribute(final AttributeName<?> attributeName) {
		if (containsAttribute(attributeName))
			throw new IllegalArgumentException("Attribute " + attributeName + "already exists");
		getAttributeMap(attributeName).generateAttribute(attributeName.getName());
		updateDescription();

	}
	/**
	 * Removes the attribute with the specified name from this AttributeManager
	 * @param attributeName the name of the attribute to remove
	 * @throws IllegalArgumentException if the attribute is not present
	 */
	public void removeAttribute(final AttributeName<?> attributeName) {
		if (!containsAttribute(attributeName))
			throw new IllegalArgumentException("Attribute " + attributeName + " not present");
		getAttributeMap(attributeName).removeAttribute(attributeName.getName());
		updateDescription();


	}
	/**
	 * Generates a new attribute of the specified name set to the specified value
	 * @param <T> the type of the attribute
	 * @param attributeName the name of the attribute to generate
	 * @param value the value to set the attribute to 
	 */
	public <T> void generateAttribute(final AttributeName<T> attributeName, final T value) {
		generateAttribute(attributeName);
		setAttributeValue(attributeName, value);
		
	}
	/**
	 * Generates a new attribute of the specified name, and then parses the passed in value and sets the attribute's
	 * value to that.
	 * @param <T> the type of the attribute
	 * @param attributeName the name of the attribute
	 * @param value the String representation of the attribute's value
	 */
	public <T> void generateAttribute(final AttributeName<T> attributeName, final String value) {
		generateAttribute(attributeName);
		setAttributeValue(attributeName, value);
	}
	
	/**
	 * Gets the String representation of the specified attribute of this AttributeManager
	 * @param attributeName the name of the attribute associated with this AttributeManager
	 * @return the attribute as a string
	 */
	public String getAttributeAsString(final AttributeName<?> attributeName) {
		return getAttribute(attributeName).toString();
	}
	/**
	 * Gets the value of the specified attribute of this AttributeManager
	 * @param <T> the type of the attribute
	 * @param attributeName the name of the attribute
	 * @param type the associated ParseType
	 * @return the value of the attribute
	 */
	public <T> T getAttributeValue(final AttributeName<T> attributeName) {
		return getAttribute(attributeName).getValue();
	}
	/**
	 * Set the value of the specified attribute of this AttributeManager
	 * @param <T> the type of the attribute
	 * @param attributeName the name of the attribute
	 * @param value the new value for the attribute
	 * @param type the associated ParseType
	 */
	public <T> void setAttributeValue(final AttributeName<T> attributeName, final T value) {
		getAttributeMap(attributeName.getType()).setAttributeValue(attributeName.getName(), value);
		updateDescription();
	}
    /**
     * Set the value of the specified attribute of this AttributeManager 
     * @param attributeName the name of the attribute
     * @param value the string representation of the new value for attribute
     */
    public void setAttributeValue(final AttributeName<?> attributeName, final String value) {
    		getAttributeMap(attributeName).setAttributeValue(attributeName.getName(), value);
    		updateDescription();
	}
	/**
	 * Generates multiple attributes, and parses values, setting the values of the generated attributes on a 1-to-1 basis 
	 * (e.g. names[0] will be set to values[0]). Attributes names and values must be the same length
	 * @param names the names of the attributes
	 * @param values the String representation of the attribute values
	 */
	public void generateAttributes(final List<AttributeName<?>> names, final String[] values) {
		if (names.size() != values.length)
			throw new IllegalArgumentException("names and values must have same length");
		for (int i = 0; i < names.size(); i++) {
			generateAttribute(names.get(i), values[i]);
		}
	}
	/**
	 * Generates multiple attributes, and parses values, setting the values of the generated attributes on a 1-to-1 basis 
	 * (e.g. names[0] will be set to values[0]). Attributes names and values must be the same length
	 * @param <T> the type of the attributes
	 * @param names the names of the Attributes
	 * @param values the values of the attributes
	 * @param type the associated ParseType
	 */
	public <T> void generateAttributes(final List<AttributeName<T>> names, final T[] values) {
		if (names.size() != values.length)
			throw new IllegalArgumentException("names and values must have same length");
		for (int i = 0; i < names.size(); i++) {
			generateAttribute(names.get(i), values[i]);
		}
	}
	/**
	 * Returns the set of all attributes managed by this AttributeManager that contains the specified chracteristics
	 * @param <T> the type of the attribute
	 * @param characteristic the characteristic
	 * @param type the associated ParseType
	 * @return all attributes of the specified type that contain the characteristic
	 */
	public <T> Set<Attribute<T>> getAttributesOfCharacteristic(final AttributeCharacteristic characteristic, final ParseType<T> type) {
		final Set<Attribute<T>> validAttributes = new HashSet<Attribute<T>>();
			getAttributeMap(type).getAllAttributes().forEach(at -> {
			if (at.hasCharacteristic(characteristic))
				validAttributes.add(at);
		});
		return validAttributes;
	}
	/**
	 * Returns the set of all attributes managed by this AttributeManager that contains the specified chracteristics
	 * @param characteristic the characteristic
	 * @return all attributes that contain the characteristic
	 */
	public Set<Attribute<?>> getAttributesOfCharacteristic(final AttributeCharacteristic characteristic) {
		final Set<Attribute<?>> validAttributes = new HashSet<Attribute<?>>();
		performOnAllMapCreators(factory -> {
			getAttributeMap(factory).getAllAttributes().forEach(at -> {
				if (at.hasCharacteristic(characteristic))
					validAttributes.add(at);
			});
		});
		return validAttributes;
	}
	/**
	 * Checks if the attribute of the given name is managed by this AttributeManager
	 * @param attributeName the name to lookup
	 * @return true if an attribute of that name has been generated for this AttributeManager
	 */
	public boolean containsAttribute(final AttributeName<?> attributeName) {
		return getAttributeMap(attributeName).containsAttribute(attributeName.getName());
	}
	private Set<Attribute<?>> getAllAttributesInOrder() {
		final Set<Attribute<?>> allAttributes = new TreeSet<Attribute<?>>((a1, a2) -> {
			if (a1.getDisplayRank() == a2.getDisplayRank())
				return 1;
			else
				return Integer.compare(a1.getDisplayRank(), a2.getDisplayRank());
		});
		performOnAllMapCreators(factory -> allAttributes.addAll(getAttributeMap(factory).getAllAttributes()));
		return allAttributes;
	}
	/** 
	 * @see java.lang.Object#toString()
	 */
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
	/**
	 * Sets the extra description of the attribute of the given name managed by this AttributeManager
	 * @param attributeName the name of the attribute
	 * @param extraDescription the value to set that attributes extra description to 
	 */
	public void setAttributeExtraDescription(final AttributeName<?> attributeName, final String extraDescription) {
		getAttribute(attributeName).setExtraDescription(extraDescription);
		updateDescription();
	}
	/**
	 * checks whether the attribute with the given name managed by this AttributeManager has a value equal
	 * to the value represented by the provided string
	 * @param attributeName the name of the attribute
	 * @param value the string representation of the target value
	 * @return true if the attribute's value equals the value represented by the String value
	 */
	public boolean attributeValueEqualsParse(final AttributeName<?> attributeName, final String value) {
		return getAttribute(attributeName).valEqualsParse(value); 
	}
	/**
	 * Returns the display string that would be created for a new attribute of the specified values
	 * @param <T> the type of the attribute
	 * @param attributeName the name of the attribute
	 * @param attributeValue the value of the attribute
	 * @param displayStringSettings settings for the display String
	 * @return the display String for the created attribute
	 */
	public static <T> String displayAttribute(final AttributeName<T> attributeName, final T attributeValue,final DisplayStringSetting... displayStringSettings)  {
		final AttributeManager manager = new AttributeManager();
		manager.generateAttribute(attributeName, attributeValue);
		final String result = manager.getAttribute(attributeName).getDisplayString(GameUtils.arrayToEnumSet(displayStringSettings, DisplayStringSetting.class));
		attributeName.getType().getMapCreator().getManagerMap().removeManager(manager);
		return result;
	}
	
	private void writeObject(final ObjectOutputStream oos) throws IOException {
		oos.defaultWriteObject();
		performOnAllMapCreators(mc -> {
			try {
				getAttributeMap(mc).writeObject(oos);
			} catch (final IOException e) {
				e.printStackTrace();
			}
		});
	}

	private void readObject(final ObjectInputStream ois) throws ClassNotFoundException, IOException {
		ois.defaultReadObject(); 
		performOnAllMapCreators(mc -> {
			mc.getManagerMap().addNewManager(this);
			try {
				getAttributeMap(mc).readObject(ois);
			} catch (ClassNotFoundException | IOException e) {
				e.printStackTrace();
			}
		});
		

	}
	private void performOnAllMapCreators(final Consumer<ManagerMapCreator<?>> consumer) {
		for (final ManagerMapCreator<?> mapCreator: AttributeFactories.getInstance().getManagerMapCreatorList())
			consumer.accept(mapCreator);
	}
	private <T> AttributeMapInterface<T> getAttributeMap(final ParseType<T> type) {
		return type.getMapCreator().getManagerMap().getMap(this);
	}
	private AttributeMapInterface<?> getAttributeMap(final AttributeName<?> attributeName) {
		return getAttributeMap(attributeName.getName());
	}
	private AttributeMapInterface<?> getAttributeMap(final String attributeName) {
		return AttributeFactories.getInstance().getManagerMapCreatorOfAttribute(attributeName).getManagerMap().getMap(this);
	}
	private <T> AttributeMapInterface<T> getAttributeMap(final ManagerMapCreator<T> mapCreator) {
		return mapCreator.getManagerMap().getMap(this);
	}
	private <T> Attribute<T> getAttribute(final AttributeName<T> attribute) {
		return getAttributeMap(attribute.getType()).getAttribute(attribute.getName());
	}
	
	
}