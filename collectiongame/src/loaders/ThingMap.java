package loaders;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import thingFramework.Creature;
import thingFramework.Item;
import thingFramework.Thing;

/**
 * Maintains a map between Thing names and things, and also can get particular types of things. 
 * @author David O'Sullivan
 *
 */
class ThingMap{
	private final Map<String, Thing> map = new HashMap<String, Thing>();
	private final Set<Creature> creatureSet = new HashSet<Creature>();
	private final Set<Item> itemSet = new HashSet<Item>();
	private final Map<String, Item> itemMap = new HashMap<String, Item>();
	private final Map<String, Creature> creatureMap = new HashMap<String, Creature>();
	
	private void throwIfContains(final Thing thing) {
		if (map.containsKey(thing.getName()))
			throw new IllegalArgumentException("ThingMap already contains: " + thing.getName());
	}
	/**
	 * Add a new Creature. Uses the Creatures's name as its key in the map
	 * @param creature the creature to add
	 */
	public void addCreature(final Creature creature) {
		throwIfContains(creature);
		map.put(creature.getName(), creature);
		creatureSet.add(creature);
		creatureMap.put(creature.getName(), creature);
	}
	/**
	 * Add a new item. Uses the item's name as its key in the map
	 * @param item the item to add
	 */
	public void addItem(final Item item) {
		throwIfContains(item);
		map.put(item.getName(), item);
		itemSet.add(item);
		itemMap.put(item.getName(), item);
	}
	/**
	 * View all the Creatures in this ThingMap
	 * @return an unmodifiableSet of all the creatures in this ThingMap
	 */
	public Set<Creature> viewCreature() {
		return Collections.unmodifiableSet(creatureSet);
	}
	/**
	 * View all the Items in this ThingMap
	 * @return an unmodifiableSet of all the items in this ThingMap
	 */
	public Set<Item> viewItems() {
		return Collections.unmodifiableSet(itemSet);
	}
	/**
	 * View All the Things in this Thing
	 * @return an unmodifiableSet of all the Things in this ThingMap
	 */
	public Collection<Thing> viewThings() {
		return viewMap().values();
	}
	/**
	 * View an unmodifiable Set of things of the provided ThingType
	 * @param type the ThingType
	 * @return a set of things of the provided ThingType
	 */
	public Set<? extends Thing> viewThings(final ThingType type) {
		switch(type) {
		case ITEM:
			return viewItems();
		case CREATURE:
			return viewCreature();
		default:
			throw new IllegalArgumentException("Unexpected Thing Type: " + type);
		}
	}
	/**
	 * View the map between thing names and things
	 * @return an unmodifiable map between thing names and things
	 */
	public Map<String, Thing> viewMap() {
		return Collections.unmodifiableMap(map);
	}
	
	/**
	 * Return true if the map has a Thing with the given name, false otherwise
	 * @param name the name to lookup
	 * @return true if the map has a Thing with the given name, false otherwise
	 */
	public boolean hasThing(final String name) {
		return map.containsKey(name);
	}
	/**
	 * Return the thing mapped to given name
	 * @param name the name of the thing
	 * @return the thing mapped to given name
	 */
	public Thing getThing(final String name) {
		return map.get(name);
	}
	/**
	 * Return the Creature mapped to given name
	 * @param name the name of the Creature
	 * @return the Creature mapped to given name
	 */
	public Creature getCreature(final String name) {
		return creatureMap.get(name);
	}
	/**
	 * Return the Item mapped to given name
	 * @param name the name of the Item
	 * @return the Item mapped to given name
	 */
	public Item getItem(final String name) {
		return itemMap.get(name);
	}
}
