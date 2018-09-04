package attributes;

/**
 * An observer for an AttributeManager, that can carry out appropriate actions when attributes are created, modified or removed
 * @author David O'Sullivan
 *
 * @param <T>
 */
public interface AttributeManagerObserver<T> {
/**
 * Carries out an action when an attribute is generated
 * @param addedAttribute the attribute that was generated
 */
public void onAttributeGenerated(Attribute<T> addedAttribute);
/**
 * Carries out an action when an attribute is removed
 * @param removedAttribute the attribute that was removed
 */
public void onAttributeRemoved(Attribute<T> removedAttribute);
/**
 * Carries out an action when the value of an attribute has changed
 * @param modifiedAttribute the attribute that was changed
 */
public void onAttributeValueChanged(Attribute<T> modifiedAttribute);
}
