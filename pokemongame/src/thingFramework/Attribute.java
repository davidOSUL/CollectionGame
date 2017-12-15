package thingFramework;

import java.io.Serializable; 
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
	private static final Attribute GPH = new Attribute("gph", ParseType.INTEGER, new Integer(0), AttributeType.STATMOD, AttributeType.GOLDMOD); 
	/**
	 *Increase in Popularity 
	 */
	private static final Attribute POPULARITY_BOOST = new Attribute("popularity boost",ParseType.INTEGER, new Integer(0), AttributeType.STATMOD, AttributeType.POPMOD);
	
	/**
	 * Increase in Gold Per Minute
	 */
	private static final Attribute GPM = new Attribute("gpm",ParseType.INTEGER, new Integer(0),AttributeType.STATMOD, AttributeType.GOLDMOD);
	
	/**
	 * Electric, etc.
	 */
	private static final Attribute TYPES = new Attribute("type", ParseType.ENUMSETPOKEMONTYPE, EnumSet.of(PokemonType.NORMAL), AttributeType.CHARACTERISTIC);
	/**
	 * Current Happiness of a pokemon
	 */
	private static final Attribute HAPPINESS = new Attribute("happiness", ParseType.INTEGER, new Integer(0),AttributeType.CHANGINGVAL, AttributeType.POKEONLY);
	/**
	 * Current Level of a pokemon
	 */
	private static final Attribute LEVEL = new Attribute("level", ParseType.INTEGER, new Integer(1),AttributeType.CHANGINGVAL, AttributeType.POKEONLY);
	/**
	 * The rarity of a pokemon, on scale of 1-99, derived from catchrate. 
	 * higher is more rare
	 */
	private static final Attribute RARITY = new Attribute("rarity", ParseType.INTEGER, new Integer(1),AttributeType.CHARACTERISTIC, AttributeType.POKEONLY);
	
	/**
	 * The catch rate of a pokemon on a scale from 3-255
	 */
	private static final Attribute CATCH_RATE = new Attribute("catch rate", ParseType.INTEGER, new Integer(3), AttributeType.CHARACTERISTIC, AttributeType.POKEONLY);
	/**
	 * The description of an item
	 */
	private static final Attribute DESCRIPTION = new Attribute("description", ParseType.STRING, new String(""), AttributeType.CHARACTERISTIC);
	/**
	 * The experience group (fast, slow, erratic, etc.) to which this pokemon belongs to.
	 */
	private static final Attribute EXPERIENCE_GROUP = new Attribute("experience group", ParseType.EXPERIENCEGROUP, ExperienceGroup.SLOW,  AttributeType.POKEONLY, AttributeType.CHARACTERISTIC);
	private Object value = null;
	static int currId = 0;
	private static Map<String, Attribute> idMap;
	private String name;
	private AttributeTypeSet atTypes;
	private final Object defaultValue;
	private int id;
	private final ParseType parsetype;
	private Attribute(Attribute at, String value) {
		if (!idMap.containsValue(at))
			throw new Error("INVALID ATTRIBUTE: " + at);
		this.name = at.name;
		this.atTypes = at.atTypes;
		this.id = at.id;
		this.parsetype = at.parsetype;
		this.defaultValue = at.defaultValue;
		parseAndSetValue(value);
	}
	private Attribute(String name, ParseType parsetype, Object defaultValue, AttributeType... types) {
		this(name, currId++, parsetype, defaultValue, types);
	}
	private Attribute(String name, int id, ParseType parsetype, Object defaultValue, AttributeType ...types) {
		this.name = name;
		this.id = id;
		this.parsetype = parsetype;
		if (!defaultValue.getClass().equals(getParseClass()))
			throw new Error("Attribute " + getName() + "'s defaultValue must be a: " + getParseClass().getName());
		if (parsetype.equals(ParseType.ENUMSETPOKEMONTYPE)) {
			if (!(((EnumSet<?>) defaultValue).iterator().next() instanceof PokemonType))
				throw new Error("default value for " + getName() + " must be a pokemontype enum set");
		}

		
		this.defaultValue = defaultValue;
		atTypes = new AttributeTypeSet(types);
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
		return getName();
		
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
				setValue(ExperienceGroup.valueOf(value.toUpperCase().trim()));
				break;
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
	public static  Attribute generateAttribute(String name, String value) {
		if (idMap.get(name) == null)
			throw new Error("INVALID ATTRIBUTE");
		return new Attribute(idMap.get(name), value);
	}
	public static Attribute generateAttribute(String name) {
		return generateAttribute(name, "");
	}
	private enum ParseType {
		INTEGER, DOUBLE, STRING, ENUMSETPOKEMONTYPE,EXPERIENCEGROUP
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
