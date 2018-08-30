package attributes;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Collection;

import interfaces.SerializableConsumer;

interface AttributeManagerHelperInterface<T> {
	void writeObject(final ObjectOutputStream oos) throws IOException;
	void copyToNewHelper(final AttributeManagerHelperInterface<T> helper);
	void readObject(final ObjectInputStream ois) throws ClassNotFoundException, IOException;
	void addNewWatcher(final AttributeManagerWatcher<T> watcher);
	Attribute<T> generateAttribute(final String name);
	Attribute<T> getAttribute(final String name);
	void removeAttribute(final String name);
	void setAttributeValue(final String name, final T value);
	void setAttributeValue(final String name, final String value);
	void setDoOnGeneration(final SerializableConsumer<Attribute<T>> consumer);
	Collection<Attribute<T>> getAllAttributes();
	boolean containsAttribute(final String name);
	Attribute<T> getAttributeTemplate(final String attributeName);
}
