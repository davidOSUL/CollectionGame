package thingFramework;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import effects.Event;
import effects.Eventful;
import game.Board;
import game.BoardAttributeManager;
import gameutils.GameUtils;
import interfaces.Imagable;

public abstract class Thing implements Serializable, Eventful, Imagable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final String name;
	private final String image;
	private final static AttributeType BOARDTYPE = AttributeType.STATMOD;
	private final Set<Attribute> boardAttributes;
	private final EnumSet<ThingType> types;
	private final Set<Attribute> attributes; 
	private final Map<String, Attribute> attributeNameMap;
	private final List<Event> eventList= new ArrayList<Event>();
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
			eventList.addAll(GameUtils.toArrayList(events));
	}
	public Thing(final String name, final String image, final Set<Attribute> attributes) {
		if (!vallidateAttributes(attributes))
			throw new Error("INVALID ATTRIBUTE FOR: " + name);
		types = setThingType();
		this.attributes = attributes;
		boardAttributes = getBoardAttributes();
		this.name = name ;
		this.image = image;
		attributeNameMap = generateAttributeNameMap(attributes);
		eventList.addAll(BoardAttributeManager.getEvents(getBoardAttributes()));
	}
	public Thing(final String name, final String image, final Set<Attribute> attributes, final Event...events ) {
		this(name, image, attributes, GameUtils.toArrayList(events));
	}
	public Thing(final String name, final String image, final Set<Attribute> attributes, final List<Event> events) {
		this(name, image, attributes);
		if (events != null)
			eventList.addAll(events);
	}
	@Override
	public List<Event> getEvents() {
		return eventList;
	}
	public abstract Thing makeCopy();
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
	public final Set<Attribute> getBoardAttributes() {
		if (boardAttributes == null) {
			return getAttributesThatContainType(BOARDTYPE);
		}
		return boardAttributes;
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
	public final Object getAttributeVal(final String name) throws AttributeNotFoundException {
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
		else {
			if (!at.shouldIgnore()) {
			attributes.add(at);
			attributeNameMap.put(at.getName(), at);
			if (at.containsType(BOARDTYPE))
				boardAttributes.add(at);
			}
		}
	}
	public final void setAttributeVal(final String name, final Object value) {
		if (containsAttribute(name))
			attributeNameMap.get(name).setValue(value);
		else
			throw new AttributeNotFoundException("ATTRIBUTE NOT FOUND");
	}
	public final List<Attribute> getAttributesOfType(final AttributeType at) {
		final List<Attribute> returnAttributes = new ArrayList<Attribute>();
		for (final Attribute a: attributes) {
			if (a.containsType(at))
				returnAttributes.add(a);
		}
		return returnAttributes;
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
		return name + (containsAttribute("description") ? ": " + getAttributeVal("description").toString() : "");
	}
	 @Override
	public String getImage() {
		return image;
	}
	public enum ThingType {
		POKEMON, ITEM;
	}
}
