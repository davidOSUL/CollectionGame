package thingFramework;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class Attribute<T> implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * Increase in Gold Per Hour
	 */
	private static final Attribute<Integer> GPH = new Attribute<Integer>("gph", AttributeType.STATMOD, AttributeType.GOLDMOD); 
	/**
	 *Increase in Popularity 
	 */
	private static final Attribute<Integer> POPULARITY_BOOST = new Attribute<>("popularity boost", AttributeType.STATMOD, AttributeType.POPMOD);
	
	/**
	 * Increase in Gold Per Minute
	 */
	private static final Attribute<Integer> GPM = new Attribute<Integer>("gpm", AttributeType.STATMOD, AttributeType.GOLDMOD);
	
	/**
	 * Electric, etc.
	 */
	private static final Attribute<PokemonType> TYPE = new Attribute<PokemonType>("type", AttributeType.CHARACTERISTIC);
	/**
	 * Current Happiness of a pokemon
	 */
	private static final Attribute<Integer> HAPPINESS = new Attribute<Integer>("happiness", AttributeType.CHANGINGVAL, AttributeType.POKEONLY);
	/**
	 * Current Level of a pokemon
	 */
	private static final Attribute<Integer> LEVEL = new Attribute<Integer>("level", AttributeType.CHANGINGVAL, AttributeType.POKEONLY);
	/**
	 * The rarity of a pokemon
	 */
	private static final Attribute<Double> RARITY = new Attribute<Double>("rarity", AttributeType.CHARACTERISTIC, AttributeType.POKEONLY);
	private T value = null;
	static int currId = 0;
	private static Map<String, Attribute> idMap;
	private String name;
	private AttributeTypeSet atTypes;
	private int id;
	
	private Attribute(Attribute<T> at, T value) {
		if (!idMap.containsValue(at))
			throw new Error("INVALID ATTRIBUTE: " + at);
		this.name = at.name;
		this.atTypes = at.atTypes;
		this.id = at.id;
		setValue(value);
	}
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
	public String getName() {
		return name;
	}
	
	@Override
	public boolean equals(Object o) {
		if (o == null)
			return false;
		if (! (o instanceof Attribute<?>))
			return false;
		return ((Attribute<?>) o).getName().equals(getName()) && ((Attribute<?>) o).getValue().equals(getValue());
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
	public T getValue() {
		return value;
	}
	public void setValue(T value) {
		this.value = value;
	}
	public boolean hasValue() {
		return this.value != null;
	}
	public static <V> Attribute<V> generateAttribute(String name, V value) {
		if (idMap.get(name) == null)
			throw new Error("INVALID ATTRIBUTE");
		return new Attribute<V>(idMap.get(name), value);
	}
	
}
