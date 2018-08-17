package attributes;

public interface AttributeManagerWatcher<T> {
public void onAttributeGenerated(Attribute<T> addedAttribute);
public void onAttributeRemoved(Attribute<T> removedAttribute);
public void onAttributeModified(Attribute<T> modifiedAttribute);
}
