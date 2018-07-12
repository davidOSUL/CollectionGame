package thingFramework;

import java.io.Serializable;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import gameutils.GameUtils;

public class Attribute implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * Nothing to do with actual attributes, just a general overview of this Thing
	 */
	private static final Attribute FLAVOR_DESCRIPTION = new Attribute(1, "Info", "flavor description",  ParseType.STRING, new String(""), AttributeType.DISPLAYTYPE);
	/**
	 * Increase in Gold Per Hour
	 */
	private static final Attribute GPH = new Attribute(2, "PokeCash/hour", "gph", ParseType.INTEGER, new Integer(0), AttributeType.DISPLAYTYPE, AttributeType.STATMOD, AttributeType.GOLDMOD).setIgnoreValAndReturn(new Integer(0)); 
	/**
	 * Increase in Gold Per Minute
	 */
	private static final Attribute GPM = new Attribute(3, "PokeCash/minute", "gpm",ParseType.INTEGER, new Integer(0), AttributeType.DISPLAYTYPE, AttributeType.STATMOD, AttributeType.GOLDMOD).setIgnoreValAndReturn(new Integer(0)); ;
	/**
	 *Increase in Popularity 
	 */
	private static final Attribute POPULARITY_BOOST = new Attribute(4, "Popularity", "popularity boost",ParseType.INTEGER, new Integer(0), AttributeType.DISPLAYTYPE, AttributeType.STATMOD, AttributeType.POPMOD).setIgnoreValAndReturn(new Integer(0)); ;
	/**
	 * Electric, etc.
	 */
	private static final Attribute TYPES = new Attribute(5, "Types", "type", ParseType.ENUMSETPOKEMONTYPE, EnumSet.of(PokemonType.NORMAL), AttributeType.DISPLAYTYPE, AttributeType.CHARACTERISTIC);
	/**
	 * Current Happiness of a pokemon (/10)
	 */
	private static final Attribute HAPPINESS = new Attribute(6, "happiness", ParseType.INTEGER, new Integer(0),AttributeType.CHANGINGVAL, AttributeType.DISPLAYTYPE, AttributeType.POKEONLY, AttributeType.OUTOFTEN);
	/**
	 * Current Level of a pokemon
	 */
	private static final Attribute LEVEL = new Attribute(7, "level", ParseType.INTEGER, new Integer(1),AttributeType.CHANGINGVAL, AttributeType.DISPLAYTYPE, AttributeType.POKEONLY);
	/**
	 * The rarity of a pokemon, on scale of 1-10, derived from catchrate. 
	 * higher is more rare. This version of rarity is used for display purposes only
	 */
	private static final Attribute RARITY_OUT_OF_10 = new Attribute(8, "Rarity", "rarity10", ParseType.INTEGER, new Integer(1), AttributeType.CHARACTERISTIC, AttributeType.DISPLAYTYPE, AttributeType.POKEONLY, AttributeType.OUTOFTEN);
	/**
	 * Whether or not this pokemon is legendary
	 */
	private static final Attribute IS_LEGENDARY = new Attribute(9, "legendary", "legendary", ParseType.BOOLEAN, Boolean.FALSE, AttributeType.POKEONLY, AttributeType.CHARACTERISTIC, AttributeType.DISPLAYTYPE);
	/**
	 * A verbal description of the events asssociated with this thing
	 */
	private static final Attribute EVENT_DESCRIPTION = new Attribute(10, "", "event description", ParseType.STRING, new String(""), AttributeType.DISPLAYTYPE);
	/**
	 * Any other extra description to put after every other displaytype
	 */
	private static final Attribute ADDITIONAL_DESCRIPTION = new Attribute(Integer.MAX_VALUE, "", "additional description", ParseType.STRING, new String(""), AttributeType.DISPLAYTYPE);
	/**
	 * If this thing only exists for a certain amount of time, how much time it exists for
	 */
	private static final Attribute TIME_LEFT = new Attribute(11, "Time left", "time left", ParseType.STRING, new String("Infinite"), AttributeType.DISPLAYTYPE);
	/**
	 * The catch rate of a pokemon on a scale from 3-255
	 */
	private static final Attribute CATCH_RATE = new Attribute("catch rate", ParseType.INTEGER, new Integer(3), AttributeType.CHARACTERISTIC, AttributeType.POKEONLY);
	/**
	 * The rarity of a pokemon, on scale of 1-99, derived from catchrate. 
	 * higher is more rare
	 */
	private static final Attribute RARITY = new Attribute("rarity", ParseType.INTEGER, new Integer(1),AttributeType.CHARACTERISTIC, AttributeType.POKEONLY);
	/**
	 * The description of an item. Note it doesn't have an orderdisplayvalue because the description is just a combination of all those elements that do
	 * (and some other text potentially)
	 */
	private static final Attribute DESCRIPTION = new Attribute("description", ParseType.STRING, new String(""), AttributeType.CHARACTERISTIC);
	/**
	 * The experience group (fast, slow, erratic, etc.) to which this pokemon belongs to.
	 */
	private static final Attribute EXPERIENCE_GROUP = new Attribute("experience group", ParseType.EXPERIENCEGROUP, ExperienceGroup.SLOW,  AttributeType.POKEONLY, AttributeType.CHARACTERISTIC);
	/**
	 * Whether or not this pokemon has both A. an evolution and B. evolves via levels
	 */
	private static final Attribute HAS_EVOLUTION = new Attribute("has evolution", ParseType.BOOLEAN, new Boolean(false), AttributeType.POKEONLY, AttributeType.CHARACTERISTIC);
	/**
	 * the next evolution for this pokemon
	 */
	private static final Attribute NEXT_EVOLUTIONS = new Attribute("next evolutions", ParseType.LISTSTRING, Arrays.asList("default"), AttributeType.POKEONLY, AttributeType.CHARACTERISTIC);
	/**
	 * the level this pokemon evolves at
	 */
	private static final Attribute LEVEL_OF_EVOLUTION = new Attribute("level of evolution", ParseType.INTEGER, new Integer(-1), AttributeType.POKEONLY, AttributeType.CHARACTERISTIC);
	/**
	 * Whether or not this thing can be removed from the board
	 */
	private static final Attribute REMOVABLE = new Attribute("removable", ParseType.BOOLEAN, new Boolean(true));

	private Object value = null;
	static int currId = 0;
	private static Map<String, Attribute> idMap;
	private final String name;
	private final String displayName;
	private final AttributeTypeSet atTypes;
	private final Object defaultValue;
	/**
	 * The value at which if the input value to generateAttribute has this value, the attribute class reccomends you ignore the value
	 * (Will not stop you from setting it however)
	 */
	private Object objectToIgnoreValueAt = null;
	private int orderOfDisplay = -1;
	private final int id;
	private final ParseType parsetype;
	/**
	 * An extra string that can be added on at the end of this toString's method
	 */
	private String extraDescription = "";
	private Attribute(final Attribute at) {
		if (!idMap.containsValue(at))
			throw new Error("INVALID ATTRIBUTE: " + at);
		this.name = at.name;
		this.displayName = at.displayName;
		this.atTypes = at.atTypes;
		this.id = at.id;
		this.orderOfDisplay = at.orderOfDisplay;
		this.parsetype = at.parsetype;
		this.defaultValue = at.defaultValue;
		this.objectToIgnoreValueAt = at.objectToIgnoreValueAt;
	}
	private Attribute(final Attribute at, final String value) {
		this(at);
		parseAndSetValue(value);
	}
	private Attribute(final int orderOfDisplay, final String name, final ParseType parsetype, final Object defaultValue, final AttributeType... types) {
		this(orderOfDisplay, name.substring(0, 1).toUpperCase()+name.substring(1), name, parsetype, defaultValue, types);
	}
	private Attribute(final int orderOfDisplay, final String displayName, final String name, final ParseType parsetype, final Object defaultValue, final AttributeType... types) {
		this(orderOfDisplay, displayName, name, currId++, parsetype, defaultValue, types);
	}
	private Attribute(final String name, final ParseType parsetype, final Object defaultValue, final AttributeType... types) {
		this(name.substring(0, 1).toUpperCase()+name.substring(1), name, parsetype, defaultValue, types);
	}
	private Attribute(final String displayName, final String name, final ParseType parsetype, final Object defaultValue, final AttributeType... types) {
		this(-1, displayName, name, currId++, parsetype, defaultValue, types);
	}
	private Attribute(final int orderOfDisplay, final String displayName, final String name, final int id, final ParseType parsetype, final Object defaultValue, final AttributeType ...types) {
		this.orderOfDisplay = orderOfDisplay;
		this.name = name;
		this.id = id;
		this.parsetype = parsetype;
		this.displayName = displayName;
		if (!defaultValue.getClass().equals(getParseClass()))
			throw new Error("Attribute " + getName() + "'s defaultValue must be a: " + getParseClass().getName());
		if (parsetype.equals(ParseType.ENUMSETPOKEMONTYPE)) {
			if (!(((EnumSet<?>) defaultValue).iterator().next() instanceof PokemonType))
				throw new Error("default value for " + getName() + " must be a pokemontype enum set");
		}
		

		
		this.defaultValue = defaultValue;
		atTypes = new AttributeTypeSet(types);
		if (orderOfDisplay == 0 || (orderOfDisplay < 0 && atTypes.containsAttributeType(AttributeType.DISPLAYTYPE)) || (orderOfDisplay > 0 && !atTypes.containsAttributeType(AttributeType.DISPLAYTYPE)))
			throw new Error("order of display should not be present for: " + getName());
		getIdMap().put(name, this);
	}
	
	private Map<String, Attribute> getIdMap() {
		if (idMap == null)
			idMap = new HashMap<String, Attribute>();
		return idMap;
	}
	public String getName() {
		return name;
	}
	
	/*@Override
	public boolean equals(final Object o) {
		if (o == null)
			return false;
		if (! (o instanceof Attribute))
			return false;
		return ((Attribute) o).getName().equals(getName()) && ((Attribute) o).getValue().equals(getValue());
	}
	@Override
	public int hashCode() {
		return Objects.hash(getName(), getValue());
	}*/
	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder(displayName);
		if (!displayName.isEmpty())
			sb.append(": ");
		if (parsetype.equals(ParseType.ENUMSETPOKEMONTYPE))
			sb.append( GameUtils.toTitleCase(getValue().toString().replace("[", "").replace("]", "").toLowerCase()));
		else if (parsetype.equals(ParseType.BOOLEAN))
			sb.append(getValue().toString().equalsIgnoreCase("true") ? "yes" : "no");
		else
			sb.append(getValue().toString());
		if (this.containsType(AttributeType.OUTOFTEN))
			sb.append("/10");
		sb.append(extraDescription);
		return sb.toString();
		
	}
	/**
	 * Sets the extra description for this attribute, which is a string that is always added after the
	 * default toString
	 * @param extraDescription
	 */
	public void setExtraDescription(final String extraDescription) {
		this.extraDescription = extraDescription;
	}
	/**
	 * @return this attribute with the value displayed followed by the name
	 */
	public String toReverseString() {
		final StringBuilder sb = new StringBuilder();
		if (parsetype.equals(ParseType.ENUMSETPOKEMONTYPE))
			sb.append( GameUtils.toTitleCase(getValue().toString().replace("[", "").replace("]", "").toLowerCase()));
		else if (parsetype.equals(ParseType.BOOLEAN))
			sb.append(getValue().toString().equalsIgnoreCase("true") ? "yes" : "no");
		else
			sb.append(getValue().toString());
		if (this.containsType(AttributeType.OUTOFTEN))
			sb.append("/10");
		sb.append(" ");
		sb.append(displayName);
		return sb.toString();
	}
	public static boolean isValidAttribute(final String name) {
		return idMap.containsKey(name);
	}
	public static Attribute getAttribute(final String name) {
		return isValidAttribute(name) ? idMap.get(name) : null;
	}
	public boolean containsType(final AttributeType at) {
		return atTypes.containsAttributeType(at);
	}
	public boolean pokeOnly() {
		return containsType(AttributeType.POKEONLY);
	}
	public boolean itemOnly() {
		return containsType(AttributeType.ITEMONLY);
	}
	public Class<?> getParseClass() {
		switch (parsetype) {
		case INTEGER:
			return Integer.class;
			
		case DOUBLE:
			return Double.class;
			
		case STRING:
			return String.class;
			
		case ENUMSETPOKEMONTYPE:
			return EnumSet.of(PokemonType.BUG).getClass();
			
		case EXPERIENCEGROUP:
			return ExperienceGroup.class;
		case BOOLEAN:
			return Boolean.class;
		case POKEMON:
			return Pokemon.class;
		case LISTSTRING:
			return Arrays.asList("default").getClass();
			
		}
		return Object.class;
	}
	public static boolean allDontContainType(final Set<Attribute> set, final AttributeType at) {
		for (final Attribute a: set) {
			if ((a.containsType(at)))
				return false;
		}
		return true;
	}
	/**
	 * @param Set of attributes
	 * @return true if all attributes are valid of a thing of Type POKEMON
	 */
	public static boolean validatePokemon(final Set<Attribute> set) {
		return allDontContainType(set, AttributeType.ITEMONLY);
	}
	/**
	 * @param set Set of Attributes
	 * @return true if all the attributes are valid for an Thing of Type ITEM
	 */
	public static boolean validateItem(final Set<Attribute> set) {
		return allDontContainType(set, AttributeType.POKEONLY);
	}
	public Object getValue() {
		return value;
	}
	private void setIgnoreVal(final Object o) {
		this.objectToIgnoreValueAt = o;
	}
	private Attribute setIgnoreValAndReturn(final Object o) {
		this.setIgnoreVal(o);
		return this;
	}
	public Object getIgnoreVal() {
		return objectToIgnoreValueAt;
	}
	public int getDisplayOrderVal(){
		if (!this.containsType(AttributeType.DISPLAYTYPE))
			throw new Error("ATTRIBUTE SHOULD NOT BE DISPLAYED");
		return orderOfDisplay;
	}
	public Object parseValue(final String value) {
		Object newVal = null;
		if (value.equals("") || value.equals(" "))
			newVal = defaultValue;
		else {
			switch (parsetype) {
			case INTEGER:
				newVal = Integer.parseInt(value);
				break;
			case DOUBLE:
				newVal = Double.parseDouble(value);
				break;
			case STRING:
				newVal = value;
				break;
			case ENUMSETPOKEMONTYPE:
				final String[] types = value.split(" ");
				PokemonType firstType;
				final PokemonType[] poketypes = new PokemonType[types.length-1];
				firstType = PokemonType.valueOf(types[0].toUpperCase().trim());
				for (int i = 1; i < types.length; i++) {
					poketypes[i-1] = PokemonType.valueOf(types[i].toUpperCase().trim());
					
				}
				newVal = EnumSet.of(firstType, poketypes);
				break;
			case EXPERIENCEGROUP:
				newVal = ExperienceGroup.valueOf(value.toUpperCase().replaceAll("\\s", ""));
				break;
			case BOOLEAN:
				newVal = Boolean.parseBoolean(value);
				break;
			case LISTSTRING:
				if (value.startsWith("["))
				newVal = Arrays.asList(value.substring(1, value.length()-1).split("\\s*,\\s*"));
				else
				newVal = Arrays.asList(value);
				break;
			case POKEMON:
				throw new Error("POKEMON SHOULD NOT BE PARSED");
				
			}
		}
		return newVal;
	}
	public void parseAndSetValue(final String value) {
		setValue(parseValue(value));
	}

	public void setValue(final Object value) {
		if (!value.getClass().equals(getParseClass()))
			throw new Error("Attribute " + getName() + "'s value must be a: " + getParseClass().getName());
		if (parsetype.equals(ParseType.ENUMSETPOKEMONTYPE)) {
			if (!(((EnumSet<?>) value).iterator().next() instanceof PokemonType))
				throw new Error("Attribute for " + getName() + " must be a pokemontype enum set");
		}
		this.value = value;
	}
	public boolean hasValue() {
		return this.value != null;
	}
	/**
	 * @param names name1, name2, ...
	 * @param values valueForName1, valueForName2, ...
	 * @return attribute1, attribute2,...
	 */
	public static Attribute[] generateAttributes(final String[] names, final String[] values) {
		if (names.length != values.length)
			throw new Error("names and values must have same length");
		final Attribute[] attributes = new Attribute[names.length];
		for (int i = 0; i < names.length; i++) {
			attributes[i] = generateAttribute(names[i], values[i]);
		}
		return attributes;
	}
	public boolean shouldIgnore() {
		return getValue().equals(getIgnoreVal());
	}
	public static  Attribute generateAttribute(final String name, final String value) {
		if (idMap.get(name) == null)
			throw new Error("INVALID ATTRIBUTE: " + name);
		return new Attribute(idMap.get(name), value);
	}
	public static Attribute generateAttributeWithValue(final String name, final Object value) {
		final Attribute at = new Attribute(idMap.get(name));
		at.setValue(value);
		return at;
	}
	public static Attribute generateAttribute(final Attribute at) {
		return generateAttributeWithValue(at.getName(), at.getValue());
	}
	public static Attribute generateAttribute(final String name) {
		return generateAttribute(name, "");
	}
	public boolean valEqualsParse(final String input) {
		final Object value = parseValue(input);
		return getValue().equals(value);
	}
	private enum ParseType {
		INTEGER, DOUBLE, STRING, ENUMSETPOKEMONTYPE, EXPERIENCEGROUP, BOOLEAN, POKEMON, LISTSTRING
	}
	/*
	 * switch (parsetype) {
		case INTEGER:
			if (!(value instanceof Integer))  throw new Error("Attribute " + getName() + "'s value must be an integer");
			break;
		case DOUBLE:
			if (!(value instanceof Double)) throw new Error("Attribute " + getName() + "'s value must be a double");
			break;
		case STRING:
			if (!(value instanceof String)) throw new Error("Attribute " + getName() + "'s value must be a String");
			break;
		case ENUMSETPOKEMONTYPE:
			if (!(value instanceof EnumSet<?>) || !(((EnumSet<?>) value).iterator().next() instanceof PokemonType)) throw new Error("Attribute " + getName() + "'s value must be a enumset");
			break;
		case EXPERIENCEGROUP:
			if (!(value instanceof ExperienceGroup)) throw new Error("Attribute " + getName() + "'s value must be a experience group variable");
			break;
		}
		
		switch (parsetype) {
			case INTEGER:
				if (!(defaultValue instanceof Integer))
					throw new Error("INVALID DEFAULT VAL");
				break;
			case DOUBLE:
				if (!(defaultValue instanceof Double))
					throw new Error("INVALID DEFAULT VAL");
				break;
			case STRING:
				if (!(defaultValue instanceof String))
					throw new Error("INVALID DEFAULT VAL");
				break;
			case ENUMSETPOKEMONTYPE:
				if (!(defaultValue instanceof EnumSet<?>) || !(((EnumSet<?>) defaultValue).iterator().next() instanceof PokemonType))
					throw new Error("INVALID DEFAULT VAL");
				break;
			case EXPERIENCEGROUP:
				if (!(defaultValue instanceof ExperienceGroup))
					throw new Error("INVALID DEFAULT VAL");
				break;
		}
	 */
}
