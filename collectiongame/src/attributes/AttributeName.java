package attributes;

import java.io.ObjectStreamException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import thingFramework.CreatureTypeSet;
import thingFramework.ExperienceGroup;

/**
 * Enumerates the names of all valid attributes. Used to query AttributeManager with compile time checking of proper
 * Object type.
 * @author David O'Sullivan
 * @param <T> the ParseType of the Attribute that this AttributeName is referring to
 *
 */
public final class AttributeName<T> implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * The amount of gold per hour that a Thing produces
	 */
	public static final AttributeName<Integer> GPH = new AttributeName<>("gph", ParseType.INTEGER);
	/**
	 * The "flavor" description of a Thing (i.e. a description relevant to backstory but not to game actions)
	 */
	public static final AttributeName<String> FLAVOR_DESCRIPTION = new AttributeName<>("flavor description", ParseType.STRING);
	/**
	 * The amount of gold per minute that a Thing produces
	 */
	public static final AttributeName<Integer> GPM = new AttributeName<>("gpm", ParseType.INTEGER);
	/**
	 * That amount of popularity that a Thing brings in 
	 */
	public static final AttributeName<Integer> POPULARITY = new AttributeName<>("popularity boost", ParseType.INTEGER);
	/**
	 * The type(s) of a creature enumerated as a CreatureTypeSet.
	 */
	public static final AttributeName<CreatureTypeSet> TYPE = new AttributeName<>("type", ParseType.CREATURE_TYPES);
	/**
	 * The current happiness of a creature
	 */
	public static final AttributeName<Integer> HAPPINESS = new AttributeName<>("happiness", ParseType.INTEGER);
	/**
	 * The current level of a creature
	 */
	public static final AttributeName<Integer> LEVEL = new AttributeName<>("level", ParseType.INTEGER);
	/**
	 * the rarity of a creature normalized to a value out of 10
	 */
	public static final AttributeName<Integer> RARITY10 = new AttributeName<>("rarity10", ParseType.INTEGER);
	/**
	 * Whether or not a creature is legendary
	 */
	public static final AttributeName<Boolean> IS_LEGENDARY = new AttributeName<>("legendary", ParseType.BOOLEAN);
	/**
	 * the description of an event that a thing possesses. 
	 */
	public static final AttributeName<String> EVENT_DESCRIPTION = new AttributeName<>("event description", ParseType.STRING);
	/**
	 * An additional string description line that is placed at the very end of a Thing's description
	 */
	public static final AttributeName<String> ADDITIONAL_DESCRIPTION = new AttributeName<>("additional description", ParseType.STRING);
	/**
	 * The amount of time remaining (usually written as HH:MM) for a Thing before it goes away or stops having a particular effect
	 */
	public static final AttributeName<String> TIME_LEFT = new AttributeName<>("time left", ParseType.STRING);
	/**
	 * The "raw" rarity metric of a creature (this is then normalized to rarity)
	 */
	public static final AttributeName<Integer> CATCH_RATE = new AttributeName<>("catch rate", ParseType.INTEGER);
	/**
	 * The rarity of a creature
	 */
	public static final AttributeName<Integer> RARITY = new AttributeName<>("rarity", ParseType.INTEGER);
	/**
	 * The experience group that a creature is in
	 */
	public static final AttributeName<ExperienceGroup> EXPERIENCE_GROUP = new AttributeName<>("experience group", ParseType.EXPERIENCE_GROUP);
	/**
	 * Whether or not this creature possesses an evolution
	 */
	public static final AttributeName<Boolean> HAS_EVOLUTION = new AttributeName<>("has evolution", ParseType.BOOLEAN);
	/**
	 * The level that a creature will evolve at
	 */
	public static final AttributeName<Integer> LEVEL_OF_EVOLUTION = new AttributeName<>("level of evolution", ParseType.INTEGER);
	/**
	 * Whether or not a thing can be removed
	 */
	public static final AttributeName<Boolean> REMOVABLE = new AttributeName<>("removable", ParseType.BOOLEAN);
	/**
	 * The list of potential evolutions for a creature
	 */
	public static final AttributeName<List<?>> NEXT_EVOLUTIONS = new AttributeName<>("next evolutions", ParseType.LIST);
	private final String name;
	private transient final ParseType<T> parseType;
	/**
	 * A map from the string name of all valid attributes to the parse type
	 */
	private static Map<String, ParseType<?>> validAttributes;
	private static Map<String, AttributeName<?>> attributeNameMap;
	private AttributeName(final String name, final ParseType<T> parseType) {
		this.name = name;
		this.parseType = parseType;
		getValidAttributes().put(name, parseType);
		getAttributeNameMap().put(name, this);
	}
	private static Map<String, ParseType<?>> getValidAttributes() {
		if (validAttributes == null)
			validAttributes = new HashMap<String, ParseType<?>>();
		return validAttributes;
	}
	private static Map<String, AttributeName<?>> getAttributeNameMap() {
		if (attributeNameMap == null)
			attributeNameMap = new HashMap<String, AttributeName<?>>();
		return attributeNameMap;
	}
	/**
	 * @return the string representation of this AttributeName 
	 */
	String getName() {
		return name;
	}
	/**
	 * @return the associated type of this AttributeName
	 */
	ParseType<T> getType() {
		return parseType;
	}
	/**
	 * Returns true if the provided name and parseType correspond to a defined AttributeName
	 * @param name the name of the Attribute
	 * @param type the type of the attribute
	 * @return  true if the provided name and parseType correspond to a defined AttributeName
	 */
	static boolean isValidAttribute(final String name, final ParseType<?> type) {
		return validAttributes.containsKey(name) && validAttributes.get(name).equals(type);
	}
	/**
	 * Returns the AttributeName<?> with the provided String name
	 * @param name the name of the AttributeName<?>
	 * @return the AttributeName<?> with the provided String name
	 */
	public static AttributeName<?> getAttributeName(final String name) {
		if (!attributeNameMap.containsKey(name))
			throw new AttributeNotFoundException(name + " is not a valid AttributeName");
		return attributeNameMap.get(name);
	}
	/**
	 * Returns the AttributeName<T> with the provided String name
	 * @param <T> the type of the AttributeName
	 * @param name the name of the AttributeName<T>
	 * @param type the ParseType<T> associated with the AttributeName<T>
	 * @return the AttributeName<T> with the provided String name
	 */
	@SuppressWarnings("unchecked")
	public static <T> AttributeName<T> getAttributeName(final String name, final ParseType<T> type) {
		if (!getAttributeNameMap().containsKey(name))
			throw new AttributeNotFoundException(name + " is not a valid AttributeName");
		if (!getAttributeNameMap().get(name).parseType.equals(type))
			throw new IllegalArgumentException("invalid type " + type + " for AttributeName " + name);
		return (AttributeName<T>) attributeNameMap.get(name); //this is a provably safe cast at this point
		
	}
	/** 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return getName();
	}
	/** 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return getName().hashCode();
	}
	/** 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(final Object obj) {
		if (!(obj instanceof AttributeName<?>))
			return false;
		return ((AttributeName<?>)obj).getName().equals(getName());
	}
	private Object readResolve() throws ObjectStreamException {
	     return getAttributeNameMap().get(getName());
	}

}
