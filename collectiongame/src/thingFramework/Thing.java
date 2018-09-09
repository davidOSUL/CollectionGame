package thingFramework;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;

import attributes.Attribute;
import attributes.AttributeManager;
import attributes.ParseType;
import attributes.attributegenerators.AttributeGenerator;
import effects.Event;
import effects.Eventful;
import interfaces.Imagable;
import interfaces.SerializablePredicate;
import model.ModelAttributeManager;
import model.ThingObserver;
import modifiers.Modifier;

/**
 * a general thing, the backend of objects in the game
 * @author David O'Sullivan
 *
 */
public abstract class Thing implements Serializable, Eventful, Imagable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final String name;
	private final String image;
	private final AttributeManager attributes;
	private final ModelAttributeManager modelAttributeManager;
	private final List<Event> eventList= new ArrayList<Event>();
	private final Set<Modifier> modifiers = new HashSet<Modifier>();
	/**
	 * Creates a new Thing with no name, image, modelAttributeManager, or attributes, and with the provided events
	 */
	Thing() {
		name = null;
		image = null;
		modelAttributeManager = null;
		attributes = null;
	}
	/**
	 * Creates a new Thing with no name, image, modelAttributeManager, or attributes, and with the provided events
	 * @param events the events to add to the event lsit
	 */
	Thing(final Event...events) {
		this();
		if (events != null)
			addToEventList(Arrays.asList(events));
	}
	/**
	 * Creates a new Thing with the provided name and image
	 * @param name the name of the Thing
	 * @param image the image of the Thing
	 */
	Thing(final String name, final String image) {
		this.name = name;
		this.image = image;
		attributes = new AttributeManager();
		attributes.setAttributeValidation(new SerializablePredicate<Attribute<?>>() {
			private static final long serialVersionUID = 1L;
			@Override
			public boolean test(final Attribute<?> at) {
				return validateAttribute(at);
			}		
		});
		modelAttributeManager = new ModelAttributeManager(this);
		attributes.addObserver(modelAttributeManager, ParseType.INTEGER);
	}
	/**
	 * Creates a new Thing with the provided name and image
	 * @param name the name of the Thing
	 * @param image the image of the Thing
	 * @param haveModelAttributes if false, won't create a modelAttributeManager
	 */
	Thing(final String name, final String image, final boolean haveModelAttributes) {
		this.name = name;
		this.image = image;
		attributes = new AttributeManager();
		attributes.setAttributeValidation(at -> validateAttribute(at));
		if (haveModelAttributes) {
			modelAttributeManager = new ModelAttributeManager(this);
			attributes.addObserver(modelAttributeManager, ParseType.INTEGER);
		}
		else {
			modelAttributeManager = null;
		}
		
	}
	/**
	 * Creates a new thing by copying over all data from another Thing
	 * @param t the thing to copy over from
	 */
	Thing(final Thing t) {
		this.name = t.name;
		this.image = t.image;
		this.attributes = new AttributeManager();
		modelAttributeManager = new ModelAttributeManager(this);
		attributes.addObserver(modelAttributeManager, ParseType.INTEGER);
		attributes.copyOverFromOldManager(t.attributes);
	}
	/** 
	 * @see effects.Eventful#getEvents()
	 */
	@Override
	public List<Event> getEvents() {
		return eventList;
	}
	/**
	 * Returns a copy of this thing Note that this does NOT copy events. (It does however create new model attribute
	 * events)
	 * @return a copy of this thing. 
	 */
	public abstract Thing makeCopy();
	/**
	 * Adds the modifier to this thing if it is able to succesfully perform the modification on this thing
	 * (that is mod.shouldModify(this) == true)
	 * @param mod the mod to add
	 * @return whether or not the modifier was succesfully added
	 */
	public boolean addModifierIfShould(final Modifier mod) {
		final boolean performed = mod.performModificationIfShould(this);
		if (performed) {
			modifiers.add(mod);
		}
		return performed;
	}
	/**
	 * Removes the modifier from this thing if it is present. If it is present performs mod.performReverseModification(this)
	 * @param mod the mod to remove
	 * @return whether or not the modifier was succesfully removed
	 */
	public boolean removeModifierIfPresent(final Modifier mod) {
		if (!modifiers.contains(mod))
			return false;
		mod.performReverseModification(this);
		modifiers.remove(mod);
		return true;
	}
	
	/**
	 * Return the text that should display to discard this Thing
	 * @return the text that should display to discard this Thing
	 */
	public String getDiscardText() {
		return "Discard " + getName();
	}
	/**
	 * Returns true if contains attribute of the specified name
	 * @param name the name of the attribute
	 * @return true if contains attribute of the specified name
	 */
	public final boolean containsAttribute(final String name) {
		return attributes.containsAttribute(name);
	}
	/**
	 * Adds attributes as generated by the provided AttributeGenerator
	 * @param generator
	 */
	public abstract void addGeneratedAttributes(AttributeGenerator generator);
	/**
	 * Validate the provided attribute to ensure it can be added to this Thing
	 * @param attribute the attribute to validate
	 * @return true if the attribute can be added
	 */
	abstract boolean validateAttribute(Attribute<?> attribute);
	/**
	 * To be called when this Thing is placed on a model
	 * @param observer the observer the Thing is placed on
	 */
	public abstract void onPlace(ThingObserver observer);
	/**
	 * To be called when this Thing is removed from a model
	 * @param observer the observer the Thing is removed from
	 */
	public abstract void onRemove(ThingObserver observer);
	/**
	 * Return value of the Attribute of the given name
	 * @param <T> the type of the attribute
	 * @param name the name of the Attribute
	 * @param type the associated ParseType
	 * @return the value of the attribute
	 */
	public final <T> T getAttributeValue(final String name, final ParseType<T> type) {
		return attributes.getAttributeValue(name, type);
	}
	/**
	 * Returns the String representation of the Attribute with the provided name
	 * @param name the name of the Attribute
	 * @return the String representation of the Attribute with the provided name
	 */
	public final String getAttributeAsString(final String name) {
		return attributes.getAttributeAsString(name);
	}
	/**
	 * Adds the attribute with the provided name to this Thing
	 * @param attributeName the name of the attribute to add
	 */
	public final void addAttribute(final String attributeName) {
		attributes.generateAttribute(attributeName);
	}
	/**
	 * Adds the attribute with the provided name to this Thing and sets its value
	 * @param <T> the type of the Attribute
	 * @param attributeName the name of the Attribute
	 * @param value the value of the Attribute
	 * @param type the associated ParseType
	 */
	public final <T> void addAttribute(final String attributeName, final T value, final ParseType<T> type) {
		attributes.generateAttribute(attributeName, value, type);
	}
	/**
	 * Adds the attribute with the provided name to this Thing, and sets its value to the value represented by the provided String
	 * @param attributeName the name of the Attribute
	 * @param value the String representation of the attribute's value
	 */
	public final void addAttribute(final String attributeName, final String value) {
		attributes.generateAttribute(attributeName, value);
	}
	/**
	 * Adds a series of attributes with the provided names, and initializes their values to the provided values. 
	 * Should correspond on a 1 to 1 basis (e.g. attributeName[0] gets value set to values[0])
	 * @param <T> the type of the attributes
	 * @param attributeNames the names of the attributes
	 * @param values the values
	 * @param type the associated ParseType
	 */
	public final <T> void addAttributes(final String[] attributeNames, final T[] values, final ParseType<T> type) {
		attributes.generateAttributes(attributeNames, values, type);
	}
	/**
	 * Adds a series of attributes with the provided names, and initializes their values to the values represented by the provided Strings. 
	 * Should correspond on a 1 to 1 basis (e.g. attributeName[0] gets value set to values[0])
	 * @param attributeNames the names of the attributes
	 * @param values the String representation of the values
	 */
	public final void addAttributes(final String[] attributeNames, final String[] values) {
		attributes.generateAttributes(attributeNames, values);
	}
	/**
	 * Removes the attribute with the provided name
	 * @param attributeName the name of the Attribute to remove
	 */
	public final void removeAttribute(final String attributeName) {
		attributes.removeAttribute(attributeName);
	}
	/**
	 * Returns true if the Attribute with the provided name's value equals the value represented by the provided string
	 * @param attributeName the name of the attribute
	 * @param value the string representation of the target value
	 * @return true if the Attribute with the provided name equals the value represented by the provide String, false otherwise
	 */
	public final boolean attributeValueEqualsParse(final String attributeName, final String value) {
		return attributes.attributeValueEqualsParse(attributeName, value);
	}
	/** 
	 * @throws IllegalStateException if the event isn't present in the list of events for this Thing or it wasn't
	 *  marked for removal
	 * @see effects.Eventful#confirmEventRemovals(java.util.Collection)
	 */
	@Override
	public void confirmEventRemovals(final Collection<Event> events) {
		events.forEach(e -> {
			if (!e.wasMarkedForRemoval())
				throw new IllegalStateException("Event " + e + " should not have been confirmed for removal");
			final boolean removed = eventList.remove(e);
			if (!removed)
				throw new IllegalStateException("Event " + e + " removal was confirmed, but was not present in eventlist");
			
		});
	}
	/**
	 * Sets the value of the attribute with the provided name
	 * @param <T> the type of the Attribute
	 * @param name the name of the Attribute
	 * @param value the value to set the Attribute to 
	 * @param type the associated ParseType
	 */
	public final <T> void setAttributeValue(final String name, final T value, final ParseType<T> type) {
		attributes.setAttributeValue(name, value, type);
	}
	/**
	 * If the attribute doesn't exist, will throw error. Otherwise sets attribute's value to action.apply(getAttributeValue()).
	 * If shouldRemoveAfter.test(newValue), then will remove instead of setting value.
	 * @param <T> the type of the attribute
	 * @param attributeName the name of the attribute
	 * @param action the action performed on the new value to determine the new value. E.g. if you wanted to add 5
	 * to the attribute you would pass in an action equal to: x -> x+5
	 * @param shouldRemoveAfter predicate that tests the new value, and if returns true will remove attribute instead of modifying it
	 * @param type the type of the attribute
	 */
	public final <T> void modifyAttribute(final String attributeName, final Function<T, T> action, final Predicate<T> shouldRemoveAfter, final ParseType<T> type) {
		if (!containsAttribute(attributeName))
			throw new IllegalArgumentException("Attribute not present");
		final T newValue = action.apply(attributes.getAttributeValue(attributeName, type));
		if (shouldRemoveAfter.test(newValue))
			removeAttribute(attributeName);
		else
			attributes.setAttributeValue(attributeName, newValue, type);
	}
	/**
	 * If the attribute doesn't exist, will set value to initialValueIfNonPresent, and then apply the modification. Otherwise sets attribute's value to action.apply(getAttributeValue()).
	 * If shouldRemoveAfter.test(newValue), then will remove instead of setting value.
	 * @param <T> the type of the attribute
	 * @param attributeName the name of the attribute
	 * @param action the action performed on the new value to determine the new value. E.g. if you wanted to add 5
	 * to the attribute you would pass in an action equal to: x -> x+5
	 * @param shouldRemoveAfter predicate that tests the new value, and if returns true will remove attribute instead of modifying it
	 * @param type the type of the attribute
	 */
	public final <T> void modifyOrCreateAttribute(final String attributeName, final Function<T, T> action, final Predicate<T> shouldRemoveAfter, final ParseType<T> type) {
		if(!containsAttribute(attributeName)) {
			addAttribute(attributeName);
		}
		modifyAttribute(attributeName, action, shouldRemoveAfter, type);
		
	}
	/**
	 * If the attribute doesn't exist, will do nothing. Otherwise sets attribute's value to action.apply(getAttributeValue()).
	 * If shouldRemoveAfter.test(newValue), then will remove instead of setting value.
	 * @param <T> the type of the attribute
	 * @param attributeName the name of the attribute
	 * @param action the action performed on the new value to determine the new value. E.g. if you wanted to add 5
	 * to the attribute you would pass in an action equal to: x -> x+5
	 * @param shouldRemoveAfter predicate that tests the new value, and if returns true will remove attribute instead of modifying it
	 * @param type the type of the attribute
	 */
	public final <T> void modifyIfContainsAttribute(final String attributeName, final Function<T, T> action, final Predicate<T> shouldRemoveAfter, final ParseType<T> type) {
		if (containsAttribute(attributeName))
			modifyAttribute(attributeName, action, shouldRemoveAfter, type);
	}
	/**
	 * if the given attribute is present, apply the remapping function func(oldVal, value). otherwise create a new attribute set to
	 * value
	 * @param <T>
	 * @param name the name of the attribute
	 * @param value New Value
	 * @param func the remapping function
	 * @param type the associatedParseType
	 */
	public final <T> void mergeAttribute(final String name, final T value, final BiFunction<? super T,? super T,? extends T> func, final ParseType<T> type) {
		if (containsAttribute(name))
			setAttributeValue(name, func.apply(getAttributeValue(name, type), value), type);
		else
			addAttribute(name, value, type);
	}

	/** 
	 * @see effects.Eventful#getName()
	 */
	@Override
	public final String getName() {
		return name;
	}
	/**
	 * @return Name as well as description attribute
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		if (name == null)
			return "BLANK ITEM";
		return "<u>" + name + "</u>" + (!attributes.toString().isEmpty() ? "\n" + attributes : "");
	}
	/** 
	 * @see interfaces.Imagable#getImage()
	 */
	@Override
	public String getImage() {
		return image;
	}
	/** 
	 * @see effects.Eventful#addToEventList(effects.Event)
	 */
	@Override
	public void addToEventList(final Event e) {
		eventList.add(e);
		e.setCreator(this);
	}
	/**
	 * Sets the extra description of the attribute with the provided name
	 * @param attributeName the name of the attribute to set the extra description of
	 * @param extraDescription the value to set that Attribute's extra description to
	 */
	public void setExtraDescription(final String attributeName, final String extraDescription) {
		attributes.setAttributeExtraDescription(attributeName, extraDescription);
	}

	
}
