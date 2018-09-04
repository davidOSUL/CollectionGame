package attributes;

import java.util.HashMap;
import java.util.Map;

/**
 * A mapping between AttributeManager and AttributeMaps of the specified type.
 * @author David O'Sullivan
 *
 * @param <T> the type of the attribute that the AttributeMap<T> has
 * @see AttributeMap
 * @see AttributeManager
 */
final class AttributeManagerMap<T> {
	private final Map<AttributeManager, AttributeMapInterface<T>> attributeManagerMap = new HashMap<AttributeManager, AttributeMapInterface<T>>();
	private final ParseType<T> parseType;
	private final AttributeCreator<T> attributeCreator;
	/**
	 * Creates a new AttributeManagerMap.
	 * @param parseType the associated ParseType
	 * @param factory the associated AttributeCreator<T>
	 */
	AttributeManagerMap(final ParseType<T> parseType, final AttributeCreator<T> factory) {
		this.parseType = parseType;
		this.attributeCreator = factory;
	}
	/**
	 * add a new manager to this map
	 * @param manager
	 */
	void addNewManager(final AttributeManager manager) {
		attributeManagerMap.put(manager, new AttributeMap<T>(manager, parseType, attributeCreator));
	}
	/**
	 * return the AttributeMapInterface<T> for the provided manager
	 * @param manager the manager to lookup
	 * @return the AttributeMapInterface<T>
	 */
	AttributeMapInterface<T> getMap(final AttributeManager manager) {
		if (!attributeManagerMap.containsKey(manager))
			throw new IllegalArgumentException("Manager not present for attributeCreator: " + this);
		return attributeManagerMap.get(manager);
	}
	/**
	 * removes the manager from this map
	 * @param manager the manager to remove
	 */
	void removeManager(final AttributeManager manager) {
		attributeManagerMap.remove(manager);
	}
	/**
	 * Copies the AttributeMap<T> from one AttributeManager to another
	 * @param oldManager the manager to copy the AttributeMap from
	 * @param newManager the manager to copy the AttributeMap to 
	 */
	void copyManagerToNewManager(final AttributeManager oldManager, final AttributeManager newManager) {
		attributeManagerMap.get(oldManager).copyToNewMap(attributeManagerMap.get(newManager));
	}
	
}