package attributes;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import interfaces.SerializableConsumer;


/**
 * Implementation of AttributeMapInterface<T>
 * @see AttributeMapInterface
 * @author David O'Sullivan
 *
 * @param <T> the type of the Attribute stored by this Map
 */
public class AttributeMap<T> implements AttributeMapInterface<T> {
	private final AttributeManager manager;
	private transient final ParseType<T> parseType;
	private final AttributeCreator<T> attributeCreator;
	private  Map<String, Attribute<T>> attributes = new HashMap<String, Attribute<T>>();
	private SerializableConsumer<Attribute<T>> doOnGeneration;
	private List<AttributeManagerObserver<T>> attributeObserver = new ArrayList<AttributeManagerObserver<T>>();
	/**
	 * Creates a new AttributeMap.
	 * @param manager the Manager associated with this map
	 * @param parseType the associated ParseType
	 * @param attributeCreator the AttributeCreator to use to create attributes
	 */
	AttributeMap(final AttributeManager manager, final ParseType<T> parseType, final AttributeCreator<T> attributeCreator) {
		this.manager = manager;
		this.parseType = parseType;
		this.attributeCreator = attributeCreator;
	}
	/** 
	 * @see attributes.AttributeMapInterface#writeObject(java.io.ObjectOutputStream)
	 */
	@Override
	public void writeObject(final ObjectOutputStream oos) throws IOException {
		oos.writeObject(attributes);
		oos.writeObject(doOnGeneration);
		oos.writeObject(attributeObserver);
	}

	/** 
	 * @see attributes.AttributeMapInterface#readObject(java.io.ObjectInputStream)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void readObject(final ObjectInputStream ois) throws ClassNotFoundException, IOException {
		attributes = (Map<String, Attribute<T>>) ois.readObject();
		for (final Attribute<T> attribute : attributes.values()) {
			attribute.setParseType(parseType);
		}
		doOnGeneration = (SerializableConsumer<Attribute<T>>) ois.readObject();
		attributeObserver =   (List<AttributeManagerObserver<T>>) ois.readObject();
		
	}
	
	/** 
	 * @see attributes.AttributeMapInterface#addNewObserver(attributes.AttributeManagerObserver)
	 */
	@Override
	public void addNewObserver(final AttributeManagerObserver<T> watcher) {
		attributeObserver.add(watcher);	
	}
	/**
	 * Adds the attribute to the map, and notifies all the observers that an attribute was generated
	 * @param name the name of the attribute
	 * @param attribute the attribute
	 */
	private void addAttribute(final String name, final Attribute<T> attribute) {
		attributes.put(name, attribute);
		attributeObserver.forEach(amw -> amw.onAttributeGenerated(attribute));
	}
	/** 
	 * @see attributes.AttributeMapInterface#generateAttribute(java.lang.String)
	 */
	@Override
	public Attribute<T> generateAttribute(final String name) {
		throwIfInvalidTemplate(name);
		if (attributes.containsKey(name))
			throw new IllegalArgumentException(name + "attribute already exists for manager:" + manager);
		final Attribute<T> attribute = attributeCreator.getAttributeTemplate(name).newCopy();
		attribute.setValueToDefault();
		addAttribute(name, attribute);
		if (doOnGeneration != null)
			doOnGeneration.accept(attribute);
		return attribute;
	}

	/** 
	 * @see attributes.AttributeMapInterface#getAttribute(java.lang.String)
	 */
	@Override
	public Attribute<T> getAttribute(final String name) {
		throwIfInvalidTemplate(name);
		if (!attributes.containsKey(name)) {
			throw new AttributeNotFoundException(name + " is a valid attribute, however it has not been generated for this manager (" + manager + ")");
		}
		return attributes.get(name);
	}

	/** 
	 * @see attributes.AttributeMapInterface#removeAttribute(java.lang.String)
	 */
	@Override
	public void removeAttribute(final String name) {
		throwIfInvalidTemplate(name);
		if (!attributes.containsKey(name)) {
			throw new AttributeNotFoundException(name + " is a valid attribute, however it has not been generated for this manager (" + manager + ")");
		}
		final Attribute<T> removedAttribute = attributes.remove(name);
		attributeObserver.forEach(amw -> amw.onAttributeRemoved(removedAttribute));
		
	}
	/** 
	 * @see attributes.AttributeMapInterface#setAttributeValue(java.lang.String, java.lang.Object)
	 */
	@Override
	public void setAttributeValue(final String name, final T value) {
		final Attribute<T> attribute = getAttribute(name);
		attribute.setValue(value);
		attributeObserver.forEach(amw -> amw.onAttributeValueChanged(attribute));
		
	}

	/** 
	 * @see attributes.AttributeMapInterface#setAttributeValue(java.lang.String, java.lang.String)
	 */
	@Override
	public void setAttributeValue(final String name, final String value) {
		final Attribute<T> attribute = getAttribute(name);
		attribute.setValueParse(value);
		attributeObserver.forEach(amw -> amw.onAttributeValueChanged(attribute));
		
	}

	/** 
	 * @see attributes.AttributeMapInterface#setDoOnGeneration(interfaces.SerializableConsumer)
	 */
	@Override
	public void setDoOnGeneration(final SerializableConsumer<Attribute<T>> consumer) {
		doOnGeneration =  consumer;
		
	}

	/** 
	 * @see attributes.AttributeMapInterface#getAllAttributes()
	 */
	@Override
	public Collection<Attribute<T>> getAllAttributes() {
		return Collections.unmodifiableCollection(attributes.values());
	}

	/** 
	 * @see attributes.AttributeMapInterface#containsAttribute(java.lang.String)
	 */
	@Override
	public boolean containsAttribute(final String name) {
		return attributeCreator.containsAttributeTemplate(name) && attributes.containsKey(name);

	}
	private void throwIfInvalidTemplate(final String attributeName) {
		if (!attributeCreator.containsAttributeTemplate(attributeName))
			throw new AttributeNotFoundException(attributeName + "is not a valid attribute");
	}
	/** 
	 * @see attributes.AttributeMapInterface#copyToNewMap(attributes.AttributeMapInterface)
	 */
	@Override
	public void copyToNewMap(final AttributeMapInterface<T> attributeMap) {
		for (final Map.Entry<String, Attribute<T>> entry: attributes.entrySet()) {
			attributeMap.generateAttribute(entry.getKey());
			attributeMap.setAttributeValue(entry.getKey(), entry.getValue().getValue());
		}
		
	}

	

}
