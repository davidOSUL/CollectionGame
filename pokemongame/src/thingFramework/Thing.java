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
import attributes.AttributeCharacteristic;
import attributes.AttributeManager;
import attributes.ParseType;
import attributes.attributegenerators.AttributeGenerator;
import effects.Event;
import effects.Eventful;
import game.Board;
import game.BoardAttributeManager;
import gameutils.GameUtils;
import interfaces.Imagable;
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
	private final static AttributeCharacteristic BOARDTYPE = AttributeCharacteristic.STATMOD;
	private final AttributeManager attributes;
	private final BoardAttributeManager boardAttributeManager;
	private final List<Event> eventList= new ArrayList<Event>();
	private final Set<Modifier> modifiers = new HashSet<Modifier>();
	protected Thing() {
		name = null;
		image = null;
		boardAttributeManager = null;
		attributes = null;
	}
	protected Thing(final Event...events) {
		this();
		if (events != null)
			addToEventList(Arrays.asList(events));
	}
	public Thing(final String name, final String image) {
		this.name = name;
		this.image = image;
		attributes = new AttributeManager();
		attributes.setAttributeValidation(at -> validateAttribute(at));
		boardAttributeManager = new BoardAttributeManager(this);
		attributes.addWatcher(boardAttributeManager, ParseType.INTEGER);
	}
	public Thing(final String name, final String image, final Event...events ) {
		this(name, image, GameUtils.toArrayList(events));
	}
	public Thing(final String name, final String image, final List<Event> events) {
		this(name, image);
		if (events != null)
			addToEventList(events);
	}
	protected Thing(final Thing t) {
		this.name = t.name;
		this.image = t.image;
		this.attributes = new AttributeManager();
		boardAttributeManager = new BoardAttributeManager(this);
		attributes.addWatcher(boardAttributeManager, ParseType.INTEGER);
		attributes.copyOverFromOldManager(t.attributes);
	}
	@Override
	public List<Event> getEvents() {
		return eventList;
	}
	/**
	 * @return a copy of this thing. Note that this does NOT copy events. (It does however create new board attribute
	 * events)
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
	
	public String getDiscardText() {
		return "Discard " + getName();
	}
	public final boolean containsAttribute(final String name) {
		return attributes.containsAttribute(name);
	}
	public abstract void addGeneratedAttributes(AttributeGenerator generator);
	protected abstract boolean validateAttribute(Attribute<?> attribute);
	public abstract void onPlace(Board board);
	public abstract void onRemove(Board board);
	public final <T> T getAttributeValue(final String name, final ParseType<T> type) {
		return attributes.getAttributeValue(name, type);
	}
	public final String getAttributeAsString(final String name) {
		return attributes.getAttributeAsString(name);
	}
	public final void addAttribute(final String attributeName) {
		attributes.generateAttribute(attributeName);
	}
	public final <T> void addAttribute(final String attributeName, final T value, final ParseType<T> type) {
		attributes.generateAttribute(attributeName, value, type);
	}
	public final void addAttribute(final String attributeName, final String value) {
		attributes.generateAttribute(attributeName, value);
	}
	public final <T> void addAttributes(final String[] attributeNames, final T[] values, final ParseType<T> type) {
		attributes.generateAttributes(attributeNames, values, type);
	}
	public final void removeAttribute(final String attributeName) {
		attributes.removeAttribute(attributeName);
	}
	public final boolean attributeValueEqualsParse(final String attributeName, final String value) {
		return attributes.attributeValueEqualsParse(attributeName, value);
	}
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
	 * If the attribute doesn't exist, will set value to valueIfNonPresent. Otherwise sets attribute's value to action.apply(getAttributeValue()).
	 * If shouldRemoveAfter.test(newValue), then will remove instead of setting value.
	 * @param <T> the type of the attribute
	 * @param attributeName the name of the attribute
	 * @param action the action performed on the new value to determine the new value. E.g. if you wanted to add 5
	 * to the attribute you would pass in an action equal to: x -> x+5
	 * @param shouldRemoveAfter predicate that tests the new value, and if returns true will remove attribute instead of modifying it
	 * @param valueIfNonPresent the value to set the attribute to if it doesn't exist
	 * @param type the type of the attribute
	 */
	public final <T> void modifyOrCreateAttribute(final String attributeName, final Function<T, T> action, final Predicate<T> shouldRemoveAfter, final T valueIfNonPresent, final ParseType<T> type) {
		if(containsAttribute(attributeName))
			modifyAttribute(attributeName, action, shouldRemoveAfter, type);
		else
			addAttribute(attributeName, valueIfNonPresent, type);
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
	 */
	public final <T> void mergeAttribute(final String name, final T value, final BiFunction<? super T,? super T,? extends T> func, final ParseType<T> type) {
		if (containsAttribute(name))
			setAttributeValue(name, func.apply(getAttributeValue(name, type), value), type);
		else
			addAttribute(name, value, type);
	}

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
	@Override
	public String getImage() {
		return image;
	}
	@Override
	public void addToEventList(final Collection<Event> events) {
		if (events == null)
			return;
		events.forEach(e -> {
			eventList.add(e);
			e.setCreator(this);
		});
	}
	@Override
	public void addToEventList(final Event e) {
		addToEventList(Arrays.asList(e));
	}
	public void setExtraDescription(final String attributeName, final String extraDescription) {
		attributes.setAttributeExtraDescription(attributeName, extraDescription);
	}

	
}
