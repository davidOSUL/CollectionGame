package game;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import modifiers.Modifier;
import thingFramework.Item;
import thingFramework.Pokemon;
import thingFramework.Thing;

public class GlobalModifierManager implements Serializable{
	private final List<Modifier<Pokemon>> pokemonModifiers = new ArrayList<Modifier<Pokemon>>();
	private final List<Modifier<Item>> itemModifiers = new ArrayList<Modifier<Item>>();
	private final List<Modifier<Thing>> thingModifiers = new ArrayList<Modifier<Thing>>();
	private final Board b;
	public GlobalModifierManager(final Board b) {
		this.b = b;
	}
	public Collection<Modifier<Thing>> getThingModifiers() {
		return Collections.unmodifiableList(thingModifiers);
	}
	public Collection<Modifier<Item>> getItemModifiers() {
		return Collections.unmodifiableList(itemModifiers);
	}
	public Collection<Modifier<Pokemon>> getPokemonModifiers() {
		return Collections.unmodifiableList(pokemonModifiers);
	}
	public void addGlobalPokemonModifier(final Modifier<Pokemon> mod) {
		pokemonModifiers.add(mod);
	}
	public void addGlobalItemModifier(final Modifier<Item> mod) {
		itemModifiers.add(mod);
	}
	public void addGlobalThingModifier(final Modifier<Thing> mod) {
		thingModifiers.add(mod);
	}
	public void removeGlobalPokemonModifier(final Modifier<Pokemon> mod) {
		pokemonModifiers.remove(mod);
	}
	public void removeGlobalItemModifier(final Modifier<Item> mod) {
		itemModifiers.remove(mod);
	}
	public void removeGlobalThingModifier(final Modifier<Thing> mod) {
		thingModifiers.remove(mod);
	}
	public void update() {
		final List<Modifier<Pokemon>> pokeToRemove = new ArrayList<Modifier<Pokemon>>();
		pokemonModifiers.forEach((mod) -> {
			if (mod.isDone(b.getTotalInGameTime()))
				pokeToRemove.add(mod);
		});
		final List<Modifier<Item>> itemsToRemove = new ArrayList<Modifier<Item>>();
		itemModifiers.forEach((mod) -> {
			if (mod.isDone(b.getTotalInGameTime()))
				itemsToRemove.add(mod);
		});
		final List<Modifier<Thing>> thingsToRemove = new ArrayList<Modifier<Thing>>();
		thingModifiers.forEach((mod) -> {
			if (mod.isDone(b.getTotalInGameTime()))
				thingsToRemove.add(mod);
		});
		pokeToRemove.forEach(mod -> b.removeGlobalPokemonModifier(mod));
		itemsToRemove.forEach(mod -> b.removeGlobalItemModifier(mod));
		thingsToRemove.forEach(mod -> b.removeGlobalThingModifier(mod));
	}
	
}
