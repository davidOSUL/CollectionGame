package attributes;

import java.util.HashMap;
import java.util.Map;

import attributes.AttributeFactories.AttributeFactory;

final class AttributeManagerHelperMap<T> {
	private final Map<AttributeManager, AttributeManagerHelperInterface<T>> attributeManagerMap = new HashMap<AttributeManager, AttributeManagerHelperInterface<T>>();
	private final ParseType<T> parseType;
	private final AttributeFactory<T> factory;
	AttributeManagerHelperMap(final ParseType<T> parseType, final AttributeFactory<T> factory) {
		this.parseType = parseType;
		this.factory = factory;
	}
	void addNewManager(final AttributeManager manager) {
		attributeManagerMap.put(manager, new AttributeManagerHelper<T>(manager, parseType, factory));
	}
	AttributeManagerHelperInterface<T> getHelper(final AttributeManager manager) {
		if (!attributeManagerMap.containsKey(manager))
			throw new IllegalArgumentException("Manager not present for factory: " + this);
		return attributeManagerMap.get(manager);
	}
	void removeManager(final AttributeManager manager) {
		attributeManagerMap.remove(manager);
	}
	void copyManagerToNewManager(final AttributeManager oldManager, final AttributeManager newManager) {
		attributeManagerMap.get(oldManager).copyToNewHelper(attributeManagerMap.get(newManager));
	}
	
}