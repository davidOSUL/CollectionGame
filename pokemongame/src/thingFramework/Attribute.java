package thingFramework;

import java.io.Serializable;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class Attribute implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * Increase in Gold Per Hour
	 */
	private static final Attribute GPH = new Attribute(1, "PokeCash/hour", "gph", ParseType.INTEGER, new Integer(0), AttributeType.DISPLAYTYPE, AttributeType.STATMOD, AttributeType.GOLDMOD).setIgnoreValAndReturn(new Integer(0)); 
	/**
	 *Increase in Popularity 
	 */
	private static final Attribute POPULARITY_BOOST = new Attribute(3, "Popularity", "popularity boost",ParseType.INTEGER, new Integer(0), AttributeType.DISPLAYTYPE, AttributeType.STATMOD, AttributeType.POPMOD).setIgnoreValAndReturn(new Integer(0)); ;
	
	/**
	 * Increase in Gold Per Minute
	 */
	private static final Attribute GPM = new Attribute(2, "PokeCash/minute", "gpm",ParseType.INTEGER, new Integer(0), AttributeType.DISPLAYTYPE, AttributeType.STATMOD, AttributeType.GOLDMOD).setIgnoreValAndReturn(new Integer(0)); ;
	
	/**
	 * Electric, etc.
	 */
	private static final Attribute TYPES = new Attribute(4, "type", ParseType.ENUMSETPOKEMONTYPE, EnumSet.of(PokemonType.NORMAL), AttributeType.DISPLAYTYPE, AttributeType.CHARACTERISTIC);
	/**
	 * Current Happiness of a pokemon
	 */
	private static final Attribute HAPPINESS = new Attribute(5, "happiness", ParseType.INTEGER, new Integer(0),AttributeType.CHANGINGVAL, AttributeType.DISPLAYTYPE, AttributeType.POKEONLY);
	/**
	 * Current Level of a pokemon
	 */
	private static final Attribute LEVEL = new Attribute(6, "level", ParseType.INTEGER, new Integer(1),AttributeType.CHANGINGVAL, AttributeType.DISPLAYTYPE, AttributeType.POKEONLY);
	/**
	 * The rarity of a pokemon, on scale of 1-99, derived from catchrate. 
	 * higher is more rare
	 */
	private static final Attribute RARITY = new Attribute("rarity", ParseType.INTEGER, new Integer(1),AttributeType.CHARACTERISTIC, AttributeType.POKEONLY);
	/**
	 * The rarity of a pokemon, on scale of 1-10, derived from catchrate. 
	 * higher is more rare. This version of rarity is used for display purposes only
	 */
	private static final Attribute RARITY_OUT_OF_10 = new Attribute(7, "Rarity", "rarity10", ParseType.INTEGER, new Integer(1), AttributeType.CHARACTERISTIC, AttributeType.DISPLAYTYPE, AttributeType.POKEONLY);
	/**
	 * The catch rate of a pokemon on a scale from 3-255
	 */
	private static final Attribute CATCH_RATE = new Attribute("catch rate", ParseType.INTEGER, new Integer(3), AttributeType.CHARACTERISTIC, AttributeType.POKEONLY);
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
	private static final Attribute LEVEL_OF_EVOLUTION = new Attribute("level of evolution", ParseType.INTEGER, new Integer(-1), AttributeType.POKEONLY, AttributeType.CHARACTERISTIC);
	private Object value = null;
	static int currId = 0;
	private static Map<String, Attribute> idMap;
	private String name;
	private String displayName;
	private AttributeTypeSet atTypes;
	private final Object defaultValue;
	/**
	 * The value at which if the input value to generateAttribute has this value, the attribute class reccomends you ignore the value
	 * (Will not stop you from setting it however)
	 */
	private Object objectToIgnoreValueAt = null;
	private int orderOfDisplay = -1;
	private int id;
	private final ParseType parsetype;
	private Attribute(Attribute at, String value) {
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
		parseAndSetValue(value);
	}
	private Attribute(int orderOfDisplay, String name, ParseType parsetype, Object defaultValue, AttributeType... types) {
		this(orderOfDisplay, name.substring(0, 1).toUpperCase()+name.substring(1), name, parsetype, defaultValue, types);
	}
	private Attribute(int orderOfDisplay, String displayName, String name, ParseType parsetype, Object defaultValue, AttributeType... types) {
		this(orderOfDisplay, displayName, name, currId++, parsetype, defaultValue, types);
	}
	private Attribute(String name, ParseType parsetype, Object defaultValue, AttributeType... types) {
		this(name.substring(0, 1).toUpperCase()+name.substring(1), name, parsetype, defaultValue, types);
	}
	private Attribute(String displayName, String name, ParseType parsetype, Object defaultValue, AttributeType... types) {
		this(-1, displayName, name, currId++, parsetype, defaultValue, types);
	}
	private Attribute(int orderOfDisplay, String displayName, String name, int id, ParseType parsetype, Object defaultValue, AttributeType ...types) {
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
	
	@Override
	public boolean equals(Object o) {
		if (o == null)
			return false;
		if (! (o instanceof Attribute))
			return false;
		return ((Attribute) o).getName().equals(getName()) && ((Attribute) o).getValue().equals(getValue());
	}
	@Override
	public int hashCode() {
		return Objects.hash(getName(), getValue());
	}
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder(displayName);
		sb.append(": ");
		if (parsetype.equals(ParseType.ENUMSETPOKEMONTYPE))
			sb.append( getValue().toString().replace("[", ""));
		else
			sb.append(getValue().toString());
		if (this.getName().equals(RARITY_OUT_OF_10.getName()))
			sb.append("/10");
		return sb.toString();
		
	}
	public static boolean isValidAttribute(String name) {
		return idMap.containsKey(name);
	}
	public static Attribute getAttribute(String name) {
		return isValidAttribute(name) ? idMap.get(name) : null;
	}
	public boolean containsType(AttributeType at) {
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
	public static boolean allDontContainType(Set<Attribute> set, AttributeType at) {
		for (Attribute a: set) {
			if ((a.containsType(at)))
				return false;
		}
		return true;
	}
	/**
	 * @param Set of attributes
	 * @return true if all attributes are valid of a thing of Type POKEMON
	 */
	public static boolean validatePokemon(Set<Attribute> set) {
		return allDontContainType(set, AttributeType.ITEMONLY);
	}
	/**
	 * @param set Set of Attributes
	 * @return true if all the attributes are valid for an Thing of Type ITEM
	 */
	public static boolean validateItem(Set<Attribute> set) {
		return allDontContainType(set, AttributeType.POKEONLY);
	}
	public Object getValue() {
		return value;
	}
	private void setIgnoreVal(Object o) {
		this.objectToIgnoreValueAt = o;
	}
	private Attribute setIgnoreValAndReturn(Object o) {
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
	public void parseAndSetValue(String value) {
		if (value.equals("") || value.equals(" "))
			setValue(defaultValue);
		else {
			switch (parsetype) {
			case INTEGER:
				setValue(Integer.parseInt(value));
				break;
			case DOUBLE:
				setValue(Double.parseDouble(value));
				break;
			case STRING:
				setValue(value);
				break;
			case ENUMSETPOKEMONTYPE:
				String[] types = value.split(" ");
				PokemonType firstType;
				PokemonType[] poketypes = new PokemonType[types.length-1];
				firstType = PokemonType.valueOf(types[0].toUpperCase().trim());
				for (int i = 1; i < types.length; i++) {
					poketypes[i] = PokemonType.valueOf(types[i].toUpperCase().trim());
				}
				setValue(EnumSet.of(firstType, poketypes));
				break;
			case EXPERIENCEGROUP:
				setValue(ExperienceGroup.valueOf(value.toUpperCase().replaceAll("\\s", "")));
				break;
			case BOOLEAN:
				setValue(Boolean.parseBoolean(value));
				break;
			case LISTSTRING:
				if (value.startsWith("["))
				setValue(Arrays.asList(value.substring(1, value.length()-1).split("\\s*,\\s*")));
				else
				setValue(Arrays.asList(value));
				break;
			case POKEMON:
				throw new Error("POKEMON SHOULD NOT BE PARSED");
				
			}
		}
	}
	public void setValue(Object value) {
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
	public static Attribute[] generateAttributes(String[] names, String[] values) {
		if (names.length != values.length)
			throw new Error("names and values must have same length");
		Attribute[] attributes = new Attribute[names.length];
		for (int i = 0; i < names.length; i++) {
			attributes[i] = generateAttribute(names[i], values[i]);
		}
		return attributes;
	}
	public boolean shouldIgnore() {
		return getValue().equals(getIgnoreVal());
	}
	public static  Attribute generateAttribute(String name, String value) {
		if (idMap.get(name) == null)
			throw new Error("INVALID ATTRIBUTE: " + name);
		return new Attribute(idMap.get(name), value);
	}
	public static Attribute generateAttribute(String name) {
		return generateAttribute(name, "");
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
