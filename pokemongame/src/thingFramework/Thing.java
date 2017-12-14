package thingFramework;

import java.io.Serializable;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public abstract class Thing implements Serializable {
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
	public Thing(String name, String image, Set<Attribute> attributes) {
		if (!vallidateAttributes(attributes))
			throw new Error("INVALID ATTRIBUTE FOR: " + name);
		types = setThingType();
		this.attributes = attributes;
		boardAttributes = getBoardAttributes();
		this.name = name ;
		this.image = image;
		attributeNameMap = generateAttributeNameMap(attributes);
	}
	public boolean containsAttribute(String name) {
		return attributeNameMap.containsKey(name);
	}
	private static Map<String, Attribute> generateAttributeNameMap(Set<Attribute> attributes){
		Map<String, Attribute> attributeNameMap = new HashMap<String, Attribute>();
		for (Attribute at: attributes) {
			attributeNameMap.put(at.getName(), at);
		}
		return attributeNameMap;
	}
	private Set<Attribute> getAttributesThatContainType(AttributeType at) {
		Set<Attribute> validAttributes = new HashSet<Attribute>();
		for (Attribute attribute: attributes) {
			if (attribute.containsType(at))
				validAttributes.add(attribute);
		}
		return validAttributes;
	}
	public Set<Attribute> getBoardAttributes() {
		if (boardAttributes == null) {
			return getAttributesThatContainType(BOARDTYPE);
		}
		return boardAttributes;
	}
	private boolean vallidateAttribute(Attribute at) {
		return vallidateAttributes(new HashSet<Attribute>(Arrays.asList(at)));
	}
	abstract boolean vallidateAttributes(Set<Attribute> attributes);
	abstract EnumSet<ThingType> setThingType();
	public EnumSet<ThingType> getThingTypes() {
		return types;
	}
	public Object getAttributeVal(String name) throws AttributeNotFoundException {
		if (containsAttribute(name))
			return attributeNameMap.get(name).getValue();
		else
			throw new AttributeNotFoundException("ATTRIBUTE NOT FOUND");
	}
	public void addAttribute(Attribute at) {
		if (!vallidateAttribute(at))
			throw new Error("INVALID ATTRIBUTE FOR: " + name);
		if (attributes.contains(at) || attributeNameMap.keySet().contains(at.getName()))
			throw new Error("ATTRIBUTE " + at.getName() + " ALREADY EXISTS FOR: " + name);
		else {
			attributes.add(at);
			attributeNameMap.put(at.getName(), at);
		}
	}
	public void setAttributeVal(String name, Object value) throws AttributeNotFoundException {
		if (containsAttribute(name))
			attributeNameMap.get(name).setValue(value);
		else
			throw new AttributeNotFoundException("ATTRIBUTE NOT FOUND");
	}
	@Override
	public String toString() {
		return name;
	}
	 public enum ThingType {
		POKEMON, ITEM, EVENTFULITEM;
	}
}
