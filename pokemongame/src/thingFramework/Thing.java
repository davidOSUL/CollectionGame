package thingFramework;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.function.BiFunction;

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
	private final static AttributeType BOARDTYPE = AttributeType.STATMOD;
	private final Map<Attribute, Event> boardAttributes;
	private final EnumSet<ThingType> types;
	private final Set<Attribute> attributes; 
	private final Map<String, Attribute> attributeNameMap;
	private final List<Event> eventList= new ArrayList<Event>();
	private final Set<Modifier<Thing>> thingModifiers = new HashSet<Modifier<Thing>>();
	protected Thing() {
		types = setThingType();
		attributes = null;
		boardAttributes = null;
		attributeNameMap = null;
		name = null;
		image = null;
	}
	protected Thing(final Event...events) {
		this();
		if (events != null)
			addToEventList(Arrays.asList(events));
	}
	public Thing(final String name, final String image, final Set<Attribute> attributes) {
		if (!vallidateAttributes(attributes))
			throw new Error("INVALID ATTRIBUTE FOR: " + name);
		types = setThingType();
		this.attributes = attributes;
		this.name = name ;
		this.image = image;
		attributeNameMap = generateAttributeNameMap(attributes);
		boardAttributes = BoardAttributeManager.getEvents(getAttributesThatContainType(BOARDTYPE), this);
		addToEventList(boardAttributes.values());
		updateDescription();
	}
	public Thing(final String name, final String image, final Set<Attribute> attributes, final Event...events ) {
		this(name, image, attributes, GameUtils.toArrayList(events));
	}
	public Thing(final String name, final String image, final Set<Attribute> attributes, final List<Event> events) {
		this(name, image, attributes);
		if (events != null)
			addToEventList(events);
	}
	protected static Set<Attribute> makeAttributeCopy(final Set<Attribute> attributes) {
		final Set<Attribute> newAttributes = new HashSet<Attribute>();
		for (final Attribute at : attributes) {
			newAttributes.add(Attribute.generateAttribute(at));
		}
		return newAttributes;
	}
	@Override
	public List<Event> getEvents() {
		return eventList;
	}
	public abstract Thing makeCopy();
	protected static <T> boolean  addModifierIfShould(final Modifier<T> mod, final Collection<Modifier<T>> toAdd, final T t) {
		final boolean performed = mod.performModificationIfShould(t);
		if (performed) {
			toAdd.add(mod);
		}
		return performed;
	}
	protected static <T>  boolean removeModifierIfPresent(final Modifier<T> mod, final Collection<Modifier<T>> toRemove, final T t) {
		if (!toRemove.contains(mod))
			return false;
		mod.performReverseModification(t);
		toRemove.remove(mod);
		return true;
	}
	/**
	 * Adds the modifier to this thing if it is able to succesfully perform the modification on this thing
	 * (that is mod.shouldModify(this) == true)
	 * @param mod the mod to add
	 * @return whether or not the modifier was succesfully added
	 */
	public boolean addThingModifierIfShould(final Modifier<Thing> mod) {
		return Thing.addModifierIfShould(mod, thingModifiers, this);
	}
	/**
	 * Removes the modifier from this thing if it is present. If it is present performs mod.performReverseModification(this)
	 * @param mod the mod to remove
	 * @return whether or not the modifier was succesfully removed
	 */
	public boolean removeThingModifierIfPresent(final Modifier<Thing> mod) {
		return Thing.removeModifierIfPresent(mod, thingModifiers, this);
	}
	
	public String getDiscardText() {
		return "Discard " + getName();
	}
	public Thing(final Thing t) {
		this(t.getName(), t.getImage(), t.getAttributes());
	}
	public final boolean containsAttribute(final String name) {
		return attributeNameMap.containsKey(name);
	}
	private final static Map<String, Attribute> generateAttributeNameMap(final Set<Attribute> attributes){
		final Map<String, Attribute> attributeNameMap = new HashMap<String, Attribute>();
		for (final Attribute at: attributes) {
			attributeNameMap.put(at.getName(), at);
		}
		return attributeNameMap;
	}

	private final Set<Attribute> getAttributesThatContainType(final AttributeType at) {
		if (attributes == null)
			return new HashSet<Attribute>();
		final Set<Attribute> validAttributes = new HashSet<Attribute>();
		for (final Attribute attribute: attributes) {
			if (attribute.containsType(at))
				validAttributes.add(attribute);
		}
		return validAttributes;
	}
	private final boolean vallidateAttribute(final Attribute at) {
		return vallidateAttributes(new HashSet<Attribute>(Arrays.asList(at)));
	}
	protected abstract boolean vallidateAttributes(Set<Attribute> attributes);
	protected abstract EnumSet<ThingType> setThingType();
	public abstract void onPlace(Board board);
	public abstract void onRemove(Board board);
	public final EnumSet<ThingType> getThingTypes() {
		return types;
	}
	public final Object getAttributeVal(final String name) {
		if (containsAttribute(name))
			return attributeNameMap.get(name).getValue();
		else
			throw new AttributeNotFoundException("ATTRIBUTE NOT FOUND");
	}
	public final Set<Attribute> getAttributes() {
		return attributes;
	}
	public final void addAttributes(final Attribute...attributes) {
		for (final Attribute at: attributes)
			addAttribute(at);
	}
	public final void addAttribute(final Attribute at) {
		if (!vallidateAttribute(at))
			throw new Error("INVALID ATTRIBUTE FOR: " + name);
		if (attributes.contains(at) || attributeNameMap.keySet().contains(at.getName()))
			throw new Error("ATTRIBUTE " + at.getName() + " ALREADY EXISTS FOR: " + name);
		else if (!at.shouldIgnore()){
			attributes.add(at);
			attributeNameMap.put(at.getName(), at);
			if (at.containsType(BOARDTYPE)) {
				final Event e=  BoardAttributeManager.getEvent(at, this);
				boardAttributes.put(at, e);
				addToEventList(e);
			}
		}
		updateDescription();
	}
	public final void removeAttribute(final String name) {
		attributes.remove(attributeNameMap.get(name));
		final Attribute at = attributeNameMap.remove(name);
		final Event e = boardAttributes.get(at);
		if (e != null) {
			e.markForRemoval();
			boardAttributes.remove(at); 
		}
		updateDescription();

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
	public Attribute getAttribute(final String name) {
		return attributeNameMap.get(name);
	}
	public final void setAttributeVal(final String name, final Object value) {
		if (containsAttribute(name)) {
			final Attribute at = attributeNameMap.get(name);
			at.setValue(value);
			if (boardAttributes.containsKey(at)) {
				BoardAttributeManager.modifyBoardEvent(at.getName(), boardAttributes.get(at), value);
			}
		}
		else
			throw new AttributeNotFoundException("ATTRIBUTE NOT FOUND");
		
		updateDescription();
	}
	public final List<Attribute> getAttributesOfType(final AttributeType at) {
		final List<Attribute> returnAttributes = new ArrayList<Attribute>();
		for (final Attribute a: attributes) {
			if (a.containsType(at))
				returnAttributes.add(a);
		}
		return returnAttributes;
	}
	/**
	 * Adds the provided value to the given attribute, and sets the attribute to that value if non-existant
	 * @param name the name of the attribute
	 * @param value the value to add/set to
	 * @param removeIfZero if set to true will remove this attribute if the value is zero
	 */
	public final void addToIntegerAttribute(final String name, final int value, final boolean removeIfZero) {
		mergeAttribute(name, value, (o, v) -> o + v);
		if (removeIfZero && (Integer)getAttributeVal(name) == 0)
			removeAttribute(name);
	}
	/**
	 * If the attribute currently exists, multiplies the provided value by the given attribute.
	 * @param name the name of the attribute
	 * @param multiplicationVal the amount to multiply by
	 * @param removeIfZero remove the attribute if value is zero
	 */
	public final void multiplyIntegerAttribute(final String name, final int multiplicationVal, final boolean removeIfZero) {
		boolean modified = false;
		if (!Attribute.isValidAttribute(name))
			throw new IllegalArgumentException("Illegel attribute name " + name);
		if (containsAttribute(name)) {
			setAttributeVal(name, ((Integer) getAttributeVal(name))*multiplicationVal);
			modified = true;
		}
		if (modified && removeIfZero && (Integer)getAttributeVal(name) == 0)
			removeAttribute(name);
	}
	/**
	 * If the attribute currently exists, performs integer division to the effect of: newAttributeVal = (currentAttributeVal)/(divideVal)
	 * @param name the name of the attribute
	 * @param multiplicationVal the amount to multiply by
	 * @param removeIfZero remove the attribute if value is zero
	 */
	public final void divideIntegerAttribute(final String name, final int divideVal, final boolean removeIfZero) {
		boolean modified = false;
		if (!Attribute.isValidAttribute(name))
			throw new IllegalArgumentException("Illegel attribute name " + name);
		if (containsAttribute(name)) {
			setAttributeVal(name, ((Integer) getAttributeVal(name))/divideVal);
			modified = true;
		}
		if (modified && removeIfZero && (Integer)getAttributeVal(name) == 0)
			removeAttribute(name);
	}
	/**
	 * if the given attribute is present, apply the remapping function func(oldVal, value). otherwise create a new attribute set to
	 * value
	 * @param <T>
	 * @param name the name of the attribute
	 * @param value New Value
	 * @param func the remapping function
	 */
	public final <T> void mergeAttribute(final String name, final T value, final BiFunction<? super T,? super T,? extends T> func) {
		if (containsAttribute(name))
			setAttributeVal(name, func.apply(((T) getAttributeVal(name)), value));
		else
			addAttribute(Attribute.generateAttribute(name, value.toString()));
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
		return name + (containsAttribute("description") ? ":\n" + getAttributeVal("description").toString() : "");
	}
	/**
	 * Update the description attribute of this thing to account for any changes that may have happened
	 */
	public void updateDescription() {
		if (!containsAttribute("description"))
			return;
		final String description = generateDescription();
		attributeNameMap.get("description").setValue(description);
	}
	private String generateDescription() {
		final StringBuilder description = new StringBuilder();
		final TreeMap<Integer, String> orderedDisplays = new TreeMap<Integer, String>();
		for (final Attribute at: getAttributesOfType(AttributeType.DISPLAYTYPE)) {

			orderedDisplays.put(at.getDisplayOrderVal(), at.toString());
		}
		boolean firstTime = true;
		int j = 0;
		for (final Integer i : orderedDisplays.keySet()) {
			j++;
			if (firstTime) {
				if (j != orderedDisplays.keySet().size())
					description.append(orderedDisplays.get(i) + "\n");
				else
					description.append(orderedDisplays.get(i));
				firstTime = false;
			} else {
				if (j != orderedDisplays.keySet().size())
					description.append(orderedDisplays.get(i).toString() + "\n");
				else
					description.append(orderedDisplays.get(i).toString());
			}

		}
		return description.toString();
	}
	@Override
	public String getImage() {
		return image;
	}
	public enum ThingType {
		POKEMON, ITEM;
	}
	public void addToEventList(final Collection<Event> events) {
		if (events == null)
			return;
		events.forEach(e -> {
			eventList.add(e);
		});
	}
	public void addToEventList(final Event e) {
		addToEventList(Arrays.asList(e));
	}

	
}
