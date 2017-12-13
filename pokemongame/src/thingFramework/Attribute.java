package thingFramework;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Attribute {
	/**
	 * Increae in Gold Per Hour
	 */
	public static final Attribute GPH = new Attribute("gpt", AttributeType.STATMOD); 
	/**
	 *Increase in Popularity Per Hour 
	 */
	public static final Attribute PPH = new Attribute("pph", AttributeType.STATMOD);
	
	/**
	 * Increase in Gold Per Minute
	 */
	public static final Attribute GPM = new Attribute("gpm", AttributeType.STATMOD);
	/**
	 * Increase in Popularity Per Minute
	 */
	public static final Attribute PPM = new Attribute("ppm", AttributeType.STATMOD);
	
	/**
	 * Electric, etc.
	 */
	public static final Attribute TYPE = new Attribute("type", AttributeType.CHARACTERISTIC);
	public static final Attribute HAPPINESS = new Attribute("happiness", AttributeType.CHANGINGVAL, AttributeType.POKEONLY);
	public static final Attribute LEVEL = new Attribute("level", AttributeType.CHANGINGVAL, AttributeType.POKEONLY);
	
	static int currId = 0;
	private static Map<String, Attribute> idMap;
	private String name;
	private AttributeTypeSet atTypes;
	private int id;
	private Attribute(String name, AttributeType... types) {
		this(name, currId++, types);
	}
	private Attribute(String name, int id, AttributeType ...types) {
		this.name = name;
		this.id = id;
		atTypes = new AttributeTypeSet(types);
		getIdMap().put(name, this);
	}
	private Map<String, Attribute> getIdMap() {
		if (idMap == null)
			idMap = new HashMap<String, Attribute>();
		return idMap;
	}
	public String toString() {
		return name;
	}
	public static boolean isValidAttribute(String name) {
		return idMap.containsKey(name);
	}
	public static Attribute getAttribute(String name) {
		return isValidAttribute(name) ? idMap.get(name) : null;
	}
	public boolean containsType(AttributeType at) {
		return atTypes.containsAttribute(at);
	}
	public boolean pokeOnly() {
		return containsType(AttributeType.POKEONLY);
	}
	public boolean itemOnly() {
		return containsType(AttributeType.ITEMONLY);
	}
	public static boolean allDontContainType(Set<Attribute> set, AttributeType at) {
		for (Attribute a: set) {
			if ((a.containsType(at)))
				return false;
		}
		return true;
	}
	public static boolean validatePokemon(Set<Attribute> set) {
		return allDontContainType(set, AttributeType.ITEMONLY);
	}
	public static boolean validateItem(Set<Attribute> set) {
		return allDontContainType(set, AttributeType.POKEONLY);
	}
	
	
}
