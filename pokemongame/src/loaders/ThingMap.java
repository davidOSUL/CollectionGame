package loaders;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import thingFramework.Item;
import thingFramework.Pokemon;
import thingFramework.Thing;

class ThingMap{
	private final Map<String, Thing> map = new HashMap<String, Thing>();
	private final Set<Pokemon> pokemonSet = new HashSet<Pokemon>();
	private final Set<Item> itemSet = new HashSet<Item>();
	private final Map<String, Item> itemMap = new HashMap<String, Item>();
	private final Map<String, Pokemon> pokemonMap = new HashMap<String, Pokemon>();
	
	private void throwIfContains(final Thing thing) {
		if (map.containsKey(thing.getName()))
			throw new IllegalArgumentException("ThingMap already contains: " + thing.getName());
	}
	public void addPokemon(final Pokemon p) {
		throwIfContains(p);
		map.put(p.getName(), p);
		pokemonSet.add(p);
		pokemonMap.put(p.getName(), p);
	}
	public void addItem(final Item i) {
		throwIfContains(i);
		map.put(i.getName(), i);
		itemSet.add(i);
		itemMap.put(i.getName(), i);
	}
	public Set<Pokemon> viewPokemon() {
		return Collections.unmodifiableSet(pokemonSet);
	}
	public Set<Item> viewItems() {
		return Collections.unmodifiableSet(itemSet);
	}
	public Collection<Thing> viewThings() {
		return viewMap().values();
	}
	public Set<? extends Thing> viewThings(final ThingType type) {
		switch(type) {
		case ITEM:
			return viewItems();
		case POKEMON:
			return viewPokemon();
		default:
			throw new IllegalArgumentException("Unexpected Thing Type: " + type);
		}
	}
	public Map<String, Thing> viewMap() {
		return Collections.unmodifiableMap(map);
	}
	public Thing get(final String name) {
		return map.get(name);
	}
	public boolean hasThing(final String name) {
		return map.containsKey(name);
	}
	public Thing getThing(final String name) {
		return map.get(name);
	}
	public Pokemon getPokemon(final String name) {
		return pokemonMap.get(name);
	}
	public Item getItem(final String name) {
		return itemMap.get(name);
	}
}
