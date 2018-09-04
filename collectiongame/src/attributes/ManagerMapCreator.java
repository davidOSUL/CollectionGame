package attributes;

/**
 * A class implementing this interface holds on to an AttributeManagerMap of a particular type
 * @author David O'Sullivan
 *
 * @param <T> the type of the attribute in the AttributeManagerMap
 * @see AttributeManagerMap
 */
interface ManagerMapCreator<T> {

	/**
	 * Returns the AttributeManagerMap<T> that this ManagerMapCreator<T> is holding on to
	 * @return the AttributeManagerMap<T> that this ManagerMapCreator<T> is holding on to
	 */
	AttributeManagerMap<T> getManagerMap();

}