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

public class AttributeManagerHelper<T> implements AttributeManagerHelperInterface<T> {
	private final AttributeManager manager;
	private transient final ParseType<T> parseType;
	private final Map<String, Attribute<T>> attributeTemplates;
	private  Map<String, Attribute<T>> associatedAttributeManagers = new HashMap<String, Attribute<T>>();
	private SerializableConsumer<Attribute<T>> doOnGenerations;
	private List<AttributeManagerWatcher<T>> attributeWatchers = new ArrayList<AttributeManagerWatcher<T>>();
	public AttributeManagerHelper(final AttributeManager manager, final ParseType<T> parseType, final Map<String, Attribute<T>> attributeTemplates) {
		this.manager = manager;
		this.parseType = parseType;
		this.attributeTemplates = attributeTemplates;
	}
	@Override
	public void writeObject(final ObjectOutputStream oos) throws IOException {
		oos.writeObject(associatedAttributeManagers);
		oos.writeObject(doOnGenerations);
		oos.writeObject(attributeWatchers);
	}

	@Override
	public void readObject(final ObjectInputStream ois) throws ClassNotFoundException, IOException {
		associatedAttributeManagers = (Map<String, Attribute<T>>) ois.readObject();
		for (final Attribute<T> attribute : associatedAttributeManagers.values()) {
			attribute.setParseType(parseType);
		}
		doOnGenerations = (SerializableConsumer<Attribute<T>>) ois.readObject();
		attributeWatchers =   (List<AttributeManagerWatcher<T>>) ois.readObject();
		
	}
	
	@Override
	public void addNewWatcher(final AttributeManagerWatcher<T> watcher) {
		attributeWatchers.add(watcher);	
	}
	private void addAttribute(final String name, final Attribute<T> attribute) {
		associatedAttributeManagers.put(name, attribute);
		attributeWatchers.forEach(amw -> amw.onAttributeGenerated(attribute));
	}
	@Override
	public Attribute<T> generateAttribute(final String name) {
		throwIfInvalidTemplate(name);
		if (associatedAttributeManagers.containsKey(name))
			throw new IllegalArgumentException(name + "attribute already exists for manager:" + manager);
		final Attribute<T> attribute = attributeTemplates.get(name).makeCopy();
		attribute.setValueToDefault();
		addAttribute(name, attribute);
		return attribute;
	}

	@Override
	public Attribute<T> getAttribute(final String name) {
		throwIfInvalidTemplate(name);
		if (!associatedAttributeManagers.containsKey(name)) {
			throw new AttributeNotFoundException(name + " is a valid attribute, however it has not been generated for this manager (" + manager + ")");
		}
		return associatedAttributeManagers.get(name);
	}

	@Override
	public void removeAttribute(final String name) {
		throwIfInvalidTemplate(name);
		if (!associatedAttributeManagers.containsKey(name)) {
			throw new AttributeNotFoundException(name + " is a valid attribute, however it has not been generated for this manager (" + manager + ")");
		}
		final Attribute<T> removedAttribute = associatedAttributeManagers.remove(name);
		attributeWatchers.forEach(amw -> amw.onAttributeRemoved(removedAttribute));
		
	}
	@Override
	public void setAttributeValue(final String name, final T value) {
		final Attribute<T> attribute = getAttribute(name);
		attribute.setValue(value);
		attributeWatchers.forEach(amw -> amw.onAttributeModified(attribute));
		
	}

	@Override
	public void setAttributeValue(final String name, final String value) {
		final Attribute<T> attribute = getAttribute(name);
		attribute.setValueParse(value);
		attributeWatchers.forEach(amw -> amw.onAttributeModified(attribute));
		
	}

	@Override
	public void setDoOnGeneration(final SerializableConsumer<Attribute<T>> consumer) {
		doOnGenerations =  consumer;
		
	}

	@Override
	public Collection<Attribute<T>> getAllAttributes() {
		return Collections.unmodifiableCollection(associatedAttributeManagers.values());
	}

	@Override
	public boolean containsAttribute(final String name) {
		return attributeTemplates.containsKey(name) && associatedAttributeManagers.containsKey(name);

	}
	private void throwIfInvalidTemplate(final String attributeName) {
		if (!attributeTemplates.containsKey(attributeName))
			throw new AttributeNotFoundException(attributeName + "is not a valid attribute");
	}
	@Override
	public void copyToNewHelper(final AttributeManagerHelperInterface<T> helper) {
		for (final Map.Entry<String, Attribute<T>> entry: associatedAttributeManagers.entrySet()) {
			helper.generateAttribute(entry.getKey());
			helper.setAttributeValue(entry.getKey(), entry.getValue().getValue());
		}
		
	}
	@Override
	public Attribute<T> getAttributeTemplate(final String attributeName) {
		return attributeTemplates.get(attributeName);
	}

}
